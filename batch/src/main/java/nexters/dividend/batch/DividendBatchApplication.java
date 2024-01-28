package nexters.dividend.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = {
        "nexters.dividend.core",
        "nexters.dividend.domain",
        "nexters.dividend.batch"
})
@EnableScheduling
public class DividendBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(DividendBatchApplication.class, args);
    }

}
