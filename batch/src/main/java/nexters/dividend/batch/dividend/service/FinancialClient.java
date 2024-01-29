package nexters.dividend.batch.dividend.service;

import nexters.dividend.batch.dividend.dto.FmpDividendResponse;

import java.util.List;

/**
 * FMP API 호출 관련 Client 인터페이스입니다.
 *
 * @author Min Ho CHO
 */
public interface FinancialClient {

    List<FmpDividendResponse> getDividendData();
}
