package io.github.rxue.investmentutility.lotsmatching;

import java.util.List;
import java.util.Map;

public record OpenLots(Map<String,List<Lot.Buy>> lots) {
    long totalCostInCent() {
        return lots.values().stream()
                .flatMap(List::stream)
                .mapToLong(Lot.Buy::valueInCent)
                .sum();
    }
    long costInCent(String securityId) {
        return lots.get(securityId).stream()
                .mapToLong(Lot.Buy::valueInCent)
                .sum();
    }
    int shareAmount(String securityId) {
        return lots.get(securityId).stream()
                .mapToInt(Lot.Buy::shareAmount)
                .sum();
    }
}
