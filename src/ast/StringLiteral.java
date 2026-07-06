package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class StringLiteral extends Expr {
	public String s;
	
	public StringLiteral(String as, int ln) {
		super(ln);
		s = as;
	}
	
	public ExprType getType(SemanticVisitor v) { return new ExprType("String"); }

}
