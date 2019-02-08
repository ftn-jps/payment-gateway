package ftnjps.paymentgateway.subscription;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import ftnjps.paymentgateway.merchant.MerchantService;
import ftnjps.paymentgateway.paypal.PaypalService;
import ftnjps.paymentgateway.transaction.Transaction;
import net.minidev.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import javax.xml.ws.Response;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private MerchantService merchantService;

    @PostMapping
    public ResponseEntity<HttpHeaders> addSubscription(@RequestBody @Valid final Subscription subscription){
        System.out.println("Adding new subscription");
        System.out.println("Subscription details: ");
        System.out.println("\tAmount: "+ subscription.getAmount());
        System.out.println("\tMerchant: "+ subscription.getMerchantId());
        Subscription s = subscriptionService.add(subscription);
        System.out.println("Generating payment-gateway frontend url for subscribtion");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location",
            "https://localhost:4201" + "/subscription/#/" + s.getToken());
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping(value = "/subscribe/{token}", produces = "application/json")
    public ResponseEntity<?> subscribe(@PathVariable final String token){
        System.out.println("Getting subscription with token: " + token);
        Subscription s = subscriptionService.findByToken(token);
        System.out.println("Subscription with token: " + token + " found!");
        System.out.println("Getting paypal access token for merchant " +
            merchantService.findByMerchantId(s.getMerchantId()).getMerchantId());
        final String accessToken = PaypalService.getPaypalAccessToken(
            merchantService.findByMerchantId(s.getMerchantId())
        );
        System.out.println("Paypal access token successfully obtained");
        //kreiras plan
        System.out.println("Creating billing plan for subscription " + s.getToken());
        final String createPlanResponse = PaypalService.createPlan(accessToken, s);
        System.out.println("Billing plan for subscription " + s.getToken() + " successfully added");
        String planUrl = JsonPath.read(createPlanResponse, "$.links[0].href");
        String planId =   JsonPath.read(createPlanResponse, "$.id");

        //aktiviras plan
        System.out.println("Activating billing plan for subscription " + s.getToken());
        final int activatePlanResponse = PaypalService.activatePlan(accessToken,planId);

        if(activatePlanResponse != 200) {
            System.out.println("Billing plan for subscription " + s.getToken() + " was not activated!");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        System.out.println("Billing plan for subscription " + s.getToken() + " successfully activated");
        //napravis agreement
        String startDate = (new Date()).toInstant().plus(Duration.ofMinutes(2)).toString();

        System.out.println("Creating billing  agreement for subscription " + s.getToken());
        final String createAgreement = PaypalService.createAgreement(accessToken,startDate, planId);
        String approvalUrl = JsonPath.read(createAgreement, "$.links[0].href");
        System.out.println("Billing agreement for subscription " + s.getToken() + " successfully created");

        String encodedToken =  new String(Base64.getEncoder().encode(accessToken.getBytes()));
        String jsonString = "{ " +
            "\"url\" : \""+ approvalUrl+"\"," +
            "\"encodedAccessToken\" : \""+ encodedToken+"\"" +
            "}";



        //sutnes ga na confirmAgreement
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }

    @GetMapping("/executeAgreement/{agreementToken}/{subscriptionToken}")
    public ResponseEntity<?> executeAgreement(
        @PathVariable String agreementToken,
        @PathVariable String subscriptionToken
    ) {
        System.out.println(agreementToken);

        Subscription s = subscriptionService.findByToken(subscriptionToken);

        final String url = "https://api.sandbox.paypal.com/v1/payments/billing-agreements/" + agreementToken + "/agreement-execute";

        final String accessToken = PaypalService.getPaypalAccessToken(merchantService.findByMerchantId(s.getMerchantId()));

        HttpGet request = new HttpGet(url);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Authorization", "Bearer " + accessToken);

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println(s.getSuccessUrl());
            final String successUrl = " { \"url\" : \" " +
                s.getSuccessUrl() +
                "\" }";

            return new ResponseEntity<>(successUrl, HttpStatus.OK);
        } catch (Exception e) {
            final String failUrl = " { \"url\" : \" " +
                s.getFailUrl() +
                "\" }";
            return new ResponseEntity<>(failUrl, HttpStatus.OK);
        }
    }



}
