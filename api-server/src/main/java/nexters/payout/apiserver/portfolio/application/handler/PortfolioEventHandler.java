package nexters.payout.apiserver.portfolio.application.handler;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PortfolioEventHandler {

    @EventListener
    void handleReadPortfolioEvent(final ReadPortfolioEvent event) {

    }

}
