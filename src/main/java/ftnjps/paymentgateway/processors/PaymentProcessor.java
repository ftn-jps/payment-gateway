package ftnjps.paymentgateway.processors;

import org.springframework.http.ResponseEntity;

import ftnjps.paymentgateway.transaction.Transaction;

public interface PaymentProcessor {
	/**
	 * Process payment
	 * @return Payment URL to redirect to
	 */
	ResponseEntity<?> process(Transaction transaction);
}
