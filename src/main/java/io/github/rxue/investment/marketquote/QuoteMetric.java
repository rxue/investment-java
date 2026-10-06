package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.Metric;

public enum QuoteMetric implements Metric {
    LATEST_MARKET_PRICE("Latest market price"),
    TRAILING_PE("Trailing P/E"),
    DIVIDEND_YIELD("Dividend yield");
    private final String label;

    QuoteMetric(String label) {
        this.label = label;
    }

    @Override
    public String label() {
        return label;
    }
}
