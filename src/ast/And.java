package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class And extends Expr {
	public Expr e1;
	public Expr e2;

	public And(int line, Expr e1, Expr e2) {
		super(line);
		this.e1 = e1;
		this.e2 = e2;
	}
	
	public ExprType getType(SemanticVisitor v) { 
		e1.getType(v);
		e2.getType(v);
		return new ExprType("boolean"); 
		}

}
