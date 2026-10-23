package io.github.rxue.investmentutility.marketquote;

import java.util.Map;
import java.util.Set;

public interface SingleQuoteFetcher {
    Map<QuoteMetric,Comparable<?>> fetch(String securityId, Set<QuoteMetric> quoteMetrics);
}
