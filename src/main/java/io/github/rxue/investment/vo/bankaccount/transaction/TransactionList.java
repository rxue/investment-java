package io.github.rxue.investment.vo.bankaccount.transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Ordered list of transactions
 *
 * @param transactionList
 */
public record TransactionList(List<Transaction> transactionList) {
    public List<Dividend> dividendsSince(LocalDate inclusiveStartDate) {
        return transactionList.stream()
                .filter(tr -> !tr.date().isBefore(inclusiveStartDate))
                .filter(Dividend.class::isInstance)
                .map(Dividend.class::cast)
                .toList();
    }
    public List<Expense> expensesSince(LocalDate inclusiveStartDate) {
        return transactionList.stream()
                .filter(tr -> !tr.date().isBefore(inclusiveStartDate))
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
    public List<Deposit> depositsSince(LocalDate inclusiveStartDate) {
        return transactionList.stream()
                .filter(tr -> !tr.date().isBefore(inclusiveStartDate))
                .filter(Deposit.class::isInstance)
                .map(Deposit.class::cast)
                .toList();
    }
    public TransactionList transactionsBy(LocalDate inclusiveEndDate) {
        List<Transaction> result = transactionList.stream()
                .filter(tr -> !tr.date().isAfter(inclusiveEndDate))
                .toList();
        return new TransactionList(result);
    }
}
