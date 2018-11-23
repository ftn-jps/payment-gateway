package ftnjps.paymentgateway.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public Transaction findOne(Long id) {
		return transactionRepository.findById(id).orElse(null);
	}

	@Override
	public List<Transaction> findAll() {
		return transactionRepository.findAll();
	}

	@Override
	public Transaction findByToken(String token) {
		return transactionRepository.findByToken(token);
	}

	@Override
	public Transaction add(Transaction input) {
		return transactionRepository.save(input);
	}

}
