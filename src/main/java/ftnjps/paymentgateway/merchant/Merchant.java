package ftnjps.paymentgateway.merchant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
public class Merchant {

	@Id
	@GeneratedValue
	@JsonProperty(access = Access.READ_ONLY)
	private Long id;
	private String merchantId;
	private String bankUrl;
	private String paypalClient;
	private String paypalSecret;


	public Merchant() {}

	public Merchant(String merchantId, String bankUrl, String paypalClient, String paypalSecret) {
		this.merchantId = merchantId;
		this.bankUrl = bankUrl;
		this.paypalClient = paypalClient;
		this.paypalSecret = paypalSecret;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getBankUrl() {
		return bankUrl;
	}

	public void setBankUrl(String bankUrl) {
		this.bankUrl = bankUrl;
	}

	public String getPaypalClient() {
		return paypalClient;
	}

	public void setPaypalClient(String paypalClient) {
		this.paypalClient = paypalClient;
	}

	public String getPaypalSecret() {
		return paypalSecret;
	}

	public void setPaypalSecret(String paypalSecret) {
		this.paypalSecret = paypalSecret;
	}

}
