package org.example;

import org.example.account.AccountController;
import org.example.account.repository.AccountRepository;
import org.example.balance.BalanceController;
import org.example.balance.BalanceService;
import org.example.document.DocumentController;
import org.example.document.repository.DocumentRepository;

public class Controller {
    private final InputView inputView;
    private final OutputView outputView;
    private final AccountController accountController;
    private final DocumentController documentController;
    private final BalanceController balanceController;

    public Controller(
            InputView inputView,
            AccountRepository accountRepository,
            OutputView outputView,
            DocumentRepository documentRepository
    ) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.accountController = new AccountController(inputView, outputView, accountRepository);
        this.documentController = new DocumentController(inputView, outputView, accountRepository, documentRepository);
        this.balanceController = new BalanceController(accountRepository, documentRepository, outputView, new BalanceService());
    }

    public void run() {
        while (runMenu()) {

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
            case "1" -> accountController.registerAccount();
            case "2" -> {
                accountController.showAccounts();
                yield true;
            }
            case "3" -> documentController.registerDocument();

            case "4" -> documentController.cancelDocument();
            case "5" -> {
                documentController.showDocuments();
                yield true;
            }

            case "6" -> documentController.showDocumentDetail();
            case "7" -> {
                balanceController.showBalance();
                yield true;
            }
            default -> {
                outputView.printError("메뉴는 0부터 7까지 입력해 주세요.");
                yield true;
            }
        };
    }
}
