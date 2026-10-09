package io.github.rxue.investment.vo.bankaccount.transaction;

import java.time.LocalDate;

public interface Transaction {
    LocalDate date();
    long cents();
}
