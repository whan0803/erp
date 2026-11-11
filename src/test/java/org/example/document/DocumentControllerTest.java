package org.example.document;

import org.example.InputView;
import org.example.OutputView;
import org.example.account.entity.Account;
import org.example.account.enums.AccountClEnum;
import org.example.account.repository.AccountRepository;
import org.example.document.entity.Document;
import org.example.document.enums.DocumentStatus;
import org.example.document.enums.DocumentType;
import org.example.document.repository.DocumentRepository;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class DocumentControllerTest {

    @Test
    void 취소_요청을_저장소에_전달하고_성공_결과를_출력한다() {
        DocumentRepository repository = new DocumentRepository();
        Document original = newLoan();
        repository.saveDocument(original);
        DocumentController controller = newController("1,2026-11-07\n", repository);

        String output = captureOutput(() -> assertTrue(controller.cancelDocument()));

        assertEquals(DocumentStatus.CANCELED, original.getStatus());
        Document reversal = repository.findByNumber(2);
        assertEquals(DocumentType.REVERSAL, reversal.getType());
        assertEquals(DocumentStatus.ACTIVE, reversal.getStatus());
        assertEquals(1, reversal.getOriginalDocumentNumber());
        assertEquals(LocalDate.of(2026, 11, 7), reversal.getTransactionDate());
        assertEquals(original.getDescription(), reversal.getDescription());
        assertEquals(original.getAmount(), reversal.getAmount());
        assertSame(original.getCreditAccount(), reversal.getDebitAccount());
        assertSame(original.getDebitAccount(), reversal.getCreditAccount());
        assertTrue(output.contains(
                "전표가 취소되었습니다. (원전표번호: 1, 취소전표번호: 2)"), output);
        assertFalse(output.contains("[ERROR]"), output);
    }

    @Test
    void 취소일이_이르면_다시_입력받고_성공한_취소_전표만_저장한다() {
        DocumentRepository repository = new DocumentRepository();
        Document original = newLoan();
        repository.saveDocument(original);
        DocumentController controller = newController(
                "1,2026-11-05\n1,2026-11-07\n", repository);

        String output = captureOutput(() -> assertTrue(controller.cancelDocument()));

        assertEquals(1L, output.lines().filter(line -> line.startsWith("[ERROR]")).count());
        assertEquals(2L, output.lines()
                .filter("취소 정보를 입력해 주세요. (원전표번호,취소일자)"::equals).count());
        assertEquals(2, repository.findAllSortedDocument().size());
        assertEquals(DocumentStatus.CANCELED, original.getStatus());
        assertEquals(1, repository.findByNumber(2).getOriginalDocumentNumber());
        assertTrue(output.contains("취소전표번호: 2"), output);
    }

    private Document newLoan() {
        Account cash = new Account("101", "현금", AccountClEnum.ASSET);
        Account loan = new Account("201", "차입금", AccountClEnum.LIABILITY);
        return new Document(LocalDate.of(2026, 11, 6),
                "운영자금 차입", cash, loan, 100_000L);
    }

    private DocumentController newController(String input, DocumentRepository repository) {
        return new DocumentController(new InputView(new Scanner(input)),
                new OutputView(), new AccountRepository(), repository);
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (PrintStream capturedOut = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            action.run();
            return buffer.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOut);
        }
    }
}

