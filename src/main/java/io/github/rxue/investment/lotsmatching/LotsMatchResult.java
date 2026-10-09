package io.github.rxue.investment.lotsmatching;

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
}
