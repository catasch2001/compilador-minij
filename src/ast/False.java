package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class False extends Expr {
	public False(int ln) {
		super(ln);
	}
	
	public ExprType getType(SemanticVisitor v) { return new ExprType("boolean"); }

}
