package ftnjps.paymentgateway.subscription;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
public class Subscription {
    public static final String subscriptionPlanUrl =
        "https://api.sandbox.paypal.com/v1/payments/billing-plans/";

    @Id
    @GeneratedValue
    @JsonProperty(access = Access.READ_ONLY)
    private Long id;

    @Positive
    private double amount;

    @NotBlank
    @JsonProperty(access = Access.READ_ONLY)
    private String token = UUID.randomUUID().toString();
    private String merchantId;
    private String successUrl;
    private String failUrl;
    private String merchantPassword;
    private int merchantOrderId;
    private String errorUrl;

    public Subscription(){}

    public Subscription(
        double amount,
        final String merchantId,
        final String successUrl,
        final String failUrl
    ) {
        this.amount = amount;
        this.merchantId = merchantId;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
    }

    public String getMerchantPassword() {
        return merchantPassword;
    }

    public void setMerchantPassword(String merchantPassword) {
        this.merchantPassword = merchantPassword;
    }

    public int getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(int merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getErrorUrl() {
        return errorUrl;
    }

    public void setErrorUrl(String errorUrl) {
        this.errorUrl = errorUrl;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getFailUrl() {
        return failUrl;
    }

    public void setFailUrl(String failUrl) {
        this.failUrl = failUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
