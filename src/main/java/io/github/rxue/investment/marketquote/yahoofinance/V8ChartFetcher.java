package io.github.rxue.investment.marketquote.yahoofinance;

import io.github.rxue.investment.vo.Pair;
import io.github.rxue.investment.vo.QuotePrice;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

class V8ChartFetcher {
    private static final String CHART_URL = "https://query2.finance.yahoo.com/v8/finance/chart/";
    // Yahoo answers 429 to the default Java user agent, and to Linux browser user agents as well
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    // long enough to reach back over weekends and public holidays to the previous trading day
    private static final int LOOKBACK_DAYS = 7;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public V8ChartFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * @return the close price of the given date, or of the latest trading day before it when there was no trading
     * on that date, paired with the date the price is actually from
     */
    public Pair<LocalDate, QuotePrice> fetchClosePrice(String securityId, LocalDate date) {
        URI uri = chartUri(securityId, date);
        HttpResponse<String> response = sendRequest(uri);
        JsonNode chart = objectMapper.readTree(response.body()).path("chart");
        JsonNode error = chart.path("error");
        if (response.statusCode() != 200 || !error.isMissingNode() && !error.isNull()) {
            throw new IllegalStateException("Yahoo Finance request for " + uri
                    + " failed with status " + response.statusCode() + ": " + error.path("description").asString(response.body()));
        }
        return getClosePrice(chart.path("result").path(0), securityId, date);
    }

    Pair<LocalDate, QuotePrice> getClosePrice(JsonNode result, String securityId, LocalDate date) {
        JsonNode meta = result.path("meta");
        // a daily timestamp is the opening time of the trading day, so its date depends on the time zone of the exchange
        ZoneId exchangeZone = ZoneId.of(meta.path("exchangeTimezoneName").asString());
        JsonNode timestamps = result.path("timestamp");
        JsonNode closes = result.path("indicators").path("quote").path(0).path("close");
        for (int i = timestamps.size() - 1; i >= 0; i--) {
            LocalDate tradingDate = Instant.ofEpochSecond(timestamps.get(i).asLong()).atZone(exchangeZone).toLocalDate();
            JsonNode close = closes.path(i);
            // the close is null for a day without trades
            if (!tradingDate.isAfter(date) && close.isNumber()) {
                return new Pair<>(tradingDate, new QuotePrice(close.decimalValue(), meta.path("currency").asString()));
            }
        }
        throw new IllegalStateException("No close price of " + securityId + " found within " + LOOKBACK_DAYS + " days up to " + date);
    }

    private static URI chartUri(String securityId, LocalDate date) {
        long period1 = date.minusDays(LOOKBACK_DAYS).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        // period2 is exclusive
        long period2 = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        return URI.create(CHART_URL + URLEncoder.encode(securityId, StandardCharsets.UTF_8)
                + "?period1=" + period1 + "&period2=" + period2 + "&interval=1d");
    }

    private HttpResponse<String> sendRequest(URI uri) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("User-Agent", USER_AGENT)
                .timeout(TIMEOUT)
                .GET()
                .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while requesting " + uri, e);
        }
    }
}
