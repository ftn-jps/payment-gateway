package ftnjps.paymentgateway.paypal;

import ftnjps.paymentgateway.merchant.Merchant;
import ftnjps.paymentgateway.merchant.MerchantService;
import ftnjps.paymentgateway.transaction.Transaction;
import ftnjps.paymentgateway.transaction.TransactionService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paypal")
public class PaypalController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping("/executePayment/{paymentId}/{payerId}/{transactionToken}")
    public ResponseEntity<?> executePayment(
        @PathVariable String paymentId,
        @PathVariable String payerId,
        @PathVariable String transactionToken
    ) {
        System.out.println(paymentId);
        System.out.println(payerId);
        System.out.println(transactionToken);

        Transaction t = transactionService.findByToken(transactionToken);

        final String url = "https://api.sandbox.paypal.com/v1/payments/payment/" + paymentId + "/execute";
        final String payload = "{\"payer_id\":\"" +
            payerId +
            "\"}";

        final StringEntity body =new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);
        final String accessToken = PaypalService.getPaypalAccessToken(merchantService.findByMerchantId(t.getMerchantId()));

        HttpPost request = new HttpPost(url);
        request.setEntity(body);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Authorization", "Bearer " + accessToken);

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println(t.getSuccessUrl());
            final String successUrl = " { \"url\" : \" " +
                t.getSuccessUrl() +
                "\" }";

            return new ResponseEntity<>(successUrl, HttpStatus.OK);
        } catch (Exception e) {
            final String failUrl = " { \"url\" : \" " +
                t.getFailUrl() +
                "\" }";
            return new ResponseEntity<>(failUrl, HttpStatus.OK);
        }
    }


}
