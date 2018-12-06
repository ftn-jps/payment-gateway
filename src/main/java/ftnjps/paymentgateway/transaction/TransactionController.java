package ftnjps.paymentgateway.transaction;

import java.net.URI;

import javax.validation.Valid;

import com.jayway.jsonpath.JsonPath;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${frontend.port}")
	private int port;

	@PostMapping
	public ResponseEntity<?> startTransaction(@RequestBody @Valid Transaction transaction) {
		Transaction newTransaction = transactionService.add(transaction);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location",
				"https://localhost:" + port + "/#/transaction/" + newTransaction.getToken());
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}

	@GetMapping("/{token}/type/{paymentType}")
	public ResponseEntity<?> forwardTransaction(
			@PathVariable String token,
			@PathVariable PaymentType paymentType) {
		Transaction transaction = transactionService.findByToken(token);
		RestTemplate restClient = new RestTemplate();

		if(paymentType == PaymentType.PAYPAL) {
			String url = "https://api.sandbox.paypal.com/v1/payments/payment";

			try {

				//TODO: popuniti ovaj payload sa pravim vrednostima. Jebiga sto je ruzno
				String payload = "{\"intent\": \"sale\",\"redirect_urls\": {\"return_url\": \"https://example.com/your_redirect_url.html\"," +
						"\"cancel_url\": \"https://example.com/your_cancel_url.html\"},\"payer\": {\"payment_method\": \"paypal\"},\"transactions\": " +
						"[{\"amount\": {\"total\": \"7.47\",\"currency\": \"USD\"}}]}";
				StringEntity body =new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

				HttpPost request = new HttpPost(url);
				request.setEntity(body);
				request.addHeader("Content-Type", "application/json");
				request.addHeader("Authorization", "Bearer " + token );

				HttpClient httpClient = HttpClientBuilder.create().build();
				HttpResponse response = httpClient.execute(request);

				String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
				System.out.println(responseString);

				String allowLink = JsonPath.read(responseString,"$.links[1].href");
				String executeLink = JsonPath.read(responseString,"$.links[2].href");

			}catch (Exception ex) {
				ex.printStackTrace();
			}

			return new ResponseEntity<>("DSADA",HttpStatus.FOUND);

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

				String[] split1 = response.getBody().split(":"); // gives array of 16 strings made from JSON object
				String[] split2 = split1[15].split("\",");      // takes **** //sandbox.coingate.com/invoice/e4ba2d6b-a0be-43bc-943c-c76233c18b19","token" ****
				String paymentUrl = "https:" + split2[0];      // and converts it into array of 2 strings where split2[0] is the url

				return new ResponseEntity<String>(paymentUrl, HttpStatus.OK);
			
			}catch (Exception ex) {
				
				ex.printStackTrace();
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		
		if(paymentType != PaymentType.BANK) // TODO
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		// BANK
		String bankUrl = merchantService
				.findByMerchantId(transaction.getMerchantId())
				.getBankUrl();
		URI response = restClientSelfSigned.postForLocation(
				bankUrl + "/api/transactions",
				transaction);
		String paymentUrl = response.toString();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location",
				paymentUrl);
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
}
