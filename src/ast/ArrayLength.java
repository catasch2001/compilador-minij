package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class ArrayLength extends Expr {
	public Expr e;

	public ArrayLength(Expr ae, int ln) {
		super(ln);
		e = ae;
	}
	
	public ExprType getType(SemanticVisitor v) { return new ExprType("int"); }

}
