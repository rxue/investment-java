package io.github.rxue.investment.lotsmatching;

import java.time.LocalDate;

public interface Lot {
    LocalDate date();

    int shareAmount();

    long valueInCent();

    record Buy(LocalDate date, int shareAmount, long valueInCent) implements Lot {
    }
    record Sell(LocalDate date, int shareAmount, long valueInCent) implements Lot {
    }
}
