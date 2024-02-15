package nexters.payout.domain.stock.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.domain.stock.domain.Stock;
import nexters.payout.domain.stock.domain.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockCommandService {

    private final StockRepository stockRepository;

    public void saveOrUpdate(String ticker, Stock stockData) {
        stockRepository.findByTicker(ticker)
                .ifPresentOrElse(
                        existing -> existing.update(stockData.getPrice(), stockData.getVolume()),
                        () -> stockRepository.save(stockData)
                );
    }
}
