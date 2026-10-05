package io.github.rxue.investment.marketquote.yahoofinance;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuoteFetcherIT {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final QuoteFetcher quoteFetcher = new QuoteFetcher(httpClient);

    @Test
    void fetch_regularMarketPrice_returnsYahooNumber() {
        YahooNumber regularMarketPrice = quoteFetcher.fetch("AAPL", YahooMetric.REGULAR_MARKET_PRICE);

        assertNotNull(regularMarketPrice);
        assertNotNull(regularMarketPrice.value());
        assertTrue(regularMarketPrice.value().signum() > 0, "regular market price should be positive but was " + regularMarketPrice.value());
        // fmt is raw rounded to 2 decimals, e.g. raw 332.955 and fmt "332.96"
        assertEquals(regularMarketPrice.value().setScale(2, RoundingMode.HALF_UP), new BigDecimal(regularMarketPrice.formatted().replace(",", "")));
    }
}
