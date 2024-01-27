package nexters.dividend.batch.application;

import lombok.RequiredArgsConstructor;
import nexters.dividend.domain.stock.StockRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockBatchService {

    private final FinancialClient financialClient;
    private final StockRepository stockRepository;

    void updateStock() {
        // TODO()
    }
}
