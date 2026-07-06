import java.io.InputStreamReader;
import ast.Goal;
import ast.visitor.JasminCodeGenerator;
import ast.visitor.MiniJPrintVisitor;
import ast.visitor.SemanticVisitor;
//import ast.visitor.JasminCodeGenerator;
import ast.visitor.Visitor;
import java_cup.runtime.Symbol;

public class Main {

	public static void main(String[] args) {
		InputStreamReader isr = new InputStreamReader(System.in);
		Scanner s = new Scanner(isr);
		parser p = new parser(s);
		try {
			Symbol root = p.parse();
			//primero se imprime el arbol
			Visitor mj = new MiniJPrintVisitor();
			Goal g = (Goal) root.value;
			mj.visit(g);
			
			//analisis semantico sobre el arbol
			SemanticVisitor sv = new SemanticVisitor();
            sv.visit(g);
            
            //Generador de codigo
             if (sv.errors.isEmpty()) {
				JasminCodeGenerator codegen = new JasminCodeGenerator(sv.programContainer, "output");
				codegen.visit(g);
				System.out.println("Archivos .j generados en /output");
			} else {
				System.out.println("No se genera codigo: hay errores semanticos.");
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void report_error(String message, Object info) {
		System.err.print(message);
		System.err.flush();
		if (info instanceof Symbol)
			if (((Symbol) info).left != -1)
				System.err.println(" at line " + ((Symbol) info).left + " of input");
			else
				System.err.println("");
		else
			System.err.println("");
	}
}