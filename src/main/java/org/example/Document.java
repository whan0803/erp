package org.example;

import java.time.LocalDate;

public class Document {
    private final LocalDate transactionDate;
    private final String description;
    private final Account debitAccount;
    private final Account creditAccount;
    private final long amount;

    public Document(
            LocalDate transactionDate,
            String description,
            Account debitAccount,
            Account creditAccount,
            long amount
    ) {
        validateTransaction(transactionDate, debitAccount, creditAccount, amount);

        this.transactionDate = transactionDate;
        this.description = validateDescription(description);
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
        this.amount = amount;
    }

    private void validateTransaction(
            LocalDate transactionDate,
            Account debitAccount,
            Account creditAccount,
            long amount
    ) {
        if (transactionDate == null
                || transactionDate.getYear() < 1
                || transactionDate.getYear() > 9999) {
            throw new IllegalArgumentException(
                    "거래일자는 0001년부터 9999년 사이여야 합니다."
            );
        }

        if (debitAccount == null || creditAccount == null) {
            throw new IllegalArgumentException("차변과 대변 계정을 모두 선택해야 합니다.");
        }

        if (debitAccount.getAccountCode().equals(creditAccount.getAccountCode())) {
            throw new IllegalArgumentException("차변과 대변에 같은 계정을 사용할 수 없습니다.");
        }

        if (amount < 1 || amount > 1_000_000_000L) {
            throw new IllegalArgumentException("금액은 1원 이상 1,000,000,000원 이하여야 합니다.");
        }
    }

    private String validateDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("적요를 입력해 주세요.");
        }

        String trimmedDescription = description.trim();
        if (trimmedDescription.isEmpty() || trimmedDescription.length() > 50) {
            throw new IllegalArgumentException("적요는 1자 이상 50자 이하여야 합니다.");
        }

        return trimmedDescription;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public Account getDebitAccount() {
        return debitAccount;
    }

    public Account getCreditAccount() {
        return creditAccount;
    }

    public long getAmount() {
        return amount;
    }
}