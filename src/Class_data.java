package src;
import java.util.HashMap;
import java.util.LinkedHashMap;
// import java.util.*;

public class Class_data extends Data{

    private LinkedHashMap<String, Integer> methodsOffset_list;
    private HashMap<String, Method_data> methods;
    private int methodOffset;

    private LinkedHashMap<String, Integer> fieldsOffset_list;   
    private HashMap<String, Variable_data> fields;
    private int fieldsOffset;

    // to see if class is main
    boolean isMain;

    public Class_data(String name, String parent) {
		super(name, parent);

        this.methodsOffset_list = new LinkedHashMap<String, Integer>();
        this.methods = new HashMap<>();
        this.methodOffset = 0;

        this.fieldsOffset_list = new LinkedHashMap<String, Integer>();
        this.fields = new HashMap<>();
        this.fieldsOffset = 0;

        isMain = false;
	}

    public boolean get_isMain(){
        return isMain;
    }

    public void set_isMain(){
        isMain = true;
    }

	public int getFields_offset() {
        return fieldsOffset;
    }

    public void setFields_offset(int fieldsOffset) {
        this.fieldsOffset = fieldsOffset;
    }

    public int getMethod_offset() {
        return methodOffset;
    }

    public void setMethod_offset(int methodOffset) {
        this.methodOffset = methodOffset;
    }

    public Method_data getMethod(String method_name){
		return methods.get(method_name);
	}

    // add method to the map that stores them throw exception if it allready exists
    // we also have a boolean so that if it is an ovveride method to add the right offset
    public void addMethod(String method_name, String method_type, Boolean isOverride) throws Exception{
        Method_data method_data = new Method_data(method_name, method_type);

		if(methods.containsKey(method_name)){
            throw new Exception("Error: method " + method_name + " has already been declared!");
        }else{

            if(method_name.equals("main")){
                set_isMain();
            }
            
            methods.put(method_name, method_data);

            if(!isOverride){
                methodsOffset_list.put(method_name, methodOffset);
                methodOffset += 8; 
            }
        }
	}	

    public Variable_data getField(String field_name){
		return fields.get(field_name);
	}

    // add a variable in the field map
	public void addField(String field_name, String field_type) throws Exception{
        Variable_data field_data = new Variable_data(field_name, field_type);

		if(fields.containsKey(field_name)){
            throw new Exception("Error: variable" + field_name + " has already been declared!");
        }else{
            fieldsOffset_list.put(field_name, fieldsOffset); 
            fields.put(field_name, field_data);
        }

        switch (field_data.getType()) {
            case "int":
                fieldsOffset += 4;
                break;
            case "boolean":
                fieldsOffset += 1; 
                break;
            default:
                fieldsOffset += 8; 
                break;
        }
	}

    // to check if the field of the class contains given type
    public boolean containsField(String field_type){

		for (HashMap.Entry<String, Variable_data> entry : fields.entrySet()) {
            Variable_data curr_fieldinfo = entry.getValue();
            
            if(curr_fieldinfo.getType() == field_type){
                return true;
            }
        }

        return false;
	}
    
    // method to print the offset of a class
    public void printOffset(){

        System.out.println("---Variables---");
        for (String key: fieldsOffset_list.keySet()){
            System.out.println(name + "." + key + " : " + fieldsOffset_list.get(key));
        }

        System.out.println("---Methods---");
        for (String key: methodsOffset_list.keySet()){
            System.out.println(name + "." + key + " : " + methodsOffset_list.get(key));
        }
        
    }
}