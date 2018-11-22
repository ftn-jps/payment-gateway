package ftnjps.paymentgateway.merchant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
public class Merchant {

	@Id
	@GeneratedValue
	@JsonProperty(access = Access.READ_ONLY)
	private Long id;

	/*
	 * ID that the bank gives to the merchant when registering for online payment
	 */
	@Pattern(regexp = "\\w{1,30}")
	private String merchantId;


	private String bankUrl;

	public Merchant() {}

	public Merchant(String merchantId, String bankUrl) {
		this.merchantId = merchantId;
		this.bankUrl = bankUrl;
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

}
