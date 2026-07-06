package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class Not extends Expr {
    public Expr e;

    public Not(Expr ae, int ln) {
        super(ln);
        e = ae;
    }
    
	public ExprType getType(SemanticVisitor v) { 
		e.getType(v);
		return new ExprType("boolean"); 
	}

}