package src;

public class Data {

	protected String name;
	protected String type;
	
	public Data (String name, String type){
		this.name = name;
		this.type = type;
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
}