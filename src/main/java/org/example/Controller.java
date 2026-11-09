package org.example;


import org.example.entity.Account;
import org.example.entity.Document;
import org.example.Enum.AccountClEnum;
import org.example.repository.AccountRepository;
import org.example.repository.DocumentRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Controller {
    private final InputView inputView;
    private final OutputView outputView;
    private final AccountRepository accountRepository;
    private final DocumentRepository documentRepository;

    public Controller(
            InputView inputView,
            AccountRepository accountRepository,
            OutputView outputView,
            DocumentRepository documentRepository
    ){
        this.inputView = inputView;
        this.accountRepository = accountRepository;
        this.outputView = outputView;
        this.documentRepository = documentRepository;
    }

    public void run() {
        while ((runMenu())) {

        }
        outputView.printExist();
    }

    public boolean runMenu() {
        outputView.printMenu();
        String line = inputView.readLine();

        if (line == null || line.trim().equals("0")) {
            return false;
        }

        return switch (line.trim()) {
            case "1" -> registerAccount();
            case "2" -> {
                outputView.printAccounts(accountRepository.findBySortedByCode());
                yield true;
            }
            case "3" -> registerDocument();
            case "5" -> {
                outputView.printDocuments(documentRepository.findAllSortedDocument());
                yield true;
            }
            case "4", "6", "7" -> {
                outputView.printError("아직 구현하지 않은 메뉴입니다.");
                yield true;
            }
            default -> {
                outputView.printError("메뉴는 0부터 7까지 입력해 주세요.");
                yield true;
            }
        };
    }


    private boolean registerAccount() {
        while (true) {
            outputView.printAccountPrompt();
            String line = inputView.readLine();

            if(line == null) {
                return false;
            }

            try{
                saveAccount(line);
                return true;
            } catch (IllegalArgumentException e) {
                outputView.printError(e.getMessage());
            }
        }
    }

    private void saveAccount(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("코드, 계정명, 분류를 입력해 주세요.");
        }

        String code = parts[0].trim();
        String name = parts[1].trim();
        AccountClEnum category = AccountClEnum.fromLabel(parts[2].trim());

        Account account = new Account(code, name, category);
        accountRepository.save(account);
        outputView.printAccountRegistered(account.getAccountCode());
    }

    public boolean registerDocument() {
        if(accountRepository.count() < 2) {
            outputView.printError("전표 작성에는 계정과목이 2개 이상 필요합니다.");
            return true;
        }
        while (true) {
            outputView.printDocumentPrompt();
            String line =  inputView.readLine();

            if(line == null) {
                return false;
            }

            try{
                Document document = saveDocument(line);
                int documentNumber = documentRepository.saveDocument(document);
                outputView.printDocumentRegistered(documentNumber);
                return true;

            } catch (IllegalArgumentException e) {
                outputView.printError(e.getMessage());
            }
        }
    }

    public Document saveDocument(String line) {
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
