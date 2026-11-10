package org.example.document.repository;

import org.example.document.entity.Document;

import javax.print.Doc;
import java.time.LocalDate;
import java.util.Comparator;
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
        Document original = documents.get(originalNumber);

        if(original == null) {
            throw new IllegalArgumentException("존재하지 않는 전표번호입니다.");
        }
        int reversalNumber = nextDocumentNumber();
        Document reversal = original.cancel(originalNumber, cancelDate);

        documents.put(reversalNumber, reversal);
        return reversalNumber;
    }

    private int nextDocumentNumber() {
        return documents.size() + 1;
    }

}
