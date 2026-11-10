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
    private final DocumentType type;
    private DocumentStatus status;
    private final int originalDocumentNumber;

    public Document(
            LocalDate transactionDate, String description,
            Account debitAccount, Account creditAccount, long amount
    ) {
        this(transactionDate, description, debitAccount, creditAccount, amount,
                DocumentType.NORMAL, DocumentStatus.ACTIVE, 0);
    }

    private Document(
            LocalDate transactionDate, String description,
            Account debitAccount, Account creditAccount, long amount,
            DocumentType type, DocumentStatus status, int originalDocumentNumber
    ) {
        validateTransaction(transactionDate, debitAccount, creditAccount, amount);

        this.transactionDate = transactionDate;
        this.description = validateDescription(description);
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.originalDocumentNumber = originalDocumentNumber;
    }

    public Document cancel(int originalNumber, LocalDate cancelDate) {
        if (type != DocumentType.NORMAL || status != DocumentStatus.ACTIVE) {
            throw new IllegalArgumentException("정상 상태의 일반 전표만 취소할 수 있습니다.");
        }

        if (originalNumber < 1) {
            throw new IllegalArgumentException("원전표번호는 1 이상이어야 합니다.");
        }

        if (cancelDate == null || cancelDate.isBefore(transactionDate)) {
            throw new IllegalArgumentException("취소일자는 원전표 거래일자와 같거나 이후여야 합니다.");
        }

        Document reversal = new Document(
                cancelDate, description, creditAccount, debitAccount, amount,
                DocumentType.REVERSAL, DocumentStatus.ACTIVE, originalNumber
        );

        this.status = DocumentStatus.CANCELED;
        return reversal;
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

    public int getOriginalDocumentNumber() {
        return originalDocumentNumber;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public DocumentType getType() {
        return type;
    }

}