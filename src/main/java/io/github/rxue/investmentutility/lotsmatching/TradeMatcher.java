package io.github.rxue.investmentutility.lotsmatching;

import io.github.rxue.investmentutility.vo.bankaccount.transaction.Trade;

import java.time.LocalDate;
import java.util.*;

import static io.github.rxue.investmentutility.vo.bankaccount.transaction.Action.BUY;

public class TradeMatcher {
    private static Lot toLot(Trade trade) {
        LocalDate date = trade.date();
        int shareAmount = trade.shareAmount();
        long centValue = Math.abs(trade.cents());
        return trade.action() == BUY ? new Lot.Buy(date, shareAmount, centValue) : new Lot.Sell(date, shareAmount, centValue);
    }
    private static Map<String,List<Lot>> lotsBySecurityId(List<Trade> tradeList) {
        Map<String,List<Lot>> result = new HashMap<>();
        for (Trade trade : tradeList) {
            Lot lot = toLot(trade);
            result.computeIfAbsent(trade.securityId(), k -> new ArrayList<>())
                    .add(lot);
        }
        return Collections.unmodifiableMap(result);
    }
    public TradeMatchResult matchInFIFO(List<Trade> tradeList) {
        return matchInFIFO(tradeList, Map.of());
    }
    public TradeMatchResult matchInFIFO(List<Trade> tradeList, Map<String,List<Lot.Buy>> openLotsMap) {
        Map<String,LotsMatchResult> result = new HashMap<>();
        LotMatcher lotMatcher = new LotMatcher();
        lotsBySecurityId(tradeList).forEach((securityId,lots) -> {
            List<Lot.Buy> openLots = openLotsMap.get(securityId);
                result.put(securityId, lotMatcher.matchInFIFO(lots, openLots == null ? List.of() : openLots));
        });
        openLotsMap.forEach((securityId, openLots) -> {
            if (!result.containsKey(securityId))
                result.put(securityId, new LotsMatchResult(List.of(), openLots));
        });
        return new TradeMatchResult(Collections.unmodifiableMap(result));
    }
}
