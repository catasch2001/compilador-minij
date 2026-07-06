package ast.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import ast.Identifier;
import ast.Type;

public class VariableScopeStack {
    public Stack<VariableScope> stack = new Stack<>();

    public void pushScope() {
        stack.push(new VariableScope());
    }

    public void popScope() {
        stack.pop();
    }

    public void insertSymbol(Type t, Identifier id) throws Exception {
        stack.peek().addSymbol(id.s, new Variable(t, id));
    }

    public Variable variableLookup(String id) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            Variable v = stack.get(i).lookup(id);
            if (v != null) return v;
        }
        return null;
    }

    public List<Variable> getUnusedInCurrentScope() {
        List<Variable> unused = new ArrayList<>();
        for (Variable v : stack.peek().varSet.values())
            if (!v.used) unused.add(v);
        return unused;
    }
}