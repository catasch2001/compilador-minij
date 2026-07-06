package ast.visitor;

import java.util.ArrayList;
import java.util.List;

import ast.And;
import ast.ArrayAssign;
import ast.ArrayLength;
import ast.ArrayLookup;
import ast.Assign;
import ast.Block;
import ast.BooleanType;
import ast.Call;
import ast.ClassDecl;
import ast.ClassDeclExtends;
import ast.ClassDeclSimple;
import ast.ClassType;
import ast.Div;
import ast.Equal;
import ast.False;
import ast.Goal;
import ast.Identifier;
import ast.IdentifierExpr;
import ast.If;
import ast.IntArrayType;
import ast.IntType;
import ast.IntegerLiteral;
import ast.LessThan;
import ast.MainClass;
import ast.MethodDecl;
import ast.MethodDeclList;
import ast.Minus;
import ast.MoreThan;
import ast.Mult;
import ast.NewArray;
import ast.NewObject;
import ast.Not;
import ast.NotEqual;
import ast.Null;
import ast.Or;
import ast.Param;
import ast.Plus;
import ast.Print;
import ast.Statement;
import ast.StringLiteral;
import ast.StringType;
import ast.This;
import ast.True;
import ast.Type;
import ast.VarDecl;
import ast.VarDeclList;
import ast.While;


public class SemanticVisitor implements Visitor {
	public List<String> errors = new ArrayList<>();
	public List<String> warnings = new ArrayList<>();
	public VariableScopeStack scopeStack = new VariableScopeStack();
	public ProgramContainer programContainer = new ProgramContainer();
	public ClassContent currentClass;
	public MethodDecl currentMethod;  
	
	public Variable lookupVariable(String name) {
	    // primero busca en scope
	    Variable v = scopeStack.variableLookup(name);
	    if (v != null) return v;

	    // si no esta busca en campos de la clase actual
	    if (currentClass != null) {
	        Variable field = currentClass.fields.get(name);
	        if (field != null) {
	            field.used = true;
	            return field;
	        }
	    }
	    return null;
	}
	
	@Override
	public void visit(Goal n) {
		//construir la tabla de símbolos
		for (int i = 0; i<n.cl.size();i++)
			registerClass(n.cl.get(i));
		
		//revisar cuerpo
		visit(n.m);
		for(int i = 0; i<n.cl.size();i++) {
	        if(n.cl.get(i) instanceof ClassDeclExtends)        
	            visit((ClassDeclExtends) n.cl.get(i));
	        else if(n.cl.get(i) instanceof ClassDeclSimple)    
	            visit((ClassDeclSimple) n.cl.get(i));
	    }
		printErrors();
	}
	
	private void registerClass(ClassDecl c) {
	    String name = (c instanceof ClassDeclExtends) 
	    		? ((ClassDeclExtends) c).i.s
	            : ((ClassDeclSimple) c).i.s;
	    String parent = (c instanceof ClassDeclExtends) 
	            ? ((ClassDeclExtends) c).j.s 
	            : null;
	    ClassContent cc = new ClassContent(name, parent);
	    VarDeclList vl = (c instanceof ClassDeclExtends) 
	    	    ? ((ClassDeclExtends) c).vl 
	    	    : ((ClassDeclSimple) c).vl;
	    MethodDeclList ml = (c instanceof ClassDeclExtends) 
	    	    ? ((ClassDeclExtends) c).ml 
	    	    : ((ClassDeclSimple) c).ml;
		for (int i = 0; i<vl.size(); i++)
			cc.fields.put(vl.get(i).i.s, new Variable(vl.get(i).t, vl.get(i).i));
		
		for (int i = 0; i<ml.size(); i++)
			cc.methods.put(ml.get(i).i.s, ml.get(i));
		
		try {
			programContainer.registerClass(cc);
		}catch(Exception e) {
			errors.add("Liena " + c.getLine() + ":" + e.getMessage());
		}
	}

	@Override
	public void visit(MainClass n) {
		currentClass = programContainer.lookupClass(n.i1.s);
		scopeStack.pushScope();
		
		for(int i = 0; i < n.vl.size();i++)
			visit(n.vl.get(i));
		for(int i = 0 ; i < n.sl.size(); i++)
			visitStatement(n.sl.get(i));
	
		checkUnusedVars();
		eliminateUnusedVars(n.vl, scopeStack.stack.peek());
	    scopeStack.popScope();
	}

	@Override
	public void visit(ClassDeclSimple n) {
		currentClass = programContainer.lookupClass(n.i.s);
		for(int i = 0; i < n.ml.size(); i++)
			visit(n.ml.get(i));
	}

	@Override
	public void visit(ClassDeclExtends n) {
		currentClass = programContainer.lookupClass(n.i.s);
		for(int i = 0; i<n.ml.size(); i++)
			visit(n.ml.get(i));
	}

