package ftnjps.paymentgateway.transaction;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
public class Transaction {

	@Id
	@GeneratedValue
	@JsonProperty(access = Access.READ_ONLY)
	private Long id;

	@Positive
	private double amount;

	@Pattern(regexp = "\\w{1,30}")
	@NotBlank
	private String merchantId;

	@Size(min = 6, max = 100)
	@NotEmpty
	private String merchantPassword;

	@Positive
	private int merchantOrderId;

	@Positive
	@JsonProperty(access = Access.READ_ONLY)
	private long merchantOrderTimestamp;

	private String successUrl;
	private String failUrl;
	private String errorUrl;

	public Transaction() {}

	public Transaction(double amount,
			String merchantId,
			String merchantPassword,
			int merchantOrderId,
			String successUrl,
			String failUrl,
			String errorUrl) {
		this.amount = amount;
		this.merchantId = merchantId;
		this.merchantPassword = merchantPassword;
		this.merchantOrderId = merchantOrderId;
		this.merchantOrderTimestamp = new Date().getTime();
		this.successUrl = successUrl;
		this.failUrl = failUrl;
		this.errorUrl = errorUrl;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public long getMerchantOrderTimestamp() {
		return merchantOrderTimestamp;
	}
	public void setMerchantOrderTimestamp(long merchantOrderTimestamp) {
		this.merchantOrderTimestamp = merchantOrderTimestamp;
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
	public String getErrorUrl() {
		return errorUrl;
	}
	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

}
