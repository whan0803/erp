package org.example.document.repository;

import org.example.account.entity.Account;
import org.example.document.entity.Document;
import org.example.account.enums.AccountClEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @Test
    void 저장된_전표가_없으면_빈_목록을_반환한다() {
        assertEquals(List.of(), repository.findAllSortedDocument());
    }

    @Test
    void 전표번호와_전표를_짝지어_번호_오름차순으로_반환한다() {
        Account cash = new Account("101", "현금", AccountClEnum.ASSET);
        Account loan = new Account("201", "차입금", AccountClEnum.LIABILITY);

        Document first = new Document(
                LocalDate.of(2026, 11, 7), "운영자금 차입", cash, loan, 100_000L
        );
        Document second = new Document(
                LocalDate.of(2026, 11, 6), "추가 차입", cash, loan, 50_000L
        );

        repository.saveDocument(first);
        repository.saveDocument(second);

        assertEquals(
                List.of(Map.entry(1, first), Map.entry(2, second)),
                repository.findAllSortedDocument()
        );
    }

}