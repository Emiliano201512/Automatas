package Compilador;

import java.io.FileWriter;
import java.io.PrintWriter;

/*
    Proyecto: Fase de análisis de compilador.
    Fase de análisis: análisis léxico.
    Entrada: Archivo txt, código fuente. Salida: Tabla de tokens.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public final class AnalizadorLexico {
    // Declaracion de variables a utilizar 
    protected Lista tokens;
    private Nodo recorrerLista;
    private String[] palabrasReservadas = {"programa", "entero", "real", "leer", "escribir", "finprograma"};
    private String apuntadorInicio, linea, mandarToken, retorno, estado;
    private char[] caracteresSimplesAceptados = {';', '=', '+', '-', '/', '*', ',','(',')'};
    private char apuntadorFinal;
    private int retornos, atributoPR, atributoI, lineaCodigo, repeticion, atributoExiste;
    private boolean flag;
    protected static Archivo archivo;
    private FileWriter fichero;
    private PrintWriter pw;
    //Variables para tabla de errores
    protected static Lista tablaErrores;
    protected static int idError = 1;

    // Constructor de analizador léxico.
    public AnalizadorLexico() {
        archivo = new Archivo("src/Archivos/prueba_funcional1.txt"); // Programa fuente creado a analizar
        atributoI = 500; // Inicializamos atributo de los identificadores
        tokens = new Lista(); // Creamos la lista de tokens
        tablaErrores = new Lista(); // Creamos la lista de errores
        linea = apuntadorInicio = mandarToken = ""; // Inicializamos cadenas en blanco
        lineaCodigo = 0; // Inicializamos la línea de código en 0
        fichero = null; 
        pw = null;
    }

    // Método para lectura del archivo con programa ejemplo y arranque del programa.
    public void run() {
        archivo.abrir(); // Se abre el archivo
        do {
            linea = archivo.leer();
            lineaCodigo++;
            if (linea != null) {
                tokenizar(); // Por cada línea, se manda a tokenizar.
            }
        } while (linea != null);        
    }
    
    // Método para identificar tokens mediante autómata
    public void tokenizar() {
        estado = "q0"; // Inicializamos el estado en q0.
        flag = false;
        
        for (int i = 0; i < linea.length(); i++) {
            apuntadorFinal = linea.charAt(i);
            switch (estado) {
                /*Estado q0 del automata: identificamos que tipo de token es ya sea un dígito, un blanco,
                carácteres simples o una letra minúscula*/ 
                case "q0":
                    if (esMinuscula(apuntadorFinal)) {
                        apuntadorInicio += apuntadorFinal; estado = "q1";
                    } else if (esDigito(apuntadorFinal)) {
                        apuntadorInicio += apuntadorFinal; estado = "q2";
                    } else if (esBlanco(apuntadorFinal)) {
                        estado = "q0";
                    } else {
                        compararCaracteres(apuntadorFinal);
                        if (flag) {
                            estado = "q6"; i--;
                        } else {
                            apuntadorInicio += apuntadorFinal;
                            estado = "q7"; i--;
                        }
                    }
                    break;
                    //Estado q1 del autómata
                case "q1":// Analizamos si es una letra minúscula 
                    if (esMinuscula(apuntadorFinal)) {
                        apuntadorInicio += apuntadorFinal;
                        estado = "q1";
                    // Analizamos si es un blanco
                    } else if (esBlanco(apuntadorFinal)) {
                        flag = false;
                    // Comparamos con las palabras reservadas si coincide lo agrega a la lista de tokens y regresamos a estado q0
                        compararPalabraReservada(apuntadorInicio);
                        if (flag) {
                            tokens.agregar(new Nodo(apuntadorInicio,"Palabra reservada", accionPalabraReservada(apuntadorInicio),atributoPalabraReservada(apuntadorInicio),lineaCodigo,estaRepetidoTokens(apuntadorInicio)));
                            mandarToken = apuntadorInicio; 
                            apuntadorInicio = "";estado = "q0"; flag = false;
                            //Si no coincide lo agrega a la lista de tokens como un identificador y regresamos a q0
                        } else {
                            tokens.agregar(new Nodo(apuntadorInicio,"identificador","variable",tieneAtributo(apuntadorInicio),lineaCodigo,estaRepetidoTokens(apuntadorInicio)));
                            mandarToken = apuntadorInicio;
                            apuntadorInicio = "";estado = "q0";
                        }
                    // Si es un digito lo agrega a la lista de errores y regresamos a estado q0
                    } else if (esDigito(apuntadorFinal)) {
                        apuntadorInicio += apuntadorFinal;
                        tablaErrores.agregar(new Nodo(Integer.toString(idError), "La cadana " + apuntadorInicio + " no es aceptada por el lenguaje", Integer.toString(lineaCodigo)));
                        idError++;
                        apuntadorInicio = ""; estado = "q0";
                    // Si es una letra mayuscula la enviamos al estado q7
                    } else if (esMayuscula(apuntadorFinal)) {
                        apuntadorInicio += apuntadorFinal;
                        estado = "q7";
                    } else { //Analizamos si es una palabra reservada o un identificador 
                        i--;flag = false;
                        compararPalabraReservada(apuntadorInicio);
                        if (flag) {
                            tokens.agregar(new Nodo(apuntadorInicio,"Palabra reservada", accionPalabraReservada(apuntadorInicio),atributoPalabraReservada(apuntadorInicio),lineaCodigo,estaRepetidoTokens(apuntadorInicio)));
                            mandarToken = apuntadorInicio; apuntadorInicio = "";estado = "q0";
                        } else {
                            tokens.agregar(new Nodo(apuntadorInicio,"identificador","variable",tieneAtributo(apuntadorInicio),lineaCodigo,estaRepetidoTokens(apuntadorInicio)));
                            mandarToken = apuntadorInicio; apuntadorInicio = "";estado = "q0";
                        }
                    }
                    break; //Estado q2 del autómata
                case "q2":
                    if (esDigito(apuntadorFinal)) {
                        apuntadorInicio += apuntadorFinal; estado = "q2";
                    } else if (apuntadorFinal == '.') {
                        apuntadorInicio += apuntadorFinal;
                        estado = "q4";
                    } else if (esBlanco(apuntadorFinal)) {
                        i--;estado = "q3";
                    } else if (esMayuscula(apuntadorFinal) || esMinuscula(apuntadorFinal)) {
                        apuntadorInicio += apuntadorFinal;
                        if (esBlanco(apuntadorFinal)) {
                            estado = "q7";
                        } else {
                            flag = false;
                            compararCaracteres(apuntadorFinal);
                            if (flag) {
                                i--; estado = "q6";
                            } else {
                                estado = "q7";
                            }
                        }
                    }else {
                            flag = false;
                            compararCaracteres(apuntadorFinal);
                            if (flag) {
                                estado = "q3";i--;
                            } else {
                                estado = "q7";
                            }
                    }
                    break;// Estado q3 del autómata
                case "q3":
                    tokens.agregar(new Nodo(apuntadorInicio,"entero","entero",Integer.parseInt(apuntadorInicio),lineaCodigo,estaRepetidoTokens(apuntadorInicio)));
                    mandarToken = apuntadorInicio;i--; apuntadorInicio = "";estado = "q0";
                    break;// Estado q4 del autómata
                case "q4":
                    if (esDigito(apuntadorFinal)) {
                        apuntadorInicio += apuntadorFinal;
                        estado = "q4";
                    } else if (esBlanco(apuntadorFinal)) {
                        i--; estado = "q5";
                    } else {
                        flag = false;
                            compararCaracteres(apuntadorFinal);
                            if (flag) {
                                estado = "q5"; i--;
                            } else {
                                estado = "q7";
                            }
                    }
                    break;// Estado q5 del autómata
                case "q5":
                    tokens.agregar(new Nodo(apuntadorInicio,"real","real", Float.parseFloat(apuntadorInicio),lineaCodigo,estaRepetidoTokens(apuntadorInicio)));
                    mandarToken = apuntadorInicio;i--; apuntadorInicio = "";estado = "q0";
                    break;// Estado q6 del autómata
                case "q6":
                    flag = false;
                    compararCaracteres(apuntadorFinal);
                    if (flag) {
                        apuntadorInicio += apuntadorFinal;
                        tokens.agregar(new Nodo(apuntadorInicio,"caracter simple",accionCaracterSimple(apuntadorInicio), atributoCaracterSimple(apuntadorInicio),lineaCodigo,estaRepetidoTokens(apuntadorInicio)));
                        mandarToken = apuntadorInicio;apuntadorInicio = "";estado = "q0";
                    } else {
                        if (esBlanco(apuntadorFinal)) {
                            estado = "q0";
                        } else {
                            apuntadorInicio += apuntadorFinal;
                            estado = "q7";
                            i--;
                        }
                    }
                    break;// Estado q7 del autómata: estado de errores.
                case "q7":
                    if (esMinuscula(apuntadorFinal)) {
                        apuntadorInicio += apuntadorFinal;estado = "q7";
                    } else if (esMayuscula(apuntadorFinal)) {
                        estado = "q7";
                    } else if (esBlanco(apuntadorFinal)) {
                        tablaErrores.agregar(new Nodo(Integer.toString(idError), "La cadana " + apuntadorInicio + " no es aceptada por el lenguaje", Integer.toString(lineaCodigo)));
                        idError++;
                        apuntadorInicio = "";estado = "q0";
                    } else {
                        tablaErrores.agregar(new Nodo(Integer.toString(idError), "La cadana " + apuntadorInicio + " no es aceptada por el lenguaje", Integer.toString(lineaCodigo)));
                        idError++;
                        apuntadorInicio = ""; estado = "q0";
                    }
                    break;
            }
        }
    }
    
    //Identifica si es una letra minúscula
    public boolean esMinuscula(char c) {
        return c >= 'a' && c <= 'z';
    }

    //Identifica si es una letra mayúscula
    public boolean esMayuscula(char c) {
        return c >= 'A' && c <= 'Z';
    }

    //Identifica si es un blanco
    public boolean esBlanco(char c) {
        return c == ' ' || c == '\n' || c == '\t';
    }
    
    //Identifica si es un dígito
    public boolean esDigito(char c){
        return (c >= '0' && c <= '9');
    }
    
    // Método para realizar la comparación con los carácteres simples permitidos
    public void compararCaracteres(char c){
        for (int j = 0; j < caracteresSimplesAceptados.length; j++) {
            if (apuntadorFinal == caracteresSimplesAceptados[j]) {
                flag = true; break;
            }
        }
    }
    
    // Método para realizar comparación con las palabras reservadas permitidas 
    public void compararPalabraReservada(String c){
        for (String palabraReservada : palabrasReservadas) {
            if (apuntadorInicio.equals(palabraReservada)) {
                flag = true; break;
            }
        }
    }
    
    // Método para asignar acciones a un caracter simple 
    public String accionCaracterSimple(String caracter) {
        switch (caracter) {
            case ";": retorno = "Punto y coma"; break;
            case "=": retorno = "Asignacion"; break;
            case "+": retorno = "Operador suma"; break;
            case "-": retorno = "Operador resta"; break;
            case "/": retorno = "Operador división"; break;
            case "*": retorno = "Operador multiplicación"; break;
            case ",": retorno = "Coma"; break;
            case "(": retorno = "Paréntesis que abre"; break;
            case ")": retorno = "Paréntesis que cierra"; break;
        }
        return retorno;
    }

    // Método para asignar atributo a cada carácter simple 
    public int atributoCaracterSimple(String caracter) {
        switch (caracter) {
            case ";": retornos = 59; break;
            case "=": retornos = 61; break;
            case "+": retornos = 43; break;
            case "/": retornos = 47; break;
            case "*": retornos = 42; break;
            case ",": retornos = 44; break;
            case "-": retornos = 45; break;
            case "(": retornos = 40; break;
            case ")": retornos = 41; break;
        }return retornos;
    }
    
    // Método para asignar atributo a cada palabra reservada 
    public int atributoPalabraReservada(String palabra){
        switch(palabra){
            case "programa": atributoPR = 400; break;
            case "entero": atributoPR = 401; break;
            case "real": atributoPR = 402;  break;
            case "leer": atributoPR = 403; break;
            case "escribir": atributoPR = 404; break;
            case "finprograma": atributoPR = 405; break;
        } return atributoPR;
    }
    
    // Método para asignar las acciones a cada palabra reservada 
    public String accionPalabraReservada(String palabra){
        switch(palabra){
            case "programa": retorno = "Inicia programa"; break;
            case "entero": retorno = "Tipo de dato"; break;
            case "real": retorno = "Tipo de dato"; break;
            case "leer": retorno = "Función de leer"; break;
            case "escribir": retorno = "Función de escribir"; break;
            case "finprograma": retorno = "Termina programa"; break;
        } return retorno;
    }
    
    // Método para analizar las líneas en la que se repite un token
    public int estaRepetidoTokens(String lexema) {
        recorrerLista = tokens.inicio;
        repeticion = 1;
        while (recorrerLista != null) {
            if (lexema.equals(recorrerLista.lexema)) {
                repeticion++;
            }recorrerLista = recorrerLista.siguiente;
        }
        return repeticion;
    }

    // Método para asignar atributos a los identificadores.
    public int tieneAtributo(String lexema) {
        recorrerLista = tokens.inicio;
        flag = false;
        while (recorrerLista != null) {
            if (lexema.equals(recorrerLista.lexema)) {
                flag = true;
                atributoExiste = recorrerLista.atributo;
                break;
            }recorrerLista = recorrerLista.siguiente;
        }
        if (!flag) {
            return atributoI++;
        } else {
            return atributoExiste;
        }
    }
    
    // Método para imprimir tabla de tokens 
    public void imprimir(){
        recorrerLista = tokens.inicio;
        System.out.print("Tabla de tokens:");
        System.out.print("\n    Token"+"\t\t   Descripción"+"\t\t\tAcción"+"\t\t\tAtributo"+"\tLinea"+"\t# repetición\n");
        while(recorrerLista != null){
            if(recorrerLista.accion.equals("real")){
                System.out.printf("%10s%30s%30s%20s%10s%10s%n",recorrerLista.lexema , recorrerLista.descripcion , recorrerLista.accion ,recorrerLista.atributoReal, recorrerLista.lineaCodigo , recorrerLista.numRep);
            }else{
                System.out.printf("%10s%30s%30s%20s%10s%10s%n",recorrerLista.lexema , recorrerLista.descripcion , recorrerLista.accion , recorrerLista.atributo , recorrerLista.lineaCodigo , recorrerLista.numRep);
            }recorrerLista = recorrerLista.siguiente;
        }
    }
    

    // Método para guardar la lista de tokens en un archivo 
    public void guardarLista() {
        recorrerLista = tokens.inicio;
        try {
            fichero = new FileWriter("src/Archivos/ListaTokens.txt");
            pw = new PrintWriter(fichero);
            pw.print("Tabla de tokens:\n");
            pw.print("\n    Token"+"\t\t   Descripción"+"\t\t\tAcción"+"\t\t\tAtributo"+"\tLinea"+"\t# repetición\n");
            while (recorrerLista != null) {
                if(recorrerLista.accion.equals("real")){
                    pw.printf("%10s%30s%30s%20s%10s%10s%n",recorrerLista.lexema , recorrerLista.descripcion , recorrerLista.accion ,recorrerLista.atributoReal, recorrerLista.lineaCodigo , recorrerLista.numRep);
                }else{
                    pw.printf("%10s%30s%30s%20s%10s%10s%n",recorrerLista.lexema , recorrerLista.descripcion , recorrerLista.accion , recorrerLista.atributo , recorrerLista.lineaCodigo , recorrerLista.numRep);
                }recorrerLista = recorrerLista.siguiente;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Nuevamente aprovechamos el finally para 
                // asegurarnos que se cierra el fichero.
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    // Método para obtener la lista de tokens, auxiliar para envio de token por token 
    public Lista getTokens() {
        return tokens;
    }
    
    public String[] getPalabrasReservadas(){
        return palabrasReservadas;
    }
}