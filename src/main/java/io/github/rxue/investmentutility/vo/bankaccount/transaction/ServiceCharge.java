package io.github.rxue.investmentutility.vo.bankaccount.transaction;

import java.time.LocalDate;

public record ServiceCharge(LocalDate date, long cents) implements Expense {
}
