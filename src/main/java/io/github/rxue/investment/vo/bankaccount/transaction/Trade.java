package io.github.rxue.investment.vo.bankaccount.transaction;

import java.time.LocalDate;

public record Trade(LocalDate date, Action action, String securityId, int shareAmount, long cents) implements Transaction {
}
