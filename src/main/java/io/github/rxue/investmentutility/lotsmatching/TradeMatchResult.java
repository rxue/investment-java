package io.github.rxue.investmentutility.lotsmatching;

import java.util.*;

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
        Map<String,Integer> result = new HashMap<>();
        lotsMatchResultMap.forEach((securityId, lotsMatchResult) -> {
            List<Lot.Buy> openLots = lotsMatchResult.openLots();
            if (!openLots.isEmpty())
                result.put(securityId, openLots.stream().mapToInt(Lot.Buy::shareAmount).sum());
        });
        return Collections.unmodifiableMap(result);
    }

    public SellMatches sellMatches() {
        Map<String,List<SellMatch>> result = lotsMatchResultMap.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().sellMatches()));
        return new SellMatches(result);
    }

    public List<OpenLots> openLotsList() {
        List<OpenLots> result = new ArrayList<>();
        lotsMatchResultMap.forEach((securityId,lotsMatchResult) -> result.add(new OpenLots(securityId, lotsMatchResult.openLots())));
        return Collections.unmodifiableList(result);
    }
    public Map<String,List<Lot.Buy>> openLotsMap() {
        Map<String,List<Lot.Buy>> result = new HashMap<>();
        lotsMatchResultMap.forEach((securityId,lotsMatchResult) -> result.put(securityId, lotsMatchResult.openLots()));
        return Collections.unmodifiableMap(result);
    }

}
