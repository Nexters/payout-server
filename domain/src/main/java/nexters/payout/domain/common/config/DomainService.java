package nexters.payout.domain.common.config;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DomainService {
}