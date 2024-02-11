package nexters.payout.domain.stock.service;

import nexters.payout.domain.common.config.DomainService;
import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.Stock;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DomainService
public class SectorAnalyzer {

    public Map<Sector, SectorInfo> calculateSectorRatios(final List<StockShare> stockShares) {
        Map<Sector, Integer> sectorCountMap = new HashMap<>();
        for (StockShare stockShare : stockShares) {
            Stock stock = stockShare.stock();
            sectorCountMap.put(stock.getSector(), sectorCountMap.getOrDefault(stock.getSector(), 0) + 1);
        }

        Map<Sector, SectorInfo> sectorInfoMap = new HashMap<>();
        for (Sector sector : Sector.values()) {
            Integer stockCountBySector = sectorCountMap.getOrDefault(sector, 0);
            if (stockCountBySector > 0) {
                Double sectorRatio = totalValueBySector(stockShares, sector) / totalValue(stockShares);
                List<StockShare> stocksBySector = getSectorStockMap(stockShares).get(sector);
                sectorInfoMap.put(sector, new SectorInfo(sectorRatio, stocksBySector));
            }
        }

        return sectorInfoMap;
    }

    private Map<Sector, List<StockShare>> getSectorStockMap(final List<StockShare> stockShares) {
        return stockShares
                .stream()
                .collect(Collectors.groupingBy(stockShare -> stockShare.stock().getSector()));
    }

    private double totalValue(final List<StockShare> stockShares) {
        return stockShares.stream()
                .mapToDouble(stockShare -> stockShare.share() * stockShare.stock().getPrice())
                .sum();
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
            Integer share
    ) {

    }
}
