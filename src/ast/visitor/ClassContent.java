package ast.visitor;

import java.util.HashMap;

import ast.MethodDecl;

public class ClassContent {
    public String className;
    public String parentName;
    public HashMap<String, MethodDecl> methods = new HashMap<>();
    public HashMap<String, Variable>   fields  = new HashMap<>();

    public ClassContent(String name, String parent) {
        this.className  = name;
        this.parentName = parent;
    }

    public MethodDecl lookupMethod(String name) {
        return methods.getOrDefault(name, null);
    }
}
