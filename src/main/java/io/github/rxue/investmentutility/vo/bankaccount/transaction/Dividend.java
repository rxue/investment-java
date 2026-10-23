package io.github.rxue.investmentutility.vo.bankaccount.transaction;

import java.time.LocalDate;

public record Dividend(LocalDate date, String securityId, int shareAmount, long cents) implements Transaction {
}
