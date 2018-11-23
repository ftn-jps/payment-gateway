package ftnjps.paymentgateway.transaction;

import java.util.List;

public interface TransactionService {

	Transaction findOne(Long id);
 	List<Transaction> findAll();
 	Transaction findByToken(String token);
 	Transaction add(Transaction input);

}
