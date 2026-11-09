package org.example;

import org.example.Enum.AccountClEnum;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputViewTest {

    private final OutputView outputView = new OutputView();

    @Test
    void 메뉴를_정해진_순서와_형식으로_출력한다() {
        String output = captureOutput(outputView::printMenu);

        assertEquals("""
                [회계 전표 관리]
                1. 계정과목 등록
                2. 계정과목 조회
                3. 전표 작성
                4. 전표 취소
                5. 전표 목록 조회
                6. 전표 상세 조회
                7. 자산·부채 현황 조회
                0. 종료
                메뉴를 선택해 주세요.
                """.stripTrailing(), output);
    }

    @Test
    void 오류_메시지_앞에_ERROR를_붙인다() {
        String output = captureOutput(
                () -> outputView.printError("이미 등록된 계정 코드입니다.")
        );

        assertEquals("[ERROR] 이미 등록된 계정 코드입니다.", output);
    }

    @Test
    void 계정과목_등록_입력을_안내한다() {
        String output = captureOutput(outputView::printAccountPrompt);

        assertEquals("계정과목을 입력해 주세요. (코드,계정명,분류)", output);
    }

    @Test
    void 등록_성공_메시지에_계정_코드를_표시한다() {
        String output = captureOutput(
                () -> outputView.printAccountRegistered("001")
        );

        assertEquals("계정과목이 등록되었습니다. (001)", output);
    }

    @Test
    void 종료_메시지를_출력한다() {
        String output = captureOutput(outputView::printExist);

        assertEquals("프로그램을 종료합니다.", output);
    }

    @Test
    void 계정이_없으면_제목과_안내만_출력한다() {
        String output = captureOutput(() -> outputView.printAccounts(List.of()));

        assertEquals("""
                [계정과목 목록]
                등록된 계정과목이 없습니다.
                """.stripTrailing(), output);
    }

    @Test
    void 계정_목록에_코드와_이름과_한글_분류를_출력한다() {
        List<Account> accounts = List.of(
                new Account("001", "현금", AccountClEnum.ASSET),
                new Account("201", "차입금", AccountClEnum.LIABILITY)
        );

        String output = captureOutput(() -> outputView.printAccounts(accounts));

        assertEquals("""
                [계정과목 목록]
                코드 | 계정명 | 분류
                001 | 현금 | 자산
                201 | 차입금 | 부채
                """.stripTrailing(), output);
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try (PrintStream capturedOut =
                     new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            action.run();

            return buffer.toString(StandardCharsets.UTF_8)
                    .replace("\r\n", "\n")
                    .stripTrailing();
        } finally {
            System.setOut(originalOut);
        }
    }
}