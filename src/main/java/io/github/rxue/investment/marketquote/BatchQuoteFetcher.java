package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.MetricValuesList;

import java.util.Set;

public interface BatchSearchService {
    MetricValuesList findMetricValues(Set<String> securityIds, Set<QuoteMetric> quoteMetrics);
}
