package io.github.rxue.investmentutility.vo.bankaccount.transaction;

import java.time.LocalDate;

public record Period(LocalDate startDate, LocalDate endDate) {
    public boolean includes(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
