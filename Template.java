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
    // include main (test)
    // -t true
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
          System.out.println("Process: `" + a + "`");
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

      System.out.println("Process param: `" + args[i] + "`");
    }

    System.out.println(className);
    System.out.println(variables);
    System.out.println(implementations);
    System.out.println(extendsName);

    createJavaOut();
    updateMake(makeRunCmd);
  }

  private static void createJavaOut() {
    String classUpper = capitalize(className);
    try {
      FileWriter file = new FileWriter(classUpper + ".java");

      writeClassHeader(file, classUpper);
      writeVars(file);
      writeConstructor(file, classUpper);
      writeMain(file);
      writeAbstractMethods(file);
      writeMutators(file);
      writeAccessors(file);
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
        Class c1 = Class.forName(libs[i] + className);
        return c1;
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

  private static void writeClassHeader(FileWriter file, String className) throws IOException {
    // only newline if imported something
    boolean imported = false;

    if (classImplements()) {
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

    // only newline if imported something
    if (imported) {
      file.write("\n");
    }

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
    for (int i = 0; i<variables.size(); i += 2) {
      file.write("\tprivate " + variables.get(i) + " "+ variables.get(i + 1) + ";\n");
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

    // +2 since every second entry is a name and not a type
    for (int i = 0; i<variables.size(); i += 2) {
      file.write(getDefaultFromTypePrimative(variables.get(i)));
      // -3 to account for 0 indexing, the fact that the last one should be a variable name and other thing im too lazy
      if (!(i>variables.size() - 3)) {
        file.write(", ");
      }
    }
    file.write(");\n");

    file.write("\t}\n");
  }

  // constructor with variables
  private static void writeVarCons(FileWriter file, String className) throws IOException {
    // TODO: if you extend a class
    // super(class type1, class type2);

    // constructor variables
    file.write("\tpublic " + className + "(");
    // practically the same as above
    for (int i = 0; i<variables.size(); i += 2) {
      file.write(variables.get(i));
      file.write(" ");
      file.write(variables.get(i + 1));
      if (!(i>variables.size() - 3)) {
        file.write(", ");
      }
    }
    file.write(") {\n");

    // +2 since every second entry is a name and not a type
    for (int i = 0; i<variables.size(); i += 2) {
      file.write("\t\t" + getMutatorFunctionWithParam(variables.get(i+1), variables.get(i+1)) + ";\n");
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

  private static String getDefaultFromTypePrimative(String type) {
    if (type.equals("String")) {
      return "\"\"";
    }

    if (type.equals("int") | type.equals("double") | type.equals("float")) {
      return "0";
    }

    if (type.equals("char")) {
      // since chars cant be empty
      return "'n'";
    }

    if (type.equals("void")) {
      return "";
    }

    // if its not any of those im just assuming its a class
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

          System.out.println(preReturn + returnType + " "+ name + "(" + paramOut + ")");
          file.write(preReturn + returnType + " "+ name + "(" + paramOut + ")" + "{\n");
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
    file.write("\tpublic " + type + " " + getAccessorFunction(var) + "{\n");
    file.write("\t\treturn " + var + ";\n");
    file.write("\t}\n");
  }

  private static void writeAccessors(FileWriter file) throws IOException {
    for (int i = 0; i<variables.size(); i += 2) {
      file.write("\n");
      writeAccessor(file, variables.get(i), variables.get(i+1));
    }
  }

  private static String getMutatorFunctionWithParam(String var, String param) {
    return "set" + capitalize(var) + "(" + param + ")";
  }

  private static void writeMutators(FileWriter file) throws IOException {
    for (int i = 0; i<variables.size(); i += 2) {
      file.write("\n");
      writeMutator(file, variables.get(i), variables.get(i+1));
    }
  }

  private static void writeMutator(FileWriter file, String type, String var) throws IOException {
    file.write("\tpublic void " + getMutatorFunctionWithParam(var, type + " " + var) + "{\n");
    file.write("\t\tthis." + var + " = " + var + ";\n");
    file.write("\t}\n");
  }

  private static String capitalize(String value) {
    return value.substring(0, 1).toUpperCase() + value.substring(1);
  }

  private static void writeToString(FileWriter file) throws IOException {
    file.write("\n\tpublic String toString() {\n\t\treturn ");
    for (int i = 0; i<variables.size(); i += 2) {
      String var = variables.get(i + 1);
      file.write("\"\\n" + var  + ": \" + " + getAccessorFunction(var));

      if (i<variables.size() - 2) {
        file.write(" + ");
      }
    }
    file.write(";\n\t}\n");
  }

  private static void updateMake(String cmdName) {
  }
}
