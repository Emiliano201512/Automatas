package Compilador;

import ED.Pila;

public class Tercetos {

    private Archivo archivo;
    private String linea, apInicio, operando, operador1, operador2;
    private Pila pila, auxPila;
    private Lista tercetos, tSimbolos, registrosU;
    private boolean flag;
    private String[] separar;
    private Nodo recorrerLista;
    private char[] operandos = {'=', '+', '-', '/', '*'};
    private int j = 1;

    public Tercetos(Lista tabla) {
        archivo = new Archivo("src/Archivos/CodigoPrefija.txt");
        tSimbolos = new Lista();
        tercetos = new Lista();
        registrosU = new Lista();
        pila = new Pila();
        auxPila = new Pila();
        linea = apInicio = operando = operador1 = operador2 = "";
        flag = false;
        tSimbolos = tabla;

        run();
    }

    public void run() {
        archivo.abrir();
        do {
            linea = archivo.leer();
            if (linea != null) {
                if (linea.contains("=")) {
                    analizarExpresion(linea.trim());
                } else {
                    analizarLinea(linea.trim());
                }
            }
        } while (linea != null);
        imprimirTercetos("tercetos");
    }

    public void analizarLinea(String auxLinea) {
        separar = auxLinea.split(" ");

        switch (separar[0].toLowerCase()) {
            case "programa":
                tercetos.agregar(new Nodo(separar[0], "", ""));
                break;
            case "entero":
                if (esVarUtilizable(separar[1])) {
                    tercetos.agregar(new Nodo("=", separar[1], "0"));
                }
                break;
            case "real":
                if (esVarUtilizable(separar[1])) {
                    tercetos.agregar(new Nodo("=", separar[1], "0.0"));
                }
                break;
            case "leer":
                tercetos.agregar(new Nodo("Leer", separar[1], ""));
                break;
            case "escribir":
                tercetos.agregar(new Nodo("Escribir", separar[1], ""));
                break;
            case "finprograma":
                tercetos.agregar(new Nodo(separar[0], "", ""));
                break;
        }
    }

    public void analizarExpresion(String expresion) {
        int i = 0;
        separar = expresion.split(" ");
        boolean flagOperando = false, flagOperador1 = false, flagOperador2 = false;
        guardarEnPila(separar);

        while (!pila.estaVacia()) {
            if (esUnOperador(pila.tope().toString().charAt(0)) && flagOperando == false) {
                operando = pila.desapilar().toString();
                flagOperando = true;
            } else if (!esUnOperador(pila.tope().toString().charAt(0)) && flagOperando == true && flagOperador2 == false) {
                operador2 = pila.desapilar().toString();
                flagOperador2 = true;
            } else if (!esUnOperador(pila.tope().toString().charAt(0)) && flagOperando == true && flagOperador1 == false) {
                operador1 = pila.desapilar().toString();
                if(optimizar()){
                    flagOperador1 = true;
                    generadorTercetos(operando, operador1, operador2, j);
                    operando = operador1 = operador2 = "";
                    flagOperando = flagOperador1 = flagOperador2 = false;
                    i = 0;
                }
//                imprimirPila("pila");
            } else if (esUnOperador(pila.tope().toString().charAt(0)) && flagOperando == true && flagOperador2 == true) {
                pila.apilar(operador2);
                pila.apilar(operando);
                while (!auxPila.estaVacia()) {
                    pila.apilar(auxPila.desapilar());
                }
                flagOperando = flagOperador2 = false;
                operando = operador2 = "";
                i++;
                apilarLoDesapilado(i);
            } else {
                auxPila.apilar(pila.desapilar());
            }
        }
    }
    
    public boolean optimizar( ){
        if(operando.equals("*") || operando.equals("/")){
            return !"1".equals(operador2);
        }return true;
    }

    public boolean esVarUtilizable(String variable) {
        recorrerLista = tSimbolos.inicio;
        flag = false;

        while (recorrerLista != null) {
            if (recorrerLista.token.equals(variable) && recorrerLista.numRep != 1) {
                flag = true;
            }

            recorrerLista = recorrerLista.siguiente;
        }

        return flag;
    }

    public void guardarEnPila(String[] expresion) {
        for (int i = expresion.length - 1; i >= 0; i--) {
            if (!expresion[i].trim().equals("")) {
                pila.apilar(expresion[i]);
            }
        }
    }

    public boolean esUnOperador(char operando) {
        flag = false;
        for (int i = 0; i < operandos.length; i++) {
            if (operando == operandos[i]) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public void apilarLoDesapilado(int i) {
        while (i != 0) {
            auxPila.apilar(pila.desapilar());
            i--;
        }
    }

    public void generadorTercetos(String operando, String operador1, String operador2, int temp) {
        if (!operador1.contains("temp")) { 
            if (operando.equals("=")) {
                tercetos.agregar(new Nodo("=", operador2, operador1));
                j=1;
            } else {
                tercetos.agregar(new Nodo("=", "temp" + temp, operador1));
                tercetos.agregar(new Nodo(nombreOperador(operando), "temp" + temp, operador2));
                pila.apilar("temp" + temp);
                apilarPilaAux();
                j++;
            }
        } else {
            switch (operando) {
                case "=":
                    tercetos.agregar(new Nodo("=", operador2, operador1));
                    pila.apilar(operador1);
                    apilarPilaAux();
                    j=1;
                    break;
                case "/":
                    tercetos.agregar(new Nodo("/", operador1, operador2));
                    pila.apilar(operador1);
                    apilarPilaAux();
                    j++;
                    break;
                case "*":
                    tercetos.agregar(new Nodo("*", operador1, operador2));
                    pila.apilar(operador1);
                    apilarPilaAux();
                    j++;
                    break;
                case "+":
                    tercetos.agregar(new Nodo("+", operador1, operador2));
                    pila.apilar(operador1);
                    apilarPilaAux();
                    j++;
                    break;
                case "-":
                    tercetos.agregar(new Nodo("-", operador1, operador2));
                    pila.apilar(operador1);
                    apilarPilaAux();
                    j++;
                    break;
                default:
                    System.out.println("Este operando no esta: " + operando);
                    break;
            }
        }
    }

    public void apilarPilaAux() {
        while (!auxPila.estaVacia()) {
            pila.apilar(auxPila.desapilar());
        }
    }

    public String nombreOperador(String operando) {
        String nomOperador = "";

        switch (operando) {
            case "/":
                nomOperador = "/";
                break;
            case "*":
                nomOperador = "*";
                break;
            case "+":
                nomOperador = "+";
                break;
            case "-":
                nomOperador = "-";
                break;
        }

        return nomOperador;
    }

    public void imprimirTercetos(String tipo) {
        switch (tipo) {
            case "tercetos":
                recorrerLista = tercetos.inicio;
                System.out.println("\n\t\t\tTabla de tercetos");
                System.out.println("---------------------------------------------------------------");
                System.out.printf("%14s%22s%20s","operando","operador1","operador2");
                System.out.println("\n---------------------------------------------------------------");
                while (recorrerLista != null) {
                    System.out.printf("%2s%12s%5s%18s%5s%16s%5s", "|",recorrerLista.idError, 
                            "|",recorrerLista.descripcion,"|", recorrerLista.linea,"|");
                    System.out.println("");
                    recorrerLista = recorrerLista.siguiente;
                }
                break;
            case "pila":
                System.out.println("Se imprimio la pila para saber el estado");
                while (!pila.estaVacia()) {
                    System.out.println(pila.desapilar());
                }
                System.out.println("");
                break;
        }
    }
}
