package Compilador;

import ED.Pila;

public class CodigoObjeto {
    private String[] separar;
    private Lista sentencias, tSimbolos;
    private Nodo recorrerLista;
    private boolean flag;
    private int j = 16;
    private Pila pila, auxPila;
    
    public void analizarLinea(String auxLinea) {
        separar = auxLinea.split(" ");

        switch (separar[0].toLowerCase()) {
            case "programa":
                sentencias.agregar(new Nodo(separar[0], "", ""));
                break;
            case "entero":
                if (esVarUtilizable(separar[1])) {
                    sentencias.agregar(new Nodo("Mov", separar[1], "0"));
                }
                break;
            case "real":
                if (esVarUtilizable(separar[1])) {
                    sentencias.agregar(new Nodo("Mov", separar[1], "0.0"));
                }
                break;
            case "leer":
                sentencias.agregar(new Nodo("Leer", separar[1], ""));
                break;
            case "escribir":
                sentencias.agregar(new Nodo("Escribir", separar[1], ""));
                break;
            case "finprograma":
                sentencias.agregar(new Nodo(separar[0], "", ""));
                break;
        }
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
    
    public void generadorTercetos(String operando, String operador1, String operador2, int temp) {
        if (!operador1.contains("R")) { 
            if (operando.equals("=")) {
                sentencias.agregar(new Nodo("Mov", operador2, operador1));
                j=16;
            } else {
                sentencias.agregar(new Nodo("LDI", "R" + temp, operador1));
                sentencias.agregar(new Nodo(nombreOperador(operando), "R" + temp, operador2));
                pila.apilar("R" + temp);
                apilarPilaAux();
                j++;
            }
        } else {
            switch (operando) {
                case "=":
                    sentencias.agregar(new Nodo("Mov", operador2, operador1));
                    pila.apilar(operador1);
                    apilarPilaAux();
                    j=16;
                    break;
                case "/":
                    sentencias.agregar(new Nodo("Div", operador1, operador2));
                    pila.apilar(operador1);
                    apilarPilaAux();
                    j++;
                    break;
                case "*":
                    sentencias.agregar(new Nodo("Mul", operador1, operador2));
                    pila.apilar(operador1);
                    apilarPilaAux();
                    j++;
                    break;
                case "+":
                    sentencias.agregar(new Nodo("Add", operador1, operador2));
                    pila.apilar(operador1);
                    apilarPilaAux();
                    j++;
                    break;
                case "-":
                    sentencias.agregar(new Nodo("Sub", operador1, operador2));
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
                nomOperador = "div";
                break;
            case "*":
                nomOperador = "Mul";
                break;
            case "+":
                nomOperador = "Add";
                break;
            case "-":
                nomOperador = "Sub";
                break;
        }

        return nomOperador;
    }
}
