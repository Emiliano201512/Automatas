package Compilador;

import ED.ConversorInfijaPrefija;
import ED.Pila;
import java.io.FileWriter;
import java.io.PrintWriter;

/*
    Proyecto: Fase de análisis de compilador.
    Fase de análisis: análisis semántico.
    Entrada: Lista de tokens, lista de símbolos y triadas de árbol sintáctico. Salida: Árbol sintáctico abstracto.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class AnalizadorSemantico {
    protected Lista simbolos, tokens, arbolAbsSin;
    private Nodo recorrerLista, recorrerListaSimbolos, recorrerArbol;
    private boolean flag, convertir;
    private String tipoOperacion, tipoAccion, triadaTemp, simbolo ,nuevaTriada;
    private String exp, operando1, operando2, operandoTipo1, operandoTipo2, tipoExpresion, expreTemp, conversion;
    private String[] expresiones;
    private ConversorInfijaPrefija conversor;
    private Pila<String> pilaAux;
    private FileWriter fichero;
    private Lista triadas;
    private int tamañoTriadas, contador;
    private ConversorCodigoPrefija conversorC;
    
    public AnalizadorSemantico(Lista simbolos, Lista tokens){
        this.simbolos = simbolos;
        this.tokens = tokens;
        arbolAbsSin = new Lista(); // Creamos lista para guardar el árbol sintáctico abstracto
        triadas = new Lista();
        flag = false;
        convertir = true;
        expreTemp = triadaTemp = "";
        conversor = new ConversorInfijaPrefija(); // Instanciamos el conversor de expresión infija a prefija
        pilaAux = new Pila<>(); // Creamos una pila auxiliar para contener los operandos.
        contador = 1;
    }
    
    public void converirTriadas(){
        tamañoTriadas = triadas.getContadorElementos();
        expresiones = new String[tamañoTriadas];
        recorrerLista = triadas.inicio;
        while(recorrerLista != null){
            conversion = conversor.convertir(recorrerLista.follow);
            conversion = conversor.invertir();
            expresiones[contador-1] = conversion; contador++;
            recorrerLista = recorrerLista.siguiente;
        }
        conversorC = new ConversorCodigoPrefija(expresiones);
    }
    
    // Método para identificar las triadas del "árbol sintáctico" con ayuda de la tabla de tokens
    public void identificarTriadas(){
        recorrerLista = tokens.inicio;
        while(recorrerLista != null){
            // Si en la tabla de tokens encontramos un identificador, nuestra bandera se pone en verdadero.
            if(recorrerLista.descripcion.equals("identificador")){
                flag = true;
            // Si en la tabla de tokens encontramos un caracter simple de tipo ';', nuestra bandera se pone en falso.
            }else if(recorrerLista.lexema.equals(";")){
                flag = false;
                // Si la triada temporal contiene el caracter simple '=' entonces mandamos a evaluar la triada.
                if(triadaTemp.contains("=")){
                    triadas.agregar(new Nodo(triadaTemp));
                    evaluarTriadas(triadaTemp);
                    // El valor de convertir se vuelve verdadero para poder convertir nuestra triada en una expresión de tipo prefija.
                    convertir = true;
                }else{ // Si no contiene el caracter simple '=' la triada temporal se pone en blanco.
                    triadaTemp = " ";
                }
            }if(flag){ // Si nuestra bandera está en verdadero, almacenamos el token en una variable temporal para almacenar la 'triada'.
                triadaTemp += recorrerLista.lexema + " ";
            }recorrerLista = recorrerLista.siguiente;
        }
    }
    
    //Método para evaluar los tipos de los operandos de las triadas
    public void evaluarTriadas(String triada){
        // Al ya tener la triada, la triada temporal se pone en blanco, así como la expresión prefija temporal
        this.triadaTemp = expreTemp = " ";
        // Si convertir está en verdadero mandamos a convertir nuestra triada en una expresión prefija.
        if(convertir){exp = conversor.convertir(triada);}
        // Tras haberla convertido, colocamos a convertir en falso, puesto que no queremos que la triada, por medio de recursividad, vuelva a convertirse
        convertir = false;
        // Validamos la expresión obteniendo el operando 1, operando 2 y símbolo.
        validar(exp);
        System.out.println("\nExpresión: "+triada.trim());
        System.out.println("Operando 1: "+operando1);
        System.out.println("Símbolo: "+simbolo);
        System.out.println("Operando 2: "+operando2);
        
        // Si el símbolo es '=' quiere decir que se trata de una asignación, caso contrario ('+','-','/','*'), se trata de una expresión de tipo expresión.
        switch (simbolo) {
            case "=":
                tipoAccion = "asignación";
                System.out.println("Se trata de una operación de: " +tipoAccion);
                    break;
            case "+":
            case "-":
            case "*":
            case "/":
                tipoAccion = "expresión";
                System.out.println("Se trata de una operación de: " +tipoAccion);
                break;
            default:
                System.out.print("El símbolo "+simbolo+" no es un operador válido.");
                break;
        }
        //Mandamos a verificar los tipos de los operandos
        operandoTipo1 = verificarTipo(operando1);
        operandoTipo2 = verificarTipo(operando2);
        //Si ambos son enteros, la expresión resultante de ellos (atributo heredado) equivale a entero.
        if(operandoTipo1.equals("entero") && operandoTipo2.equals("entero")){
            tipoOperacion = "entero";
            System.out.println("La comparación de tipos es: entero con entero");
            // Comprobamos tipos dependiendo de su tipo de operación y acción.
            comprobarTipos(tipoOperacion, tipoAccion);
            tipoAccion = "";tipoOperacion = "";
        //Si uno equivale a entero y otro a real, la expresión resultante de ellos (atributo heredado) equivale a real.
        }else if(operandoTipo1.equals("entero") && operandoTipo2.equals("real")){
            tipoOperacion = "real";
            System.out.println("La comparación de tipos es: entero con real");
            comprobarTipos(tipoOperacion, tipoAccion);
            tipoAccion = "";tipoOperacion = "";
        //Si uno equivale a real y otro a entero, la expresión resultante de ellos (atributo heredado) equivale a real.
        }else if(operandoTipo1.equals("real") && operandoTipo2.equals("entero")){
            tipoOperacion = "real";
            System.out.println("La comparación de tipos es: real con entero");
            comprobarTipos(tipoOperacion, tipoAccion);
            tipoAccion = "";tipoOperacion = "";
        //Si ambos son reales, la expresión resultante de ellos (atributo heredado) equivale a real.
        }else if(operandoTipo1.equals("real") && operandoTipo2.equals("real")){
            tipoOperacion = "real";
            System.out.println("La comparación de tipos es: real con real");
            comprobarTipos(tipoOperacion, tipoAccion);
            tipoAccion = "";tipoOperacion = "";
        }else{
            tipoOperacion = "error"; // De otro modo, tenemos un error de tipo semántico. 
        }
        
    }
    
    // Método para comprobar el tipo de operación, agregar los atributos al árbol sintáctico abstracto y actualizar tipo de atributos heredados
    public void comprobarTipos(String tipoOperacion, String tipoAccion){
        if("expresión".equals(tipoAccion)){
            tipoExpresion = tipoOperacion;
            System.out.println("La triada fue de tipo " + tipoOperacion);
            agregarExprArbol(operando1); 
            agregarExprArbol(operando2);
            // Aplicamos recursividad
            exp = " expresión " + exp.substring(expreTemp.length(), exp.length());
            evaluarTriadas(exp);
        }else if("asignación".equals(tipoAccion)){
            tipoExpresion = tipoOperacion;
            System.out.println("La expresión fue de tipo " + tipoExpresion);
            // Actualizamos el tipo del atributo heredado
            String actualizar = operando1.trim();
            System.out.println("Variable a actualizar: " + actualizar);
            agregarExprArbol(operando1);
            agregarExprArbol(operando2);
            agregarExprArbol("");
            // Recorremos el AST para actualizar el tipo del atributo heredado
            recorrerListaSimbolos = arbolAbsSin.inicio;
            while(recorrerListaSimbolos != null){
                if(recorrerListaSimbolos.atributoArb.equals(actualizar)){
                    recorrerListaSimbolos.tipoAtrArb = tipoExpresion;
                    break;
                }recorrerListaSimbolos = recorrerListaSimbolos.siguiente;
            }
            //Recorremos la lista de símbolos para actualizar el tipo del atributo heredado
            recorrerListaSimbolos = simbolos.inicio;
            while(recorrerListaSimbolos != null){
                if(recorrerListaSimbolos.token.equals(actualizar)){
                    recorrerListaSimbolos.tipo = tipoExpresion;
                    break;
                }recorrerListaSimbolos = recorrerListaSimbolos.siguiente;
            }
            tipoExpresion = "";
        }
    }
    
    // Método para agregar los atributos sintetizados y heredados junto con su tipo al AST
    public void agregarExprArbol(String op){
        arbolAbsSin.agregar(new Nodo(op,verificarTipo(op)));
        if(op.equals("")){
            arbolAbsSin.agregar(new Nodo(" "," "));
        }
    }
    
    // Método para validar la triada obteniendo los operandos y el operador
    public void validar(String expresion){
        nuevaTriada = "";
        for (int i = 0; i < expresion.length(); i++) {
            expreTemp+= expresion.charAt(i);
            // Identificamos si lo evaluado al recorrer la expresión no equivale a un caracter simple y lo apilamos en una pila
            if(expresion.charAt(i)!='*' && expresion.charAt(i) != '/' && expresion.charAt(i) != '-' && expresion.charAt(i) != '+' && expresion.charAt(i)!='='){
                nuevaTriada += expresion.charAt(i);
                if(expresion.charAt(i) == ' ' && !" ".equals(nuevaTriada)){
                    pilaAux.apilar(nuevaTriada); nuevaTriada = "";
                }
            }else{
                // Identificamos los operandos y operadores
                simbolo = expresion.charAt(i)+"";
                operando1 = pilaAux.desapilar()+"";
                operando2 = pilaAux.desapilar()+"";
                break;
            }
        }
    }
    
    // Verificamos los tipos
    public String verificarTipo(String operando){
        if(operando.trim().equals("expresión")){
            return tipoExpresion; 
        }else{
            recorrerListaSimbolos = simbolos.inicio;
            while(recorrerListaSimbolos != null){
                if(recorrerListaSimbolos.token.equals(operando.trim())){
                    operando = recorrerListaSimbolos.tipo;
                    return operando;
                }recorrerListaSimbolos = recorrerListaSimbolos.siguiente;
            }return null;
        }
    }
    
    // Método para imprimir el árbol sintáctico abstracto
    public void imprimir(){
        recorrerArbol = arbolAbsSin.inicio;
        System.out.println("\n\tToken"+"\t\t\tTipo");
        while(recorrerArbol != null){
            if(recorrerArbol.tipoAtrArb!=null){
                System.out.printf("%1s%14s%8s%16s%10s%n",
                        "|", recorrerArbol.atributoArb, "|" , recorrerArbol.tipoAtrArb, "|");
            }recorrerArbol = recorrerArbol.siguiente;
        }
    }
    
    //Guardamos en una lista el árbol sintáctico abstracto
    public void guardarLista() {
        recorrerArbol = arbolAbsSin.inicio;
        
        try {
            fichero = new FileWriter("src/Archivos/arbolAbsSin.txt");
            PrintWriter pw = new PrintWriter(fichero);
            pw.print("Árbol sintáctico abstracto:\n");
            while (recorrerArbol != null) {
                if (recorrerArbol.tipoAtrArb != null) {
                    pw.println(String.format("%1s%14s%8s%16s%10s%n"
                            , "|" , recorrerArbol.atributoArb , "|" , recorrerArbol.tipoAtrArb , "|"));
                }recorrerArbol = recorrerArbol.siguiente;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fichero)  fichero.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    // Identificación de errores semánticos.
    public void erroresIdentificadores(){
        recorrerListaSimbolos = simbolos.inicio;
        while(recorrerListaSimbolos != null){
            if(recorrerListaSimbolos.tipo.equals("")){
                // Si la variable no está declarada
                AnalizadorLexico.tablaErrores.agregar(new Nodo(Integer.toString(AnalizadorLexico.idError), "La variable " + recorrerListaSimbolos.token + " no esta declarada", recorrerListaSimbolos.linea));
                AnalizadorLexico.idError++;
                imprimirErrores();System.exit(0);
            }
            if(recorrerListaSimbolos.numRep == 1 && recorrerListaSimbolos.tipo.equals("real") && recorrerListaSimbolos.valorReal == 0){
                // Si las variables no se están utilizando
                AnalizadorLexico.tablaErrores.agregar(new Nodo(Integer.toString(AnalizadorLexico.idError), "La variable " + recorrerListaSimbolos.token + " no se esta utilizando", recorrerListaSimbolos.linea));
                AnalizadorLexico.idError++;imprimirErrores(); System.exit(0);
            }
            if(recorrerListaSimbolos.numRep == 1 && recorrerListaSimbolos.tipo.equals("entero") && recorrerListaSimbolos.valor == 0){
                AnalizadorLexico.tablaErrores.agregar(new Nodo(Integer.toString(AnalizadorLexico.idError), "La variable " + recorrerListaSimbolos.token + " no se esta utilizando", recorrerListaSimbolos.linea));
                AnalizadorLexico.idError++;imprimirErrores(); System.exit(0);
            }
            recorrerListaSimbolos = recorrerListaSimbolos.siguiente;
        }
    }
    
    //Imprimimos los errores recorriendo la lista de errores.
    public void imprimirErrores(){
        System.out.println("------------Errores------------");
        recorrerLista = AnalizadorLexico.tablaErrores.inicio;
        while(recorrerLista != null){
            System.out.println("Se produjo un error en la linea: " + recorrerLista.linea + " " + recorrerLista.descripcion + ". Id de error: " + recorrerLista.idError);
            recorrerLista = recorrerLista.siguiente;
        }System.out.println("-------------------------------");
    }

    public Lista getSimbolos() {
        return simbolos;
    }    
    
    
    public static void main(String[] args) {
        TablaSimbolos ts = new TablaSimbolos();
        ts.imprimir();
        AnalizadorSemantico semantico = new AnalizadorSemantico(ts.getTablaSimbolos(), ts.getTablaTokens());
        semantico.identificarTriadas();
        ts.imprimir();
        semantico.erroresIdentificadores();
        semantico.imprimir();
        semantico.guardarLista();
        semantico.converirTriadas();
        
        System.out.println("---------------------------------------------------------");
        Tercetos tercetos = new Tercetos(semantico.getSimbolos());
    }
}