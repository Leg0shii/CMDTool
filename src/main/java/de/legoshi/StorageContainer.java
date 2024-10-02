package de.legoshi;

import java.util.HashMap;

public class StorageContainer {
    
    private final HashMap<String, String> storage = new HashMap<>();
    
    public void addItem(String key, String value) {
        storage.put(key, value);
    }
    
    public void removeItem(String key) {
        storage.remove(key);
    }
    
    public void clearStorage() {
        storage.clear();
    }
    
    public void printStorageContents() {
        storage.forEach((key, value) -> System.out.println(key + ": " + value));
    }
    
}
