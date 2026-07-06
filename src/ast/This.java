package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class This extends Expr {
	public This(int ln) {
		super(ln);
	}
	
	@Override
	public ExprType getType(SemanticVisitor v) {
	    return new ExprType(v.currentClass.className);
	}
}
