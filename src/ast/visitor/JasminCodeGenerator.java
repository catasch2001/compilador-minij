package ast.visitor;

import java.io.File;
import java.util.Stack;

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
import ast.Expr;
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
import ast.StringLiteral;
import ast.StringType;
import ast.This;
import ast.True;
import ast.Type;
import ast.VarDecl;
import ast.While;

public class JasminCodeGenerator implements Visitor {
	private ProgramContainer  programContainer;
	private String outputDir;
	
	//estado temporal para generar una clase
	private StringBuilder classCode;
	private String currentClassName;
	
	//estado temporal para generar un metodo
	private StringBuilder methodBody;
	private JasminVariableScope localVars;
	private int nextLocalIndex;
	
	//pila para generador de codigo
	private Stack<String> exprCode = new Stack<>();
	//pila para tipo de expresion
	private Stack<String> exprType = new Stack<>();
	
	private int labelCounter = 0;
	private String newLabel() { return "L" + (labelCounter++); }

	private String jasminType(Type t) {
	    if (t instanceof IntType) return "I";
	    if (t instanceof BooleanType) return "Z";
	    if (t instanceof IntArrayType) return "[I";
	    if (t instanceof StringType) return "Ljava/lang/String;";
	    if (t instanceof ClassType) return "L" + ((ClassType) t).className + ";";
	    return "V";
	}

	private String jasminTypeFromName(String name) {
	    switch (name) {
	        case "int": return "I";
	        case "boolean": return "Z";
	        case "int[]": return "[I";
	        case "String": return "Ljava/lang/String;";
	        case "void": return "V";
	        default: return "L" + name + ";";
	    }
	}

	private String returnInst(String type) {
	    if (type.equals("int") || type.equals("boolean")) return "    ireturn";
	    if (type.equals("void")) return "    return";
	    return "    areturn";
	}

	private String typeNameOf(Type t) {
	    if (t instanceof IntType) return "int";
	    if (t instanceof BooleanType) return "boolean";
	    if (t instanceof IntArrayType) return "int[]";
	    if (t instanceof StringType) return "String";
	    if (t instanceof ClassType) return ((ClassType) t).className;
	    return "void";
	}
	
	private void binaryArith(Expr e1, Expr e2, String op) {
	    visit(e1);
	    String c1 = exprCode.pop();
	    exprType.pop();

	    visit(e2);
	    String c2 = exprCode.pop();
	    exprType.pop();

	    exprCode.push(c1 + c2 + "    " + op + "\n");
	    exprType.push("int");
	}


	private void binaryCompare(Expr e1, Expr e2, String cmpInst) {
	    visit(e1);
	    String c1 = exprCode.pop(); exprType.pop();
	    visit(e2);
	    String c2 = exprCode.pop(); exprType.pop();
	
	    String trueLabel = newLabel();
	    String endLabel = newLabel();
	
	    StringBuilder sb = new StringBuilder();
	    sb.append(c1).append(c2);
	    sb.append("	").append(cmpInst).append(" ").append(trueLabel).append("\n");
	    sb.append("	iconst_0\n");
	    sb.append("	goto ").append(endLabel).append("\n");
	    sb.append(trueLabel).append(":\n");
	    sb.append("	iconst_1\n");
	    sb.append(endLabel).append(":\n");
	
	    exprCode.push(sb.toString());
	    exprType.push("boolean");
	}

