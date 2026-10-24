package io.github.rxue.investmentutility.lotsmatching;

import io.github.rxue.investmentutility.vo.bankaccount.transaction.Trade;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static io.github.rxue.investmentutility.vo.bankaccount.transaction.Action.SELL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradeMatcherTest {

    @Test
    void matchInFIFO_open_lots_of_security_without_trades_are_kept() {
        final TradeMatcher tradeMatcher = new TradeMatcher();
        Map<String,List<Lot.Buy>> openLotsMap = Map.of(
                "MRNA", List.of(new Lot.Buy(LocalDate.of(2026, 1, 13), 20, 57596)),
                "SAGCV", List.of(new Lot.Buy(LocalDate.of(2025, 8, 8), 50, 38900)));
        Trade mrnaSell = new Trade(LocalDate.of(2026, 1, 14), SELL, "MRNA", 20, 61200);

        TradeMatchResult result = tradeMatcher.matchInFIFO(List.of(mrnaSell), openLotsMap);

        assertEquals(Map.of("SAGCV", 50), result.openShareAmountBySecurity());
        assertEquals(38900, result.totalOpenLotsCostInCent());
    }
}
