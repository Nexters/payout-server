package nexters.payout.apiserver.portfolio.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import nexters.payout.apiserver.stock.application.dto.response.StockShareResponse;
import nexters.payout.domain.stock.domain.Sector;
import nexters.payout.domain.stock.domain.service.SectorAnalysisService.SectorInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record SectorRatioResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "sector name")
        String sectorName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "sector value")
        String sectorValue,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "sector ratio")
        Double sectorRatio,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<StockShareResponse> stockShares
) {
    public static List<SectorRatioResponse> fromMap(final Map<Sector, SectorInfo> sectorRatioMap) {
        return sectorRatioMap.entrySet()
                .stream()
                .map(entry -> new SectorRatioResponse(
                        entry.getKey().getName(),
                        entry.getKey().name(),
                        entry.getValue().ratio(),
                        entry.getValue()
                                .stockShares()
                                .stream()
                                .map(StockShareResponse::from)
                                .collect(Collectors.toList()))
                )
                .collect(Collectors.toList());
    }
}
