package org.example.document;

import org.example.InputView;
import org.example.OutputView;
import org.example.account.entity.Account;
import org.example.account.repository.AccountRepository;
import org.example.document.entity.Document;
import org.example.document.record.CancellationRequest;
import org.example.document.repository.DocumentRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DocumentController {
    private final InputView inputView;
    private final OutputView outputView;
    private final AccountRepository accountRepository;
    private final DocumentRepository documentRepository;

    public DocumentController(
            InputView inputView,
            OutputView outputView,
            AccountRepository accountRepository,
            DocumentRepository documentRepository
    ) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.accountRepository = accountRepository;
        this.documentRepository = documentRepository;
    }

    public boolean registerDocument() {
        if (accountRepository.count() < 2) {
            outputView.printError("전표 작성에는 계정과목이 2개 이상 필요합니다.");
            return true;
        }
        while (true) {
            outputView.printDocumentPrompt();
            String line = inputView.readLine();

            if (line == null) {
                return false;
            }

            try {
                Document document = parseDocument(line);
                int documentNumber = documentRepository.saveDocument(document);
                outputView.printDocumentRegistered(documentNumber);
                return true;

            } catch (IllegalArgumentException e) {
                outputView.printError(e.getMessage());
            }
        }
    }

    private Document parseDocument(String line) {
        String[] parts = line.split(",", -1);

        if (parts.length != 5) {
            throw new IllegalArgumentException("거래일자, 적요, 차변코드, 대변코드, 금액을 입력해 주세요.");
        }

        LocalDate date = parseTransactionDate(parts[0].trim());
        String description = parts[1].trim();
        Account debit = accountRepository.findByCode(parts[2].trim());
        Account credit = accountRepository.findByCode(parts[3].trim());
        long amount = parseAmount(parts[4].trim());


        return new Document(date, description, debit, credit, amount);

    }

    public void showDocuments() {
        outputView.printDocuments(documentRepository.findAllSortedDocument());
    }


    public boolean cancelDocument() {
        if (documentRepository.findAllSortedDocument().isEmpty()) {
            outputView.printError("등록된 전표가 없습니다.");
            return true;
        }

        while (true) {
            outputView.printCancellationPrompt();
            String line = inputView.readLine();

            if (line == null) {
                return false;
            }

            try {
                CancellationRequest request = parseCancellationRequest(line);
                int reversalNumber = cancelDocument(request);
                outputView.printDocumentCanceled(request.originalNumber(), reversalNumber);
                return true;
            } catch (IllegalArgumentException e) {
                outputView.printError(e.getMessage());
            }
        }
    }


    private int cancelDocument(CancellationRequest request) {
        return documentRepository.cancelDocument(
                request.originalNumber(), request.cancelDate()
        );
    }

    public boolean showDocumentDetail() {
        if (documentRepository.findAllSortedDocument().isEmpty()) {
            outputView.printError("등록된 전표가 없습니다.");
            return true;
        }

        while (true) {
            outputView.printDocumentDetailPrompt();
            String line = inputView.readLine();

            if (line == null) {
                return false;
            }

            try {
                int number = parseDocumentNumber(line.trim());
                Document document = documentRepository.findByNumber(number);
                outputView.printDocumentDetail(number, document);
                return true;
            } catch (IllegalArgumentException e) {
                outputView.printError(e.getMessage());
            }
        }
    }

    private CancellationRequest parseCancellationRequest(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("원전표번호와 취소일자를 입력해 주세요.");
        }

        int originalNumber = parseDocumentNumber(parts[0].trim());
        LocalDate cancelDate = parseTransactionDate(parts[1].trim());
        return new CancellationRequest(originalNumber, cancelDate);
    }

    private int parseDocumentNumber(String number) {
        String message = "전표번호는 1부터 2,147,483,647까지 입력해 주세요.";
        if (!number.matches("[1-9][0-9]{0,9}")) {
            throw new IllegalArgumentException(message);
        }

        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(message, e);
        }
    }

    private LocalDate parseTransactionDate(String value) {
        if (!value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            throw new IllegalArgumentException("거래일자는 yyyy-MM-dd 형식으로 입력해 주세요.");
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("존재하지 않는 거래일자입니다.", e);
        }
    }

    private long parseAmount(String value) {
        if (!value.matches("[1-9][0-9]{0,9}")) {
            throw new IllegalArgumentException(
                    "금액은 앞자리 0 없이 1원 이상 1,000,000,000원 이하로 입력해 주세요."
            );
        }

        return Long.parseLong(value);
    }
}
