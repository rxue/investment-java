package io.github.rxue.investment.publicapi;

import io.github.rxue.investment.lotsmatching.Lot;
import io.github.rxue.investment.lotsmatching.SellMatch;
import io.github.rxue.investment.lotsmatching.SellMatches;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SellMatchesPublicApiTest {

    @Test
    void totalGainInCent_sums_gains_and_losses_across_securities() {
        SellMatch mrnaGain = new SellMatch(
                new Lot.Sell(LocalDate.of(2026, 1, 14), 20, 61200),
                List.of(new Lot.Buy(LocalDate.of(2026, 1, 13), 20, 57596)));
        SellMatch sagcvLoss = new SellMatch(
                new Lot.Sell(LocalDate.of(2026, 5, 7), 50, 35000),
                List.of(new Lot.Buy(LocalDate.of(2025, 8, 8), 50, 38900)));
        SellMatches sellMatches = new SellMatches(Map.of(
                "MRNA", List.of(mrnaGain),
                "SAGCV", List.of(sagcvLoss)));

        assertEquals(3604 - 3900, sellMatches.totalGainInCent());
    }
}
