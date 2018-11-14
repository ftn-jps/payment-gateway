package ftnjps.paymentgateway.bank;

import java.util.List;

public interface BankService {

	Bank findOne(Long id);

	List<Bank> findAll();

	Bank findByIin(String iin);

	Bank add(Bank input);

}
