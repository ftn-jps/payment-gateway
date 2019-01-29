package ftnjps.paymentgateway;

import javax.annotation.PostConstruct;

import ftnjps.paymentgateway.subscription.Subscription;
import ftnjps.paymentgateway.subscription.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ftnjps.paymentgateway.merchant.Merchant;
import ftnjps.paymentgateway.merchant.MerchantService;
import ftnjps.paymentgateway.transaction.Transaction;
import ftnjps.paymentgateway.transaction.TransactionService;

@Component
public class TestData {

	@Autowired
	private MerchantService merchantService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private SubscriptionService subscriptionService;

	@PostConstruct
	private void init() {
		Merchant m1 = new Merchant(
			"test",
			"https://localhost:8085",
			"ATpIbDWRhS_L3c46Iz9-qgE1l_Iisfg6u4luC-JOPxfp2Klg6VrRz1ANceUgXSH-OTEckdBLwIKrs4Ug",
			"EDDxtGUSysQafC--ODcd8jKoE8ZFNNa3tNB82I4u4lTPU3cNjeV3DsWO8NhE3H1mK46Euz0ZtOD5HNBO"
			);
		merchantService.add(m1);
		// payment of BITCOINS is in $, so the amount needs to be low, bcs there is a limited amount of them in the virtual wallet
		Transaction t1 = new Transaction(1,
				"test",
				"testtest",
				14,
				"WORKING",
				"SOMETHING NOT WORKING",
				"NOTHING IS WORKING");
		t1.setToken("111");
		transactionService.add(t1);

		Subscription s1 = new Subscription(
			9.99,
			"test",
			"http://localhost:4201/subscription/success",
			"http://localhost:4201/subscription/failure"
		);
		s1.setToken("13");
		subscriptionService.add(s1);
	}

}
