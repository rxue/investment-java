package io.github.rxue.investment.marketquote.yahoofinance;

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
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

class QuoteFetcher {
    private static final String COOKIE_URL = "https://fc.yahoo.com";
    private static final String CRUMB_URL = "https://query2.finance.yahoo.com/v1/test/getcrumb";
    private static final String QUOTE_SUMMARY_URL = "https://query2.finance.yahoo.com/v10/finance/quoteSummary/";
    // Yahoo answers 429 to the default Java user agent, and to Linux browser user agents as well
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String crumb;

    public QuoteFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
        /*this.httpClient = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(TIMEOUT)
                .build();*/
    }

    /**
     * @return an instance of the type class of the given metric
     */
    Map<YahooMetric,Comparable<?>> fetch(String securityId, Set<YahooMetric> metrics) {
        final URI uri = quoteSummaryUri(commaDelimitedModules(metrics), securityId);
        HttpResponse<String> response = sendRequest(uri);
        if (response.statusCode() == 401) {
            // the crumb has expired
            crumb = null;
            response = sendRequest(uri);
        }
        JsonNode quoteSummary = objectMapper.readTree(response.body()).path("quoteSummary");
        JsonNode error = quoteSummary.path("error");
        if (response.statusCode() != 200 || !error.isMissingNode() && !error.isNull()) {
            throw new IllegalStateException("Yahoo Finance request for " + uri
                    + " failed with status " + response.statusCode() + ": " + error.path("description").asString(response.body()));
        }
        return getValues(quoteSummary, metrics);
    }
    private Comparable<?> getValue(JsonNode quoteSummary, YahooMetric metric) {
        JsonNode value = quoteSummary.path("result").path(0).path(metric.v10Module()).path(metric.metricName());
        return objectMapper.treeToValue(value, metric.typeClass());
    }
    Map<YahooMetric,Comparable<?>> getValues(JsonNode quoteSummary, Set<YahooMetric> yahooMetrics) {
        return yahooMetrics.stream()
                .map(yahooMetric -> new AbstractMap.SimpleImmutableEntry<>(yahooMetric, getValue(quoteSummary, yahooMetric)))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }



    private URI quoteSummaryUri(String modules, String securityId) {
        return URI.create(QUOTE_SUMMARY_URL + encode(securityId) + "?modules=" + encode(modules) + "&crumb=" + encode(crumb()));
    }

    private String crumb() {
        if (crumb == null) {
            // the response itself is irrelevant, the request is only made to get the session cookie
            sendRequest(URI.create(COOKIE_URL));
            HttpResponse<String> response = sendRequest(URI.create(CRUMB_URL));
            if (response.statusCode() != 200 || response.body().isBlank()) {
                throw new IllegalStateException("Failed to get the Yahoo Finance crumb, status " + response.statusCode() + ": " + response.body());
            }
            crumb = response.body();
        }
        return crumb;
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

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String commaDelimitedModules(Set<YahooMetric> metrics) {
        return metrics.stream()
                .map(YahooMetric::v10Module)
                .distinct()
                .sorted()
                .collect(joining(","));
    }
}
