
package io.github.rxue.investment.marketquote;

import io.github.rxue.investment.vo.MetricValues;
import io.github.rxue.investment.vo.MetricValuesList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contract: report currency has to be configured so that the LATEST_MARKET_PRICE_IN_REPORT_CURRENCY can be calculated
 */
public interface SearchService {
    Map<QuoteMetric,Comparable<?>> findMetricValues(String securityId, Set<QuoteMetric> quoteMetrics);
    default MetricValuesList findMetricValues(Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        List<MetricValues> result = securityIds.stream()
                .map(secId -> toMetricValues(secId, findMetricValues(secId, quoteMetrics)))
                .toList();
        return new MetricValuesList(result);
    }
    private MetricValues toMetricValues(String securityId, Map<QuoteMetric,Comparable<?>> quoteMetricValues) {
        return new MetricValues(securityId, Collections.unmodifiableMap(quoteMetricValues));
    }
}
