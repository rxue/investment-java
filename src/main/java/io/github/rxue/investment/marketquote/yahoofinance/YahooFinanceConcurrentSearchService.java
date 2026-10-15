package io.github.rxue.investment.marketquote.yahoofinance;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.SearchService;
import io.github.rxue.investment.vo.MetricValues;
import io.github.rxue.investment.vo.MetricValuesList;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class YahooFinanceConcurrentSearchService implements SearchService {
    private final List<SearchService> slaveServices;
    private final ExecutorService executorService;
    public YahooFinanceConcurrentSearchService(int threads, String reportCurrency, ExecutorService executorService) {
        this.executorService = executorService;
        this.slaveServices = new ArrayList<>(threads);
        IntStream.range(0, threads).forEach(i -> slaveServices.add(new YahooFinanceSearchService(reportCurrency)));
    }


    @Override
    public Map<QuoteMetric, Comparable<?>> findMetricValues(String securityId, Set<QuoteMetric> quoteMetrics) {
        Future<Map<QuoteMetric,Comparable<?>>> singleResult = executorService.submit(() -> slaveServices.get(0).findMetricValues(securityId, quoteMetrics));
        try {
            return singleResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public MetricValuesList findMetricValues(Set<String> securityIds, Set<QuoteMetric> quoteMetrics) {
        List<String> ids = new ArrayList<>(securityIds);
        List<Callable<Map<QuoteMetric, Comparable<?>>>> tasks = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            SearchService slaveService = slaveServices.get(i % slaveServices.size());
            String securityId = ids.get(i);
            tasks.add(() -> slaveService.findMetricValues(securityId, quoteMetrics));
        }
        try {
            // blocks until every task has completed (normally or exceptionally)
            List<Future<Map<QuoteMetric, Comparable<?>>>> futures = executorService.invokeAll(tasks);
            Map<String, MetricValues> metricValuesBySecurityId = new HashMap<>();
            for (int i = 0; i < ids.size(); i++) {
                metricValuesBySecurityId.put(ids.get(i), SearchService.toMetricValues(ids.get(i), futures.get(i).get()));
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
