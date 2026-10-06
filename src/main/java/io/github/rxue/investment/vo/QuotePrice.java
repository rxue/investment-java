package io.github.rxue.investment.vo;

import java.math.BigDecimal;

public record QuotePrice(BigDecimal value, String currency) implements Comparable<QuotePrice> {

    @Override
    public int compareTo(QuotePrice quotePrice) {
        throw new UnsupportedOperationException();
    }
}
