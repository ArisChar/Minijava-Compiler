package src;
import java.util.Map;
import java.util.LinkedHashMap;

public class Symbol_table {

    private LinkedHashMap<String, Class_data> class_list;
    private String Class_scope;
    private String Method_scope;
    
    public Symbol_table() {
        this.class_list = new LinkedHashMap<String, Class_data>();
        this.Method_scope = null;
        this.Class_scope = null;
    }

    public String getMethod_scope() {
        return Method_scope;
    }

    public void setMethod_scope(String method_scope) {
        this.Method_scope = method_scope;
    }

    public void resetMethod_scope() {
        this.Method_scope = null;
    }

    public String getClass_scope() {
        return Class_scope;
    }

    public void setClass_scope(String class_scope) {
        this.Class_scope = class_scope;
    }

    public void resetClass_scope() {
        this.Class_scope = null;
    }

    public Class_data getClass_data(String class_name){
        return class_list.get(class_name);
    }

    // add class to symboltable if it already contains it throw exception
    public void addClass(String class_name, String parent_name) throws Exception{
        Class_data class_data = new Class_data(class_name, parent_name);
        if(class_list.containsKey(class_name)){
            throw new Exception("Error: class " + class_name + " has already been declared!");
        }else{
            // if class name is same with parent means we do not have a parent
            if(class_name.equals(parent_name)){
                class_list.put(class_name, class_data);
            }else{
                // check if parent class exists else throw exception
                if(class_list.containsKey(parent_name)){
                    Class_data parent = getClass_data(parent_name);

                    class_data.setFields_offset(parent.getFields_offset());
                    class_data.setMethod_offset(parent.getMethod_offset());
                    class_list.put(class_name, class_data);
                }else{
                    throw new Exception("Error: parent class " + parent_name + " has not been declared!");
                }
            }
        }
    }

    // method to print the offsets of the program
    public void printOffset_symTable() {

        for (Map.Entry<String, Class_data> entry : class_list.entrySet()) {
			Class_data class_data = entry.getValue();

            if(class_data.get_isMain()){
                continue;
            }else{
                System.out.println("-----------Class "+ class_data.getName() +"-----------");
                class_data.printOffset();
            }
			System.out.println();
		}
	}

    // check if method is override and then add the method to the class of the symboltable
    public void overrideCheck(String argumentList, String method_name, String method_type, Class_data class_data) throws Exception{
        String class_name = class_data.getName();
        String parent_class = class_data.getType();        
        String[] args = argumentList.split(",");
        boolean isOverride;

        // if the name of the class is the same with the parent we do not have a parent
        if(parent_class.equals(class_name)){
            isOverride = false;
        }else{
            isOverride = true;
            boolean hasMethod = true;
            Class_data parent = this.getClass_data(parent_class);
            Method_data par_method = parent.getMethod(method_name);

            // check if the method exists in the parent class and all its parent( if there are any)
            while(hasMethod){
                if(par_method != null){
                    // check the return type and the number of parameters
                    if(!par_method.getType().equals(method_type)){
                        throw new Exception("Error: override method " + method_name + "has different return type!");
                    }else if(args.length != par_method.getTotal_param()){
                        throw new Exception("Error: override method " + method_name + " has different number of parameters!");
                    }else{
                        // check the alls parameters type one by one
                        int i = 0;
                        for(String arg: args){
                            String[] types = arg.trim().split("\\s+");
                            Variable_data param = par_method.getParN(i);
                            if(!types[0].equals(param.getType())){
                                throw new Exception("Error: override method " + method_name + " has wrong type of parameters!");
                            }
                            i++;
                        }
                    }
                    hasMethod = false;
                }else{
                    if ((parent.getName()).equals(parent.getType())){
                        hasMethod = false;
                    }else{
                        parent = this.getClass_data(parent.getType());
                        par_method = parent.getMethod(method_name);
                    }
                }
            }
        }
        // add method to class of symboltable
        class_data.addMethod(method_name, method_type, isOverride);
        Method_data method = class_data.getMethod(method_name);

        if(argumentList != ""){
            for(String arg: args){
                // add the parameters to the method we found
                String[] types = arg.trim().split("\\s+");
                String var_name = types[1];
                String var_type = types[0];
                
                method.addParam(var_name, var_type);
            }   
        }else{
            method.addParam("", "");
        }
    }

    // check if value is of primary type
    public boolean checkPrimary(String type){
        if(type.equals("int") || type.equals("boolean") || type.equals("int[]") || type.equals("boolean[]")){
            return true;
        }else{
            return false;
        }
    }

