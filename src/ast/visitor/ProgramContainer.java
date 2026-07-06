package ast.visitor;

import java.util.HashMap;

import ast.MethodDecl;

public class ProgramContainer {
    private HashMap<String, ClassContent> classes = new HashMap<>();

    public void registerClass(ClassContent c) throws Exception {
        if (classes.containsKey(c.className))
            throw new Exception("Clase duplicada: " + c.className);
        classes.put(c.className, c);
    }

    public ClassContent lookupClass(String name) {
        return classes.getOrDefault(name, null);
    }

    public MethodDecl lookupMethodInHierarchy(String className, String methodName) {
        ClassContent cc = lookupClass(className);
        while (cc != null) {
            MethodDecl m = cc.lookupMethod(methodName);
            if (m != null) return m;
            cc = lookupClass(cc.parentName);
        }
        return null;
    }

    public boolean checkInheritanceOf(String child, String parent) {
        ClassContent cc = lookupClass(child);
        while (cc != null) {
            if (cc.className.equals(parent)) return true;
            cc = lookupClass(cc.parentName);
        }
        return false;
    }
}
