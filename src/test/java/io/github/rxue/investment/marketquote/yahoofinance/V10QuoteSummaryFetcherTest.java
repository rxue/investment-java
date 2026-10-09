package io.github.rxue.investment.marketquote.yahoofinance;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class QuoteFetcherTest {

    @Test
    void getValue_no_trailing_PE() {
        final QuoteFetcher quoteFetcher = new QuoteFetcher(null);
        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode quoteSummary = objectMapper.readTree("""
                {"result": [{"summaryDetail": {}}], "error": null}
                """);

        assertNull(quoteFetcher.getValue(quoteSummary, YahooMetric.TRAILING_PE));
    }
    @Test
    void getValue_empty_dividend_yield() {
        final QuoteFetcher quoteFetcher = new QuoteFetcher(null);
        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode quoteSummary = objectMapper.readTree("""
                {"result": [{"summaryDetail": {"dividendYield": {}}}], "error": null}
                """);

        assertNull(quoteFetcher.getValue(quoteSummary, YahooMetric.DIVIDEND_YIELD));
    }
    @Test
    void getValue_currency() {
        final QuoteFetcher quoteFetcher = new QuoteFetcher(null);
        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode quoteSummary = objectMapper.readTree("""
                {"result": [{"price": {"currency": "USD"}}], "error": null}
                """);

        assertEquals("USD", quoteFetcher.getValue(quoteSummary, YahooMetric.CURRENCY));
    }
    @Test
    void getValues_no_trailing_PE() {
        final QuoteFetcher quoteFetcher = new QuoteFetcher(null);
        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode quoteSummary = objectMapper.readTree("""
                {"result": [{"summaryDetail": {}}], "error": null}
                """);
        Map<YahooMetric,Object> values = quoteFetcher.getValues(quoteSummary, Set.of(YahooMetric.TRAILING_PE));
        assertNull(values.get(YahooMetric.TRAILING_PE));
    }
}
