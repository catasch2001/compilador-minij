package ast.visitor;

import java.util.HashMap;

public class JasminVariableScope {
	private HashMap<String, Integer> indexMap = new HashMap<>();
	private HashMap<String, String> typeMap = new HashMap<>();

	public void insert(String name, int index, String type) {
		indexMap.put(name, index);
		typeMap.put(name, type);
	}
	
	public Integer lookupIndex(String name) {
		return indexMap.getOrDefault(name, null);
	}
	
	public String lookupType(String name) {
		return typeMap.getOrDefault(name, null);
	}
	
	public boolean contains(String name) {
		return indexMap.containsKey(name);
	}
} 
