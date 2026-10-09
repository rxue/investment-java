package io.github.rxue.investment.lotsmatching;

import java.time.LocalDate;

public sealed interface Lot {
    LocalDate date();

    int shareAmount();

    long valueInCent();

    record Buy(LocalDate date, int shareAmount, long valueInCent) implements Lot {
        public Buy {
            validate(shareAmount, valueInCent);
        }
    }
    record Sell(LocalDate date, int shareAmount, long valueInCent) implements Lot {
        public Sell {
            validate(shareAmount, valueInCent);
        }
    }

    private static void validate(int shareAmount, long valueInCent) {
        if (shareAmount <= 0)
            throw new IllegalArgumentException("shareAmount must be positive: " + shareAmount);
        if (valueInCent < 0)
            throw new IllegalArgumentException("valueInCent must not be negative: " + valueInCent);
    }
}
