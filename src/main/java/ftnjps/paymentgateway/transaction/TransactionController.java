package ftnjps.paymentgateway.transaction;

import javax.validation.Valid;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ftnjps.paymentgateway.processors.PaymentProcessor;
import ftnjps.paymentgateway.processors.PaymentType;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	@Autowired
	private ApplicationContext springContext;

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

	@GetMapping("/getTransaction/{token}")
	public ResponseEntity<?> getTransactionByToken(
		@PathVariable String token
	) {
		return new ResponseEntity<>(transactionService.findByToken(token), HttpStatus.OK);
	}


	@GetMapping("/{token}/type/{paymentType}")
	public ResponseEntity<?> forwardTransaction(
			@PathVariable String token,
			@PathVariable PaymentType paymentType)
	{
		System.out.println("Getting transaction with token " + token );
		Transaction transaction = transactionService.findByToken(token);
		System.out.println("Transaction with token " + token + " successfully obtained");

		String className = paymentType.name().substring(0, 1)
				+ paymentType.name().substring(1).toLowerCase();

		PaymentProcessor processor;
		try {
			processor = (PaymentProcessor)
					springContext.getBean(
							Class.forName("ftnjps.paymentgateway.processors." + className + "Processor"));
			return processor.process(transaction);
		} catch (BeansException | ClassNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}



}
