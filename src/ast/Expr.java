package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public abstract class Expr extends Node {

	public Expr(int line) {
		super(line);
	}
	
	public abstract ExprType getType(SemanticVisitor v);

}
