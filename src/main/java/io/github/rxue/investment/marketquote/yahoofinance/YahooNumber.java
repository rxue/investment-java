package io.github.rxue.investment.marketquote.yahoofinance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

record YahooNumber(@JsonProperty("raw") BigDecimal value, @JsonProperty("fmt") String formatted) {
}
