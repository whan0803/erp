package org.example;


import org.example.Enum.AccountClEnum;
import org.example.repository.AccountRepository;


public class Controller {
    private final InputView inputView;
    private final OutputView outputView;
    private final AccountRepository accountRepository;

    public Controller(
            InputView inputView,
            AccountRepository accountRepository,
            OutputView outputView
    ){
        this.inputView = inputView;
        this.accountRepository = accountRepository;
        this.outputView = outputView;
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

            
            case "2", "3", "4", "5", "6", "7" -> {
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






}
