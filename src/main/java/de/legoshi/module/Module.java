package de.legoshi.module;

public interface Module {
    
    void init();
    
    void load();
    
    boolean handleInput(String input);
    
}
