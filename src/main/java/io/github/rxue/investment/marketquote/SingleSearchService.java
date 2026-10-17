package io.github.rxue.investment.marketquote;

import java.util.Map;
import java.util.Set;

public interface SingleSearchService {
    Map<QuoteMetric,Comparable<?>> findMetricValues(String securityId, Set<QuoteMetric> quoteMetrics);
}
