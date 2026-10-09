package io.github.rxue.investment.lotsmatching;

import io.github.rxue.investment.vo.bankaccount.transaction.Trade;

import java.time.LocalDate;
import java.util.*;

import static io.github.rxue.investment.vo.bankaccount.transaction.Action.BUY;

public class TradeMatcher {
    private static Lot toLot(Trade trade) {
        LocalDate date = trade.date();
        int shareAmount = trade.shareAmount();
        long centValue = trade.cents();
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
    TradeMatchResult matchInFIFO(List<Trade> tradeList) {
        Map<String,LotsMatchResult> result = new HashMap<>();
        LotMatcher lotMatcher = new LotMatcher();
        lotsBySecurityId(tradeList).forEach((securityId,lots) -> {
            result.put(securityId, lotMatcher.matchInFIFO(lots));
        });
        return new TradeMatchResult(Collections.unmodifiableMap(result));
    }
}
