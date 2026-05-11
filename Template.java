import java.util.*;
import java.io.*;

public class Template {
  // i do not particulary want to pass these as parameters so whatever
  private static String className = "";
  private static ArrayList<String> variables = new ArrayList<>();
  private static ArrayList<String> implementations = new ArrayList<>();
  private static ArrayList<String> extensions = new ArrayList<>();

  public static void main(String args[]) {
    // class name
    // -c className
    // implements
    // -i Interface Interface2
    // extends
    // -e Object Object2
    // variables with getters and setters
    // -v String var1 int var2 
    // make file with run command run
    // -m run
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
        extensions.add(args[i]);
        break;
        case 'm':
        makeRunCmd = args[i];
        break;
      }

      System.out.println("Process param: `" + args[i] + "`");
    }

    System.out.println(className);
    System.out.println(variables);
    System.out.println(implementations);
    System.out.println(extensions);

    createJavaOut();
    updateMake(makeRunCmd);
  }

  private static void createJavaOut() {
    String classUpper = className.substring(0, 1).toUpperCase() + className.substring(1);
    try {
      FileWriter file = new FileWriter(classUpper + ".java");
      file.write("public class " + classUpper + " {\n");

      writeConstructor(file, classUpper);



      file.write("}");
      file.close();
    } catch (IOException e) {
      System.out.println("Error: " + e);
      System.exit(-1);
    }
  }

  // default + one with vars
  private static void writeConstructor(FileWriter file, String className) throws IOException {
    writeDefCons(file, className);
    file.write("\n");
    writeVarCons(file, className);
  }

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

  private static void writeVarCons(FileWriter file, String className) throws IOException {
    // constructor variables
    file.write("\tpublic " + className + "(");
    for (int i = 0; i<variables.size(); i += 2) {
      file.write(variables.get(i));
      file.write(" ");
      file.write(variables.get(i + 1));
      // -3 to account for 0 indexing, the fact that the last one should be a variable name and other thing im too lazy
      if (!(i>variables.size() - 3)) {
        file.write(", ");
      }
    }
    file.write(") {\n");

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

  private static String getDefaultFromTypePrimative(String type) {
    if (type.equals("String")) {
      return "\"\"";
    }

    if (type.equals("int") | type.equals("double") | type.equals("float")) {
      return "0";
    }

    // if its not any of those im just assuming its a class
    return "new " + type + "()";
  }

  private static void writeVar(FileWriter file, String type, String var) throws IOException {
  }

  private static void getAccessor(FileWriter file, String type, String var) throws IOException {
  }

  private static void writeAccessor(FileWriter file, String type, String var) throws IOException {
  }

  private static void getMutator(FileWriter file, String type, String var) throws IOException {
  }

  private static void writeMutator(FileWriter file, String type, String var) throws IOException {
  }

  private static void updateMake(String cmdName) {
    System.out.println(cmdName);
  }
}
