import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class Template {
  // i do not particulary want to pass these as parameters so whatever
  private static String className = "";
  private static ArrayList<String> variables = new ArrayList<>();
  private static ArrayList<String> implementations = new ArrayList<>();
  private static String extendsName = "";
  private static boolean abstractClass = false;
  private static boolean includeMain = false;

  public static void main(String args[]) {
    // class name
    // -c className
    // implements
    // -i Interface Interface2
    // extends
    // -e Object
    // variables with getters and setters
    // -v String var1 int var2 
    // make file with run command run
    // -m run
    // abstract or not
    // -a true
    String makeRunCmd = "";
    // default is classname
    char processType = 'c';
    boolean evaluatedParam = true;

    for (int i = 0; i < args.length; i++) {
      char c = args[i].charAt(0);
      // if new process Type
      if (c == '-') {
        if (!evaluatedParam) {
          System.out.println("No evaluation paramaters between processes `" + args[i-1] + "` and `" + args[i] + "`.");
          System.exit(-1);
        }

        if (i == args.length -1) {
          System.out.println("No evaluation paramaters for last process: `" + args[i] + "`.");
          System.exit(-1);
        }
        // arg
        char a = args[i].charAt(1);

        switch (a) {
          case 'c':
          case 'v':
          case 'i':
          case 'e':
          case 'm':
          case 'a':
          processType = a;
          evaluatedParam = false;
          break;
          default:
          System.out.println("Invalid argument: `" + a + "`");
          System.exit(-1);
        }

        continue;
      }

      evaluatedParam = true;
      // if its not a new process type use the process type to evaluate the stuff
      switch (processType) {
        case 'c':
        className = args[i];
        break;
        case 'v':
        variables.add(args[i]);
        break;
        case 'i':
        implementations.add(args[i]);
        break;
        case 'e':
        extendsName = args[i];
        break;
        case 'm':
        makeRunCmd = args[i];
        if (abstractClass) {
          System.out.println("Error cannot be abstract and have a main function, note that having a make section creates a main function.");
          System.exit(-1);
        }
        includeMain = true;
        break;
        case 'a':
        if (includeMain) {
          System.out.println("Error cannot be abstract and have a main function, note that having a make section creates a main function.");
          System.exit(-1);
        }
        if (args[i].equals("true")) {
          abstractClass = true;
        } else {
          abstractClass = false;
        }
        break;
      }
    }

    System.out.println("Class name: " + className);
    System.out.println("Variables: " + variables);
    System.out.println("Implementations: " + implementations);
    System.out.println("Extensions: " + extendsName);

    createJavaOut();
    updateMake(makeRunCmd);
    compile();
  }

  private static void createJavaOut() {
    String classUpper = capitalize(className);
    try {
      FileWriter file = new FileWriter(classUpper + ".java");

      // just because it looks cooler if you print lots of things :P
      System.out.println("Writing imports...");
      writeImports(file);
      System.out.println("Writing class header...");
      writeClassHeader(file, classUpper);
      System.out.println("Writing variable definitions...");
      writeVars(file);
      System.out.println("Writing constructor...");
      writeConstructor(file, classUpper);
      System.out.println("Writing main...");
      writeMain(file);
      System.out.println("Writing abstract methods...");
      writeAbstractMethods(file);
      System.out.println("Writing mutators...");
      writeMutators(file);
      System.out.println("Writing accessors...");
      writeAccessors(file);
      System.out.println("Writing toString...");
      writeToString(file);

      file.write("}");
      file.close();
    } catch (IOException e) {
      System.out.println("Error: " + e);
      System.exit(-1);
    }
  }


  private static String libs[] = new String[]{
    // current dir
    "",
    "java.io.",
    "java.util.",
    "java.lang."
  };

  // this _should_ work for classes and interfaces
  private static String getLibPath(String className) throws ClassNotFoundException {
    for (int i = 0; i<libs.length; i++) {
      // see if its in one of the lib paths
      try {
        Class c1 = Class.forName(libs[i] + className);
        return libs[i] + className;
      } catch (ClassNotFoundException e) {}
    }

    // if it doesnt find any
    throw new ClassNotFoundException();
  }

  private static Class getClass(String className) throws ClassNotFoundException {
    for (int i = 0; i<libs.length; i++) {
      try {
        Class c = Class.forName(libs[i] + className);
        return c;
      } catch (ClassNotFoundException e) {}
    }

    // if it doesnt find any
    throw new ClassNotFoundException();
  }

  private static boolean classExtends() {
    if (!extendsName.trim().isEmpty()) {
      return true;
    }

    return false;
  }

  private static boolean classImplements() {
    if (implementations.size() >= 1) {
      return true;
    }

    return false;
  }

  private static boolean isFromJavaLang(String str) {
    String import_dir = str.substring(0, str.lastIndexOf('.') + 1);
    if (import_dir.equals("java.lang.")) {
      return true;
    }

    return false;
  }

  private static void writeImports(FileWriter file) throws IOException {
    // only newline if imported something
    boolean imported = false;

    if (classImplements()) {
      // implementations imports
      for (int i = 0; i<implementations.size(); i++) {
        try {
          String path = getLibPath(implementations.get(i));
          // no import path needed if its in local dir
          if (!path.equals(implementations.get(i))) {
            file.write("import " + path + ";\n");
          }

          imported = true;
        } catch (ClassNotFoundException e) {
          System.out.println("Could not find `" + implementations.get(i) + "` import path, likely spelling mistake, external library or missing .class file.");
          System.exit(-1);
        }
      }
    }

    // extention imports
    if (classExtends()) {
      try {
        String path = getLibPath(extendsName);
        // no import path needed if its in local dir
        if (!path.equals(extendsName)) {
          file.write("import " + path + ";\n");
          imported = true;
        }

      } catch (ClassNotFoundException e) {
        System.out.println("Could not find `" + extendsName + "` import path, likely spelling mistake, external library or missing .class file.");
        System.exit(-1);
      }
    }

    // variable imports
    if (variables.size() > 0) {
      for (int i = 0; i<variables.size(); i += 3) {
        try {
          if (isPrimativeStrict(variables.get(i))) {
            continue;
          }

          String path = getLibPath(variables.get(i));
          // no import path needed if its in local dir
          if (path.equals(variables.get(i))) {
            continue;
          }

          // if its from java lang you dont need imports (e.g. String)
          if (!isFromJavaLang(path)) {
            file.write("import " + path + ";\n");
          } else {
            System.out.println("Import from java lang ignoring.");
          }

          imported = true;
        } catch (ClassNotFoundException e) {
          System.out.println("Could not find `" + variables.get(i) + "` import path, assuming a suitable class will be made or you made a spelling mistake.");
        }
      }

    }

    // only newline if imported something
    if (imported) {
      file.write("\n");
    }
  }

  private static void writeClassHeader(FileWriter file, String className) throws IOException {
    file.write("public");

    if (abstractClass) {
      file.write(" abstract");
    }

    file.write(" class " + className);

    if (classExtends()) {
      file.write(" extends " + extendsName);
    }

    if (classImplements()) {
      file.write(" implements ");
      for (int i = 0; i<implementations.size(); i++) {
        file.write(implementations.get(i));
        if (!(i>implementations.size() - 2)) {
          file.write(", ");
        }
      }
    }

    file.write(" {\n");
  }

  private static void writeVars(FileWriter file) throws IOException {
    for (int i = 0; i<variables.size(); i += 3) {
      String afterPrivate = "";
      String varParams = variables.get(i + 2);
      
      if (varIsStatic(varParams)) {
        afterPrivate += "static ";
      }

      if (varIsFinal(varParams)) {
        afterPrivate += "final ";
      }

      file.write("\tprivate " + afterPrivate + variables.get(i) + " "+ variables.get(i + 1));
      if (varIsFinal(varParams)) {
        file.write(" = " + getDefaultFromTypePrimative(variables.get(i)));
      }
      file.write(";\n");
    }

    file.write("\n");
  }

  // default + one with vars
  private static void writeConstructor(FileWriter file, String className) throws IOException {
    writeDefCons(file, className);
    file.write("\n");
    writeVarCons(file, className);
  }

  // default constructor
  private static void writeDefCons(FileWriter file, String className) throws IOException {
    file.write("\tpublic " + className + "() {\n");
    file.write("\t\tthis(");

    boolean prev = false;
    // +2 since every second entry is a name and not a type
    for (int i = 0; i<variables.size(); i += 3) {
      if (varIsDefault(variables.get(i + 2))) {
        if (prev) {
          file.write(", ");
        }
        file.write(getDefaultFromTypePrimative(variables.get(i)));
        prev = true;
      }
    }
    file.write(");\n");

    file.write("\t}\n");
  }

  // constructor with variables
  private static void writeVarCons(FileWriter file, String className) throws IOException {

    // constructor variables
    file.write("\tpublic " + className + "(");
    boolean prev = false;
    // practically the same as above
    for (int i = 0; i<variables.size(); i += 3) {
      if (varIsDefault(variables.get(i + 2))) {
        if (prev) {
          file.write(", ");
        }

        file.write(variables.get(i));
        file.write(" ");
        file.write(variables.get(i + 1));

        prev = true;
      }
    }
    file.write(") {\n");


    // check if you need to super
    try {
      Class<?> cls = getClass(extendsName);
      Constructor<?> cons[] = cls.getConstructors();

      // returns first constructor with some parameters
      for (int i = 0; i<cons.length; i++) {
        Parameter params[] = cons[i].getParameters();
        if (params.length > 0) {
          file.write("\t\tsuper(");
          prev = false;

          // just writing the vars with default types
          for (int j = 0; j<params.length; j++) {
            if (prev) {
              file.write(", ");
            }

            // holy what a line
            file.write(getDefaultFromTypePrimative(stripBeforeLastDot(params[j].getType().toString())));

            prev = true;
          }
          file.write(");\n");
        }
      }

    } catch (ClassNotFoundException e) {
      System.out.println("Extends class definition not found, cannot write default super.");
    }

    // +2 since every second entry is a name and not a type
    for (int i = 0; i<variables.size(); i += 3) {
      if (varIsDefault(variables.get(i + 2))) {
        file.write("\t\t" + getMutatorFunctionWithParam(variables.get(i+1), variables.get(i+1)) + ";\n");
      }
    }

    file.write("\t}\n");
  }

  public static void writeMain(FileWriter file) throws IOException {
    if (includeMain) {
      file.write("\n\tpublic static void main(String args[]) {\n");
      file.write("\t\t\n");
      file.write("\t}\n");
    }
  }

  private static boolean isPrimativeStrict(String type) {
    if (type.equals("int") | type.equals("double") | type.equals("float") | type.equals("long") | type.equals("short")
    | type.equals("char") | type.equals("boolean") | type.equals("byte")) {
      return true;
    }

    return false;
  }

  // probably shouldnt have named this primative
  // oh well
  private static String getDefaultFromTypePrimative(String type) {
    if (type.equals("String")) {
      return "\"\"";
    }

    if (type.equals("int") | type.equals("double") | type.equals("float") | type.equals("long") | type.equals("short")) {
      return "0";
    }

    if (type.equals("char")) {
      // since chars cant be empty
      return "'n'";
    }

    if (type.equals("boolean")) {
      return "false";
    }

    if (type.equals("void")) {
      return "";
    }

    // if its not any of those im just assuming its a class
    // i _should_ get the class type go through its constructors to get a valid one but i do not care
    // this is a template script not a do everything script
    return "new " + type + "()";
  }

  private static void writeInterfaceMethods(FileWriter file) throws IOException {
    for (int i = 0; i<implementations.size(); i++) {
      writeClassAbstractMethods(file, implementations.get(i));
    }
  }

  private static void writeExtendedClassMethods(FileWriter file) throws IOException {
    writeClassAbstractMethods(file, extendsName);
  }

  public static void writeClassAbstractMethods(FileWriter file, String className) throws IOException {
    try {
      Class<?> cls = getClass(className);
      // kinda cool
      // https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html#getDeclaredMethods--
      // this doesnt return the parents methods so i should probably add some extra logic to this
      Method[] methods = cls.getDeclaredMethods();

      for (int i = 0; i < methods.length; i++) {
        // if a method is abstract write it as an implementation
        if (Modifier.isAbstract(methods[i].getModifiers())) {
          Method m = methods[i];
          // stuff before the return type
          String preReturn = "\n\t";

          if (Modifier.isPrivate(m.getModifiers())) {
            preReturn += "private ";
          } else {
            preReturn += "public ";
          }

          if (Modifier.isStatic(m.getModifiers())) {
            preReturn += "static ";
          }

          // some of these give extra info which you dont need
          String returnType = stripBeforeLastDot(m.getReturnType().toString());
          String name = m.getName();
          Parameter params[] = m.getParameters();
          String paramOut = "";

          for (int j = 0; j<params.length ; j++) {
            paramOut += stripBeforeLastDot(params[j].getType().toString()) + " ";
            paramOut += params[j].getName();
            if (j<params.length - 1) {
              paramOut += ", ";
            }
          }

          file.write(preReturn + returnType + " "+ name + "(" + paramOut + ")" + " {\n");
          file.write("\t\treturn " + getDefaultFromTypePrimative(returnType) + ";");
          file.write("\n\t}\n");
        }
      }

      // this shouldnt happen since it exits earlier if a class isnt found
    } catch (ClassNotFoundException e) {}
  }

  public static String stripBeforeLastDot(String str) {
    return str.substring(str.lastIndexOf('.') + 1);
  }

  // check interfaces and extends class for abstract methods that need to be implemented in here
  private static void writeAbstractMethods(FileWriter file) throws IOException {
    // if its not an abstract class it should have implementations for all abstract methods
    if (!abstractClass) {
      writeInterfaceMethods(file);
      writeExtendedClassMethods(file);
    }
  }

  private static String getAccessorFunction(String var) {
    return "get" + capitalize(var) + "()";
  }

  private static void writeAccessor(FileWriter file, String type, String var) throws IOException {
    file.write("\tpublic " + type + " " + getAccessorFunction(var) + " {\n");
    file.write("\t\treturn " + var + ";\n");
    file.write("\t}\n");
  }

  private static void writeAccessors(FileWriter file) throws IOException {
    for (int i = 0; i<variables.size(); i += 3) {
      if (varIsDefault(variables.get(i + 2))) {
        file.write("\n");
        writeAccessor(file, variables.get(i), variables.get(i+1));
      }
    }
  }

  private static String getMutatorFunctionWithParam(String var, String param) {
    return "set" + capitalize(var) + "(" + param + ")";
  }

  private static void writeMutators(FileWriter file) throws IOException {
    for (int i = 0; i<variables.size(); i += 3) {
      if (varIsDefault(variables.get(i + 2))) {
        file.write("\n");
        writeMutator(file, variables.get(i), variables.get(i+1));
      }
    }
  }

  private static void writeMutator(FileWriter file, String type, String var) throws IOException {
    file.write("\tpublic void " + getMutatorFunctionWithParam(var, type + " " + var) + " {\n");
    file.write("\t\tthis." + var + " = " + var + ";\n");
    file.write("\t}\n");
  }

  private static String capitalize(String value) {
    return value.substring(0, 1).toUpperCase() + value.substring(1);
  }

  private static void writeToString(FileWriter file) throws IOException {
    file.write("\n\tpublic String toString() {\n\t\treturn ");
    boolean prev = false;
    for (int i = 0; i<variables.size(); i += 3) {
      if (varIsDefault(variables.get(i + 2))) {
        if (prev) {
          file.write(" + ");
        }
        String var = variables.get(i + 1);
        file.write("\"\\n" + var  + ": \" + " + getAccessorFunction(var));

        prev = true;
      }
    }
    file.write(";\n\t}\n");
  }

  // default means there should be getters and setters
  private static boolean varIsDefault(String params) {
    if (params.contains("d")) {
      return true;
    }

    return false;
  }

  private static boolean varIsFinal(String params) {
    if (params.contains("f")) {
      return true;
    }

    return false;
  }

  private static boolean varIsStatic(String params) {
    if (params.contains("s")) {
      return true;
    }

    return false;
  }

  private static void updateMake(String cmdName) {
    // check if makefile exists
    final String name = "makefile";
    File checkFile = new File(name);
    FileWriter file;
    boolean exists = true;


    if (!(checkFile.exists() && !checkFile.isDirectory())) {
      System.out.println("Makefile doesnt exist generating new makefile...");

      exists = false;
    }

    try {
      // true means append
      file = new FileWriter(name, true);

    if (!exists) {
      try {
        file.write("all:\n");
        file.write("\tjavac -parameters *.java\n");

      } catch (IOException e ) {
        System.out.println("Error writing to makefile.");
        System.exit(-1);
      }
    }

      file.write("\n" + cmdName + ": all\n");
      file.write("\tjava " + className);

      file.close();
    } catch (IOException e ) {
      System.out.println("Error writing to makefile.");
      System.exit(-1);
    }
  }

  private static void compile() {
    System.out.println("Compiling " + className + ".java...");
    Runtime runtime = Runtime.getRuntime();
    String cmd[] = new String[]{"javac", className + ".java"};

    try {
      Process process = runtime.exec(cmd);
      String out = convertStreamToString(process.getInputStream());
      System.out.println(out);
    } catch (IOException e) {
      System.err.println("Error: " + e);
      System.exit(-1);
    }
  }

  static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}
