package org.example;

import org.example.entity.Account;
import org.example.Enum.AccountClEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {

    @Test
    void 계정_코드의_앞자리_0을_유지한다() {
        Account account = new Account("001", "현금", AccountClEnum.ASSET);

        assertEquals("001", account.getAccountCode());
    }

    @Test
    void 계정명은_앞뒤_공백만_제거한다() {
        Account account = new Account("001", "  운영 자금  ", AccountClEnum.ASSET);

        assertEquals("운영 자금", account.getAccountName());
    }

    @ParameterizedTest
    @EnumSource(AccountClEnum.class)
    void 네_가지_계정_분류를_저장할_수_있다(AccountClEnum category) {
        Account account = new Account("001", "테스트 계정", category);

        assertEquals(category, account.getAccountCl());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"01", "0001", "A01", "００１"})
    void 코드가_ASCII_숫자_세_자리가_아니면_거절한다(String code) {
        assertThrows(IllegalArgumentException.class,
                () -> new Account(code, "현금", AccountClEnum.ASSET));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "123456789012345678901"})
    void 계정명이_비어_있거나_20자를_넘으면_거절한다(String name) {
        assertThrows(IllegalArgumentException.class,
                () -> new Account("001", name, AccountClEnum.ASSET));
    }

    @Test
    void 계정명이_정확히_20자면_허용한다() {
        String name = "가".repeat(20);
        Account account = new Account("001", name, AccountClEnum.ASSET);

        assertEquals(name, account.getAccountName());
    }

    @Test
    void 계정_분류가_null이면_거절한다() {
        assertThrows(IllegalArgumentException.class,
                () -> new Account("001", "현금", null));
    }
}