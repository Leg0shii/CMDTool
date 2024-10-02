package de.legoshi.util;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClassUtil {
    public static Class<?> ModClass = null;

    private static Set<Class<?>> classes = null;
    private static File modJarFile = null;

    public static File getJarFile() {
        if(modJarFile != null) return modJarFile;
        try {
            modJarFile = new File(ClassUtil.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI());
        } catch (URISyntaxException e) {
            // API.LOGGER.info("Failed to get mod jar path");
        }
        return modJarFile;
    }

    public static <A extends Annotation> List<Tuple<A, Class<?>>> getClassAnnotations(Class<A> annotationClass) {
        return getClassAnnotations(classes(), annotationClass);
    }

    public static <A extends Annotation> List<Tuple<A, Class<?>>> getClassAnnotations(Collection<Class<?>> classes, Class<A> annotationClass) {
        List<Tuple<A, Class<?>>> annotations = new ArrayList<>();
        for (Class<?> c : classes) {
            if (c.isAnnotationPresent(annotationClass)) {
                annotations.add(new Tuple<>(c.getAnnotation(annotationClass), c));
            }
        }
        return annotations;
    }

    private static Set<Class<?>> classes() {
        if (classes == null) {
            InputStream in = FileUtil.getResource("classes.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            classes = new HashSet<>();
            try {
                String[] classList = reader.readLine().split(";");
                for (String s : classList) {
                    classes.add(Class.forName(s));
                }
            } catch (IOException | ClassNotFoundException ignored) {
                // API.LOGGER.fatal("Failed to load all classes in classes.txt, maybe it was not regenerated correctly?");
            }
        }
        return classes;
    }

    public static <A extends Annotation> List<Tuple<A, Field>> getFieldAnnotations(Class<A> annotationClass) {
        return getFieldAnnotations(classes(), annotationClass);
    }

    public static <A extends Annotation> List<Tuple<A, Field>> getFieldAnnotations(Collection<Class<?>> classes, Class<A> annotationClass) {
        List<Tuple<A, Field>> annotations = new ArrayList<>();
        for (Class<?> c : classes) {
            for (Field f : c.getFields()) {
                if (f.isAnnotationPresent(annotationClass)) {
                    annotations.add(new Tuple<>(f.getAnnotation(annotationClass), f));
                }
            }
        }
        return annotations;
    }

    public static <A extends Annotation> List<Tuple<A, Method>> getMethodAnnotations(Class<A> annotationClass) {
        return getMethodAnnotations(classes(), annotationClass);
    }

    public static <A extends Annotation> List<Tuple<A, Method>> getMethodAnnotations(Collection<Class<?>> classes, Class<A> annotationClass) {
        List<Tuple<A, Method>> annotations = new ArrayList<>();
        for (Class<?> c : classes) {
            for (Method m : c.getMethods()) {
                if (m.isAnnotationPresent(annotationClass)) {
                    annotations.add(new Tuple<>(m.getAnnotation(annotationClass), m));
                }
            }
        }

        return annotations;
    }

    public static List<Class<?>> classes(String packageName) {
        String path = packageName.replace(".", File.separator);
        String path2 = packageName.replace(".", "/");
        List<Class<?>> classes = new ArrayList<>();

        String classPaths = System.getProperty("java.class.path");
        if (classPaths == null || !classPaths.contains(path) && !classPaths.contains(path2)) {
            classPaths = System.getProperty("legacyClassPath");
        }
        if (classPaths == null) {
            return classes;
        }

        String[] classPathEntries = classPaths.split(System.getProperty("path.separator"));

        // API.LOGGER.info(classPaths);

        String name;
        for (String classpathEntry : classPathEntries) {
            if (classpathEntry.endsWith(".jar")) {
                File jar = new File(classpathEntry);
                try {
                    JarInputStream is = new JarInputStream(Files.newInputStream(jar.toPath()));
                    JarEntry entry;
                    while ((entry = is.getNextJarEntry()) != null) {
                        name = entry.getName();
                        if (name.endsWith(".class")) {
                            if (name.contains(path) || name.contains(path2)) {
                                String classPath = name.substring(0, entry.getName().length() - 6);
                                classPath = classPath.replaceAll("[|/]", ".");
                                classes.add(Class.forName(classPath));
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                    // API.LOGGER.debug("Exception during class loading: ", ex);
                }
            } else {
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);
                    if (!base.isDirectory()) continue;
                    for (File file : Objects.requireNonNull(base.listFiles())) {
                        name = file.getName();
                        if (name.endsWith(".class")) {
                            name = name.substring(0, name.length() - 6);
                            classes.add(Class.forName(packageName + "." + name));
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                    // API.LOGGER.debug("Exception during class loading: ", ex);
                }
            }
        }

        return classes;
    }
    
    public static void setModClass(Class<?> callerClass) {
        ModClass = callerClass;
    }
}
