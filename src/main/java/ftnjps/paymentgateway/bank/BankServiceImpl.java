package ftnjps.paymentgateway.bank;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class BankServiceImpl implements BankService {

	@Autowired
	private BankRepository bankRepository;

	@Override
	public Bank findOne(Long id) {
		return bankRepository.findById(id).orElse(null);
	}

	@Override
	public List<Bank> findAll() {
		return bankRepository.findAll();
	}

	@Override
	public Bank findByIin(String iin) {
		return bankRepository.findByIin(iin);
	}

	@Override
	public Bank add(Bank input) {
		return bankRepository.save(input);
	}

}
