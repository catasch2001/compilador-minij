package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class Null extends Expr {
	public Null(int ln) {
		super(ln);
	}
	
	public ExprType getType(SemanticVisitor v) { return new ExprType("null"); }

}
