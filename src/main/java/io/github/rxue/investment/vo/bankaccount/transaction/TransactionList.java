package io.github.rxue.investment.vo.bankaccount.transaction;

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
}
