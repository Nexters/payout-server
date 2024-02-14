package nexters.payout.domain.dividend.application;

import lombok.RequiredArgsConstructor;
import nexters.payout.core.exception.error.NotFoundException;
import nexters.payout.domain.dividend.application.dto.UpdateDividendRequest;
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

    public void save(Dividend dividend) {
        dividendRepository.save(dividend);
    }

    public void update(UUID dividendId, UpdateDividendRequest request) {
        Dividend dividend = dividendRepository.findById(dividendId)
                .orElseThrow(() -> new NotFoundException(String.format("not found dividend [%s]", dividendId)));
        dividend.update(request.dividend(), request.paymentDate(), request.declarationDate());
    }
}
