package ast;

public class If extends Statement {
	public Expr e;
	public Statement s1, s2;
	public boolean hasElse;
	

    // con else
    public If(Expr ae, Statement as1, Statement as2, int ln) {
        super(ln);
        e = ae;
        s1 = as1;
        s2 = as2;
        hasElse = true;
    }

    // sin else
    public If(Expr ae, Statement as1, int ln) {
        super(ln);
        e = ae;
        s1 = as1;
        s2 = null;
        hasElse = false;
    }
}
