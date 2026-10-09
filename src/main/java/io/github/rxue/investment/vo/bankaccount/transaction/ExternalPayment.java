package io.github.rxue.investment.vo.bankaccount.transaction;

import java.time.LocalDate;

public record ExternalPayment(LocalDate date, long cents) implements Expense {
}
