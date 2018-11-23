package ftnjps.paymentgateway.transaction;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	@Value("${server.port}")
	private int port;

	@PostMapping
	public ResponseEntity<?> startTransaction(@RequestBody @Valid Transaction transaction) {
		transactionService.add(transaction);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location",
				"https://localhost:" + port + "/#/transaction/" + transaction.getToken());
		return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
	}

	@PostMapping("/{token}/type/{paymentType}")
	public ResponseEntity<?> forwardTransaction(
			@PathVariable String token,
			@PathVariable PaymentType paymentType) {
		Transaction transaction = transactionService.findByToken(token);

		if(paymentType != PaymentType.BANK) // TODO
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		// BANK
		String bankUrl = merchantService
				.findByMerchantId(transaction.getMerchantId())
				.getBankUrl();
		RestTemplate rest = new RestTemplate();
		@SuppressWarnings("unchecked")
		HashMap<String, String> response = rest.getForObject(bankUrl, HashMap.class);
		String paymentUrl = response.get("paymentUrl");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Location",
				paymentUrl);
		return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
	}
}
