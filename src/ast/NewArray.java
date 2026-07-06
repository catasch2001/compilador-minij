package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class NewArray extends Expr {
	public Expr e;

	public NewArray(Expr ae, int ln) {
		super(ln);
		e = ae;
	}
	public ExprType getType(SemanticVisitor v) { 
		e.getType(v);
		return new ExprType("int[]"); }

}
