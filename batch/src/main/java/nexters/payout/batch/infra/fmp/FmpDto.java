package nexters.payout.batch.infra.fmp;


import nexters.payout.batch.application.FinancialClient.DividendData;
import nexters.payout.core.time.DateFormat;

record FmpStockData(
        String symbol,
        String companyName,
        String exchangeShortName,
        Double price,
        Integer volume,
        String sector,
        String industry
) {
}

record FmpVolumeData(
        String symbol,
        Integer volume,
        Integer avgVolume
) {
}

record FmpDividendData(
        String date,
        String label,
        Double adjDividend,
        String symbol,
        Double dividend,
        String recordDate,
        String paymentDate,
        String declarationDate
) {
    DividendData toDividendData() {
        return new DividendData(
                DateFormat.parseInstant(date),
                label, adjDividend, symbol, dividend,
                DateFormat.parseInstant(recordDate),
                DateFormat.parseInstant(paymentDate),
                DateFormat.parseInstant(declarationDate));
    }
}