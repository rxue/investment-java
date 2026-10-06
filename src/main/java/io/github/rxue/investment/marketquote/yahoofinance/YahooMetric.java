package io.github.rxue.investment.marketquote.yahoofinance;

import io.github.rxue.investment.vo.Number;

enum YahooMetric {
    REGULAR_MARKET_PRICE("price", "regularMarketPrice", Number.class),
    CURRENCY("price", "currency", String.class),
    TRAILING_PE("summaryDetail", "trailingPE", Number.class),
    DIVIDEND_YIELD("summaryDetail", "dividendYield", Number.class);

    private final String v10Module;
    private final String name;
    private final Class<?> typeClass;

    YahooMetric(String v10Module, String name, Class<?> typeClass) {
        this.v10Module = v10Module;
        this.name = name;
        this.typeClass = typeClass;
    }

    String v10Module() {
        return v10Module;
    }

    String metricName() {
        return name;
    }

    Class<?> typeClass() {
        return typeClass;
    }
}
