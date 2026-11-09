package org.example.repository;

import org.example.entity.Account;
import org.example.Enum.AccountClEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountRepositoryTest {

    private final AccountRepository repository = new AccountRepository();

    @Test
    void 저장한_계정을_조회할_수_있다() {
        Account account = new Account("001", "현금", AccountClEnum.ASSET);

        repository.save(account);

        assertEquals(List.of(account), repository.findBySortedByCode());
    }

    @Test
    void 중복_코드를_거절하고_기존_계정을_보존한다() {
        Account original = new Account("001", "현금", AccountClEnum.ASSET);
        Account duplicate = new Account("001", "보통예금", AccountClEnum.ASSET);
        repository.save(original);

        assertThrows(IllegalArgumentException.class,
                () -> repository.save(duplicate));

        List<Account> accounts = repository.findBySortedByCode();
        assertEquals(1, accounts.size());
        assertSame(original, accounts.get(0));
    }

    @Test
    void 계정_코드_오름차순으로_조회한다() {
        repository.save(new Account("201", "차입금", AccountClEnum.LIABILITY));
        repository.save(new Account("011", "보통예금", AccountClEnum.ASSET));
        repository.save(new Account("001", "현금", AccountClEnum.ASSET));

        List<String> codes = repository.findBySortedByCode().stream()
                .map(Account::getAccountCode)
                .toList();

        assertEquals(List.of("001", "011", "201"), codes);
    }

    @Test
    void 저장된_계정이_없으면_빈_목록을_반환한다() {
        assertEquals(List.of(), repository.findBySortedByCode());
    }
}