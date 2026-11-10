package org.example.document.repository;

import org.example.document.entity.Document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentRepository {

   private final Map<Integer, Document> documents = new HashMap<>();


    public int saveDocument(Document document){
        int number = nextDocumentNumber();
        documents.put(number, document);
        return number;
    }

    public List<Map.Entry<Integer, Document>> findAllSortedDocument() {
        return documents.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> Map.entry(entry.getKey(), entry.getValue()))
                .toList();
    }

    public int cancelDocument(int originalNumber, LocalDate cancelDate) {
        Document original = findByNumber(originalNumber);
        int reversalNumber = nextDocumentNumber();
        Document reversal = original.cancel(originalNumber, cancelDate);

        documents.put(reversalNumber, reversal);
        return reversalNumber;
    }

    public Document findByNumber(int number) {
        Document document = documents.get(number);
        if(document == null) {
            throw new IllegalArgumentException("존재하지 않는 전표번호입니다.");
        }

        return document;
    }

    private int nextDocumentNumber() {
        return documents.size() + 1;
    }

    public boolean isEmpty() {
        return documents.isEmpty();
    }

}
