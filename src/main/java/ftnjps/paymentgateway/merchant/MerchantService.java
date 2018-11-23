package ftnjps.paymentgateway.merchant;

import java.util.List;

public interface MerchantService {

	Merchant findOne(Long id);
 	List<Merchant> findAll();
 	Merchant findByMerchantId(String merchantId);
 	Merchant add(Merchant input);

}
