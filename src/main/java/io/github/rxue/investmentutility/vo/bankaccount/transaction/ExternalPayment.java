package io.github.rxue.investmentutility.vo.bankaccount.transaction;

import java.time.LocalDate;

public record ExternalPayment(LocalDate date, long cents) implements Expense {
}
