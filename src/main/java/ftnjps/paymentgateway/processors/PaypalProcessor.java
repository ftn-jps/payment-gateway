package ftnjps.paymentgateway.processors;

import java.util.Base64;

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
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.JsonPath;

import ftnjps.paymentgateway.merchant.Merchant;
import ftnjps.paymentgateway.merchant.MerchantService;
import ftnjps.paymentgateway.paypal.PaypalService;
import ftnjps.paymentgateway.transaction.Transaction;

@Component
public class PaypalProcessor implements PaymentProcessor{

	@Autowired
	private MerchantService merchantService;

	@Override
	public ResponseEntity<?> process(Transaction transaction) {
		System.out.println("Getting merchant with id " + transaction.getMerchantId());
		final Merchant merchant = merchantService.findByMerchantId(transaction.getMerchantId());


		if(merchant.getPaypalSecret() == null){
			System.out.println("Merchant with id " + merchant.getMerchantId() + " doesn't support paypal payments");
			return new ResponseEntity<>("Current merchant doesn't" +
				" support paypal payments", HttpStatus.OK);
		}

		System.out.println("Merchant with id " + merchant.getMerchantId() + " successfully obtained");

		final String accessToken = PaypalService.getPaypalAccessToken(merchant);
		final String encodedAccessToken =
			new String(Base64.getEncoder().encode(accessToken.getBytes()));

		try {
			final String url = "https://api.sandbox.paypal.com/v1/payments/payment";
			final String payload =
				"{\"intent\": \"sale\",\"redirect_urls\": {\"return_url\": " +
				"\"https://localhost:4201/paypal/success/" + transaction.getToken() + "\"," +
				"\"cancel_url\": \"https://localhost:4201/paypal/failure/" + transaction.getToken() +  "\"}," +
				"\"payer\":" +
				" {\"payment_method\": \"paypal\"},\"transactions\": " +
				"[{\"amount\": {\"total\": \"" +
				String.valueOf(transaction.getAmount()) +
				"\",\"currency\": \"USD\"}}]}";
			final StringEntity body =new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

			System.out.println("Generating request for creating payment");
			HttpPost request = new HttpPost(url);
			request.setEntity(body);
			request.addHeader("Content-Type", "application/json");
			request.addHeader("Authorization", "Bearer " + accessToken );

			HttpClient httpClient = HttpClientBuilder.create().build();
			System.out.println("Sending request for creating payment");
			HttpResponse response = httpClient.execute(request);

			System.out.println("Payment created successfully");
			String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

			String allowLink = JsonPath.read(responseString,"$.links[1].href");
			final String encodedAllowLink =
				new String(Base64.getEncoder().encode(allowLink.getBytes()));

			System.out.println("Redirecting user to allow link");
			return new ResponseEntity<>(encodedAllowLink + "\n" + encodedAccessToken,HttpStatus.OK);

		}catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
