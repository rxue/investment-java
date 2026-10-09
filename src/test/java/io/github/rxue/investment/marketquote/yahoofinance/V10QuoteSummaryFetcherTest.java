package io.github.rxue.investment.marketquote.yahoofinance;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class V10QuoteSummaryFetcherTest {

    @Test
    void getValue_no_trailing_PE() {
        final V10QuoteSummaryFetcher v10QuoteSummaryFetcher = new V10QuoteSummaryFetcher(null);
        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode quoteSummary = objectMapper.readTree("""
                {"result": [{"summaryDetail": {}}], "error": null}
                """);

        assertNull(v10QuoteSummaryFetcher.getValue(quoteSummary, YahooMetric.TRAILING_PE));
    }
    @Test
    void getValue_empty_dividend_yield() {
        final V10QuoteSummaryFetcher v10QuoteSummaryFetcher = new V10QuoteSummaryFetcher(null);
        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode quoteSummary = objectMapper.readTree("""
                {"result": [{"summaryDetail": {"dividendYield": {}}}], "error": null}
                """);

        assertNull(v10QuoteSummaryFetcher.getValue(quoteSummary, YahooMetric.DIVIDEND_YIELD));
    }
    @Test
    void getValue_currency() {
        final V10QuoteSummaryFetcher v10QuoteSummaryFetcher = new V10QuoteSummaryFetcher(null);
        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode quoteSummary = objectMapper.readTree("""
                {"result": [{"price": {"currency": "USD"}}], "error": null}
                """);

        assertEquals("USD", v10QuoteSummaryFetcher.getValue(quoteSummary, YahooMetric.CURRENCY));
    }
    @Test
    void getValues_no_trailing_PE() {
        final V10QuoteSummaryFetcher v10QuoteSummaryFetcher = new V10QuoteSummaryFetcher(null);
        final ObjectMapper objectMapper = new ObjectMapper();
        JsonNode quoteSummary = objectMapper.readTree("""
                {"result": [{"summaryDetail": {}}], "error": null}
                """);
        Map<YahooMetric,Object> values = v10QuoteSummaryFetcher.getValues(quoteSummary, Set.of(YahooMetric.TRAILING_PE));
        assertNull(values.get(YahooMetric.TRAILING_PE));
    }
}
