package ftnjps.paymentgateway.bankclient;

import java.util.List;

import ftnjps.paymentgateway.bank.Bank;

public interface BankClientService {

	BankClient findOne(Long id);

	List<BankClient> findAll();

	BankClient findByPan(String pan);

	List<BankClient> findByBank(Bank bank);

	BankClient findByMerchantId(String merchantId);

	BankClient add(BankClient input);

}
