package io.github.rxue.investmentutility.marketquote;

import java.util.Map;

public record QuoteMetricValues(String securityId, Map<QuoteMetric,Comparable<?>> values) {
}
