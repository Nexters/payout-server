package nexters.dividendbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = { "nexters.dividendbatch", "nexters.domain" })
public class DividendBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(DividendBatchApplication.class, args);
    }

}
