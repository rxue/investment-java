package io.github.rxue.investmentutility.vo.bankaccount.transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Ordered list of transactions
 *
 * @param transactionList
 */
public record TransactionList(List<Transaction> transactionList) {
    public List<Dividend> dividends() {
        return transactionList.stream()
                .filter(Dividend.class::isInstance)
                .map(Dividend.class::cast)
                .toList();
    }
    public List<Expense> expenses() {
        return transactionList.stream()
                .filter(Expense.class::isInstance)
                .map(Expense.class::cast)
                .toList();
    }
    public List<Trade> trades() {
        return transactionList.stream()
                .filter(Trade.class::isInstance)
                .map(Trade.class::cast)
                .toList();
    }
    public List<Deposit> deposits() {
        return transactionList.stream()
                .filter(Deposit.class::isInstance)
                .map(Deposit.class::cast)
                .toList();
    }
    public TransactionList by(LocalDate inclusiveEndDate) {
        List<Transaction> result = transactionList.stream()
                .filter(tr -> !tr.date().isAfter(inclusiveEndDate))
                .toList();
        return new TransactionList(result);
    }
    public TransactionList since(LocalDate inclusiveDate) {
        List<Transaction> result = transactionList.stream()
                .filter(tr -> !tr.date().isBefore(inclusiveDate))
                .toList();
        return new TransactionList(result);
    }
}
