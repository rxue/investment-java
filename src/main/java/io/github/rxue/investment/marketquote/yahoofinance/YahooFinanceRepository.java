package io.github.rxue.investment.marketquote.yahoofinance;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.vo.QuotePrice;
import io.github.rxue.investment.vo.Number;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.*;

import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.*;
import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.DIVIDEND_YIELD;
import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.TRAILING_PE;
import static java.util.stream.Collectors.toMap;

public class YahooFinanceRepository implements Repository {
    private final QuoteFetcher quoteFetcher;

    /**
     * @param httpClient must have a cookie handler, Yahoo Finance requires the session cookie
     */
    public YahooFinanceRepository(HttpClient httpClient) {
        this(new QuoteFetcher(httpClient));
    }

    YahooFinanceRepository(QuoteFetcher quoteFetcher) {
        this.quoteFetcher = quoteFetcher;
    }

    @Override
    public Map<QuoteMetric,Comparable<?>> findMetricValues(String securityId, Set<QuoteMetric> quoteMetrics) {
        Map<YahooMetric,Object> yahooMetricValues = quoteFetcher.fetch(securityId, allNeededYahooMetrics(quoteMetrics));
        return quoteMetrics.stream()
                .map(quoteMetric -> Map.entry(quoteMetric, getQuoteMetricValue(quoteMetric, yahooMetricValues)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Comparable<?> getQuoteMetricValue(QuoteMetric quoteMetric, Map<YahooMetric,Object> yahooMetricValues) {
        return switch(quoteMetric) {
            case LATEST_MARKET_PRICE -> {
                BigDecimal priceValue = ((Number) yahooMetricValues.get(YahooMetric.REGULAR_MARKET_PRICE)).value();
                yield new QuotePrice(priceValue, (String) yahooMetricValues.get(CURRENCY));
            }
            case TRAILING_PE -> (Number) yahooMetricValues.get(YahooMetric.TRAILING_PE);
            case DIVIDEND_YIELD -> (Number) yahooMetricValues.get(YahooMetric.DIVIDEND_YIELD);
        };
    }

    private static Set<YahooMetric> allNeededYahooMetrics(Set<QuoteMetric> quoteMetrics) {
        Set<YahooMetric> result = new HashSet<>();
        for (QuoteMetric quoteMetric : quoteMetrics) {
            switch(quoteMetric) {
                case LATEST_MARKET_PRICE -> {
                    result.add(REGULAR_MARKET_PRICE);
                    result.add(CURRENCY);
                }
                case TRAILING_PE -> result.add(TRAILING_PE);
                case DIVIDEND_YIELD -> result.add(DIVIDEND_YIELD);
            }
        }
        return Collections.unmodifiableSet(result);
    }
}
