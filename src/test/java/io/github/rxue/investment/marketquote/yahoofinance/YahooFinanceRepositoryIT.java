package io.github.rxue.investment.marketquote.yahoofinance;

import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.vo.QuotePrice;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

import io.github.rxue.investment.vo.NumberWithFormat;

import static io.github.rxue.investment.marketquote.QuoteMetric.DIVIDEND_YIELD;
import static io.github.rxue.investment.marketquote.QuoteMetric.LATEST_MARKET_PRICE;
import static org.junit.jupiter.api.Assertions.*;

public class YahooFinanceRepositoryIT {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final YahooFinanceRepository repository = new YahooFinanceRepository(new QuoteFetcher(httpClient));

    @Test
    void findMetricValues_latestMarketPriceAndDividendYield() {
        Set<QuoteMetric> quoteMetrics = Set.of(LATEST_MARKET_PRICE, DIVIDEND_YIELD);

        Map<QuoteMetric, Comparable<?>> values = repository.findMetricValues("AAPL", quoteMetrics);

        assertEquals(quoteMetrics, values.keySet());
        QuotePrice latestMarketPrice = (QuotePrice) values.get(LATEST_MARKET_PRICE);
        assertTrue(latestMarketPrice.value().signum() > 0, "latest market price should be positive but was " + latestMarketPrice.value());
        assertEquals("USD", latestMarketPrice.currency());
        // the dividend yield is a fraction, e.g. 0.004 for 0.4%
        BigDecimal dividendYield = ((NumberWithFormat) values.get(DIVIDEND_YIELD)).value();
        assertTrue(dividendYield.signum() > 0 && dividendYield.compareTo(BigDecimal.ONE) < 0, "dividend yield should be between 0 and 1 but was " + dividendYield);
    }
}
