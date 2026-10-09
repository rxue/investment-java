package io.github.rxue.investment.lotsmatching;

import java.util.List;

public record SellMatch(Lot.Sell sellLot, List<Lot.Buy> buyLots) {
}
