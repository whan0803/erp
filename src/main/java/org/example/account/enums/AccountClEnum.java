package org.example.account.enums;

import org.example.document.entity.Document;

import java.util.List;

public enum AccountClEnum {
    ASSET("자산"),
    LIABILITY("부채"),
    REVENUE("수익"),
    EXPENSE("비용");

    private final String label;

    AccountClEnum(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

    public static AccountClEnum fromLabel(String label){
        for(AccountClEnum category: values()) {
            if(category.label.equals(label)) {
                return category;
            }
        }

        throw new IllegalArgumentException(
                "계정 분류는 자산, 부채, 수익, 비용 중 하나여야 합니다."
        );
    }

    public long calculatorBalance(long debitTotal, long creditTotal) {
        return switch (this) {
            case ASSET, EXPENSE -> debitTotal - creditTotal;
            case LIABILITY, REVENUE -> creditTotal - debitTotal;
        };
    }
}
