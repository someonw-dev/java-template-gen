import java.io.*;

public class Test {
  public static void main(String args[]) {
    Runtime runtime = Runtime.getRuntime();
    String cmd[] = new String[]{"java", "Template", "-c", "-"};

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
