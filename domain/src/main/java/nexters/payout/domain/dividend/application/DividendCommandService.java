package nexters.payout.domain.dividend.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.core.time.InstantProvider;
import nexters.payout.domain.dividend.domain.Dividend;
import nexters.payout.domain.dividend.domain.repository.DividendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DividendCommandService {

    private final DividendRepository dividendRepository;

    public void saveOrUpdate(UUID stockId, Dividend dividendData) {
        dividendRepository.findByStockIdAndExDividendDate(stockId, dividendData.getExDividendDate())
                .ifPresentOrElse(
                        existing -> existing.update(
                                dividendData.getDividend(),
                                dividendData.getPaymentDate(),
                                dividendData.getDeclarationDate()
                        ),
                        () -> dividendRepository.save(dividendData)
                );
    }

    public void deleteInvalidDividend() {
        dividendRepository.deleteByYearAndCreatedAt(InstantProvider.getThisYear(), InstantProvider.getYesterday());
    }
}
