package ftnjps.paymentgateway.processors;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ftnjps.paymentgateway.merchant.MerchantService;
import ftnjps.paymentgateway.transaction.Transaction;

@Component
public class BankProcessor implements PaymentProcessor{

	@Autowired
	private MerchantService merchantService;
	@Autowired
	RestTemplate restClientSelfSigned;

	@Override
	public ResponseEntity<?> process(Transaction transaction) {
		System.out.println("Getting bank url from merchant with id " + transaction.getMerchantId());
		String bankUrl = merchantService
				.findByMerchantId(transaction.getMerchantId())
				.getBankUrl();

		if(bankUrl == null) {
			System.out.println("Merchant with id "
					+ transaction.getMerchantId()
					+ " doesn't support credit card payments");
			return new ResponseEntity<>("Current mercant doesn't " +
				"support bank payments", HttpStatus.BAD_REQUEST);
		}

		System.out.println("Bank url successfully obtained: " + bankUrl);
		URI response = restClientSelfSigned.postForLocation(
				bankUrl + "/api/transactions",
				transaction);
		String paymentUrl = response.toString();
		System.out.println("Redirecting to credit card payment url");
		return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
	}

}
