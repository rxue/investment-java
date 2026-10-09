package io.github.rxue.investment.lotsmatching;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

class LotMatcher {

    LotsMatchResult matchInFIFO(List<Lot> lots) {
        Queue<Lot.Buy> remainingLots = new ArrayDeque<>();
        for (Lot lot : lots) {
            if (lot instanceof Lot.Buy buyLot)
                remainingLots.add(buyLot);
        }
        return new LotsMatchResult(List.of(), remainingLots.stream().toList());
    }
}
