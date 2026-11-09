package org.example;

import org.example.Enum.AccountClEnum;

public class Account {
    private final String accountCode;
    private final String accountName;
    private final AccountClEnum accountCl;



    public Account(String accountCode, String accountName, AccountClEnum accountCl) {

        this.accountCode = validateAccountCode(accountCode);
        this.accountName = validateAccountName(accountName);
        this.accountCl = validateAccountCl(accountCl);
    }

    private String validateAccountCode(String accountCode) {
        if(accountCode == null || !accountCode.matches("[0-9]{3}")){
            throw new IllegalArgumentException("계정 코드는 숫자 3자리여야 합니다.");
        }
        return accountCode;
    }


    private String validateAccountName(String accountName) {
        if(accountName == null) {
            throw new IllegalArgumentException("계정명을 입력해주세요.");
        }

        String trimName = accountName.trim();

        if(trimName.isEmpty() || trimName.length() > 20) {
            throw new IllegalArgumentException("계정명은 1자 이상 20자 이하여야 합니다.");
        }

        return trimName;
    }

    private AccountClEnum validateAccountCl(AccountClEnum accountCl) {
        if(accountCl == null) {
            throw new IllegalArgumentException("계정 분류를 선택해주세요");
        }
        return accountCl;
    }


    public String getAccountCode() {
        return accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public AccountClEnum getAccountCl() {
        return accountCl;
    }


}
