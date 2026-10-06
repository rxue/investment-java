package io.github.rxue.investment.vo;

import java.math.BigDecimal;

public record Number(BigDecimal value, String formatted) implements Comparable<Number> {
    @Override
    public String toString() {
        return formatted;
    }

    @Override
    public int compareTo(Number another) {
        return value.compareTo(another.value);
    }
}
