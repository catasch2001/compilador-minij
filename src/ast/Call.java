package ast;

import ast.visitor.ExprType;
import ast.visitor.SemanticVisitor;

public class Call extends Expr {
	public Expr e;
	public Identifier i;
	public ExprList el;

	public Call(Expr ae, Identifier ai, ExprList ael, int ln) {
		super(ln);
		e = ae;
		i = ai;
		el = ael;
	}
	

	@Override
	public ExprType getType(SemanticVisitor v) {
	    // evaluar argumentos
	    for (int idx = 0; idx < el.size(); idx++)
	        el.get(idx).getType(v);

	    String ownerType = e.getType(v).getName();
	    MethodDecl m = v.programContainer
	                    .lookupMethodInHierarchy(ownerType, i.s);
	    if (m == null) {
	        v.errors.add("Linea " + getLine() + ": Metodo no definido: " + ownerType + "." + i.s);
	        return new ExprType("error");
	    }

	    //verificar cantidad y tipos de parámetros
	    if (el.size() != m.fl.size()) {
	        v.errors.add("Linea " + getLine() + ": Numero de argumentos incorrecto en " 
	                     + ownerType + "." + i.s
	                     + ". Esperado: " + m.fl.size()
	                     + ", obtenido: " + el.size());
	    } else {
	        for (int idx = 0; idx < el.size(); idx++) {
	            String argType   = el.get(idx).getType(v).getName();
	            String paramType = v.typeNameOf(m.fl.get(idx).t);
	            if (!v.isCompatible(paramType, argType))
	                v.errors.add("Linea " + getLine() + ": Argumento " + idx + " de "
	                             + ownerType + "." + i.s
	                             + ". Esperado: " + paramType
	                             + ", obtenido: " + argType);
	        }
	    }

	    return new ExprType(v.typeNameOf(m.t));
	}
}
