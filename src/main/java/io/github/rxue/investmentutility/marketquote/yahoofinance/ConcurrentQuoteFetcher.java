package io.github.rxue.investmentutility.marketquote.yahoofinance;

import io.github.rxue.investmentutility.marketquote.BatchQuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteFetcher;
import io.github.rxue.investmentutility.marketquote.QuoteMetric;
import io.github.rxue.investmentutility.vo.MetricValues;
import io.github.rxue.investmentutility.vo.MetricValuesList;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 * Currently, each thread has its own http client, separate Yahoo login on each worker thread. There is potent concurrency issues:
 *
 * Many threads, each fetches a cookie and a crumb before fetching any quotes.
 * Bursts of crumb requests are what Yahoo tends to rate-limit (HTTP 429 ERROR response code). One rejected crumb fails the whole batch,
 * and since each thread maintains its own crumb, you can't tell which ticker hit it
 *
 */
public class ConcurrentQuoteFetcher implements BatchQuoteFetcher {
    private final QuoteFetcher quoteFetcher;
    private final ExecutorService executorService;
    public ConcurrentQuoteFetcher(String reportCurrency, ExecutorService executorService) {
        this.executorService = executorService;
        this.quoteFetcher = new YahooFinanceQuoteFetcher(reportCurrency);
    }

    @Override
    public MetricValuesList fetchAll(Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        List<Callable<Map<QuoteMetric,Comparable<?>>>> tasks = new ArrayList<>();
        List<String> securityIdList = new ArrayList<>();
        for (String securityId : securityIds) {
            tasks.add(() -> quoteFetcher.fetch(securityId, quoteMetrics));
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
