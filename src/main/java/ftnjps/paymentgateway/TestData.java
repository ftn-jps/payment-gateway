package ftnjps.paymentgateway;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ftnjps.paymentgateway.bank.Bank;
import ftnjps.paymentgateway.bank.BankService;
import ftnjps.paymentgateway.bankclient.BankClient;
import ftnjps.paymentgateway.bankclient.BankClientService;

@Component
public class TestData {

	@Autowired
	private BankService bankService;
	@Autowired BankClientService bankClientService;

	@PostConstruct
	private void init() {
		Bank bank1 = new Bank("512345", "First Bank");
		bankService.add(bank1);

		Bank bank2 = new Bank("444444", "Second Bank");
		bankService.add(bank2);

		BankClient client1 = new BankClient(bank1.getIin() + "1234567890",
				"Bojan Stipic",
				"123",
				1605390265000l, // 14.11.2020.
				bank1,
				"bojan",
				"123456");
		bankClientService.add(client1);

		BankClient client2 = new BankClient(bank1.getIin() + "1134567890",
				"Filip Petrovic",
				"123",
				1605390265000l, // 14.11.2020.
				bank1,
				"filip",
				"123456");
		bankClientService.add(client2);

		BankClient client3 = new BankClient(bank2.getIin() + "1114567890",
				"Ivan Jancic",
				"123",
				1605390265000l, // 14.11.2020.
				bank2,
				"ivan",
				"123456");
		bankClientService.add(client3);
	}

}
