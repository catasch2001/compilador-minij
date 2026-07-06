package ast.visitor;

import java.util.HashMap;

public class VariableScope {
	public HashMap<String, Variable> varSet = new HashMap<>();
	
	public VariableScope(){
		varSet = new HashMap<>();
	}
	
	public void addSymbol(String id, Variable var) throws Exception {
       if(varSet.containsKey(id))
    	   throw new Exception("Variable duplicada: "+id);
       varSet.put(id, var);
	}
	
	public Variable lookup(String id) {
        return varSet.getOrDefault(id, null);
    }

}
