package nexters.dividend.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = { "nexters.dividend.batch", "nexters.dividend.domain" })
public class DividendBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(DividendBatchApplication.class, args);
    }

}
