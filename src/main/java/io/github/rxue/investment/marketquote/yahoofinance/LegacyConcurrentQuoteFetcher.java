package io.github.rxue.investment.marketquote.yahoofinance;

import io.github.rxue.investment.marketquote.BatchQuoteFetcher;
import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.QuoteFetcher;
import io.github.rxue.investment.vo.MetricValues;
import io.github.rxue.investment.vo.MetricValuesList;

import java.util.*;
import java.util.concurrent.*;

public class ConcurrentQuoteFetcher implements BatchQuoteFetcher {
    private final ThreadLocal<QuoteFetcher> worker;
    private final ExecutorService executorService;
    public ConcurrentQuoteFetcher(String reportCurrency, ExecutorService executorService) {
        this.executorService = executorService;
        this.worker = ThreadLocal.withInitial(() -> new YahooFinanceQuoteFetcher(reportCurrency));
    }

    @Override
    public MetricValuesList fetchAll(Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        List<Callable<Map<QuoteMetric,Comparable<?>>>> tasks = new ArrayList<>();
        List<String> securityIdList = new ArrayList<>();
        for (String securityId : securityIds) {
            tasks.add(() -> worker.get().fetch(securityId, quoteMetrics));
            securityIdList.add(securityId);
        }
        try {
            // blocks until every task has completed (normally or exceptionally)
            List<Future<Map<QuoteMetric,Comparable<?>>>> futures = executorService.invokeAll(tasks);
            Map<String,MetricValues> metricValuesBySecurityId = new HashMap<>();
            for (int i = 0; i < securityIdList.size(); i++) {
                String secId = securityIdList.get(i);
                metricValuesBySecurityId.put(secId, QuoteFetcher.toMetricValues(secId, futures.get(i).get()));
            }
            return new MetricValuesList(metricValuesBySecurityId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
