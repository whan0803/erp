package org.example.repository;

import org.example.Account;

import java.util.HashMap;
import java.util.Map;

public class AccountRepository {

    private final Map<String, Account> accounts = new HashMap<>();




    public void save(Account account){
        String code = account.getAccountCode();

        if(accounts.containsKey(code)) {
            throw new IllegalArgumentException("이미 등록된 계정 코드입니다.");
        }

        accounts.put(code, account);
    }


}
