package nexters.dividend.batch.dividend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FMP API에서 반환된 배당금 정보를 표현하는 dto 클래스입니다.
 *
 * @author Min Ho CHO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FmpDividendResponse {

    private String date;
    private String label;
    private Double adjDividend;
    private String symbol;
    private Double dividend;
    private String recordDate;
    private String paymentDate;
    private String declarationDate;
}
