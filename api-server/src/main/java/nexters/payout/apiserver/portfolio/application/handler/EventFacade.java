package nexters.payout.apiserver.portfolio.application.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventFacade {

    private final PortfolioEventHandler portfolioEventHandler;

    @EventListener
    void publishReadPortfolioEvent(final ReadPortfolioEvent event) {
        try {
            portfolioEventHandler.handleReadPortfolioEvent(event);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("[ReadPortfolioEvent] optimistic lock exception!", e);
            publishReadPortfolioEvent(event);
        }
    }
}
