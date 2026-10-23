package io.github.rxue.investmentutility.vo.bankaccount.transaction;

import java.time.LocalDate;

public interface Transaction {
    LocalDate date();
    long cents();
}
