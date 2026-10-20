package io.github.rxue.investment.lotsmatching;

import java.util.List;
import java.util.Map;

public record OpenLots(Map<String,List<Lot.Buy>> openBuyLots) {
    long costInCent() {
        return openBuyLots.values().stream()
                .flatMap(List::stream)
                .mapToLong(Lot.Buy::valueInCent)
                .sum();
    }
}
