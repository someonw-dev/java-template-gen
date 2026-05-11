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
    ArrayList<String> implementations = new ArrayList<>();
    ArrayList<String> extensions = new ArrayList<>();
    String makeRunCmd = "";
    // default is classname
    char processType = 'c';
    boolean evaluatedParam = true;

    for (int i = 0; i < args.length; i++) {
      char c = args[i].charAt(0);
      // if new process Type
      if (c == '-') {
        if (!evaluatedParam) {
          System.out.println("No evaluated paramaters between processes `" + args[i-1] + "` and `" + args[i] + "`.");
          System.exit(-1);
        }
        // arg
        char a = args[i].charAt(1);

        switch (a) {
          case 'c':
          case 'i':
          case 'e':
          case 'v':
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
        case 'i':
        break;
        case 'e':
        break;
        case 'v':
        break;
        case 'm':
        break;
      }

      System.out.println("Process param: `" + args[i] + "`");

    }
  }

  public static void updateMake(String cmdName) {
  }
}
