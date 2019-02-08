package ftnjps.paymentgateway.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import ftnjps.paymentgateway.merchant.Merchant;
import ftnjps.paymentgateway.merchant.MerchantService;
import ftnjps.paymentgateway.transaction.Transaction;

@Component
public class BitcoinProcessor implements PaymentProcessor{

	@Autowired
	private MerchantService merchantService;

	@Override
	public ResponseEntity<?> process(Transaction transaction) {
		System.out.println("Getting merchant with id " + transaction.getMerchantId());
		final Merchant merchant = merchantService.findByMerchantId(transaction.getMerchantId());

		if(merchant.getBitcoinToken() == null) {
			System.out.println("Merchant with id " + merchant.getMerchantId() + " doesn't support bitcoin payments");
			return new ResponseEntity<>("Current merchant doesn't" +
				" support bitcoin payments", HttpStatus.OK);
		}

		System.out.println("Merchant with id " + merchant.getMerchantId() + " successfully obtained");

		String url = "https://api-sandbox.coingate.com/v2/orders";

		try {
			System.out.println("Generating request for bitcoin payment");
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/x-www-form-urlencoded");
			headers.set("Authorization", "Token " + merchant.getBitcoinToken());

			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("order_id", transaction.getId() + "");
			map.add("price_amount", transaction.getAmount() + "");
			map.add("price_currency", "USD");
			map.add("receive_currency", "USD");
			map.add("title", transaction.getToken());
			map.add("cancel_url", transaction.getFailUrl());
			map.add("success_url", transaction.getSuccessUrl());

			System.out.println("Sending request for bitcoin payment");
			RestTemplate restClient = new RestTemplate();
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			ResponseEntity<String> response = restClient.postForEntity(url, request, String.class);
			System.out.println("Payment created successfully");
			JsonParser basicJsonParser = new BasicJsonParser();
			String paymentUrl = (String)basicJsonParser.parseMap(response.getBody()).get("payment_url");

			System.out.println("Redirecting user to allow link");
			return new ResponseEntity<String>(paymentUrl, HttpStatus.OK);

		}catch (Exception ex) {

			ex.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
