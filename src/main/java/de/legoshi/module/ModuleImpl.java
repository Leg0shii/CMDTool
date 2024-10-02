package de.legoshi.module;

import java.io.IOException;

public class ModuleImpl {
    
    private final String name;
    private final Module module;
    private final ModuleFinder.CustomClassLoader loader;
    
    public ModuleImpl(String name, Module module, ModuleFinder.CustomClassLoader loader) {
        this.name = name;
        this.module = module;
        this.loader = loader;
    }
    
    public void closeLoader() {
        if (loader == null) return;
        try {
            loader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getName() {
        return name;
    }
    
    public Module getModule() {
        return module;
    }
}
