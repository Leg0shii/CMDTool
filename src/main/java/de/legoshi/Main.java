package de.legoshi;

import de.legoshi.module.ModuleFinder;
import de.legoshi.module.ModuleManager;

public class Main {
    
    public static CMDTool cmdTool;
    public static String tim = "TIM";
    
    public static void main(String[] args) {
        StorageContainer storageContainer = new StorageContainer();
        cmdTool = new CMDTool(storageContainer);
    
        ModuleFinder.init();
        ModuleManager.initAllModules();
        
        cmdTool.load();
    }
    
}
