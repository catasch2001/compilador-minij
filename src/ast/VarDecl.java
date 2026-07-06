package ast;

public class VarDecl extends Node {
	public Type t;
	public Identifier i;

	public VarDecl(Type at, Identifier ai, int ln) {
		super(ln);
		t = at;
		i = ai;
	}
}
