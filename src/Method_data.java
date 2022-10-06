package src;
import java.util.HashMap;
import java.util.ArrayList;

public class Method_data extends Data{

	private ArrayList<Variable_data> par_list;
    private HashMap<String, Variable_data> par_map;
    private HashMap<String, Variable_data> var_map;

    public Method_data(String name, String type) {
		super(name, type);
        this.par_list = new ArrayList<Variable_data> ();
        this.par_map = new HashMap<> ();
        this.var_map = new HashMap<> ();
    }

    // return the nth values of the list
    public Variable_data getParN(int n){
        return par_list.get(n);
    }

    // add a parameter to the map and  the list
    public void addParam(String param_name, String param_type) throws Exception{
        Variable_data par = new Variable_data(param_name, param_type);

        if(par_map.containsKey(param_name)){
            throw new Exception("Error: variable " + param_name + " has already been declared in this method!");
        }else{
            par_map.put(param_name, par);
            par_list.add(par);
        }
    }

    // get the number of total parameters of a method
    public int getTotal_param() {
		return par_list.size();
	}

    // get a variable data by first checking in the parameters and then the variables
    public Variable_data getVarData(String name){
        if(par_map.containsKey(name)){
            return par_map.get(name);
        }else{
            if(var_map.containsKey(name)){
                return var_map.get(name);
            }else{
                return null;
            }
        }
    }

    // add a variable in the method class throw exception if it already exist in variable or the parameter map
    public void addVar(String var_name, String var_type) throws Exception{
        Variable_data var = new Variable_data(var_name, var_type);

        if(var_map.containsKey(var_name)){
            throw new Exception("Error: variable " + var_name + " has already been declared in this method!");
        }else if(par_map.containsKey(var_name)){
            throw new Exception("Error: variable " + var_name + " has already been declared in this method!");
        }else{
            var_map.put(var_name, var);
        }
    }
}