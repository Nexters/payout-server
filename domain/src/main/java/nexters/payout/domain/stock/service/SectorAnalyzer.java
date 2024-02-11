package nexters.payout.domain.stock.service;

import nexters.payout.domain.common.config.DomainService;
import nexters.payout.domain.dividend.Dividend;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@DomainService
public class SectorAnalyzer {

    public Map<Sector, SectorInfo> calculateSectorRatios(final List<StockShare> stockShares) {
        Map<Sector, Integer> sectorCountMap = getSectorCountMap(stockShares);
        Map<Sector, List<StockShare>> sectorStockMap = getSectorStockMap(stockShares);
        double totalValue = totalValue(stockShares);

        Map<Sector, SectorInfo> sectorInfoMap = new HashMap<>();

        for (Sector sector : Sector.values()) {
            if (stockCountBySector(sectorCountMap, sector) > 0) {
                Double sectorRatio = totalValueBySector(stockShares, sector) / totalValue;
                sectorInfoMap.put(sector, new SectorInfo(sectorRatio, getStocks(sectorStockMap, sector)));
            }
        }

        return sectorInfoMap;
    }

    private Map<Sector, Integer> getSectorCountMap(List<StockShare> stockShares) {
        return stockShares
                .stream()
                .map(stockShare -> stockShare.stock().getSector())
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
    }

    private Map<Sector, List<StockShare>> getSectorStockMap(final List<StockShare> stockShares) {
        return stockShares
                .stream()
                .collect(Collectors.groupingBy(stockShare -> stockShare.stock().getSector()));
    }

    private static double totalValue(final List<StockShare> stockShares) {
        return stockShares.stream()
                .mapToDouble(stockShare -> stockShare.share() * stockShare.stock().getPrice())
                .sum();
    }

    private List<StockShare> getStocks(Map<Sector, List<StockShare>> sectorStockMap, Sector sector) {
        return sectorStockMap.getOrDefault(sector, Collections.emptyList());
    }

    private Integer stockCountBySector(Map<Sector, Integer> sectorCountMap, Sector sector) {
        return sectorCountMap.getOrDefault(sector, 0);
    }

    private double totalValueBySector(final List<StockShare> stockShares, final Sector sector) {
        return stockShares.stream()
                .filter(share -> share.stock().getSector().equals(sector))
                .mapToDouble(stockShare -> stockShare.share() * stockShare.stock().getPrice())
                .sum();
    }

    public record SectorInfo(
            Double ratio,
            List<StockShare> stockShares
    ) {

    }

    public record StockShare(
            Stock stock,
            Dividend dividend,
            Integer share
    ) {

    }
}
