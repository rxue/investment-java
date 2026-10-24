package io.github.rxue.investmentutility.lotsmatching;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradeMatchResultTest {

    @Test
    void openShareAmountBySecurity_security_without_open_lots_is_excluded() {
        Lot.Buy mrnaBuy = new Lot.Buy(LocalDate.of(2026, 1, 13), 20, 57596);
        Lot.Sell mrnaSell = new Lot.Sell(LocalDate.of(2026, 1, 14), 20, 61200);
        Lot.Buy sagcvBuy = new Lot.Buy(LocalDate.of(2025, 8, 8), 50, 38900);
        TradeMatchResult tradeMatchResult = new TradeMatchResult(Map.of(
                "MRNA", new LotsMatchResult(List.of(new SellMatch(mrnaSell, List.of(mrnaBuy))), List.of()),
                "SAGCV", new LotsMatchResult(List.of(), List.of(sagcvBuy))));

        assertEquals(Map.of("SAGCV", 50), tradeMatchResult.openShareAmountBySecurity());
    }
}
