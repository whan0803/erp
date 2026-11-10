package org.example.account;

import org.example.InputView;
import org.example.OutputView;
import org.example.account.entity.Account;
import org.example.account.enums.AccountClEnum;
import org.example.account.repository.AccountRepository;

public class AccountController {
    private final InputView inputView;
    private final OutputView outputView;
    private final AccountRepository accountRepository;

    public AccountController(InputView inputView, OutputView outputView, AccountRepository accountRepository) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.accountRepository = accountRepository;
    }

    public boolean registerAccount() {
        while (true) {
            outputView.printAccountPrompt();
            String line = inputView.readLine();

            if (line == null) {
                return false;
            }

            try {
                String code = saveAccount(line);
                outputView.printAccountRegistered(code);
                return true;
            } catch (IllegalArgumentException e) {
                outputView.printError(e.getMessage());
            }
        }
    }

    private String saveAccount(String line) {
        Account account = parseAccount(line);
        accountRepository.save(account);
        return  account.getAccountCode();
    }

    private static Account parseAccount(String line) {
        String[] parts = line.split(",", -1);

        if (parts.length != 3) {
            throw new IllegalArgumentException("코드, 계정명, 분류를 입력해 주세요.");
        }

        String code = parts[0].trim();
        String name = parts[1].trim();
        AccountClEnum category = AccountClEnum.fromLabel(parts[2].trim());

        return new Account(code, name, category);
    }

    public void showAccounts() {
        outputView.printAccounts(accountRepository.findBySortedByCode());
    }
}
