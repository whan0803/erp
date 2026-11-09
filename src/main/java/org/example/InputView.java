package org.example;

import java.util.Scanner;

public class InputView {
    private final Scanner scanner;

    public InputView(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine() {
        if(!scanner.hasNextLine()) {
                return null;
        }

        return scanner.nextLine();
    }

}
