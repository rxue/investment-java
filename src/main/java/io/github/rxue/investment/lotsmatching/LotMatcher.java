package io.github.rxue.investment.lotsmatching;

import io.github.rxue.investment.vo.Pair;

import java.util.*;

class LotMatcher {

    private static SellMatch matchSell(Lot.Sell sellLot, Queue<Lot.Buy> remainingLots) {
        int matchedShares = 0;
        List<Lot.Buy> matchedBuyLots = new ArrayList<>();
        while (!remainingLots.isEmpty()) {
            Lot.Buy poppedBuyLot = remainingLots.remove();
            final int poppedBuyLotShareAmount = poppedBuyLot.shareAmount();
            final int remainingShareAmount = sellLot.shareAmount() - matchedShares;
            if (remainingShareAmount > poppedBuyLotShareAmount) {
                matchedBuyLots.add(poppedBuyLot);
                matchedShares += poppedBuyLotShareAmount;
            } else if (remainingShareAmount == poppedBuyLotShareAmount) {
                matchedBuyLots.add(poppedBuyLot);
                return new SellMatch(sellLot, Collections.unmodifiableList(matchedBuyLots));
            } else {
                Pair<Lot.Buy, Lot.Buy> resultLots = poppedBuyLot.remove(remainingShareAmount);
                matchedBuyLots.add(resultLots.right());//?
                remainingLots.add(resultLots.left());
                return new SellMatch(sellLot, Collections.unmodifiableList(matchedBuyLots));
            }
        }
        throw new IllegalStateException();
    }

    LotsMatchResult matchInFIFO(List<Lot> lots) {
        Queue<Lot.Buy> remainingLots = new ArrayDeque<>();
        List<SellMatch> sellMatches = new ArrayList<>();
        for (Lot lot : lots) {
            if (lot instanceof Lot.Buy buyLot)
                remainingLots.add(buyLot);
            else if (lot instanceof Lot.Sell sellLot) {
                sellMatches.add(matchSell(sellLot, remainingLots));
            }
        }
        return new LotsMatchResult(Collections.unmodifiableList(sellMatches), remainingLots.stream().toList());
    }
}
