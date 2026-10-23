package io.github.rxue.investmentutility.lotsmatching;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    public Map<String,Integer> openShareAmountBySecurity() {
        return lotsMatchResultMap.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().openShareAmount()));
    }

    public SellMatches sellMatches() {
        Map<String,List<SellMatch>> result = lotsMatchResultMap.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().sellMatches()));
        return new SellMatches(result);
    }

    public OpenLots openLots() {
        Map<String,List<Lot.Buy>> result = new HashMap<>();
        lotsMatchResultMap.forEach((securityId,lotsMatchResult) -> result.put(securityId, lotsMatchResult.openLots()));
        return new OpenLots(Collections.unmodifiableMap(result));
    }

}
