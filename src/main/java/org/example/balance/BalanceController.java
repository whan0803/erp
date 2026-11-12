package org.example.balance;

import org.example.InputView;
import org.example.OutputView;
import org.example.account.entity.Account;
import org.example.account.enums.AccountClEnum;
import org.example.account.repository.AccountRepository;
import org.example.document.entity.Document;
import org.example.document.repository.DocumentRepository;

import java.util.List;
import java.util.Map;

public class BalanceController {
    
    private final OutputView outputView;
    private final  BalanceService balanceService;
    private final AccountRepository accountRepository;
    private final DocumentRepository documentRepository;

    public BalanceController(
                AccountRepository accountRepository,
                DocumentRepository documentRepository,
                OutputView outputView,
                BalanceService balanceService

        ){
            this.accountRepository = accountRepository;
            this.documentRepository = documentRepository;
            this.outputView = outputView;
            this.balanceService = balanceService;
        }

        public void showBalance() {
            List<Account> accounts = accountRepository.findBySortedByCode();
            List<Document> documents = documentRepository.findAllSortedDocument().stream()
                    .map(entry -> entry.getValue()).toList();

            showCategory(accounts, documents, AccountClEnum.ASSET);
            showCategory(accounts, documents, AccountClEnum.LIABILITY);

        }


        public void showCategory(
                List<Account> accounts, List<Document> documents, AccountClEnum category
        ){
            Map<Account, Long> balances = balanceService.calculateBalance(accounts, documents, category);

            long total = balanceService.calculateTotal(balances);

            outputView.printBalances(category.getLabel(), balances, total);

        }
}