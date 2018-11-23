package ftnjps.paymentgateway;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ftnjps.paymentgateway.merchant.Merchant;
import ftnjps.paymentgateway.merchant.MerchantService;

@Component
public class TestData {

	@Autowired
	private MerchantService merchantService;

	@PostConstruct
	private void init() {
		Merchant m1 = new Merchant("test", "https://localhost:8085");
		merchantService.add(m1);
	}

}
