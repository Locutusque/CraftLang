package org.locutusque.CL;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Import extends CraftLang {
    public static void importClass(String classPath) throws Exception {
        String className = classPath.substring(classPath.lastIndexOf('.') + 1);
        String packagePath = classPath.replace('.', '/') + ".cl";
        File file = new File(packagePath);

        if (!file.exists()) {
            throw new Exception("Class file not found.");
        }

        Scanner scanner = new Scanner(file);
        List<String> script = new ArrayList<>();

        while (scanner.hasNextLine()) {
            script.add(scanner.nextLine() + "\n");
        }

        scanner.close();

        String code = interpretProgram(script);
        Path scriptPath = Paths.get(className + ".java").toAbsolutePath().normalize();
        String scriptDir = scriptPath.getParent().toString();
        System.out.println(scriptDir);

        // Write the code to a Java file
        Path cacheFilePath = Paths.get(scriptDir + "/packages/CraftLangCache.java");
        Files.write(cacheFilePath, code.getBytes());

        // Compile the Java file into a class file
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(cacheFilePath.toFile()));
        compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
        fileManager.close();

        // Dynamically load the compiled class
        CLClassLoader classLoader = new CLClassLoader(classPath);
        Class<?> importedClass = classLoader.loadClass(className);
        // Store the imported class in the ImportRegistry
        ImportRegistry.addClass(className, importedClass);
    }


    public static Object invokeStaticMethod(String className, String methodName, Object... args) throws Exception {
        Class<?> importedClass = ImportRegistry.getClass(className);

        if (importedClass == null) {
            throw new Exception("Class not found: " + className);
        }

        Method method = importedClass.getMethod(methodName);
        return method.invoke(null, args);
    }

    public static class ImportRegistry {
        private static final Map<String, Class<?>> classMap = new HashMap<>();

        public static void addClass(String className, Class<?> importedClass) {
            classMap.put(className, importedClass);
        }

        public static Class<?> getClass(String className) {
            return classMap.get(className);
        }
    }
}

