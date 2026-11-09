package org.example.repository;

import org.example.Account;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountRepository {

    private final Map<String, Account> accounts = new HashMap<>();

    public int count() {
        return  accounts.size();
    }



    public void save(Account account){
        String code = account.getAccountCode();

        if(accounts.containsKey(code)) {
            throw new IllegalArgumentException("이미 등록된 계정 코드입니다.");
        }

        accounts.put(code, account);
    }

    public Account findByCode(String code) {
        Account account = accounts.get(code);

        if(account == null) {
        throw new IllegalArgumentException(
                "등록되지 않은 계정 코드입니다. (" + code + ")"
        );}
        return account;
    }

    public List<Account> findBySortedByCode() {
        return accounts.values().stream()
                .sorted(Comparator.comparing(Account::getAccountCode))
                .toList();
    }
}
