package de.legoshi;

import de.legoshi.module.Module;
import de.legoshi.module.ModuleImpl;
import de.legoshi.module.ModuleManager;

import java.util.Scanner;

public class CMDTool implements Module {
    
    public StorageContainer container;
    
    public CMDTool(StorageContainer container) {
        this.container = container;
    }
    
    @Override
    public void init() {
    
    }
    
    @Override
    public void load() {
        System.out.println("Program loaded. Please enter something :) !");
        Scanner scanner = new Scanner(System.in);
        String input = "";
        
        while (handleInput(input)) {
            input = scanner.nextLine();
        }
        
        System.out.println("Program stopped o: !");
    }
    
    @Override
    public boolean handleInput(String input) {
        if (input.equals("reload")) {
            ModuleManager.reloadAllModules();
            System.out.println("Reloaded all modules!\n");
        }
        
        if (input.equals("stop")) {
            return false;
        }
        
        for (ModuleImpl moduleImpl : ModuleManager.moduleMap.values()) {
            return moduleImpl.getModule().handleInput(input);
        }
        
        return true;
    }
    
    public StorageContainer getStorage() {
        return container;
    }
    
}