
package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.MetricValues;
import io.github.rxue.investment.vo.MetricValuesList;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Contract: report currency has to be configured so that the LATEST_MARKET_PRICE_IN_REPORT_CURRENCY can be calculated
 */
public interface QuoteFetcher extends BatchQuoteFetcher, SingleQuoteFetcher {
    @Override
    default MetricValuesList fetchAll(Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        Map<String,MetricValues> result = securityIds.stream()
                .map(secId ->new AbstractMap.SimpleImmutableEntry<>(secId, toMetricValues(secId, fetch(secId, quoteMetrics))))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new MetricValuesList(result);
    }
    static MetricValues toMetricValues(String securityId, Map<QuoteMetric,Comparable<?>> quoteMetricValues) {
        return new MetricValues.Builder(securityId)
                .addAll(quoteMetricValues)
                .build();
    }
}
