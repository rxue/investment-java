package io.github.rxue.investment.lotsmatching;

import java.util.Collections;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class TradeMatchResult {
    private final Map<String,LotsMatchResult> lotsMatchResultMap;

    public TradeMatchResult(Map<String,LotsMatchResult> lotsMatchResultMap) {
        this.lotsMatchResultMap = Collections.unmodifiableMap(lotsMatchResultMap);
    }
    public long totalOpenLotsCostInCent() {
        return lotsMatchResultMap.values().stream()
                .mapToLong(LotsMatchResult::openLotsCostInCent)
                .sum();
    }
    public Map<String,Integer> sharesAmountBySecurity() {
        return lotsMatchResultMap.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().openShareAmount()));
    }


}
