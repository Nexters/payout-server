package nexters.payout.apiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"nexters.payout.core",
		"nexters.payout.domain",
		"nexters.payout.apiserver"
})
public class PayoutApiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayoutApiServerApplication.class, args);
	}

}
