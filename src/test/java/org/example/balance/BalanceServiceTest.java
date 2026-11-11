package org.example.balance;

import org.example.account.entity.Account;
import org.example.account.enums.AccountClEnum;
import org.example.document.entity.Document;
import org.example.document.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BalanceServiceTest {
    private final BalanceService service = new BalanceService();
    private final LocalDate date = LocalDate.of(2026, 11, 6);

    @ParameterizedTest
    @CsvSource({"ASSET, 100", "EXPENSE, 100", "LIABILITY, -100", "REVENUE, -100"})
    void 차변_금액은_계정_분류에_맞게_계산한다(AccountClEnum category, long expected) {
        Account target = new Account("101", "대상 계정", category);
        Account other = new Account("201", "상대 계정", AccountClEnum.LIABILITY);
        assertEquals(expected, service.calculateBalance(target,
                List.of(new Document(date, "거래", target, other, 100L))));
    }

    @ParameterizedTest
    @CsvSource({"ASSET, -100", "EXPENSE, -100", "LIABILITY, 100", "REVENUE, 100"})
    void 대변_금액은_계정_분류에_맞게_계산한다(AccountClEnum category, long expected) {
        Account target = new Account("101", "대상 계정", category);
        Account other = new Account("201", "상대 계정", AccountClEnum.ASSET);
        assertEquals(expected, service.calculateBalance(target,
                List.of(new Document(date, "거래", other, target, 100L))));
    }

    @Test
    void 거래_없는_계정도_0원으로_포함하고_다른_분류는_제외한다() {
        Account cash = new Account("101", "현금", AccountClEnum.ASSET);
        Account loan = new Account("201", "차입금", AccountClEnum.LIABILITY);
        Account revenue = new Account("401", "수익", AccountClEnum.REVENUE);
        Account expense = new Account("501", "비용", AccountClEnum.EXPENSE);
        Map<Account, Long> balances = service.calculateBalance(
                List.of(cash, loan, revenue, expense), List.of(), AccountClEnum.ASSET);
        assertEquals(Map.of(cash, 0L), balances);
        assertEquals(0L, service.calculateTotal(balances));
    }

    @Test
    void 음수_잔액을_부호_그대로_합산한다() {
        Account cash = new Account("101", "현금", AccountClEnum.ASSET);
        Account bank = new Account("102", "예금", AccountClEnum.ASSET);
        Account expense = new Account("501", "비용", AccountClEnum.EXPENSE);
        List<Document> documents = List.of(
                new Document(date, "이체", bank, cash, 100L),
                new Document(date, "지출", expense, bank, 30L));
        Map<Account, Long> balances = service.calculateBalance(
                List.of(cash, bank, expense), documents, AccountClEnum.ASSET);
        assertEquals(-100L, balances.get(cash));
        assertEquals(70L, balances.get(bank));
        assertEquals(-30L, service.calculateTotal(balances));
    }

    @Test
    void 원전표와_취소_전표를_함께_집계하여_상쇄한다() {
        Account cash = new Account("101", "현금", AccountClEnum.ASSET);
        Account loan = new Account("201", "차입금", AccountClEnum.LIABILITY);
        DocumentRepository repository = new DocumentRepository();
        int number = repository.saveDocument(new Document(date, "차입", cash, loan, 100_000L));
        repository.cancelDocument(number, date.plusDays(1));
        List<Document> documents = repository.findAllSortedDocument().stream()
                .map(Map.Entry::getValue).toList();
        assertEquals(0L, service.calculateBalance(cash, documents));
        assertEquals(0L, service.calculateBalance(loan, documents));
    }

    @Test
    void 누계가_int_범위를_넘어도_정확히_계산한다() {
        Account cash = new Account("101", "현금", AccountClEnum.ASSET);
        Account loan = new Account("201", "차입금", AccountClEnum.LIABILITY);
        List<Document> documents = List.of(
                new Document(date, "차입1", cash, loan, 1_000_000_000L),
                new Document(date, "차입2", cash, loan, 1_000_000_000L),
                new Document(date, "차입3", cash, loan, 1_000_000_000L));
        Map<Account, Long> balances = service.calculateBalance(
                List.of(cash, loan), documents, AccountClEnum.ASSET);
        assertEquals(3_000_000_000L, service.calculateTotal(balances));
        assertEquals(3_000_000_000L, service.calculateBalance(loan, documents));
    }
}
