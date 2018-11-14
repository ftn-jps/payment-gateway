package ftnjps.paymentgateway.bankclient;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ftnjps.paymentgateway.bank.Bank;

@Repository
public interface BankClientRepository extends JpaRepository<BankClient, Long> {

	BankClient findByPan(String pan);
	List<BankClient> findByBank(Bank bank);
	BankClient findByMerchantId(String merchantId);

}
