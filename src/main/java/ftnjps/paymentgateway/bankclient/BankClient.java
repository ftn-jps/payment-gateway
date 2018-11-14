package ftnjps.paymentgateway.bankclient;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import ftnjps.paymentgateway.bank.Bank;

@Entity
public class BankClient {

	@Id
	@GeneratedValue
	@JsonProperty(access = Access.READ_ONLY)
	private Long id;

	// Payment card number
	@Pattern(regexp = "\\d{10,19}")
	@NotEmpty
	private String pan;

	@NotBlank
	private String name;

	// Card verification code
	@Pattern(regexp = "\\d{3,4}")
	@NotEmpty
	private String cvc;

	@Positive
	private long validUntilTimestamp;

	@ManyToOne
	private Bank bank;

	/*
	 * ID that the bank gives to the merchant when registering for online payment
	 *
	 * If client is not a merchant, this should be set to null
	 */
	@Pattern(regexp = "\\w{30}")
	private String merchantId;

	/*
	 *  Password that the bank gives to the merchant when registering for online payment
	 *
	 *  If client is not a merchant, this should be set to null
	 */
	@Size(min = 6, max = 100)
	private String merchantPassword;

	public BankClient() {}

	public BankClient(String pan,
			String name,
			String cvc,
			long validUntilTimestamp,
			Bank bank,
			String merchantId,
			String merchantPassword) {
		this.pan = pan;
		this.name = name;
		this.cvc = cvc;
		this.validUntilTimestamp = validUntilTimestamp;
		this.bank = bank;
		this.merchantId = merchantId;
		this.merchantPassword = merchantPassword;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCvc() {
		return cvc;
	}

	public void setCvc(String cvc) {
		this.cvc = cvc;
	}

	public long getValidUntilTimestamp() {
		return validUntilTimestamp;
	}

	public void setValidUntilTimestamp(long validUntilTimestamp) {
		this.validUntilTimestamp = validUntilTimestamp;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
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

}
