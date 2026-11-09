package org.example.repository;

import org.example.entity.Document;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentRepository {

   private final Map<Integer, Document> documents = new HashMap<>();


    public int saveDocument(Document document){
        int documentNumber = documents.size() + 1;
        documents.put(documentNumber, document);
        return documentNumber;
    }

    public List<Map.Entry<Integer, Document>> findAllSortedDocument() {
        return documents.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> Map.entry(entry.getKey(), entry.getValue()))
                .toList();
    }
}
