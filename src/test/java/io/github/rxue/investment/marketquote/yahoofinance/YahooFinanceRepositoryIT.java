package io.github.rxue.investment.marketquote.yahoofinance;

import io.github.rxue.investment.fx.ecb.ECBFxRateFetcher;
import io.github.rxue.investment.marketquote.QuoteMetric;
import io.github.rxue.investment.vo.QuotePrice;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
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
    private final YahooFinanceRepository repository = new YahooFinanceRepository(new ECBFxRateFetcher(httpClient), new V10QuoteSummaryFetcher(httpClient), new V8ChartFetcher(httpClient));

    @Test
    void findMetricValues_latestMarketPriceAndDividendYield() {
        Set<QuoteMetric> quoteMetrics = Set.of(LATEST_MARKET_PRICE, DIVIDEND_YIELD);

        Map<QuoteMetric, Comparable<?>> values = repository.findMetricValues("AAPL", quoteMetrics);

        assertEquals(quoteMetrics, values.keySet());
        QuotePrice latestMarketQuotePrice = (QuotePrice) values.get(LATEST_MARKET_PRICE);
        assertTrue(latestMarketQuotePrice.value().signum() > 0, "latest market price should be positive but was " + latestMarketQuotePrice.value());
        assertEquals("USD", latestMarketQuotePrice.currency());
        // the dividend yield is a fraction, e.g. 0.004 for 0.4%
        BigDecimal dividendYield = ((NumberWithFormat) values.get(DIVIDEND_YIELD)).value();
        assertTrue(dividendYield.signum() > 0 && dividendYield.compareTo(BigDecimal.ONE) < 0, "dividend yield should be between 0 and 1 but was " + dividendYield);
    }

    @Test
    void findClosePrice_inEuroOnPublicHoliday() {
        // 2026-01-01 had neither trading nor an ECB rate: on 2025-12-31 PFE closed at 24.90 USD and EUR/USD was 1.1750
        QuotePrice closePrice = repository.findClosePrice("PFE", LocalDate.of(2026, 1, 1), "EUR");

        assertEquals("EUR", closePrice.currency());
        assertEquals(new BigDecimal("21.19"), closePrice.value().setScale(2, RoundingMode.HALF_UP));
    }

}
