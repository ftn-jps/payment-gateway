package ftnjps.paymentgateway;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ftnjps.paymentgateway.merchant.Merchant;
import ftnjps.paymentgateway.merchant.MerchantService;
import ftnjps.paymentgateway.subscription.Subscription;
import ftnjps.paymentgateway.subscription.SubscriptionService;
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
				"1111-1111",
				"https://localhost:8085",
				"ATpIbDWRhS_L3c46Iz9-qgE1l_Iisfg6u4luC-JOPxfp2Klg6VrRz1ANceUgXSH-OTEckdBLwIKrs4Ug",
				"EDDxtGUSysQafC--ODcd8jKoE8ZFNNa3tNB82I4u4lTPU3cNjeV3DsWO8NhE3H1mK46Euz0ZtOD5HNBO",
                "oYHbxBkuBPtPu2c1FDszn7EQ1jq3YKjB1xeC-7ob"
				);
		merchantService.add(m1);
		Merchant m2 = new Merchant(
				"2222-2222",
				"https://localhost:8085",
				"AYlJx-Cj7IAnpE2hUhMCkUC7yr2k7gc7fTpIltzCx1K49ud8vrZlygsjyAb06BqV5jg8qTXNmY1horoN",
				"EBg6mriKkYfLz7z3e7NGCEVYVis5gttdpWO3KjK7WOWhRgc5YpCwTR7334CqaMoU_wWNFQj5QLDOQvYR",
            "exVoz3vPTSzMR2zhp-sPVndb2xGmEmPaEH72V9ab"

				);
		merchantService.add(m2);
		Merchant m3 = new Merchant(
				"3333-3333",
				"https://localhost:8085",
				"AWzSm6cXMy0x7R8_O1vyEFGBYFR3mpcg4TlymBqLAtd8F8zVdEumPng4lt6DpEVmllFqKvPKiVAsy2lP",
				"EMvoym1t4MEP8ZMJurUSzvP1PxmxwVRKMDFCCjX0a_DSKPSt7GxJcBUDhx24aCsndC68TtuTq_PWgB8g",
            "FzQjbFWsjfH4LtVzwse6c33hGBWa1fiYag8g24ou"
				);
		merchantService.add(m3);
		Merchant m4 = new Merchant(
				"4444-4444",
				"https://localhost:8085",
				"AUTVmSk_KTNwyvq5CObIwz_oyQJKSuzVKHWRaxy46xDzXt3NTBpcgK5rp5LrDrgzrs1yA5ygf_hX9_wk",
				"ECKCchD_eMAtgvU2qudU2hbw-Z3GLjn8nXFJa3yHyDgXKUjO1l_8h7ZTRAaxgCMozO8pLoRBeG7h2rZs",
            "oYHbxBkuBPtPu2c1FDszn7EQ1jq3YKjB1xeC-7ob"
				);
		merchantService.add(m4);

		// payment of BITCOINS is in $, so the amount needs to be low, bcs there is a limited amount of them in the virtual wallet
		Transaction t1 = new Transaction(1,
				"1111-1111",
				null,
				19,
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
