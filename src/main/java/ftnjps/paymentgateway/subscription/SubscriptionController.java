package ftnjps.paymentgateway.subscription;

import com.jayway.jsonpath.JsonPath;
import ftnjps.paymentgateway.merchant.MerchantService;
import ftnjps.paymentgateway.paypal.PaypalService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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
        Subscription s = subscriptionService.add(subscription);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location",
            "https://localhost:4201" + "/subscription/#/" + s.getToken());
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/{token}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable final String token){
        return new ResponseEntity<Subscription>(subscriptionService.findByToken(token), HttpStatus.OK);
    }

    @GetMapping("/subscribe/{token}")
    public ResponseEntity<?> subscribe(@PathVariable final String token){
        Subscription s = subscriptionService.findByToken(token);
        final String accessToken = PaypalService.getPaypalAccessToken(
            merchantService.findByMerchantId(s.getMerchantId())
        );

        //kreiras plan
        final String createPlanResponse = PaypalService.createPlan(accessToken, s.getAmount() );
        String planUrl = JsonPath.read(createPlanResponse, "$.links[0].href");
        String planId =   JsonPath.read(createPlanResponse, "$.id");

        //aktiviras plan
        final int activatePlanResponse = PaypalService.activatePlan(accessToken,planId);
        if(activatePlanResponse != 200) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //napravis agreement
        String twoMinsLaterTime = (new Date()).toInstant().plus(Duration.ofMinutes(2)).toString();
        System.out.println(twoMinsLaterTime);
        final String createAgreement = PaypalService.createAgreement(accessToken,twoMinsLaterTime);
        System.out.println(createAgreement);

        //sutnes ga na confirmAgreement
        return null;
    }
}
