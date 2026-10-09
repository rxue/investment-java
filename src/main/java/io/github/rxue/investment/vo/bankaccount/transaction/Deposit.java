package io.github.rxue.investment.vo.bankaccount.transaction;

import java.time.LocalDate;

public record Deposit(LocalDate date, long cents) implements Transaction {
}
