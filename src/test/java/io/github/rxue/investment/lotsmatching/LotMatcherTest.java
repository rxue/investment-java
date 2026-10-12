package io.github.rxue.investment.lotsmatching;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LotMatcherTest {

    @Test
    void matchInFIFO_MRNA_two_buys_then_one_sell() {
        final LotMatcher lotMatcher = new LotMatcher();
        Lot.Buy firstBuy = new Lot.Buy(LocalDate.of(2026, 1, 13), 20, 57596);
        Lot.Buy secondBuy = new Lot.Buy(LocalDate.of(2026, 1, 13), 20, 57468);
        Lot.Sell sell = new Lot.Sell(LocalDate.of(2026, 1, 14), 20, 61200);

        LotsMatchResult result = lotMatcher.matchInFIFO(List.of(firstBuy, secondBuy, sell));

        assertEquals(List.of(new SellMatch(sell, List.of(firstBuy))), result.sellMatches());
        assertEquals(List.of(secondBuy), result.openLots());
        assertEquals(57468, result.openLotsCostInCent());
        assertEquals(20, result.openShareAmount());
    }

    @Test
    void matchInFIFO_SAGCV_three_buys_then_one_sell_splitting_third_buy() {
        final LotMatcher lotMatcher = new LotMatcher();
        Lot.Buy firstBuy = new Lot.Buy(LocalDate.of(2025, 8, 8), 50, 38900);
        Lot.Buy secondBuy = new Lot.Buy(LocalDate.of(2025, 8, 25), 70, 51300);
        Lot.Buy thirdBuy = new Lot.Buy(LocalDate.of(2025, 11, 3), 80, 55300);
        Lot.Sell sell = new Lot.Sell(LocalDate.of(2026, 5, 7), 140, 187400);

        LotsMatchResult result = lotMatcher.matchInFIFO(List.of(firstBuy, secondBuy, thirdBuy, sell));

        Lot.Buy soldPartOfThirdBuy = new Lot.Buy(LocalDate.of(2025, 11, 3), 20, 13825);
        Lot.Buy openPartOfThirdBuy = new Lot.Buy(LocalDate.of(2025, 11, 3), 60, 41475);
        assertEquals(List.of(new SellMatch(sell, List.of(firstBuy, secondBuy, soldPartOfThirdBuy))), result.sellMatches());
        assertEquals(List.of(openPartOfThirdBuy), result.openLots());
        assertEquals(41475, result.openLotsCostInCent());
        assertEquals(60, result.openShareAmount());
    }
}
