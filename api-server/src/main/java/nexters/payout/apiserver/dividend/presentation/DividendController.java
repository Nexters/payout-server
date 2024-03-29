package nexters.payout.apiserver.dividend.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexters.payout.apiserver.dividend.application.DividendQueryService;
import nexters.payout.apiserver.dividend.application.dto.request.DividendRequest;
import nexters.payout.apiserver.dividend.application.dto.response.MonthlyDividendResponse;
import nexters.payout.apiserver.dividend.application.dto.response.YearlyDividendResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/dividends")
public class DividendController implements DividendControllerDocs {

    private final DividendQueryService dividendQueryService;

    @PostMapping("/monthly")
    public ResponseEntity<List<MonthlyDividendResponse>> getMonthlyDividends(
            @RequestBody @Valid final DividendRequest request
    ) {
        return ResponseEntity.ok(dividendQueryService.getMonthlyDividends(request));
    }

    @PostMapping("/yearly")
    public ResponseEntity<YearlyDividendResponse> getYearlyDividends(
            @RequestBody @Valid final DividendRequest request
    ) {
        return ResponseEntity.ok(dividendQueryService.getYearlyDividends(request));
    }
}
