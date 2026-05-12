import java.io.Serializable;
import java.util.Scanner;

public class Thing extends BaseThing implements Serializable, Area {
	private String var1;
	private int var2;
	private final int MAX = 0;
	private Scanner input;

	public Thing() {
		this("", 0);
	}

	public Thing(String var1, int var2) {
		setVar1(var1);
		setVar2(var2);
	}

	public static void main(String args[]) {
		
	}

	public void Something(){
		return ;
	}

	public String returnSomething(String var1, int var2){
		return "";
	}

	public void setVar1(String var1){
		this.var1 = var1;
	}

	public void setVar2(int var2){
		this.var2 = var2;
	}

	public String getVar1(){
		return var1;
	}

	public int getVar2(){
		return var2;
	}

	public String toString() {
		return "\nvar1: " + getVar1() + "\nvar2: " + getVar2();
	}
}
