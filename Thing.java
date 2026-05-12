import java.io.Serializable;

public class Thing extends BaseThing implements Area, Serializable {
	private String name;
	private int count;

	public Thing() {
		this("", 0);
	}

	public Thing(String name, int count) {
		setName(name);
		setCount(count);
	}

	public void Something(){
		return ;
	}

	public String returnSomething(String var1, int var2){
		return "";
	}

	public void setName(String name){
		this.name = name;
	}

	public void setCount(int count){
		this.count = count;
	}

	public String getName(){
		return name;
	}

	public int getCount(){
		return count;
	}

	public String toString() {
		return "\nname: " + getName() + "\ncount: " + getCount();
	}
}
