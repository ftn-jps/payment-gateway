package ftnjps.paymentgateway.subscription;

import org.springframework.stereotype.Service;

@Service
public interface SubscriptionService {
    Subscription add(final Subscription input);
    Subscription findOne(final Long id);
    Subscription findByToken(final String token);
}
