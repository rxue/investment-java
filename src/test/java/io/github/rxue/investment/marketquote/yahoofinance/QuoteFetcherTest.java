package io.github.rxue.investment.marketquote.yahoofinance;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;

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
