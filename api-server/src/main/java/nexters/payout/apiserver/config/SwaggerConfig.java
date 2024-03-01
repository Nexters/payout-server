package nexters.payout.apiserver.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .addServersItem(new Server().url("/"))
                .info(getPayoutServerInfo());
    }

    private Info getPayoutServerInfo() {
        return new Info().title("Payout Server API")
                .description("Payout Server API 명세서입니다.")
                .version("1.0.0");
    }
}
