package ftnjps.paymentgateway.paypal;

import com.jayway.jsonpath.JsonPath;
import ftnjps.paymentgateway.merchant.Merchant;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Base64;

@Service
public class PaypalService {
    public static String getPaypalAccessToken(final Merchant merchant){
        if("".equals(merchant.getPaypalSecret()) || merchant.getPaypalSecret() == null) {
            throw new EntityNotFoundException("Merchant that started the transaction doesn't" +
                "have a paypal account.");
        }
        final String getAccessTokenUrl = "https://api.sandbox.paypal.com/v1/oauth2/token";
        final String clientId = merchant.getPaypalClient();
        final String secretId = merchant.getPaypalSecret();
        final String authorizatioHeaderValue =
            "Basic " +
                new String(Base64.getEncoder().encode((clientId + ":" + secretId).getBytes()
                ));
        final String getAccessTokenPayload = "grant_type=client_credentials";
        final StringEntity getAccessTokenBody =
            new StringEntity(getAccessTokenPayload, ContentType.APPLICATION_FORM_URLENCODED);

        final HttpPost getAccessTokenRequest = new HttpPost(getAccessTokenUrl);

        getAccessTokenRequest.setEntity(getAccessTokenBody);
        getAccessTokenRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        getAccessTokenRequest.addHeader("Authorization", authorizatioHeaderValue );

        final HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpResponse response = httpClient.execute(getAccessTokenRequest);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            String accessToken = JsonPath.read(responseString, "$.access_token");
            return accessToken;
        } catch (Exception e) {
            throw new RuntimeException("Error ocurred when trying to get paypal token");
        }
    }

    public static String createPlan(String accessToken, double amount){
        final String url = "https://api.sandbox.paypal.com/v1/payments/billing-plans";
        final String payload = "{\"name\":\"Magazine subscription plan\",\"description\":\"A plan that is defined for" +
            " billing user for accessing magazine\",\"type\":\"fixed\",\"payment_definitions\":[{\"name\":\"Regular " +
            "payment definition\",\"type\":\"REGULAR\",\"frequency\":\"MO" +
            "NTH\",\"frequency_interval\":\"1\",\"amount\":{\"value\":" +
            amount +
            "," +
            "\"currency\":\"USD\"},\"cycles\":\"24\"}]," +
            "\"merchant_preferences\":{\"setup_fee\":{\"value\":\"0\",\"currency\":\"USD\"}," +
            "\"return_url\":\"http://localhost:4201/subscription/success\"," +
            "\"cancel_url\":" + "\"http://localhost:4201/subscription/failure\"," +
            "\"auto_bill_amount\":\"YES\"," +
            "\"initial_fail_amount_action\":\"CONTINUE\",\"max_fail_attempts\":\"0\"}}";

        final StringEntity body =new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

        HttpPost request = new HttpPost(url);
        request.setEntity(body);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Authorization", "Bearer " + accessToken);

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            return responseString;
        } catch (Exception e) {
            return null;
        }

    }

    public static int activatePlan(final String accessToken, final String planId){
        final String url = "https://api.sandbox.paypal.com/v1/payments/billing-plans";
        final String payload = "[{\"op\":\"replace\"," +
            "\"path\":\"/\",\"value\":{\"state\":\"ACTIVE\"}}]";

        final StringEntity body =new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

        HttpPatch request = new HttpPatch(url + "/" + planId);
        request.setEntity(body);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Authorization", "Bearer " + accessToken);

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpClient.execute(request);
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return 500;
        }
    }

    public static String createAgreement(
        final String accessToken,
        final String startDate,
        final String planId
    ){
        final String url = "https://api.sandbox.paypal.com/v1/payments/billing-agreements";
        final String payload =
            "{\"name\":\"Sporazum o placanju\"," +
            "\"description\":\"Sporazum o placanju magazina na mesecnom nivou od strane korisnika\"," +
            "\"start_date\":\"2019-06-17T9:45:04Z\"," +
            "\"payer\":{\"payment_method\":\"paypal\",\"payer_info\":{\"email\":\"fpetrovic-buyer@ymail.com\"}}," +
            "\"plan\":{\"id\":\"" +
            planId +
            "\"}}";

        final StringEntity body =new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

        HttpPost request = new HttpPost(url);
        request.setEntity(body);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Authorization", "Bearer " + accessToken);

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            return responseString;
        } catch (Exception e) {
            return null;
        }
    }

}
