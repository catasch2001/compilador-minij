package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;
import ast.visitor.Variable;

public class IdentifierExpr extends Expr {
	public String s;

	public IdentifierExpr(String as, int ln) {
		super(ln);
		s = as;
	}

	@Override
    public ExprType getType(SemanticVisitor v) {
        Variable var = v.lookupVariable(s);
        if (var == null) {
            v.errors.add("Variable no declarada: " + s);
            return new ExprType("error");
        }
        var.used = true;
        return new ExprType(var.getTypeName());
    }
}
