package org.example.repository;

import org.example.Account;
import org.example.Document;
import org.example.Enum.AccountClEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentRepositoryTest {

    private final DocumentRepository repository = new DocumentRepository();

    @Test
    void 저장할_때마다_전표번호를_1부터_순서대로_부여한다() {
        Account cash = new Account("001", "현금", AccountClEnum.ASSET);
        Account loan = new Account("201", "차입금", AccountClEnum.LIABILITY);

        Document first = new Document(
                LocalDate.of(2026, 11, 6), "첫 번째 차입", cash, loan, 100_000L
        );
        Document second = new Document(
                LocalDate.of(2026, 11, 7), "두 번째 차입", cash, loan, 50_000L
        );

        int firstNumber = repository.saveDocument(first);
        int secondNumber = repository.saveDocument(second);

        assertEquals(1, firstNumber);
        assertEquals(2, secondNumber);
    }
}