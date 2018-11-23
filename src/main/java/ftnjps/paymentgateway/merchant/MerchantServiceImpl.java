package ftnjps.paymentgateway.merchant;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl implements MerchantService {

	@Autowired
	private MerchantRepository merchantRepository;

	@Override
	public Merchant findOne(Long id) {
		return merchantRepository.findById(id).orElse(null);
	}

	@Override
	public List<Merchant> findAll() {
		return merchantRepository.findAll();
	}

	@Override
	public Merchant findByMerchantId(String merchantId) {
		return merchantRepository.findByMerchantId(merchantId);
	}

	@Override
	public Merchant add(Merchant input) {
		return merchantRepository.save(input);
	}

}
