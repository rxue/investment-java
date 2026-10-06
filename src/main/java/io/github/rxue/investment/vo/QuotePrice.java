package io.github.rxue.investment.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record QuotePrice(BigDecimal value, String currency) implements Comparable<QuotePrice> {

    @Override
    public String toString() {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString() + " " + currency;
    }

    @Override
    public int compareTo(QuotePrice quotePrice) {
        throw new UnsupportedOperationException();
    }
}
