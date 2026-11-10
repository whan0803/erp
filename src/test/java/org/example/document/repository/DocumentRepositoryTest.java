package org.example.document.repository;

import org.example.account.entity.Account;
import org.example.document.entity.Document;
import org.example.account.enums.AccountClEnum;
import org.example.document.enums.DocumentStatus;
import org.example.document.enums.DocumentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void 취소하면_원전표를_보존하고_차대변을_뒤집은_전표를_저장한다() {
        Document original = newLoan();
        LocalDate cancelDate = original.getTransactionDate().plusDays(1);
        int originalNumber = repository.saveDocument(original);

        int reversalNumber = repository.cancelDocument(originalNumber, cancelDate);

        List<Map.Entry<Integer, Document>> documents = repository.findAllSortedDocument();
        assertEquals(2, reversalNumber);
        assertEquals(List.of(originalNumber, reversalNumber),
                documents.stream().map(Map.Entry::getKey).toList());
        assertSame(original, documents.get(0).getValue());
        assertEquals(DocumentType.NORMAL, original.getType());
        assertEquals(DocumentStatus.CANCELED, original.getStatus());
        assertEquals(0, original.getOriginalDocumentNumber());

        Document reversal = documents.get(1).getValue();
        assertEquals(DocumentType.REVERSAL, reversal.getType());
        assertEquals(DocumentStatus.ACTIVE, reversal.getStatus());
        assertEquals(originalNumber, reversal.getOriginalDocumentNumber());
        assertEquals(cancelDate, reversal.getTransactionDate());
        assertEquals(original.getDescription(), reversal.getDescription());
        assertEquals(original.getAmount(), reversal.getAmount());
        assertSame(original.getCreditAccount(), reversal.getDebitAccount());
        assertSame(original.getDebitAccount(), reversal.getCreditAccount());
    }

    @Test
    void 없는_번호나_거래일보다_이른_취소일은_상태와_번호를_바꾸지_않는다() {
        Document original = newLoan();
        int number = repository.saveDocument(original);
        LocalDate date = original.getTransactionDate();

        assertThrows(IllegalArgumentException.class, () -> repository.cancelDocument(99, date));
        assertThrows(IllegalArgumentException.class,
                () -> repository.cancelDocument(number, date.minusDays(1)));

        assertEquals(DocumentStatus.ACTIVE, original.getStatus());
        assertEquals(List.of(Map.entry(number, original)), repository.findAllSortedDocument());
        assertEquals(2, repository.cancelDocument(number, date));
    }

    @Test
    void 이미_취소된_원전표와_취소_전표는_취소할_수_없다() {
        Document original = newLoan();
        LocalDate date = original.getTransactionDate();
        int number = repository.saveDocument(original);
        int reversalNumber = repository.cancelDocument(number, date);

        assertThrows(IllegalArgumentException.class, () -> repository.cancelDocument(number, date));
        assertThrows(IllegalArgumentException.class, () -> repository.cancelDocument(reversalNumber, date));

        List<Map.Entry<Integer, Document>> documents = repository.findAllSortedDocument();
        assertEquals(2, documents.size());
        assertEquals(DocumentStatus.CANCELED, original.getStatus());
        assertEquals(DocumentStatus.ACTIVE, documents.get(1).getValue().getStatus());
        assertEquals(3, repository.saveDocument(newLoan()));
    }

    private Document newLoan() {
        Account cash = new Account("101", "현금", AccountClEnum.ASSET);
        Account loan = new Account("201", "차입금", AccountClEnum.LIABILITY);
        return new Document(LocalDate.of(2026, 11, 6), "운영자금 차입", cash, loan, 100_000L);
    }
}
