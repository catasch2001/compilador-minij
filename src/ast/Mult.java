package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class Mult extends Expr {
	public Expr e1;
	public Expr e2;

	public Mult(Expr ae1, Expr ae2, int ln) {
		super(ln);
		e1 = ae1;
		e2 = ae2;
	}
	
	public ExprType getType(SemanticVisitor v) { 
		String left = e1.getType(v).getName();
		String right = e2.getType(v).getName();
		if(!left.equals("int"))
			v.errors.add("Linea" + getLine() + " : operador izquierdo de '*' debe ser int, obtenido: " + left);
		if(!right.equals("int"))
			v.errors.add("Linea" + getLine() + " : operador derecho de '*' debe ser int, obtenido: " + right);
		return new ExprType("int"); 
	}
}
