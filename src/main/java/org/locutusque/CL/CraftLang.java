package org.locutusque.CL;

import org.locutusque.CL.Exceptions.SyntaxError;
import org.locutusque.CL.Objects.JavaSourceFromString;
import org.locutusque.CL.Utils.Dictionary;
import org.locutusque.CL.Utils.Pair;

import javax.tools.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CraftLang {
    private static Map<String, String> variableClasses = new HashMap<>();
    private static String className;
    static Map<String, String> SYNTAX = new HashMap<>() {{
        put("PRINT", "System.out.println( ");
        put("SET", "");
        put("SETJ", " Object");
        put("TO", "=");
        put("TO-CLS", " = new");
        put("IF", " if ");
        put("THEN", " {");
        put("ELSE", "} else { ");
        put("FOR", "for");
        put("IN", " : ");
        put("RANGE", " new org.locutusque.CL.Utils.Range");
        put("WHILE", " while");
        put("NOT", " ! ");
        put("AND", " && ");
        put("OR", " || ");
        put("FUNCTION", " void ");
        put("FUN", " void ");
        put("STATIC", " static ");
        put("RETURN", " return ");
        put("BACK", "return");
        put("OBJECT", " class ");
        put("THIS", "this");
        put("METHOD", " public void ");
        put("IS", " == ");
        put("NONE", "null");
        put("NULL", "null");
        put("TRUE", "true");
        put("FALSE", "false");
        put("EQUALS", "==");
        put("NOT_EQUALS", " != ");
        put("GREATER_THAN", " > ");
        put("LESS_THAN", " < ");
        put("GREATER_THAN_OR_EQUAL", " >= ");
        put("LESS_THAN_OR_EQUAL", " <= ");
        put("PLUS", " + ");
        put("MINUS", " - ");
        put("MULTIPLY", " * ");
        put("DIVIDE", " / ");
        put("MODULO", " % ");
        put("INCREMENT", " += ");
        put("DECREMENT", " -= ");
        put("LENGTH", " length(");
        put("SELF", " this ");
        put("A", " ");
        put("-F", "import");
        put("-I", "import");
        put("USING", "import");
        put("PACKAGE", "import");
        put("NAME", "package");
        put("NAMED", "as");
        put("TOSTRING", "String.valueOf(");
        put("TOLIST", "Arrays.asList(");
        put("ATTRIBUTE", "this ");
        put("ATTRIBUTE.", "this.");
        put("SELF.", "this.");
        put("END", " ) ");
        put("I", "int i");
        put("STR", "String");
        put("DICT", "new org.locutusque.CL.Utils.Dictionary<>(");
        put("MAIN", "public static void main");
        put("STR[]", "String[]");
        put("str[]", "String[]");
        put("DYNAMIC", "org.locutusque.CL.Dynamic");
    }};


    /**
     *
     * @param program Interprets the given program
     * @return the Java translated code
     * @throws Exception for unexpected parentheses
     */
    private static String interpretProgram(List<String> program) throws Exception {
        StringBuilder translatedProgram = new StringBuilder();
        // Stack to keep track of open parentheses and open braces
        Stack<Character> openParenthesesStack = new Stack<>();
        Stack<Character> openBracesStack = new Stack<>();
        String currentVariable = null;
        Pattern pattern = Pattern.compile("(public|private|protected|static|final)*\\s*(class|interface|enum)\\s+(\\w+)");

        boolean unclosedParentheses = false; // Flag to track if there are any unclosed parentheses
        for (String line : program) {
            Matcher matcher = pattern.matcher(line);
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//") || line.startsWith("/*") || line.startsWith("*/") || line.startsWith("*comment")) {
                continue;
            }
            if (matcher.find()) {
                className = matcher.group(3);
                SYNTAX.put("INIT", " public " + className);
            }
            if (line.toUpperCase().startsWith("SET ")) {
                currentVariable = line.substring(4);
                variableClasses.put(currentVariable, null);
            }
            StringBuilder translatedLine = new StringBuilder();
            String[] words = line.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                boolean foundWord = false;
                if (word.toUpperCase().contains("END")) {
                    word = word.replace("end", " ) ");
                }
                if (!line.toUpperCase().contains("PRIVATE CLASS") && line.toUpperCase().contains("CLASS")) {
                    word = word.replace("class", "public class");
                }
                if (!line.toUpperCase().contains("PRIVATE STATIC") ||
                        !line.toUpperCase().contains("PUBLIC STATIC") ||
                        !line.toUpperCase().contains("PRIVATE") &&
                                line.toUpperCase().contains("DEF")) {
                    word = word.replace("def", "public Object");
                }
                if (SYNTAX.containsKey(word.toUpperCase())) {
                    if (word.equalsIgnoreCase("SET")) {
                        // Set the current variable to the next word
                        currentVariable = words[i + 1];
                        variableClasses.put(currentVariable, null);
                        foundWord = true;
                    } else {
                        translatedLine.append(SYNTAX.get(word.toUpperCase()));
                        if (SYNTAX.get(word.toUpperCase()).contains("(")) {
                            openParenthesesStack.push('(');
                            unclosedParentheses = true; // Set flag if there are unclosed parentheses
                            translatedLine.setLength(translatedLine.length() - 1);
                        } else if (SYNTAX.get(word.toUpperCase()).contains(")")) {
                            if (!openParenthesesStack.isEmpty()) {
                                openParenthesesStack.pop();
                                translatedLine.setLength(translatedLine.length() - 1);
                            } else {
                                throw new Exception("Unexpected closing parenthesis: " + word);
                            }
                        }

                        foundWord = true;
                    }
                } else {
                    translatedLine.append(word);
                }
                if (i < words.length - 1 && !SYNTAX.containsKey(words[i + 1].toUpperCase())) {
                    translatedLine.append(" ");
                }
            }
            if (!unclosedParentheses && // Check if there are any unclosed parentheses
                    translatedLine.length() > 0 &&
                    translatedLine.charAt(translatedLine.length() - 1) != ';' &&
                    translatedLine.charAt(translatedLine.length() - 1) != '{' &&
                    translatedLine.charAt(translatedLine.length() - 1) != '}'
            ) {
                translatedLine.append(';');
            }

            translatedProgram.append(" ".repeat(openBracesStack.size() * 4)).append(translatedLine.toString().trim()).append('\n');
            while (!openParenthesesStack.isEmpty() && translatedProgram.charAt(translatedProgram.length() - 1) != ')' && translatedProgram.charAt(translatedProgram.length() - 1) != '{') {
                translatedProgram.append(");");
                openParenthesesStack.pop();
            }
            if (unclosedParentheses) { // Reset flag if there are unclosed parentheses
                unclosedParentheses = false;
            }
            translatedProgram.append("\n");
        }
        return translatedProgram.toString();
    }
    /**
     *
     * @param filePath The path of the file that you want to interpret. This path must be a string, and cannot be an instance of Path.
     * @param targetPath The folder where you want to store the output file. Cannot be an instance of a path as well, must be a string
     * @throws Exception Checks for syntax errors
     */
    public static void compileProgram(String filePath, String targetPath) throws Exception, SyntaxError {
        Path path = Paths.get(filePath);
        List<String> program = Files.readAllLines(path);
        String translatedCode = interpretProgram(program);

        // Write the translated code to a .java file
        String fileName = className + ".java";
        String target = targetPath + "\\" + fileName;
        Files.write(Path.of(target), translatedCode.getBytes());

        // Create a new Java file object with the translated code
        JavaFileObject file = new JavaSourceFromString(className, translatedCode, JavaFileObject.Kind.SOURCE);

        // Get the Java compiler and create a new file manager
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        // Set the output directory for the compiled classes
        File outputDir = new File(targetPath);
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(outputDir));

        // Redirect compiler's output to capture diagnostic messages
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StringWriter writer = new StringWriter();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(writer, fileManager, diagnosticCollector, null, null, List.of(file));
        long lineNumber = -1;
        try {
            boolean success = task.call();
            System.out.println(success);
            if (success) {
                System.out.println("Program compiled successfully.");
                Files.deleteIfExists(Paths.get(target));
            } else {
                String compilerOutput = writer.toString();
                Files.deleteIfExists(Paths.get(target));
                // Process the diagnostics to retrieve line number and code snippet for each error
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics()) {
                    if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                        lineNumber = diagnostic.getLineNumber();
                        System.out.println(diagnostic.getMessage(null));
                    }
                }
                if (Long.valueOf(lineNumber).intValue() != -1) {
                    throw new SyntaxError("Unknown Token", null, Long.valueOf(lineNumber).intValue() - 1);
                }
            }
        } catch (Exception e) {
            String compilerOutput = writer.toString();

            // Process the diagnostics to retrieve line number and code snippet for each error
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticCollector.getDiagnostics()) {
                if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                    lineNumber = diagnostic.getLineNumber();
                    System.out.println(diagnostic.getMessage(null));
                }
            }
            if (Long.valueOf(lineNumber).intValue() != -1) {
                throw new SyntaxError("Unknown Token", null, Long.valueOf(lineNumber).intValue() - 1);
            }
        } finally {
            // Close the file manager and restore the standard output
            fileManager.close();
            writer.close();
        }
    }
    private static String getClassName(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
    /**
     * Deprecated because syntax being checked is outdated, will return incorrect errors.
     * @param code checks if the given code has any syntax errors. This is good for use of IDLE, IDE, etc.
     * @return a list of errors in the code.
     */
    @Deprecated
    public static List<String[]> checkSyntax(String code) {
        Stack<Pair<Character, Integer>> bracketStack = new Stack<>();
        List<String[]> errors = new ArrayList<>();
        for (int i = 0; i < code.length(); i++) {
            char currentChar = code.charAt(i);
            if (currentChar == '(') {
                bracketStack.push(new Pair<>('(', i));
            } else if (currentChar == ')') {
                if (bracketStack.empty() || bracketStack.peek().getSecond() != '(') {
                    errors.add(new String[] {"Mismatched closing parenthesis", Integer.toString(i)});
                } else {
                    bracketStack.pop();
                }
            }
            for (Map.Entry<String, String> entry : CraftLang.SYNTAX.entrySet()) {
                String keyword = entry.getKey();
                String value = entry.getValue();
                if (i <= code.length() - value.length() && code.substring(i, i + value.length()).equals(value)) {
                    if (keyword.equals("THIS") && (!code.substring(0, i).contains("class ") || !code.substring(0, i).contains("this"))) {
                        errors.add(new String[] {"Invalid use of 'this'", Integer.toString(i)});
                    } else if (keyword.equals("STATIC") && !code.substring(0, i).contains("class ")) {
                        errors.add(new String[] {"Invalid use of 'static'", Integer.toString(i)});
                    } else if (Arrays.asList("OBJECT", "INIT", "SINGLEINIT", "ATTRIBUTE", "SELF", "METHOD").contains(keyword) && !code.substring(0, i).contains("class ")) {
                        errors.add(new String[] {String.format("Invalid use of '%s'", keyword.toLowerCase()), Integer.toString(i)});
                    } else if (Arrays.asList("A", "-F", "-I", "USING", "PACKAGE", "NAMED").contains(keyword) && !code.substring(0, i).contains("import ")) {
                        errors.add(new String[] {String.format("Invalid use of '%s'", keyword.toLowerCase()), Integer.toString(i)});
                    } else if (Arrays.asList("PRINT", "SET", "LENGTH", "TOSTRING", "TOLIST").contains(keyword) && (i == 0 || Character.isLetterOrDigit(code.charAt(i - 1)))) {
                        errors.add(new String[] {String.format("Missing whitespace before '%s'", value.trim()), Integer.toString(i)});
                    } else if (Arrays.asList("IF", "FOR", "WHILE").contains(keyword) && (i == 0 || Character.isLetterOrDigit(code.charAt(i - 1)))) {
                        errors.add(new String[] {String.format("Missing whitespace before '%s'", value.trim()), Integer.toString(i)});
                    } else if (Arrays.asList("THEN", "ELSE", "IN", "EQUALS", "NOT_EQUALS", "GREATER_THAN", "LESS_THAN", "GREATER_THAN_OR_EQUAL", "LESS_THAN_OR_EQUAL", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "MODULO", "INCREMENT", "DECREMENT", "TO").contains(keyword) && (i == code.length() - 1 || !Character.isWhitespace(code.charAt(i + value.length())))) {
                        errors.add(new String[] {String.format("Missing whitespace after '%s'", value.trim()), Integer.toString(i)});
                    }
                }
            }
        }
        if (!bracketStack.empty()) {
            errors.add(new String[] {"Unclosed opening parenthesis", Integer.toString(bracketStack.peek().getSecond())});
        }
        return errors;
    }

    /**
     *
     * @param packagePath The package that you want to compile
     * @return Not recommended for personal use, see importCLPackage() instead
     * @throws Exception if the package does not exist
     */
    public static String getPackage(String packagePath) throws Exception {
        File file = new File(packagePath);
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

        FileWriter writer = new FileWriter(scriptDir + "\\packages\\CraftLangCache.java");
        writer.write(code);
        writer.close();

        return scriptDir + "\\packages\\CraftLangCache.java";
    }

    /**
     *
     * @param filePath The package that you want to import from EnProg
     * @param className The class name of the file that you want to import
     * @return an instance of the class, can be implemented into Java, and programming languages like Java such as Kotlin.
     * @throws Exception
     */
    @Deprecated
    public static Object importPackage(String filePath, String className) throws Exception {
        String importFilePath = getPackage(filePath);
        File file = new File(importFilePath);
        Scanner scanner = new Scanner(file);
        StringBuilder scriptCode = new StringBuilder();
        while (scanner.hasNextLine()) {
            scriptCode.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        Object instance;
        try {
            Class<?> importedClass = Class.forName(className);
            instance = importedClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new Exception(className + " is not defined in the imported file");
        }
        return instance;
    }
    /*public static Object importCLPackage(String filePath, String className) throws Exception {
        String importFilePath = getPackage(filePath);
        File file = new File(importFilePath);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/
    public static void main(String[] args) throws Exception, SyntaxError {
        System.out.println("Starting CraftLang!");
        switch (args[0]) {
            case "run" -> compileProgram(args[1], args[2]);
            case "import" -> importPackage(args[1], args[2]);
            case "get_package" -> getPackage(args[1]);
            case "check" -> checkSyntax(args[1]);
            case "translate" -> interpretProgram(Collections.singletonList(args[1]));
            default -> throw new RuntimeException("Unknown command: " +  args[1]);
        }
    }
    private static boolean isJavaSyntaxValid(String code) {
        // Get an instance of the JavaCompiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // Create a DiagnosticCollector to collect compilation diagnostics
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        // Create a new JavaFileManager
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);

        // Create a Java source file object from the given code
        JavaFileObject sourceFile = new JavaSourceFromString("TestClass", code, JavaFileObject.Kind.SOURCE);

        // Create a list of Java source files to compile
        Iterable<? extends JavaFileObject> compilationUnits = List.of(sourceFile);

        // Set up the compilation task
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

        // Perform the compilation
        boolean success = task.call();

        // Close the file manager
        try {
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return success;
    }

}