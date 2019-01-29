package ftnjps.paymentgateway.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements  SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription add(Subscription input) {
        return subscriptionRepository.save(input);
    }

    @Override
    public Subscription findOne(Long id) {
        return subscriptionRepository.findById(id).orElse(null);
    }

    @Override
    public Subscription findByToken(String token) {
        return subscriptionRepository.findByToken(token);
    }
}
