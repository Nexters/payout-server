package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.domain.stock.Sector;
import nexters.payout.domain.stock.service.SectorAnalyzer;
import nexters.payout.domain.stock.service.SectorAnalyzer.StockShare;
import nexters.payout.domain.stock.service.SectorAnalyzer.SectorInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record SectorRatioResponse(
        String sectorName,
        Double sectorRatio,
        List<StockResponse> stocks
) {
    public static List<SectorRatioResponse> fromMap(final Map<Sector, SectorInfo> sectorRatioMap) {
        return sectorRatioMap.entrySet()
                .stream()
                .map(entry -> new SectorRatioResponse(
                        entry.getKey().getName(),
                        entry.getValue().ratio(),
                        entry.getValue()
                                .stockShares()
                                .stream()
                                .map(StockShare::stock)
                                .map(StockResponse::from)
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }
}
