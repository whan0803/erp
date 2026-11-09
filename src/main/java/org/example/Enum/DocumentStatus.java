package org.example.Enum;

public enum DocumentStatus {
    ACTIVE("정상"),
    CANCELED("취소");

    private final String label;

    DocumentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
