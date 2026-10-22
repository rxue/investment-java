package io.github.rxue.investment.marketquote;

import java.util.Map;

public record QuoteMetricValues(String securityId, Map<QuoteMetric,Comparable<?>> values) {
}
