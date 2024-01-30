package nexters.dividend.batch.infra.fmp;


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