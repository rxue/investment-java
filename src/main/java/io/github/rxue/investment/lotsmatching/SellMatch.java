package io.github.rxue.investment.lotsmatching;

import java.util.List;

public record SellMatch(Lot.Sell sellLot, List<Lot.Buy> buyLots) {
    public long gainInCent() {
        return sellLot.valueInCent() - buyLots.stream().mapToLong(Lot.Buy::valueInCent).sum();
    }
}
