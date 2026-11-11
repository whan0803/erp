package org.example.balance;

import org.example.Controller;
import org.example.InputView;
import org.example.OutputView;
import org.example.account.repository.AccountRepository;
import org.example.document.repository.DocumentRepository;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class BalanceControllerTest {
    @Test
    void 메뉴_7번에서_빈_분류와_0원_합계를_출력한다() {
        String output = runWithInput("7\n0\n");
        assertTrue(output.contains("""
                [자산 현황]
                등록된 자산 계정이 없습니다.
                자산 합계: 0원
                [부채 현황]
                등록된 부채 계정이 없습니다.
                부채 합계: 0원
                """), output);
        assertFalse(output.contains("[ERROR]"), output);
        assertTrue(output.stripTrailing().endsWith("프로그램을 종료합니다."), output);
    }

    @Test
    void 거래_없는_계정을_포함하여_코드순으로_출력하고_수익비용은_제외한다() {
        String output = runWithInput("""
                1
                201,차입금,부채
                1
                102,예금,자산
                1
                101,현금,자산
                1
                401,용역수익,수익
                1
                501,소모품비,비용
                3
                2026-11-06,차입,101,201,100000
                3
                2026-11-06,지출,501,101,10000
                3
                2026-11-06,수익,101,401,30000
                7
                0
                """);
        assertTrue(output.contains("""
                [자산 현황]
                101 현금 | 120,000원
                102 예금 | 0원
                자산 합계: 120,000원
                [부채 현황]
                201 차입금 | 100,000원
                부채 합계: 100,000원
                """), output);
        assertFalse(output.contains("[ERROR]"), output);
        assertFalse(output.contains("401 용역수익 |"), output);
        assertFalse(output.contains("501 소모품비 |"), output);
    }

    @Test
    void 미래일자와_음수_잔액을_그대로_집계한다() {
        String output = runWithInput("""
                1
                101,현금,자산
                1
                201,차입금,부채
                3
                9999-12-31,상환,201,101,10000
                7
                0
                """);
        assertTrue(output.contains("""
                [자산 현황]
                101 현금 | -10,000원
                자산 합계: -10,000원
                [부채 현황]
                201 차입금 | -10,000원
                부채 합계: -10,000원
                """), output);
        assertFalse(output.contains("[ERROR]"), output);
    }

    private String runWithInput(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try (Scanner scanner = new Scanner(input);
             PrintStream capturedOut = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            new Controller(new InputView(scanner), new AccountRepository(),
                    new OutputView(), new DocumentRepository()).run();
            return buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        } finally {
            System.setOut(originalOut);
        }
    }
}