	@Override
	public void visit(MethodDecl n) {
		currentMethod = n;
		scopeStack.pushScope();
		
		//registrar parametros
		for ( int i = 0; i<n.fl.size(); i++) {
			try {
				scopeStack.insertSymbol(n.fl.get(i).t, n.fl.get(i).i);
			}catch(Exception e) {
				errors.add(e.getMessage());
			}
		}
		//registrar variables locales
		for ( int i = 0; i< n.vl.size(); i++)
			visit(n.vl.get(i));
		
		//chequear statements
		for(int i = 0 ; i<n.sl.size(); i++)
			visitStatement(n.sl.get(i));
		
		//tipo de retorno
		String retType = typeNameOf(n.t);
		String exprType = n.e.getType(this).getName();
		if (!isCompatible(retType, exprType))
			errors.add("Liena " + n.getLine() + ": retorno incorrecto en '" + n.i.s + "'. Esperado: " + retType + ", obtenido: "+ exprType);
		
		checkUnusedVars();
		eliminateUnusedVars(n.vl, scopeStack.stack.peek());
	    scopeStack.popScope();
	}

	@Override
	public void visit(VarDecl n) {
	    try {
	        scopeStack.insertSymbol(n.t, n.i);
	    } catch (Exception e) {
	        errors.add("Linea " + n.getLine() + ": " + e.getMessage());
	    }
	}
	
	@Override
	public void visit(Assign n) {
	    Variable v = lookupVariable(n.i.s);
	    if (v == null) {
	        errors.add("Linea " + n.getLine() + ": variable no declarada: " + n.i.s);
	        return;
	    }
	    v.used = true;
	    String varType  = v.getTypeName();
	    String exprType = n.e.getType(this).getName();
	    if (exprType.equals("error")) return; 
	    if (!isCompatible(varType, exprType))
	        errors.add("Linea " + n.getLine() + ": tipo incorrecto en asignacion a '"
	                   + n.i.s + "'. Esperado: " + varType
	                   + ", obtenido: " + exprType);
	}
	
	private void visitStatement(Statement s) {
	    if (s instanceof Assign)      visit((Assign) s);
	    else if (s instanceof If)     visit((If) s);
	    else if (s instanceof While)  visit((While) s);
	    else if (s instanceof Print)  visit((Print) s);
	    else if (s instanceof Block)  visit((Block) s);
	    else if (s instanceof ArrayAssign) visit((ArrayAssign) s);
	}

	private void checkUnusedVars() {
	    for (Variable v : scopeStack.getUnusedInCurrentScope()) {
	        // advertir solo variables locales
	        boolean isParam = false;
	        if (currentMethod != null) {
	            for (int i = 0; i < currentMethod.fl.size(); i++) {
	                if (currentMethod.fl.get(i).i.s.equals(v.id.s)) {
	                    isParam = true;
	                    break;
	                }
	            }
	        }
	        if (!isParam)
	            warnings.add("WARNING: variable '" + v.id.s + "' declarada pero no usada");
	    }
	}
	private void eliminateUnusedVars(VarDeclList vl, VariableScope scope) {
		List<VarDecl> toRemove = new ArrayList<>();
		for(int i = 0; i<vl.size();i++) {
			VarDecl vd = vl.get(i);
			Variable v = scope.lookup(vd.i.s);
			if(v!=null && !v.used) {
				toRemove.add(vd);
			}
		}
		for(VarDecl vd : toRemove) {
			vl.remove(vd);
			System.out.println("  [AST] Variable eliminada del arbol: " + vd.i.s);
		}
	}
	
	public boolean isCompatible(String expected, String actual) {
	    if (expected.equals(actual)) return true;
	    if (actual.equals("null"))   return true;
	    return programContainer.checkInheritanceOf(actual, expected);
	}

	public String typeNameOf(Type t) {
	    if (t instanceof IntType)      return "int";
	    if (t instanceof BooleanType)  return "boolean";
	    if (t instanceof IntArrayType) return "int[]";
	    if (t instanceof StringType)   return "String";
	    if (t instanceof ClassType)    return ((ClassType) t).className;
	    return "error";
	}

	private void printErrors() {
		for (String w : warnings)
			System.out.println("" + w);
		
	    if (errors.isEmpty()) {
	        System.out.println("Analisis semantico: OK");
	    } else {
	        System.out.println("### Errores semanticos ###");
	        for (String e : errors) System.out.println("  " + e);
	        System.out.println(errors.size() + " error(es) encontrado(s).");
	        System.out.println(warnings.size() + " warning(s) encontrado(s).");
	    }
	}

	@Override
	public void visit(Param n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IntArrayType n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IntType n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ClassType n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Block n) {
		for (int i = 0; i<n.sl.size();i++)
			visitStatement(n.sl.get(i));
	}

	@Override
	public void visit(If n) {
		n.e.getType(this);
		visitStatement(n.s1);
		if (n.hasElse) visitStatement(n.s2);
	}

	@Override
	public void visit(While n) {
		n.e.getType(this);
		visitStatement(n.s);
	}

	@Override
	public void visit(Print n) {
		n.e.getType(this);
	}

	@Override
	public void visit(ArrayAssign n) {
		Variable v = lookupVariable(n.i.s);
	    if (v != null) v.used = true;
	    n.e1.getType(this);
	    n.e2.getType(this);
	}

	@Override
	public void visit(And n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Or n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Equal n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NotEqual n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LessThan n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MoreThan n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Plus n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Minus n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Mult n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Div n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayLookup n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ArrayLength n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Call n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IntegerLiteral n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IdentifierExpr n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(This n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NewArray n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NewObject n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Not n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(True n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(False n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Null n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(StringLiteral n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(StringType n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(BooleanType n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Identifier n) {
		// TODO Auto-generated method stub
		
	}
	

}