    public void methodCheck(String method_type, String returnType) throws Exception {
        
        if(method_type.equals(returnType)){
            return ;
        }else{
            String curr_class = this.getClass_scope();
            Class_data class_data = this.getClass_data(curr_class);

            String class_name = class_data.getName();
            String parent_class = class_data.getType();

            // check if class has parent
            if(parent_class.equals(class_name)){
                throw new Exception("Error: on method return type!");
            }else{
                // check in a loop the parents of the class
                while(!method_type.equals(parent_class)){
                    Class_data parent = this.getClass_data(parent_class);

                    // if name of class is the same with parent means it has no parent
                    if ((parent.getName()).equals(parent.getType())){
                        throw new Exception("Error: on method return type!");
                    }else{
                        parent = this.getClass_data(parent.getType());
                        parent_class = parent.getType();
                    }
                }
                return;
            }
        }
    }

    public void assignCheck(String value1, String value2) throws Exception{
        // check if values are primary
        if(this.checkPrimary(value1) && this.checkPrimary(value2)){
            if(value1.equals(value2)){
                return;
            }else{
                throw new Exception("Error: values are not of same Primary type!");
            }
        }

        if(this.getClass_data(value2) == null){
            throw new Exception("Error: Value does not exist!");
        }else{
            Class_data class_data = this.getClass_data(value2);
            String class_name = class_data.getName();
          
            // check in a loop the parents of the class
            while(!class_name.equals(value1)){
                String parent_class = class_data.getType();
                if ((class_name).equals(parent_class)){
                    throw new Exception("Error: Value does not exist!");
                }else{
                    class_data = this.getClass_data(parent_class);
                    class_name = class_data.getName();
                }
            }
            return;
        }
    }

    // retrieve the type of given value
    public String retrieveType(String name){
        String class_name = this.getClass_scope();
        String method_name = this.getMethod_scope();

        // System.out.println("class_name: " + class_name);
        // System.out.println("method_name: " + method_name);
        // System.out.println("Name: " + name );

        if(class_name == null && method_name == null){
            return null;
        }

        Class_data class_data = this.getClass_data(class_name);
        Method_data method = class_data.getMethod(method_name);

        // check the field of the given class by checking tha class and its super classes
        while(class_data.getField(name) == null){
            String parent_name = class_data.getType();
            if(class_name.equals(parent_name)){
                class_data = this.getClass_data(this.getClass_scope());
                method = class_data.getMethod(this.getMethod_scope());
                // if we did not find break 
                break;
            }else{
                class_data = this.getClass_data(parent_name);
                class_name = class_data.getName();
            }

        }

        if(method_name != null){
            Variable_data var = method.getVarData(name);
            if(var != null){
                return var.getType();
            }
        }

        // if the value we want to check is the same with the class the return is class type
        if(name == class_name){
            return name;
        }else{
            // else check to see if a class type variable was defined
            if( class_data.getField(name) == null){
                if(class_data.containsField(name)){
                    return name;
                }else{
                    return null;
                }
            }else{
                Variable_data var = class_data.getField(name);
                return var.getType();
            }
            
        }
        
    }

    public String messageCheck(Class_data class_data, String method_name, String argumentList)throws Exception{
        // check for the method in the class and its ancestors 
        while(class_data.getMethod(method_name) == null){
            String class_name = class_data.getName();
            String parent_name = class_data.getType();

            // if name of class is the same with parent means it has no parent
            if(class_name.equals(parent_name)){
                throw new Exception("Error: in MessageSend, method does not exist!");
            }else{
                class_data = this.getClass_data(parent_name);
            }
        }

        Method_data method = class_data.getMethod(method_name);

        if(argumentList != null){
            String[] args = argumentList.split(",");

            if(args.length != method.getTotal_param()){
                throw new Exception("Error: in MessageSend, wrong number of arguments in method");
            }
            
            int n = 0;
            for(String arg: args){
                String var_type = arg.trim();
                String par_type = method.getParN(n).getType();
                if(this.checkPrimary(var_type)){
                    if(!var_type.equals(par_type)){
                        throw new Exception("Error in MessageSend, argument type is wrong!");
                    }
                }else{
                    // check if var exist
                    if(this.getClass_data(var_type) == null){
                        throw new Exception("Error in MessageSend, argument type is wrong!");
                    }else{

                        class_data = this.getClass_data(var_type);
                        String class_name = class_data.getName();

                        while(!class_name.equals(par_type)){
                            String parent_name = class_data.getType();
                            if(class_name.equals(parent_name)){
                                throw new Exception("Error in MessageSend, argument type is wrong!");
                            }else{
                                class_data = this.getClass_data(parent_name);
                                class_name = class_data.getName();
                            }
                        }
                    }
                }
                n++;
            }
        }
        return method.getType();
    }
}