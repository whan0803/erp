package org.example.document.enums;

public enum DocumentType {
    NORMAL("일반"),
    REVERSAL(" 취소");

    private final String label;

    DocumentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
