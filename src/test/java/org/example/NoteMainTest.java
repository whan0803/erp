package org.example;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InputViewTest {

    @Test
    void 입력한_한_줄을_반환한다() {
        InputView inputView = new InputView(new Scanner("1\n"));

        String result = inputView.readLine();

        assertEquals("1", result);
    }

    @Test
    void 입력이_종료되면_null을_반환한다() {
        InputView inputView = new InputView(new Scanner(""));

        String result = inputView.readLine();
        assertNull(result);
    }
}