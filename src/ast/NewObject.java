package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class NewObject extends Expr {
	public Identifier i;

	public NewObject(Identifier ai, int ln) {
		super(ln);
		i = ai;
	}
	
	@Override
	public ExprType getType(SemanticVisitor v) {
	    return new ExprType(i.s);
	}
}
