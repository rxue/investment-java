package io.github.rxue.investment.fx.ecb;

import io.github.rxue.investment.fx.FxRateFetcher;
import io.github.rxue.investment.vo.Pair;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

public class ECBFxRateFetcher implements FxRateFetcher {
    private static final String EXR_DATA_URL = "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    // long enough to reach back over weekends and public holidays to the previous publishing day
    private static final int LOOKBACK_DAYS = 7;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ECBFxRateFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
    @Override
    public final Pair<LocalDate,BigDecimal> fetchRate(String baseCurrency, String quoteCurrency, LocalDate date) {
        if ("EUR".equals(baseCurrency)){
            return fetchFxRateFromEuro(quoteCurrency, date);
        }
        throw new UnsupportedOperationException();
    }

    /**
     * @return the reference rate of the given date, or of the latest publishing day before it when no rate was
     * published on that date, paired with the date the rate is actually from
     */
    private Pair<LocalDate,BigDecimal> fetchFxRateFromEuro(String currency, LocalDate date) {
        if ("EUR".equals(currency)) {
            return new Pair<>(date, BigDecimal.ONE);
        }
        URI uri = URI.create(String.format(EXR_DATA_URL, currency)
                + "?startPeriod=" + date.minusDays(LOOKBACK_DAYS) + "&endPeriod=" + date + "&format=jsondata");
        HttpResponse<String> response = sendRequest(uri);
        // the body is empty when the currency exists but has no rate in the period
        if (response.statusCode() == 404 || response.statusCode() == 200 && response.body().isBlank()) {
            throw new IllegalStateException("No ECB exchange rate of " + currency + " found within " + LOOKBACK_DAYS + " days up to " + date);
        }
        if (response.statusCode() != 200) {
            throw new IllegalStateException("ECB request for " + uri + " failed with status " + response.statusCode() + ": " + response.body());
        }
        return getLatestRate(objectMapper.readTree(response.body()));
    }

    /**
     * The response is in SDMX-JSON, e.g.
     * <pre>{@code
     * {
     *   "dataSets": [ {
     *     "series": {
     *       "0:0:0:0:0": {
     *         "observations": {
     *           "0": [1.159, 0, 0, null, null],
     *           "1": [1.1578, 0, 0, null, null]
     *         }
     *       }
     *     }
     *   } ],
     *   "structure": {
     *     "dimensions": {
     *       "observation": [ {
     *         "id": "TIME_PERIOD",
     *         "values": [ { "id": "2026-09-01" }, { "id": "2026-09-02" } ]
     *       } ]
     *     }
     *   }
     * }
     * }</pre>
     * The observation keys are indices into the values of TIME_PERIOD, days without a published rate are absent
     */
    Pair<LocalDate,BigDecimal> getLatestRate(JsonNode root) {
        JsonNode observations = root.path("dataSets").path(0).path("series").properties().stream()
                .findFirst()
                .map(series -> series.getValue().path("observations"))
                .orElseThrow(() -> new IllegalStateException("No exchange rate series found in the ECB response"));
        int latestIndex = observations.properties().stream()
                .map(Map.Entry::getKey)
                .mapToInt(Integer::parseInt)
                .max()
                .orElseThrow(() -> new IllegalStateException("No exchange rate observation found in the ECB response"));
        BigDecimal rate = observations.path(String.valueOf(latestIndex)).path(0).decimalValue();
        return new Pair<>(getObservationDate(root, latestIndex), rate);
    }

    private static LocalDate getObservationDate(JsonNode root, int observationIndex) {
        for (JsonNode dimension : root.path("structure").path("dimensions").path("observation")) {
            if ("TIME_PERIOD".equals(dimension.path("id").asString())) {
                return LocalDate.parse(dimension.path("values").path(observationIndex).path("id").asString());
            }
        }
        throw new IllegalStateException("No TIME_PERIOD dimension found in the ECB response");
    }

    private HttpResponse<String> sendRequest(URI uri) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Accept", "application/json")
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
