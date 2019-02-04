package ftnjps.paymentgateway.transaction;

import java.net.URI;
import java.util.Base64;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import ftnjps.paymentgateway.merchant.Merchant;
import ftnjps.paymentgateway.paypal.PaypalService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import ftnjps.paymentgateway.merchant.MerchantService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	@Autowired
	private MerchantService merchantService;
	@Autowired
	RestTemplate restClientSelfSigned;

	@Value("${frontend.url}")
	private String frontendUrl;

	@PostMapping
	public ResponseEntity<?> startTransaction(@RequestBody @Valid Transaction transaction) {
		Transaction newTransaction = transactionService.add(transaction);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location",
				frontendUrl + "/#/transaction/" + newTransaction.getToken());
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}

	@GetMapping("/{token}/type/{paymentType}")
	public ResponseEntity<?> forwardTransaction(
			@PathVariable String token,
			@PathVariable PaymentType paymentType)
	{
		Transaction transaction = transactionService.findByToken(token);
		RestTemplate restClient = new RestTemplate();

		if(paymentType == PaymentType.PAYPAL) {
			final Merchant merchant = merchantService.findByMerchantId(transaction.getMerchantId());

			if(merchant.getPaypalSecret() == null){
				return new ResponseEntity<>("Current merchant doesn't" +
					" support paypal payments", HttpStatus.OK);
			}

			final String accessToken = PaypalService.getPaypalAccessToken(merchant);
			final String encodedAccessToken =
				new String(Base64.getEncoder().encode(accessToken.getBytes()));

			try {
				final String url = "https://api.sandbox.paypal.com/v1/payments/payment";
				final String payload =
					"{\"intent\": \"sale\",\"redirect_urls\": {\"return_url\": " +
					"\"https://localhost:4201/paypal/success\"," +
					"\"cancel_url\": \"https://localhost:4201/paypal/failure\"},\"payer\":" +
					" {\"payment_method\": \"paypal\"},\"transactions\": " +
					"[{\"amount\": {\"total\": \"" +
					String.valueOf(transaction.getAmount()) +
					"\",\"currency\": \"USD\"}}]}";
				final StringEntity body =new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

				HttpPost request = new HttpPost(url);
				request.setEntity(body);
				request.addHeader("Content-Type", "application/json");
				request.addHeader("Authorization", "Bearer " + accessToken );

				HttpClient httpClient = HttpClientBuilder.create().build();
				HttpResponse response = httpClient.execute(request);

				String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
				System.out.println(responseString);

				String allowLink = JsonPath.read(responseString,"$.links[1].href");
				final String encodedAllowLink =
					new String(Base64.getEncoder().encode(allowLink.getBytes()));

				return new ResponseEntity<>(encodedAllowLink + "\n" + encodedAccessToken,HttpStatus.OK);

			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if(paymentType == PaymentType.BITCOIN) {

			String url = "https://api-sandbox.coingate.com/v2/orders";

			try {
				HttpHeaders headers = new HttpHeaders();
				headers.set("Content-Type", "application/x-www-form-urlencoded");
				headers.set("Authorization", "Token FzQjbFWsjfH4LtVzwse6c33hGBWa1fiYag8g24ou");

				MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
				map.add("order_id", transaction.getId() + "");
				map.add("price_amount", transaction.getAmount() + "");
				map.add("price_currency", "USD");
				map.add("receive_currency", "USD");
				map.add("title", token);

				HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
				ResponseEntity<String> response = restClient.postForEntity(url, request, String.class);

				JsonParser basicJsonParser = new BasicJsonParser();
				String paymentUrl = (String)basicJsonParser.parseMap(response.getBody()).get("payment_url");

				return new ResponseEntity<String>(paymentUrl, HttpStatus.OK);

			}catch (Exception ex) {

				ex.printStackTrace();
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}

		// BANK

		String bankUrl = merchantService
				.findByMerchantId(transaction.getMerchantId())
				.getBankUrl();

		if(bankUrl == null) {
			return new ResponseEntity<>("Current mercant doesn't " +
				"support bank payments", HttpStatus.BAD_REQUEST);
		}
		URI response = restClientSelfSigned.postForLocation(
				bankUrl + "/api/transactions",
				transaction);
		String paymentUrl = response.toString();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location",
			paymentUrl);
//		return new ResponseEntity<>(headers, HttpStatus.FOUND);
		return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
	}



}
