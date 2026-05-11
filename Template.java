import java.util.ArrayList;

public class Template {
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
    String className = "";
    ArrayList<String> variables = new ArrayList<>();
    String makeRunCmd = "";
    char processType;

    for (int i = 0; i < args.length; i++) {
      char c = args[i].charAt(0);
      // if new process Type
      if (c == '-') {
        // arg
        if (args[i].length() < 2) {
          System.err.println("test");
          System.exit(-1);
        }

        char a = args[i].charAt(1);
        switch (c) {
          case 'c':
          break;
          case 'i':
          break;
          case 'e':
          break;
          case 'v':
          break;
          case 'm':
          break;
        }
      }
    }
  }

  public static void updateMake(String cmdName) {
  }
}
