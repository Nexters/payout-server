package nexters.dividend.batch.infra.fmp;


record StockData(
        String symbol,
        String companyName,
        String exchangeShortName,
        Double price,
        Integer volume,
        String sector,
        String industry
) {
}

record VolumeData(
        String symbol,
        Integer volume,
        Integer avgVolume
) {
}

record SectorData(
        String ticker,
        String sector,
        String industry
) {

}
