package org.example;

import java.util.List;

public class OutputView {

    public void printMenu() {
        System.out.print("""
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
            """);
    }

    public void printError(String message) {
        System.out.println("[ERROR] " + message);
    }


    public void printAccountPrompt() {
        System.out.println("계정과목을 입력해 주세요. (코드,계정명,분류)");
    }

    public void printAccountRegistered(String code) {
        System.out.println("계정과목이 등록되었습니다. (" + code + ")");
    }

    public void printExist() {
        System.out.println("프로그램을 종료합니다.");
    }

    public void printAccounts(List<Account> accounts) {
        System.out.println("[계정과목 목록]");

        if (accounts.isEmpty()) {
            System.out.println("등록된 계정과목이 없습니다.");
            return;
        }

        System.out.println("코드 | 계정명 | 분류");

        for (Account account : accounts) {
            System.out.println(account.getAccountCode() + " | "
                    + account.getAccountName() + " | "
                    + account.getAccountCl().getLabel());
        }
    }

    public void printDocumentPrompt() {
        System.out.println("전표를 입력해 주세요. (거래일자,적요,차변코드,대변코드,금액)");
    }

    public void printDocumentRegistered(int documentNumber) {
        System.out.println("전표가 작성되었습니다. (전표번호: " + documentNumber + ")");
    }



}
