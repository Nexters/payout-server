package nexters.payout.domain.stock.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.core.exception.error.NotFoundException;
import nexters.payout.domain.stock.application.dto.UpdateStockRequest;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockCommandService {
    private final StockRepository stockRepository;

    public void save(Stock stock) {
        stockRepository.save(stock);
    }

    public void update(String ticker, UpdateStockRequest request) {
        Stock stock = stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new NotFoundException(String.format("not found ticker [%s]", ticker)));
        stock.update(request.price(), request.volume());
    }
}
