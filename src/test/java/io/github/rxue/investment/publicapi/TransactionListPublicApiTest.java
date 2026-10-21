package io.github.rxue.investment.publicapi;

import io.github.rxue.investment.vo.bankaccount.transaction.Action;
import io.github.rxue.investment.vo.bankaccount.transaction.Deposit;
import io.github.rxue.investment.vo.bankaccount.transaction.Dividend;
import io.github.rxue.investment.vo.bankaccount.transaction.ExternalPayment;
import io.github.rxue.investment.vo.bankaccount.transaction.ServiceCharge;
import io.github.rxue.investment.vo.bankaccount.transaction.Trade;
import io.github.rxue.investment.vo.bankaccount.transaction.TransactionList;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionListPublicApiTest {
    private static final Deposit DEPOSIT = new Deposit(LocalDate.of(2026, 1, 2), 100000);
    private static final Trade BUY = new Trade(LocalDate.of(2026, 1, 13), Action.BUY, "MRNA", 20, 57596);
    private static final ServiceCharge SERVICE_CHARGE = new ServiceCharge(LocalDate.of(2026, 1, 13), 900);
    private static final Trade SELL = new Trade(LocalDate.of(2026, 1, 14), Action.SELL, "MRNA", 20, 61200);
    private static final Dividend DIVIDEND = new Dividend(LocalDate.of(2026, 2, 1), "MRNA", 20, 1500);
    private static final ExternalPayment EXTERNAL_PAYMENT = new ExternalPayment(LocalDate.of(2026, 2, 5), 20000);

    private static final TransactionList TRANSACTIONS = new TransactionList(
            List.of(DEPOSIT, BUY, SERVICE_CHARGE, SELL, DIVIDEND, EXTERNAL_PAYMENT));

    @Test
    void dividends() {
        assertEquals(List.of(DIVIDEND), TRANSACTIONS.dividends());
    }

    @Test
    void expenses() {
        assertEquals(List.of(SERVICE_CHARGE, EXTERNAL_PAYMENT), TRANSACTIONS.expenses());
    }

    @Test
    void trades() {
        assertEquals(List.of(BUY, SELL), TRANSACTIONS.trades());
    }

    @Test
    void deposits() {
        assertEquals(List.of(DEPOSIT), TRANSACTIONS.deposits());
    }

    @Test
    void by_includes_end_date() {
        TransactionList result = TRANSACTIONS.by(LocalDate.of(2026, 1, 14));

        assertEquals(List.of(DEPOSIT, BUY, SERVICE_CHARGE, SELL), result.transactionList());
    }
}
