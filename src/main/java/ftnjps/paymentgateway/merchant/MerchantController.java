package ftnjps.paymentgateway.merchant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @PostMapping("/add")
    public ResponseEntity<?> addMerchant(@RequestBody final Merchant merchant){
        System.out.println("Checking if merchant with id " + merchant.getMerchantId() + "exists...");
        Merchant existing = merchantService.findByMerchantId(merchant.getMerchantId());
        System.out.println("Merchant with id " + merchant.getMerchantId() + "doesn't exist. New merchant can be added");

        if(existing != null) {
            return new ResponseEntity<>(
                "Merchant with id " + merchant.getMerchantId() + " already exists",
                HttpStatus.BAD_REQUEST
            );
        }

        merchantService.add(merchant);

        return new ResponseEntity<>(merchant,HttpStatus.CREATED);
    }
}
