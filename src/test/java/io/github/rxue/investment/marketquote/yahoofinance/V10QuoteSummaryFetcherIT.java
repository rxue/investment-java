package io.github.rxue.investment.marketquote.yahoofinance;

import org.junit.jupiter.api.Test;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

import io.github.rxue.investment.vo.NumberWithFormat;
import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.CURRENCY;
import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.REGULAR_MARKET_PRICE;
import static io.github.rxue.investment.marketquote.yahoofinance.YahooMetric.TRAILING_PE;
import static org.junit.jupiter.api.Assertions.*;

class V10QuoteSummaryFetcherIT {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final V10QuoteSummaryFetcher v10QuoteSummaryFetcher = new V10QuoteSummaryFetcher(httpClient);

    @Test
    void fetch_metricsOfMultipleModules() {
        // REGULAR_MARKET_PRICE and CURRENCY are in module price, TRAILING_PE is in module summaryDetail
        Set<YahooMetric> metrics = Set.of(REGULAR_MARKET_PRICE, CURRENCY, TRAILING_PE);

        Map<YahooMetric,Object> values = v10QuoteSummaryFetcher.fetch("AAPL", metrics);

        assertEquals(metrics, values.keySet());
        NumberWithFormat regularMarketPrice = (NumberWithFormat) values.get(REGULAR_MARKET_PRICE);
        assertTrue(regularMarketPrice.value().signum() > 0, "regular market price should be positive but was " + regularMarketPrice.value());
        assertEquals("USD", values.get(CURRENCY));
        NumberWithFormat trailingPE = (NumberWithFormat) values.get(TRAILING_PE);
        assertTrue(trailingPE.value().signum() > 0, "trailing P/E should be positive but was " + trailingPE.value());
    }
}
