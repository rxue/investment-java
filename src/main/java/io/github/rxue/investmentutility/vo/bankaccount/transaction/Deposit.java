package io.github.rxue.investmentutility.vo.bankaccount.transaction;

import java.time.LocalDate;

public record Deposit(LocalDate date, long cents) implements Transaction {
}
