package io.github.rxue.investment.marketquote.yahoofinance;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.Repository;
import io.github.rxue.investment.vo.QuotePrice;
import io.github.rxue.investment.vo.NumberWithFormat;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.*;

import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.DIVIDEND_YIELD;
import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.TRAILING_PE;

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
        Map<QuoteMetric,Comparable<?>> resultValues = new HashMap<>();
        for (QuoteMetric quoteMetric : quoteMetrics) {
            resultValues.put(quoteMetric, getQuoteMetricValue(quoteMetric, yahooMetricValues));
        }
        return Collections.unmodifiableMap(resultValues);
    }

    private static Comparable<?> getQuoteMetricValue(QuoteMetric quoteMetric, Map<YahooMetric,Object> yahooMetricValues) {
        return switch(quoteMetric) {
            case COMPANY_NAME -> (String) yahooMetricValues.get(YahooMetric.LONG_NAME);
            case LATEST_MARKET_PRICE -> {
                BigDecimal priceValue = ((NumberWithFormat) yahooMetricValues.get(YahooMetric.REGULAR_MARKET_PRICE)).value();
                yield new QuotePrice(priceValue, (String) yahooMetricValues.get(YahooMetric.CURRENCY));
            }
            case REGULAR_MARKET_CHANGE_PERCENT -> (NumberWithFormat) yahooMetricValues.get(YahooMetric.REGULAR_MARKET_CHANGE_PERCENT);
            case TRAILING_PE -> (NumberWithFormat) yahooMetricValues.get(YahooMetric.TRAILING_PE);
            case DIVIDEND_YIELD -> (NumberWithFormat) yahooMetricValues.get(YahooMetric.DIVIDEND_YIELD);
            case DIVIDEND_PAYOUT_RATIO -> (NumberWithFormat) yahooMetricValues.get(YahooMetric.DIVIDEND_PAYOUT_RATIO);
        };
    }

    private static Set<YahooMetric> allNeededYahooMetrics(Set<QuoteMetric> quoteMetrics) {
        Set<YahooMetric> result = new HashSet<>();
        for (QuoteMetric quoteMetric : quoteMetrics) {
            switch(quoteMetric) {
                case COMPANY_NAME -> result.add(YahooMetric.LONG_NAME);
                case LATEST_MARKET_PRICE -> {
                    result.add(YahooMetric.REGULAR_MARKET_PRICE);
                    result.add(YahooMetric.CURRENCY);
                }
                case REGULAR_MARKET_CHANGE_PERCENT -> result.add(YahooMetric.REGULAR_MARKET_CHANGE_PERCENT);
                case TRAILING_PE -> result.add(YahooMetric.TRAILING_PE);
                case DIVIDEND_YIELD -> result.add(YahooMetric.DIVIDEND_YIELD);
                case DIVIDEND_PAYOUT_RATIO -> result.add(YahooMetric.DIVIDEND_PAYOUT_RATIO);
            }
        }
        return Collections.unmodifiableSet(result);
    }
}
