package org.example;

import org.example.repository.AccountRepository;

import java.util.Scanner;

public class NoteMain {
    public static void main(String[] args) {
        InputView inputView = new InputView(new Scanner(System.in));
        AccountRepository accountRepository = new AccountRepository();
        OutputView outputView = new OutputView();

        Controller controller = new Controller(
                inputView, accountRepository, outputView
        );
        controller.run();
    }
}