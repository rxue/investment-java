package io.github.rxue.investmentutility.lotsmatching;

import java.time.LocalDate;
import java.util.List;

record LotsMatchResult(List<SellMatch> sellMatches, List<Lot.Buy> openLots) {
    public long openLotsCostInCent() {
        return openLots.stream()
                .mapToLong(Lot::valueInCent)
                .sum();
    }
    public int openShareAmount() {
        return openLots.stream()
                .mapToInt(Lot::shareAmount)
                .sum();
    }
    List<SellMatch> sellMatchesSince(LocalDate inclusiveDate) {
        return sellMatches.stream()
                .filter(sellMatch -> !sellMatch.sellLot().date().isBefore(inclusiveDate))
                .toList();
    }
}
