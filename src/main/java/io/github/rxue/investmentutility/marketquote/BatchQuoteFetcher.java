package io.github.rxue.investmentutility.marketquote;

import io.github.rxue.investmentutility.vo.MetricValuesList;

import java.util.Set;

public interface BatchQuoteFetcher {
    MetricValuesList fetchAll(Set<String> securityIds, Set<QuoteMetric> quoteMetrics);
}
