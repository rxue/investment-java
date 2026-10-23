package io.github.rxue.investmentutility.fx;

import io.github.rxue.investmentutility.vo.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FxRateFetcher {
    /**
     *
     * @param baseCurrency
     * @param quoteCurrency
     * @param date
     * @return
     */
    Pair<LocalDate,BigDecimal> fetchRate(String baseCurrency, String quoteCurrency, LocalDate date);
}
