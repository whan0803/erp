package org.example.document.entity;

import org.example.account.entity.Account;
import org.example.document.enums.DocumentStatus;
import org.example.document.enums.DocumentType;

import java.time.LocalDate;

public class Document {
    private final LocalDate transactionDate;
    private final String description;
    private final Account debitAccount;
    private final Account creditAccount;
    private final long amount;
    private final Integer originalDocumentNumber;
    private final DocumentType type;
    private final DocumentStatus status;
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
        this.originalDocumentNumber = null;
        this.type = DocumentType.NORMAL;
        this.status = DocumentStatus.ACTIVE;
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

        description = description.trim();
        if (description.isEmpty() || description.length() > 50) {
            throw new IllegalArgumentException("적요는 1자 이상 50자 이하여야 합니다.");
        }

        return description;
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

    public Integer getOriginalDocumentNumber() {
        return originalDocumentNumber;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public DocumentType getType() {
        return type;
    }
}