package org.example.balance;


import org.example.account.entity.Account;
import org.example.account.enums.AccountClEnum;
import org.example.document.entity.Document;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BalanceService {
    public long calculateBalance(Account account, List<Document> documents) {
        long debitTotal = 0L;
        long creditTotal = 0L;
        String code = account.getAccountCode();

        for (Document document: documents) {
            if(code.equals(document.getDebitAccount().getAccountCode())) {
                debitTotal += document.getAmount();
            }
            if(code.equals(document.getCreditAccount().getAccountCode())) {
                creditTotal += document.getAmount();
            }
        }

        return account.getAccountCl().calculatorBalance(debitTotal, creditTotal);
    }

    public Map<Account, Long> calculateBalance(
            List<Account> accounts, List<Document> documents, AccountClEnum category
    ) {
        Map<Account, Long> balances = new LinkedHashMap<>();

        for(Account account: accounts) {
            if(account.getAccountCl() == category) {
                balances.put(account, calculateBalance(account, documents));
            }
        }

        return balances;
    }

    public long calculateTotal(Map<Account, Long> balances) {
        return balances.values().stream().mapToLong(Long::longValue).sum();
    }
}
