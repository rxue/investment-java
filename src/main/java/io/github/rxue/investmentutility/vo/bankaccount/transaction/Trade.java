package io.github.rxue.investmentutility.vo.bankaccount.transaction;

import java.time.LocalDate;

public record Trade(LocalDate date, Action action, String securityId, int shareAmount, long cents) implements Transaction {
}
