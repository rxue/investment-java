package io.github.rxue.investment.vo;

import java.math.BigDecimal;

public record NumberWithFormat(BigDecimal value, String formatted) implements Comparable<NumberWithFormat> {
    @Override
    public String toString() {
        return formatted;
    }

    @Override
    public int compareTo(NumberWithFormat another) {
        return value.compareTo(another.value);
    }
}
