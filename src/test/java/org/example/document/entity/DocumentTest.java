package org.example.document.entity;

import org.example.account.entity.Account;
import org.example.document.entity.Document;
import org.example.account.enums.AccountClEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.document.enums.DocumentStatus;
import org.example.document.enums.DocumentType;

import static org.junit.jupiter.api.Assertions.assertNull;

class DocumentTest {

    private static final LocalDate DATE = LocalDate.of(2024, 2, 29);

    private final Account cash = new Account("001", "현금", AccountClEnum.ASSET);
    private final Account loan = new Account("201", "차입금", AccountClEnum.LIABILITY);

    @Test
    void 전표에_거래일자와_적요와_차대변_계정과_금액을_저장한다() {
        Document document = new Document(DATE, "  운영 자금 차입  ", cash, loan, 100_000L);

        assertEquals(DATE, document.getTransactionDate());
        assertEquals("운영 자금 차입", document.getDescription());
        assertEquals(cash, document.getDebitAccount());
        assertEquals(loan, document.getCreditAccount());
        assertEquals(100_000L, document.getAmount());
    }

    @Test
    void 차변과_대변의_계정_코드가_같으면_거절한다() {
        Account sameCode = new Account("001", "다른 이름", AccountClEnum.ASSET);

        assertThrows(IllegalArgumentException.class,
                () -> new Document(DATE, "계정 이동", cash, sameCode, 10_000L));
    }

    @Test
    void 분류가_같아도_계정_코드가_다르면_허용한다() {
        Account bank = new Account("002", "보통예금", AccountClEnum.ASSET);

        Document document = new Document(DATE, "현금 입금", bank, cash, 10_000L);

        assertEquals("002", document.getDebitAccount().getAccountCode());
        assertEquals("001", document.getCreditAccount().getAccountCode());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 1_000_000_000L})
    void 최소_금액과_최대_금액을_허용한다(long amount) {
        Document document = new Document(DATE, "차입", cash, loan, amount);

        assertEquals(amount, document.getAmount());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L, 1_000_000_001L})
    void 허용_범위_밖의_금액은_거절한다(long amount) {
        assertThrows(IllegalArgumentException.class,
                () -> new Document(DATE, "차입", cash, loan, amount));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void 적요가_비어_있으면_거절한다(String description) {
        assertThrows(IllegalArgumentException.class,
                () -> new Document(DATE, description, cash, loan, 10_000L));
    }

    @Test
    void 적요는_50자까지_허용한다() {
        String description = "가".repeat(50);
        Document document = new Document(DATE, description, cash, loan, 10_000L);

        assertEquals(description, document.getDescription());
        assertThrows(IllegalArgumentException.class,
                () -> new Document(DATE, description + "가", cash, loan, 10_000L));
    }

    @Test
    void 거래일자가_없거나_허용_연도_범위를_벗어나면_거절한다() {
        assertThrows(IllegalArgumentException.class,
                () -> new Document(null, "차입", cash, loan, 10_000L));
        assertThrows(IllegalArgumentException.class,
                () -> new Document(LocalDate.of(0, 1, 1), "차입", cash, loan, 10_000L));
        assertThrows(IllegalArgumentException.class,
                () -> new Document(LocalDate.of(10_000, 1, 1), "차입", cash, loan, 10_000L));
    }

    @Test
    void 차변이나_대변_계정이_없으면_거절한다() {
        assertThrows(IllegalArgumentException.class,
                () -> new Document(DATE, "차입", null, loan, 10_000L));
        assertThrows(IllegalArgumentException.class,
                () -> new Document(DATE, "차입", cash, null, 10_000L));
    }

    @Test
    void 일반_전표는_정상_상태로_생성되고_원전표번호가_없다() {
        Account cash = new Account("001", "현금", AccountClEnum.ASSET);
        Account loan = new Account("201", "차입금", AccountClEnum.LIABILITY);

        Document document = new Document(
                LocalDate.of(2026, 11, 6), "운영자금 차입", cash, loan, 100_000L
        );

        assertEquals(DocumentType.NORMAL, document.getType());
        assertEquals(DocumentStatus.ACTIVE, document.getStatus());
        assertNull(document.getOriginalDocumentNumber());
    }
}