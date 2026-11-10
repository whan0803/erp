package org.example;

import org.example.account.repository.AccountRepository;
import org.example.document.repository.DocumentRepository;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControllerTest {

    @Test
    void 계정을_등록하면_메뉴로_돌아가고_0으로_종료한다() {
        String output = runWithInput("1\n001,현금,자산\n0\n");

        assertTrue(output.contains("계정과목이 등록되었습니다. (001)"), output);
        assertEquals(2L, countMenus(output), output);
        assertFalse(output.contains("[ERROR]"), output);
        assertTrue(output.stripTrailing().endsWith("프로그램을 종료합니다."), output);
    }

    @Test
    void 잘못된_메뉴를_입력하면_오류를_출력하고_메뉴를_다시_보여준다() {
        String output = runWithInput("9\n0\n");

        assertTrue(output.contains("[ERROR]"), output);
        assertEquals(2L, countMenus(output), output);
    }

    @Test
    void 계정_코드가_중복되면_같은_등록_입력을_다시_받는다() {
        String output = runWithInput(
                "1\n001,현금,자산\n1\n001,예금,자산\n002,예금,자산\n0\n"
        );

        assertEquals(1L, output.lines()
                .filter("계정과목이 등록되었습니다. (001)"::equals)
                .count(), output);

        int errorIndex = output.indexOf("[ERROR] 이미 등록된 계정 코드입니다.");
        int successIndex = output.indexOf("계정과목이 등록되었습니다. (002)");

        assertTrue(errorIndex >= 0, output);
        assertTrue(successIndex > errorIndex, output);

        String retryOutput = output.substring(errorIndex, successIndex);
        assertTrue(retryOutput.contains(
                "계정과목을 입력해 주세요. (코드,계정명,분류)"
        ), output);
        assertFalse(retryOutput.contains("[회계 전표 관리]"), output);
    }

    @Test
    void 계정_등록_중에_입력이_끝나면_등록하지_않고_종료한다() {
        String output = runWithInput("1\n");

        assertTrue(output.contains("계정과목을 입력해 주세요."), output);
        assertFalse(output.contains("계정과목이 등록되었습니다."), output);
        assertTrue(output.stripTrailing().endsWith("프로그램을 종료합니다."), output);
    }

    private long countMenus(String output) {
        return output.lines()
                .filter("[회계 전표 관리]"::equals)
                .count();
    }

    private String runWithInput(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (Scanner scanner = new Scanner(input);
             PrintStream capturedOut = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);

            Controller controller = new Controller(
                    new InputView(scanner),
                    new AccountRepository(),
                    new OutputView(),
                    new DocumentRepository()
            );
            controller.run();

            return buffer.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void 등록된_계정이_없으면_안내만_출력한다() {
        String output = runWithInput("2\n0\n");
        String normalizedOutput = String.join("\n", output.lines().toList());

        assertTrue(normalizedOutput.contains("""
            [계정과목 목록]
            등록된 계정과목이 없습니다.
            """), output);
        assertFalse(output.contains("코드 | 계정명 | 분류"), output);
        assertFalse(output.contains("[ERROR]"), output);
    }

    @Test
    void 계정과목을_등록_순서와_관계없이_코드_오름차순으로_조회한다() {
        String output = runWithInput(
                "1\n201,차입금,부채\n1\n001,현금,자산\n2\n0\n"
        );
        String normalizedOutput = String.join("\n", output.lines().toList());

        assertTrue(normalizedOutput.contains("""
            [계정과목 목록]
            코드 | 계정명 | 분류
            001 | 현금 | 자산
            201 | 차입금 | 부채
            """), output);
        assertFalse(output.contains("[ERROR]"), output);
    }

    @Test
    void 계정이_하나면_전표_입력을_받지_않고_메뉴로_돌아간다() {
        String output = runWithInput("1\n001,현금,자산\n3\n0\n");

        assertTrue(output.contains(
                "[ERROR] 전표 작성에는 계정과목이 2개 이상 필요합니다."
        ), output);
        assertFalse(output.contains("전표를 입력해 주세요."), output);
        assertEquals(3L, countMenus(output), output);
        assertTrue(output.stripTrailing().endsWith("프로그램을 종료합니다."), output);
    }

    @Test
    void 계정이_정확히_두_개면_전표를_작성하고_메뉴로_돌아간다() {
        String output = runWithInput("""
            1
            001,현금,자산
            1
            201,차입금,부채
            3
            2026-11-06,운영자금 차입,001,201,100000
            0
            """);

        assertTrue(output.contains(
                "전표를 입력해 주세요. (거래일자,적요,차변코드,대변코드,금액)"
        ), output);
        assertTrue(output.contains("전표가 작성되었습니다. (전표번호: 1)"), output);
        assertFalse(output.contains("[ERROR]"), output);
        assertEquals(4L, countMenus(output), output);
        assertTrue(output.stripTrailing().endsWith("프로그램을 종료합니다."), output);
    }

    @Test
    void 전표가_없으면_목록_제목과_안내만_출력한다() {
        String output = runWithInput("5\n0\n");
        String normalized = output.replace("\r\n", "\n");

        assertTrue(normalized.contains("""
            [전표 목록]
            등록된 전표가 없습니다.
            """), output);
        assertFalse(output.contains("번호 | 거래일자 | 구분 | 상태 | 원전표 | 금액 | 적요"), output);
        assertFalse(output.contains("[ERROR]"), output);
    }

    @Test
    void 전표_목록은_번호순으로_일반_전표의_기본값을_출력한다() {
        String output = runWithInput("""
            1
            101,현금,자산
            1
            201,차입금,부채
            3
            2026-11-07,운영자금 차입,101,201,100000
            3
            2026-11-06,추가 차입,101,201,50000
            5
            0
            """);
        String normalized = output.replace("\r\n", "\n");

        assertTrue(normalized.contains("""
                [전표 목록]
                번호 | 거래일자 | 구분 | 상태 | 원전표 | 금액 | 적요
                1 | 2026-11-07 | 일반 | 정상 | - | 100,000원 | 운영자금 차입
                2 | 2026-11-06 | 일반 | 정상 | - | 50,000원 | 추가 차입
                """), output);
        assertFalse(output.contains("[ERROR]"), output);
    }

    @Test
    void 전표가_없으면_취소_입력을_받지_않고_메뉴로_돌아간다() {
        String output = runWithInput("4\n0\n");

        assertTrue(output.contains("[ERROR] 등록된 전표가 없습니다."), output);
        assertFalse(output.contains("취소 정보를 입력해 주세요."), output);
        assertEquals(2L, countMenus(output), output);
    }

    @Test
    void 잘못된_취소_번호를_다시_입력받고_취소_전표를_목록에_표시한다() {
        String output = runWithInput("""
                1
                101,현금,자산
                1
                201,차입금,부채
                3
                2026-11-06,운영자금 차입,101,201,100000
                4
                +1,2026-11-07
                01,2026-11-07
                2147483648,2026-11-07
                1,2026-11-07
                5
                0
                """);

        assertEquals(3L, output.lines().filter(line -> line.startsWith("[ERROR]")).count(), output);
        assertEquals(4L, output.lines().filter(line -> line.startsWith("취소 정보를 입력해 주세요.")).count(),
                output);
        assertEquals(6L, countMenus(output), output);
        assertTrue(output.contains("전표가 취소되었습니다. (원전표번호: 1, 취소전표번호: 2)"), output);
        assertTrue(output.replace("\r\n", "\n").contains("""
                [전표 목록]
                번호 | 거래일자 | 구분 | 상태 | 원전표 | 금액 | 적요
                1 | 2026-11-06 | 일반 | 취소 | - | 100,000원 | 운영자금 차입
                2 | 2026-11-07 | 취소 | 정상 | 1 | 100,000원 | 운영자금 차입
                """), output);
    }
}
