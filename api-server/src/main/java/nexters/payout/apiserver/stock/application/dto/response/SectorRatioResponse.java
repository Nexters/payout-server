package nexters.payout.apiserver.stock.application.dto.response;

import nexters.payout.domain.stock.Sector;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record SectorRatioResponse(
        String stockName,
        Double sectorRatio
) {
    public static List<SectorRatioResponse> createResponseList(Map<Sector, Double> sectorRatioMap) {
        return sectorRatioMap.entrySet()
                .stream()
                .map(entry -> new SectorRatioResponse(entry.getKey().getName(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
