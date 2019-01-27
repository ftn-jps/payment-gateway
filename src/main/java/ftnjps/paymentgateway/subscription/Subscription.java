package ftnjps.paymentgateway.subscription;

import javax.persistence.Entity;

@Entity
public class Subscription {
    public static final String subscriptionPlanUrl =
        "https://api.sandbox.paypal.com/v1/payments/billing-plans/";


    private String subscriptionPlanId;
    private String subscriptionAgreementId;

}
