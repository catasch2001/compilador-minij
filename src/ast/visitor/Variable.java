package ast.visitor;

import ast.BooleanType;
import ast.ClassType;
import ast.Identifier;
import ast.IntArrayType;
import ast.IntType;
import ast.StringType;
import ast.Type;

public class Variable {
	public Type type;
	public Identifier id;
	public boolean used;

	public Variable (Type t, Identifier i) {
		this.type = t;
		this.id = i;
		this.used = false;
	}
	
	public String getTypeName() {
	    if (type instanceof IntType)      return "int";
	    if (type instanceof BooleanType)  return "boolean";
	    if (type instanceof IntArrayType) return "int[]";
	    if (type instanceof StringType)   return "String";
	    if (type instanceof ClassType)    return ((ClassType) type).className;
	    return "error";
	}

}