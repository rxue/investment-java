package io.github.rxue.investment.marketquote.yahoofinance;

enum YahooMetric {
    REGULAR_MARKET_PRICE("price", "regularMarketPrice", YahooNumber.class),
    CURRENCY("price", "currency", String.class),
    TRAILING_PE("summaryDetail", "trailingPE", YahooNumber.class),
    DIVIDEND_YIELD("summaryDetail", "dividendYield", YahooNumber.class);

    private final String v10Module;
    private final String name;
    private final Class<? extends Comparable<?>> typeClass;

    YahooMetric(String v10Module, String name, Class<? extends Comparable<?>> typeClass) {
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

    Class<? extends Comparable<?>> typeClass() {
        return typeClass;
    }
}
