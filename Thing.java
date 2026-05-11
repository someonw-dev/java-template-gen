include java.io.Serializable;

public class Thing extends Object1 implements Serializable {
	private String var1;
	private int var2;

	public Thing() {
		this("", 0);
	}

	public Thing(String var1, int var2) {
		setVar1(var1);
		setVar2(var2);
	}

	public String getVar1(){
		return var1;
	}

	public int getVar2(){
		return var2;
	}

	public void setVar1(String var1){
		this.var1 = var1;
	}

	public void setVar2(int var2){
		this.var2 = var2;
	}
}