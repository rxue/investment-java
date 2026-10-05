package io.github.rxue.investment.marketquote.yahoofinance;

enum YahooMetric {
    REGULAR_MARKET_PRICE("price", "regularMarketPrice", YahooNumber.class);

    private final String module;
    private final String name;
    private final Class<?> typeClass;

    YahooMetric(String module, String name, Class<?> typeClass) {
        this.module = module;
        this.name = name;
        this.typeClass = typeClass;
    }

    String module() {
        return module;
    }

    String metricName() {
        return name;
    }

    Class<?> typeClass() {
        return typeClass;
    }
}