	private void writeFile(String className, String code) {
	    try {
	        java.io.FileWriter fw = new java.io.FileWriter(new File(outputDir, className + ".j"));
	        fw.write(code);
	        fw.close();
	    } catch (java.io.IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public JasminCodeGenerator(ProgramContainer pc, String outputDir) {
		this.programContainer = pc;
		this.outputDir = outputDir;
		new File(outputDir).mkdirs();
	}
	

	
	@Override
	public void visit(Goal n) {
		visit(n.m);
		for (int i = 0; i<n.cl.size(); i++) {
			ClassDecl c = n.cl.get(i);
			if ( c instanceof ClassDeclSimple) {
				visit((ClassDeclSimple) c);
			}else if (c instanceof ClassDeclExtends) {
				visit((ClassDeclExtends) c);
			}
		}
		
	}

	@Override
	public void visit(MainClass n) {
		currentClassName = n.i1.s;
		classCode = new StringBuilder();
		
		classCode.append(".class public ").append(currentClassName).append("\n");
		classCode.append(".super java/lang/Object\n\n");
		
		//contructor por defecto
		classCode.append(".method public <init>()V\n");
		classCode.append("	.limit stack 1\n");
		classCode.append("	.limit locals 1\n");
		classCode.append("	aload_0\n");
	    classCode.append("	invokespecial java/lang/Object/<init>()V\n");
	    classCode.append("	return\n");
	    classCode.append(".end method\n\n");
	    
	    //main statico 
	    localVars = new JasminVariableScope();
	    nextLocalIndex = 0;
	    localVars.insert(n.i2.s, nextLocalIndex++, "String[]");
	    
	    for (int i = 0; i< n.vl.size(); i++) {
	    	VarDecl vd = n.vl.get(i);
	    	localVars.insert(vd.i.s, nextLocalIndex++, typeNameOf(vd.t));
	    }
	    
	    methodBody = new StringBuilder();
	    
	    for ( int i = 0; i<n.sl.size();i++) {
	    	visit(n.sl.get(i));
	    }
	    
	    classCode.append(".method public static main([Ljava/lang/String;)V\n");
	    classCode.append("	.limit stack 20\n");
	    classCode.append("	.limit locals ").append(Math.max(nextLocalIndex, 1)).append("\n");
	    classCode.append(methodBody);
	    classCode.append("	return\n");
	    classCode.append(".end method\n");

	    writeFile(currentClassName, classCode.toString());
	    
	}

	@Override
	public void visit(ClassDeclSimple n) {
	    currentClassName = n.i.s;
	    classCode = new StringBuilder();

	    classCode.append(".class public ").append(currentClassName).append("\n");
	    classCode.append(".super java/lang/Object\n\n");

	    for (int i = 0; i < n.vl.size(); i++) {
	        VarDecl f = n.vl.get(i);
	        classCode.append(".field private ").append(f.i.s)
	                  .append(" ").append(jasminType(f.t)).append("\n");
	    }
	    classCode.append("\n");
	    classCode.append(".method public <init>()V\n");
	    classCode.append("	.limit stack 1\n");
	    classCode.append("	.limit locals 1\n");
	    classCode.append("	aload_0\n");
	    classCode.append("	invokespecial java/lang/Object/<init>()V\n");
	    classCode.append("	return\n");
	    classCode.append(".end method\n\n");

	    for (int i = 0; i < n.ml.size(); i++)
	        visit(n.ml.get(i));

	    writeFile(currentClassName, classCode.toString());
	}

	@Override
	public void visit(ClassDeclExtends n) {
	    currentClassName = n.i.s;
	    classCode = new StringBuilder();

	    classCode.append(".class public ").append(currentClassName).append("\n");
	    classCode.append(".super ").append(n.j.s).append("\n\n");

	    for (int i = 0; i < n.vl.size(); i++) {
	        VarDecl f = n.vl.get(i);
	        classCode.append(".field private ").append(f.i.s)
	                  .append(" ").append(jasminType(f.t)).append("\n");
	    }
	    classCode.append("\n");
	    classCode.append(".method public <init>()V\n");
	    classCode.append("	.limit stack 1\n");
	    classCode.append("	.limit locals 1\n");
	    classCode.append("	aload_0\n");
	    classCode.append("	invokespecial ").append(n.j.s).append("/<init>()V\n");
	    classCode.append("	return\n");
	    classCode.append(".end method\n\n");
	    for (int i = 0; i < n.ml.size(); i++)
	        visit(n.ml.get(i));

	    writeFile(currentClassName, classCode.toString());

		
	}

	@Override
	public void visit(VarDecl n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MethodDecl n) {
		localVars = new JasminVariableScope();
		nextLocalIndex = 0;
		localVars.insert("this", nextLocalIndex++, currentClassName);

		StringBuilder params = new StringBuilder();
		for (int i = 0; i < n.fl.size(); i++) {
		    Param p = n.fl.get(i);
		    String t = typeNameOf(p.t);
		    params.append(jasminType(p.t));
		    localVars.insert(p.i.s, nextLocalIndex++, t);
		}

		for (int i = 0; i < n.vl.size(); i++) {
		    VarDecl vd = n.vl.get(i);
		    localVars.insert(vd.i.s, nextLocalIndex++, typeNameOf(vd.t));
		}

	    String descriptor = "(" + params + ")" + jasminType(n.t);

	    methodBody = new StringBuilder();

	    for (int i = 0; i < n.sl.size(); i++)
	        visit(n.sl.get(i));

	    visit(n.e);
	    String retCode = exprCode.pop();
	    String retType = exprType.pop();
	    methodBody.append(retCode);
	    methodBody.append(returnInst(retType)).append("\n");

	    classCode.append(".method public ").append(n.i.s).append(descriptor).append("\n");
	    classCode.append("	.limit stack 20\n");
	    classCode.append("	.limit locals ").append(Math.max(nextLocalIndex, 1)).append("\n");
	    classCode.append(methodBody);
	    classCode.append(".end method\n\n");
		
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
		for (int i = 0; i < n.sl.size(); i++)
	        visit(n.sl.get(i));
	}

	@Override
	public void visit(If n) {
		String elseLabel = newLabel();
	    String endLabel = (n.hasElse) ? newLabel() : elseLabel;

	    visit(n.e);
	    methodBody.append(exprCode.pop());
	    exprType.pop();
	    methodBody.append("	ifeq ").append(elseLabel).append("\n");

	    visit(n.s1);
	    if (n.hasElse) {
	        methodBody.append("	goto ").append(endLabel).append("\n");
	        methodBody.append(elseLabel).append(":\n");
	        visit(n.s2);
	        methodBody.append(endLabel).append(":\n");
	    } else {
	        methodBody.append(elseLabel).append(":\n");
	    }

	}

	@Override
	public void visit(While n) {
		String startLabel = newLabel();
	    String endLabel = newLabel();

	    methodBody.append(startLabel).append(":\n");
	    visit(n.e);
	    methodBody.append(exprCode.pop());
	    exprType.pop();
	    methodBody.append("	ifeq ").append(endLabel).append("\n");

	    visit(n.s);
	    methodBody.append("	goto ").append(startLabel).append("\n");
	    methodBody.append(endLabel).append(":\n");
		
	}

	@Override
	public void visit(Print n) {
		visit(n.e);
	    String code = exprCode.pop();
	    String type = exprType.pop();

	    methodBody.append("	getstatic java/lang/System/out Ljava/io/PrintStream;\n");
	    methodBody.append(code);
	    if (type.equals("int") || type.equals("boolean"))
	        methodBody.append("	invokevirtual java/io/PrintStream/println(I)V\n");
	    else
	        methodBody.append("	invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");

	}

	@Override
	public void visit(Assign n) {
	    visit(n.e);
	    String code = exprCode.pop();
	    exprType.pop();

	    Integer idx = localVars.lookupIndex(n.i.s);
	    if (idx != null) {
	        String type = localVars.lookupType(n.i.s);
	        methodBody.append(code);
	        if (type.equals("int") || type.equals("boolean"))
	            methodBody.append("	istore ").append(idx).append("\n");
	        else
	            methodBody.append("	astore ").append(idx).append("\n");
	    } else {
	    	String fieldType = lookupFieldType(n.i.s);
	        String jType = jasminTypeFromName(fieldType);
	        methodBody.append("	aload_0\n");
	        methodBody.append(code);
	        methodBody.append("	putfield ").append(currentClassName).append("/")
	                   .append(n.i.s).append(" ").append(jType).append("\n");
	    }
	}

	@Override
	public void visit(ArrayAssign n) {
	    Integer idx = localVars.lookupIndex(n.i.s);
	    String loadArr;
	    if (idx != null) {
	        loadArr = "	aload " + idx + "\n";
	    } else {
	        String jType = jasminTypeFromName(lookupFieldType(n.i.s));
	        loadArr = "	aload_0\n	getfield " + currentClassName + "/" + n.i.s + " " + jType + "\n";
	    }

	    visit(n.e1);
	    String idxCode = exprCode.pop();
	    exprType.pop();

	    visit(n.e2);
	    String valCode = exprCode.pop();
	    exprType.pop();

	    methodBody.append(loadArr)
	              .append(idxCode)
	              .append(valCode)
	              .append("	iastore\n");
	}

	@Override
	public void visit(And n) {
		String falseLabel = newLabel();
	    String endLabel = newLabel();

	    visit(n.e1);
	    String c1 = exprCode.pop(); exprType.pop();
	    visit(n.e2);
	    String c2 = exprCode.pop(); exprType.pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(c1);
	    sb.append("	ifeq ").append(falseLabel).append("\n");
	    sb.append(c2);
	    sb.append("	ifeq ").append(falseLabel).append("\n");
	    sb.append("	iconst_1\n");
	    sb.append("	goto ").append(endLabel).append("\n");
	    sb.append(falseLabel).append(":\n");
	    sb.append("	iconst_0\n");
	    sb.append(endLabel).append(":\n");
	    exprCode.push(sb.toString());
	    exprType.push("boolean");
	}

	@Override
	public void visit(Or n) {
		String trueLabel = newLabel();
	    String endLabel = newLabel();
	    visit(n.e1);
	    String c1 = exprCode.pop(); exprType.pop();
	    visit(n.e2);
	    String c2 = exprCode.pop(); exprType.pop();
	    StringBuilder sb = new StringBuilder();
	    sb.append(c1);
	    sb.append("	ifne ").append(trueLabel).append("\n");
	    sb.append(c2);
	    sb.append("	ifne ").append(trueLabel).append("\n");
	    sb.append("	iconst_0\n");
	    sb.append("	goto ").append(endLabel).append("\n");
	    sb.append(trueLabel).append(":\n");
	    sb.append("	iconst_1\n");
	    sb.append(endLabel).append(":\n");

	    exprCode.push(sb.toString());
	    exprType.push("boolean");
	}

	@Override
	public void visit(Equal n) {
	    binaryCompare(n.e1, n.e2, "if_icmpeq");		
	}

	@Override
	public void visit(NotEqual n) {
	    binaryCompare(n.e1, n.e2, "if_icmpne");
	}

	@Override
	public void visit(LessThan n) {
	    binaryCompare(n.e1, n.e2, "if_icmplt");		
	}

	@Override
	public void visit(MoreThan n) {
	    binaryCompare(n.e1, n.e2, "if_icmpgt");		
	}

	@Override
	public void visit(Plus n) {
		binaryArith(n.e1, n.e2, "iadd");		
	}

	@Override
	public void visit(Minus n) {
		binaryArith(n.e1, n.e2, "isub");
		
	}

	@Override
	public void visit(Mult n) {
		binaryArith(n.e1, n.e2, "imul");
		
	}

	@Override
	public void visit(Div n) {
		binaryArith(n.e1, n.e2, "idiv");
		
	}

	@Override
	public void visit(ArrayLookup n) {
		visit(n.e1);
	    String arrCode = exprCode.pop();
	    exprType.pop();

	    visit(n.e2);
	    String idxCode = exprCode.pop();
	    exprType.pop();

	    exprCode.push(arrCode + idxCode + "	iaload\n");
	    exprType.push("int");
	}

	@Override
	public void visit(ArrayLength n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Call n) {
		visit(n.e);
	    String objCode = exprCode.pop();
	    String objType = exprType.pop();

	    StringBuilder argsCode = new StringBuilder();
	    StringBuilder descParams = new StringBuilder();
	    for (int i = 0; i < n.el.size(); i++) {
	        visit(n.el.get(i));
	        argsCode.append(exprCode.pop());
	        descParams.append(jasminTypeFromName(exprType.pop()));
	    }

	    MethodDecl m = programContainer.lookupMethodInHierarchy(objType, n.i.s);
	    String retType = (m != null) ? typeNameOf(m.t) : "int";

	    StringBuilder sb = new StringBuilder();
	    sb.append(objCode).append(argsCode);
	    sb.append("	invokevirtual ").append(objType).append("/").append(n.i.s)
	      .append("(").append(descParams).append(")").append(jasminTypeFromName(retType)).append("\n");
	    exprCode.push(sb.toString());
	    exprType.push(retType);
	}

	@Override
	public void visit(IntegerLiteral n) {
		exprCode.push("	ldc " + n.i + "\n");
	    exprType.push("int");
	}

	@Override
	public void visit(IdentifierExpr n) {
		Integer idx = localVars.lookupIndex(n.s);
	    if (idx != null) {
	        String type = localVars.lookupType(n.s);
	        if (type.equals("int") || type.equals("boolean")) {
	            exprCode.push("	iload " + idx + "\n");
	        } else {
	            exprCode.push("	aload " + idx + "\n");
	        }
	        exprType.push(type);
	    } else {
	        String fieldType = lookupFieldType(n.s);
	        String jType = jasminTypeFromName(fieldType);
	        exprCode.push("	aload_0\n	getfield " + currentClassName + "/" + n.s + " " + jType + "\n");
	        exprType.push(fieldType);
	    }
	}
	private String lookupFieldType(String fieldName) {
	    ClassContent cc = programContainer.lookupClass(currentClassName);
	    while (cc != null) {
	        Variable v = cc.fields.get(fieldName);
	        if (v != null) return v.getTypeName();
	        cc = programContainer.lookupClass(cc.parentName);
	    }
	    return "int";
	}

	@Override
	public void visit(This n) {
		exprCode.push("	aload_0\n");
	    exprType.push(currentClassName);
	}

	@Override
	public void visit(NewArray n) {
		visit(n.e);
	    String sizeCode = exprCode.pop();
	    exprType.pop();

	    exprCode.push(sizeCode + "	newarray int\n");
	    exprType.push("int[]");
	}

	@Override
	public void visit(NewObject n) {
	    exprCode.push("	new " + n.i.s + "\n	dup\n	invokespecial " + n.i.s + "/<init>()V\n");
	    exprType.push(n.i.s);
	}

	@Override
	public void visit(Not n) {
		visit(n.e);
	    String code = exprCode.pop(); exprType.pop();

	    String trueLabel = newLabel();
	    String endLabel = newLabel();

	    StringBuilder sb = new StringBuilder();
	    sb.append(code);
	    sb.append("	ifeq ").append(trueLabel).append("\n");
	    sb.append("	iconst_0\n");
	    sb.append("	goto ").append(endLabel).append("\n");
	    sb.append(trueLabel).append(":\n");
	    sb.append("	iconst_1\n");
	    sb.append(endLabel).append(":\n");

	    exprCode.push(sb.toString());
	    exprType.push("boolean");
	}

	@Override
	public void visit(True n) {
		exprCode.push("	iconst_1\n");
	    exprType.push("boolean");
	}

	@Override
	public void visit(False n) {
		exprCode.push("	iconst_0\n");
	    exprType.push("boolean");
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
