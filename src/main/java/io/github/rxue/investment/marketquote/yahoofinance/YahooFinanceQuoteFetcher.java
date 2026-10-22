package io.github.rxue.investment.marketquote.yahoofinance;

import io.github.rxue.investment.fx.FxRateFetcher;
import io.github.rxue.investment.fx.ecb.ECBFxRateFetcher;
import io.github.rxue.investment.marketquote.BatchSearchService;
import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.marketquote.SearchService;
import io.github.rxue.investment.vo.NumberWithFormat;
import io.github.rxue.investment.vo.Pair;
import io.github.rxue.investment.vo.QuotePrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class YahooFinanceSearchService implements SearchService {
    private final V10QuoteSummaryFetcher v10QuoteSummaryFetcher;
    private final FxRateFetcher fxRateFetcher;
    private final String reportCurrency;
    public YahooFinanceSearchService(String reportCurrency) {
        this(newHttpClient(), reportCurrency);
    }
    public static HttpClient newHttpClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    public YahooFinanceSearchService(HttpClient httpClient, String reportCurrency) {
        this(new V10QuoteSummaryFetcher(httpClient), new ECBFxRateFetcher(httpClient), reportCurrency);
    }
    YahooFinanceSearchService(V10QuoteSummaryFetcher v10QuoteSummaryFetcher, FxRateFetcher fxRateFetcher, String reportCurrency) {
        this.v10QuoteSummaryFetcher = v10QuoteSummaryFetcher;
        this.fxRateFetcher = fxRateFetcher;
        this.reportCurrency = reportCurrency;
    }

    @Override
    public Map<QuoteMetric,Comparable<?>> findMetricValues(String securityId, Set<QuoteMetric> quoteMetrics) {
        Map<YahooMetric,Object> yahooMetricValues = v10QuoteSummaryFetcher.fetch(securityId, allNeededYahooMetrics(quoteMetrics));
        Map<QuoteMetric,Comparable<?>> resultValues = new HashMap<>();
        for (QuoteMetric quoteMetric : quoteMetrics) {
            resultValues.put(quoteMetric, getQuoteMetricValue(quoteMetric, yahooMetricValues));
        }
        return Collections.unmodifiableMap(resultValues);
    }

    private Comparable<?> getQuoteMetricValue(QuoteMetric quoteMetric, Map<YahooMetric,Object> yahooMetricValues) {
        return switch(quoteMetric) {
            case COMPANY_NAME -> (String) yahooMetricValues.get(YahooMetric.LONG_NAME);
            case LATEST_MARKET_PRICE -> {
                BigDecimal priceValue = ((NumberWithFormat) yahooMetricValues.get(YahooMetric.REGULAR_MARKET_PRICE)).value();
                yield new QuotePrice(priceValue, (String) yahooMetricValues.get(YahooMetric.CURRENCY));
            }
            case LATEST_MARKET_PRICE_IN_REPORT_CURRENCY -> {
                BigDecimal priceValue = ((NumberWithFormat) yahooMetricValues.get(YahooMetric.REGULAR_MARKET_PRICE)).value();
                String originalCurrency = (String) yahooMetricValues.get(YahooMetric.CURRENCY);
                Pair<LocalDate,BigDecimal> fxRateOnDate = fxRateFetcher.fetchRate(reportCurrency, originalCurrency, LocalDate.now());
                yield new QuotePrice(priceValue.divide(fxRateOnDate.right(), 4, RoundingMode.HALF_EVEN), reportCurrency);
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
                case LATEST_MARKET_PRICE,LATEST_MARKET_PRICE_IN_REPORT_CURRENCY -> {
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
