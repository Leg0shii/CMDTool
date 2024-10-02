package de.legoshi.module;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
    
    public static final HashMap<String, ModuleImpl> moduleMap = new HashMap<>();
    
    public static void reloadAllModules() {
        loadModules(false);
    }
    
    public static void initAllModules() {
        loadModules(true);
    }
    
    private static void loadModules(boolean init) {
        Map<ModuleConfig, File> modules = ModuleFinder.findAllModules();
        
        for (Map.Entry<ModuleConfig, File> module : modules.entrySet()) {
            try {
                registerModule(ModuleFinder.getAsImpl(module.getKey(), module.getValue()), init);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void registerModule(ModuleImpl module, boolean init) {
        ModuleImpl prev = moduleMap.put(module.getName(), module);
        if (prev != null) prev.closeLoader();
        
        try {
            if (init) {
                module.getModule().init();
            } else {
                module.getModule().load();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
