package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class IntegerLiteral extends Expr {
	public int i;

	public IntegerLiteral(int ai, int ln) {
		super(ln);
		i = ai;
	}
	
	public ExprType getType(SemanticVisitor v) { return new ExprType("int"); }

}
