package ftnjps.paymentgateway.transaction;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	RestTemplate restClient;

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

		if(paymentType == PaymentType.BITCOIN) {
			
			String url = "https://api-sandbox.coingate.com/v2/orders";
			
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

			HttpHeaders headersForRedirectingToBitcoinPaymentURL = new HttpHeaders();
			headersForRedirectingToBitcoinPaymentURL.add("Location", paymentUrl);
			
			return new ResponseEntity<>(headersForRedirectingToBitcoinPaymentURL, HttpStatus.FOUND);
			
		}
		
		if(paymentType != PaymentType.BANK) // TODO
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		// BANK
		String bankUrl = merchantService
				.findByMerchantId(transaction.getMerchantId())
				.getBankUrl();
		URI response = restClient.postForLocation(
				bankUrl + "/api/transactions",
				transaction);
		String paymentUrl = response.toString();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location",
				paymentUrl);
		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
}
