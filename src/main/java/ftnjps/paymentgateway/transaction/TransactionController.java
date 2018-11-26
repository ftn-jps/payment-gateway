package ftnjps.paymentgateway.transaction;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	@Value("${server.port}")
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
		return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
	}
}
