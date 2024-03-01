package nexters.payout.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = {
        "nexters.payout.core",
        "nexters.payout.domain",
        "nexters.payout.batch"
})
@EnableScheduling
public class PayoutBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayoutBatchApplication.class, args);
    }

}
