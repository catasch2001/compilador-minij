# Compilador miniJ

Proyecto de la materia Compiladores. Implementa un compilador completo para el lenguaje **miniJ**, desde el análisis léxico hasta la generación de código Jasmin ejecutable en la JVM.

**Alumna:** Luciana Catalina Scheid Ortiz

## Tabla de contenidos

- [Parte 1 y 2 — Scanner y Parser](#parte-1-y-2--scanner-y-parser)
- [Parte 3 — Análisis Semántico](#parte-3--análisis-semántico)
- [Parte 4 — Generador de Código](#parte-4--generador-de-código)
- [Cómo ejecutar](#cómo-ejecutar)

---

## Parte 1 y 2 — Scanner y Parser

### Scanner

Reconoce todos los tokens del lenguaje miniJ (integrado con CUP):

- **Palabras reservadas:** `class`, `extends`, `public`, `static`, `void`, `main`, `return`, `if`, `else`, `while`, `new`, `this`, `true`, `false`, `null`, `int`, `boolean`, `String`, `length`
- **Operadores:** `+ - * / && || == != < > ! =`
- **Delimitadores:** `( ) { } [ ] ; , .`
- **Literales:** enteros (`INTEGER_LITERAL`) y strings (`STRING_LITERAL`)
- **Identificadores:** letras, dígitos y `_`, comenzando con letra
- `System.out.println` reconocido como token especial `PRINTLN`
- Comentarios `/* */` y `//` ignorados, espacios y saltos de línea ignorados
- Caracteres no reconocidos generan mensaje de error y continúan el análisis

### Parser

Implementado con CUP siguiendo la gramática de miniJ, con precedencias declaradas (`OR`, `AND`, igualdad, comparación, suma/resta, mult/div, `NOT`, acceso a arrays, `IF`/`ELSE`).

**Extensiones sobre la especificación base**, necesarias porque algunos ejemplos de `SamplePrograms` usaban gramática estándar de Java:

- `VarDecl` limitada a tipos primitivos (`int`, `int[]`, `boolean`) para eliminar ambigüedad con asignaciones
- Producción `IF` sin `ELSE` para soportar if simple
- Tipo `String` y `StringLiteral`
- Operadores `||`, `!=`, `>`, `/`, `!` con sus nodos AST correspondientes

**Conflicto shift-reduce resuelto:** entre `StatementList` e `Identifier`, el parser no podía determinar con 1 token de lookahead si un `IDENTIFIER` iniciaba una `VarDecl` o un `Statement` de asignación. Se resolvió limitando `VarDecl` a tipos primitivos (terminales distintos de `IDENTIFIER`), quedando 1 conflicto esperado declarado con `-expect 1` en `build.xml`.

Resultado: 46 terminales, 20 no-terminales, 66 producciones, 162 estados, 0 conflictos inesperados. Probado exitosamente con `QuickSort`.

---

## Parte 3 — Análisis Semántico

Se agregó el nodo `MethodBody` (`Object[]`) en la gramática para distinguir variables locales de sentencias dentro de un método, eliminando un conflicto shift/reduce adicional (2 conflictos esperados en total).

Se extendió el AST agregando `getType(SemanticVisitor v)` a los nodos de expresión (literales, operadores aritméticos y booleanos, accesos a arrays, llamadas a métodos).

### Arquitectura

- **`ExpeType`**: encapsula el tipo resultante de una expresión
- **`Variable`**: envuelve tipo e identificador, con flag `used` para detectar variables no utilizadas
- **`VariableScope`** / **`VariableScopeStack`**: manejo de scopes anidados y detección de duplicados
- **`ClassContent`** / **`ProgramContainer`**: tabla de símbolos global — indexa clases, campos, métodos y resuelve herencia
- **`SemanticVisitor`**: visitor principal, construye las tablas de símbolos y recorre el AST verificando tipos y scope en dos pasadas (primero registra clases, luego valida los cuerpos)

### Errores detectados

- Variables no declaradas o declaradas más de una vez en el mismo scope
- Tipo incorrecto en asignaciones o en el retorno de métodos
- Llamadas a métodos no existentes en la clase o su jerarquía
- Número o tipos de argumentos incorrectos en llamadas a métodos
- Clases declaradas más de una vez
- Operadores aritméticos aplicados a tipos no numéricos
- Variables no usadas (advertencia + eliminación del AST)

Validado contra `SemanticAnalizarTest.java`, detectando correctamente 11 errores semánticos esperados.

---

## Parte 4 — Generador de Código

Última fase del compilador: a partir del AST validado semánticamente, genera archivos de bytecode Jasmin (`.j`) ensamblables y ejecutables en la JVM.

### `JasminCodeGenerator`

Implementa la interfaz `Visitor` y recorre el AST generando instrucciones Jasmin.

- `classCode`: acumula el código de la clase actual
- `methodBody`: cuerpo del método en generación
- `localVars` (`JasminVariableScope`): mapea nombre de variable → slot JVM y tipo
- `exprCode` / `exprType`: pilas para composición de expresiones
- `labelCounter`: contador de etiquetas únicas

**Métodos auxiliares:**
- `jasminType` / `jasminTypeFromName`: conversión de tipo AST a descriptor JVM
- `binaryArith`: genera `iadd`/`isub`/`imul`/`idiv` visitando ambos operandos
- `binaryCompare`: genera comparaciones con saltos, dejando 0 o 1 en la pila
- `newLabel()`: produce etiquetas únicas (`L0`, `L1`, ...)
- `lookupFieldType`: busca el tipo de un campo subiendo la jerarquía vía `ProgramContainer`

### `JasminVariableScope`

Gestiona el ámbito de variables locales de cada método con dos `HashMap`, exponiendo `insert()`, `lookupIndex()` y `lookupType()`.

### Ejecución

Se agregaron targets al `build.xml`:
- `run-jasmin`: ensambla los `.j` generados (debe correr después de `run-main`)
- `run-compiled`: ejecuta la clase compilada desde la carpeta `output`

> Requiere tener `jasmin.jar` cargado en las propiedades del proyecto.

**Ejemplo:** el programa `Factorial` genera `Factorial.j` y `Fac.j`, ensamblados y ejecutados correctamente sobre la JVM.

---

## Cómo ejecutar

```bash
ant run-cup        # genera parser.java y sym.java
ant run-jflex      # genera el scanner
ant run-main       # compila el .java de miniJ y corre el análisis semántico + genera .j
ant run-jasmin     # ensambla los .j generados con Jasmin
ant run-compiled   # ejecuta el bytecode resultante
```
