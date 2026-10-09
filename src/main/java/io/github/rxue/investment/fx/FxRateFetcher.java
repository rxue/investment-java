package io.github.rxue.investment.fx;

import io.github.rxue.investment.vo.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FxRateFetcher {
    Pair<LocalDate,BigDecimal> fetchRate(String baseCurrency, String quoteCurrency, LocalDate date);
}
