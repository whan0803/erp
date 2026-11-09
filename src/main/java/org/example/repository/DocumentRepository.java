package org.example.repository;

import org.example.Account;
import org.example.Document;

import java.util.HashMap;
import java.util.Map;

public class DocumentRepository {

   private final Map<Integer, Document> documents = new HashMap<>();


    public int saveDocument(Document document){
        int documentNumber = documents.size() + 1;
        documents.put(documentNumber, document);
        return documentNumber;
    }
}
