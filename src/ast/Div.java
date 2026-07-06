package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class Div extends Expr {
	public Expr e1;
	public Expr e2;

	public Div(Expr ae1, Expr ae2, int ln) {
		super(ln);
		e1 = ae1;
		e2 = ae2;
	}
	
	public ExprType getType(SemanticVisitor v) { 
		String left = e1.getType(v).getName();
		String right = e2.getType(v).getName();
		if(!left.equals("int"))
			v.errors.add("Linea" + getLine() + " : operador izquierdo de '/' debe ser int, obenido: " + left);
		if(!right.equals("int"))
			v.errors.add("Linea" + getLine() + " : operador derecho de '/' debe ser int, obenido: " + right);
		return new ExprType("int"); 
	}

}
