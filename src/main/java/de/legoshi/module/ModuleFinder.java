package de.legoshi.module;

import de.legoshi.Main;
import de.legoshi.util.ClassUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class ModuleFinder {
    
    private static final String MODULE_FOLDER_NAME = "modules";
    private static File modDir;
    
    public static void init() {
        modDir = getModDir();
        if (!modDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            modDir.mkdir();
        }
    }
    
    private static File getModDir() {
        return new File("./mods");
    }
    
    public static Map<ModuleConfig, File> findAllModules() {
        HashMap<ModuleConfig, File> modules = new HashMap<>();
        registerModulesInFolder(modules, modDir);
        File moduleDir = new File(modDir, MODULE_FOLDER_NAME);
        if (moduleDir.exists()) {
            registerModulesInFolder(modules, moduleDir);
        }
        
        return modules;
    }
    
    private static void registerModulesInFolder(Map<ModuleConfig, File> modules, File folder) {
        File[] files = folder.listFiles();
        
        if (files == null) {
            return;
        }
    
        fileLoop:
        for (File file : files) {
            if (!file.getName().endsWith(".jar")) continue;
            ModuleConfig config = getConfigFromModule(file);
            if (config == null) continue;
        
            for (Map.Entry<ModuleConfig, File> e : modules.entrySet()) {
                if (e.getKey().moduleName.equals(config.moduleName)) {
                    continue fileLoop;
                }
            }
        
            modules.put(config, file);
        }
    }
    
    private static ModuleConfig getConfigFromModule(File modJar) {
        ModuleConfig config = null;
        try (JarFile jarFile = new JarFile(modJar)) {
            ZipEntry entry = jarFile.getJarEntry("module.config.json");
            if (entry == null) return null;
            try (InputStream stream = jarFile.getInputStream(entry)) {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject)jsonParser.parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
                
                config = new ModuleConfig(
                        (String) jsonObject.get("moduleName"),
                        (String) jsonObject.get("mainClass")
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return config;
    }
    
    public static ModuleImpl getAsImpl(ModuleConfig config, File modJar) throws Exception {
        Module module;
        
        URL[] jars = {modJar.toURI().toURL()};
        CustomClassLoader loader = new CustomClassLoader(config.mainClass, jars, Main.class.getClassLoader());
        Class<?> moduleClass = loader.loadClass(config.mainClass);
        module = (Module) moduleClass.newInstance();
        
        return new ModuleImpl(config.moduleName, module, loader);
    }
    
    public static class CustomClassLoader extends URLClassLoader {
        private final String packageName;
        
        public CustomClassLoader(String module, URL[] urls, ClassLoader parent) {
            super(urls, parent);
            packageName = module.substring(0, module.lastIndexOf("."));
        }
    
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try {
                return super.loadClass(name, resolve);
            } catch (ClassNotFoundException ignored) {
            }
        
            synchronized (getClassLoadingLock(name)) {
                Class<?> c = findClass(name);
                if (resolve) resolveClass(c);
                return c;
            }
        }
    }
    
}
