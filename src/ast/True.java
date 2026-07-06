package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class True  extends Expr{
	public True(int ln) {
		super(ln);
	}
	public ExprType getType(SemanticVisitor v) { return new ExprType("boolean"); }

}
