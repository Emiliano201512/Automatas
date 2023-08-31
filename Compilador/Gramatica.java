package Compilador;

import java.util.Arrays;

/*
    Proyecto: Fase de análisis de compilador.
    Fase de análisis: análisis sintáctico descendente.
    Entrada: Archivo de texto con gramática libre de contexto. 
    Salida: Arreglos de símbolos terminales, no terminales, lados derechos, lados izquierdos y producciones de la gramática.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class Gramatica {
    private Archivo archivo;
    private String linea, apuntadorInicio, estado;
    private char apuntadorFinal;
    private String [][] arrTerminales;
    private String [][] arrNoTerminales;
    private String [][] arrLadoDerecho, arrLadosIzquierdos;
    private Lista auxTerminales, auxNoTerminales, auxLadoDerechos, auxLadoIzquierdos, listaProducciones;
    private boolean flag;
    private Nodo auxRecorrer;
    private int contadorListas;
    
    public Gramatica() {
        archivo = new Archivo("src/archivos/Gramatica"); //Gramática libre de contextp a analizar 
        auxLadoDerechos = new Lista(); // Lista para almacenar lados derechos
        auxLadoIzquierdos = new Lista(); // Lista para almacenar lados izquierdos
        auxNoTerminales = new Lista(); // Lista para almacenar simbolos no terminales.
        auxTerminales = new Lista(); // Lista para almacenar simbolos terminales.
        listaProducciones = new Lista(); // Lista para almacenar producciones.
        this.run();
    }
    
    public void run(){
        archivo.abrir(); // Abrimos el archivo de la gramática.
        do {
            linea = archivo.leer(); // Leemos línea por línea (producción por producción).
            if (linea != null) {
                separarTermNoTerm(); // Método para separar terminales y no terminales
                separarLadosDerechos(); // Método para separar lados derechos
                separarLadosIzquierdos(); // Método para separar lados izquierdos
            }
        } while (linea != null);
        instanciarArreglos(); 
        guardarListaEnArreglo("terminales");
        guardarListaEnArreglo("noTerminales");
        guardarListaEnArreglo("ladosDerechos");
        guardarListaEnArreglo("ladosIzquierdos");
        generarListaProducciones();
    }
    
    // Método para separar terminales y no terminales
    public void separarTermNoTerm(){
        estado = "q0"; // Inicializamos el estado en q0. Se utilizó el formato de automáta para realizar la separación.
        apuntadorInicio = ""; // Inicializamos el apuntador en blanco.
        for (int i = 0; i < linea.length(); i++) {
            apuntadorFinal = linea.charAt(i); // Declaramos a apuntadorFinal como el char que se está analizando el la línea
            switch(estado){
                case "q0": // Se evalúa si es no terminal.
                    if(apuntadorFinal == '<'){
                        apuntadorInicio += apuntadorFinal; // Almacenamos en apuntador el char evaluado de la línea y nos movemos de estado
                        estado = "q1";
                    // Si no es no terminal ni símbolos de transición o epsilon, entonces es un terminal y nos movemos de estado.
                    }else if(apuntadorFinal == ' ' || apuntadorFinal == '¬' || apuntadorFinal == '>' || apuntadorFinal == 'ε'){
                    }else {
                        estado = "q3";
                        apuntadorInicio += apuntadorFinal;
                    }
                    break;
                case "q1": // Se cierra la evaluación del símbolo no terminal.
                    if(apuntadorFinal == '>'){
                        apuntadorInicio += apuntadorFinal;
                        i--; estado = "q2";
                    }else apuntadorInicio += apuntadorFinal;
                    break;
                case "q2": // Se almacena en la lista de no terminales y se reinicia el apuntadorInicio y estado
                    if (auxNoTerminales.inicio == null) {
                        auxNoTerminales.agregar(new Nodo(apuntadorInicio));
                        apuntadorInicio = "";
                        estado = "q0";
                        i++;
                    } else {
                        if (estaEnLaLista(apuntadorInicio, "No terminales")) {
                            auxNoTerminales.agregar(new Nodo(apuntadorInicio));
                        }
                        apuntadorInicio = "";estado = "q0";
                    }
                    break;
                case "q3": // Cuando se encuentra un blanco, se manda a guardar un terminal
                    if(apuntadorFinal == ' '){
                        i--;
                        estado = "q4";
                    }else apuntadorInicio += apuntadorFinal; // Si no es un blanco, se almacena en el apuntador
                    break;
                case "q4": // Se añade a la lista de terminales y se reinicia el apuntadorInicio y estado
                    if(auxTerminales.inicio == null){
                        auxTerminales.agregar(new Nodo(apuntadorInicio));
                        apuntadorInicio = "";estado = "q0";
                    }else{
                        if(estaEnLaLista(apuntadorInicio, "Terminales")){
                            auxTerminales.agregar(new Nodo(apuntadorInicio));
                        }
                        apuntadorInicio = "";estado = "q0";
                    } break;
            }
        }
    }
    
    // Método para separar lados derechos
    public void separarLadosDerechos(){
        estado = "q0";
        apuntadorInicio = "";
        for (int i = 0; i < linea.length(); i++) {
            apuntadorFinal = linea.charAt(i);
            switch(estado){
                case "q0": // Si nos encontramos parte del símbolo que divide los lados derechos de los izquierdos, cambiamos de estado
                    if(apuntadorFinal == '¬')
                        estado = "q1";
                    break;
                case "q1": // Si encontramos un espacio en blanco (es decir, que ya pasó por todo el lado izquierdo y el símbolo que divide
                    //los lados derechos de los izquierdos, cambiamos de estado a almacenar los lados derechos de cada producción
                    if(apuntadorFinal == ' ')
                        estado = "q2";
                    break;
                case "q2":
                    apuntadorInicio += apuntadorFinal;
                    break;
            }
        }
        //Guardamos los lados derechos de la producción en la lista.
        auxLadoDerechos.agregar(new Nodo(apuntadorInicio));
    }
    
    // Método para separar lados izquierdos
    public void separarLadosIzquierdos(){
        estado = "q0";
        apuntadorInicio = "";
        for (int i = 0; i < linea.length(); i++) {
            apuntadorFinal = linea.charAt(i);
            switch(estado){
                case "q0": // Si nos encontramos en la línea un símbolo '<' que indica el primer símbolo no terminal (lado izquierdo), almacenamos y cambiamos de estado.
                    if (apuntadorFinal == '<') {
                        apuntadorInicio += apuntadorFinal;
                        estado = "q1";
                    }break;
                case "q1": // Si nos encontramos en la línea un símbolo '>' que indica que se cierra el primer símbolo no terminal (lado izquierdo), almacenamos y cambiamos de estado.
                    if (apuntadorFinal == '>') {
                        apuntadorInicio += apuntadorFinal;
                        estado = "q2";
                    } else { // Si no es el símbolo que cierra el primer símbolo no terminal, lo acumulamos.
                        apuntadorInicio += apuntadorFinal;
                    } break;
                case "q2": // Termina la evaluación de la línea.
                    break;
            }
        }
        //Guardamos el lado izquierdo de la producción en la lista.
        auxLadoIzquierdos.agregar(new Nodo(apuntadorInicio));
    }
    
    // Método para saber si el símbolo no terminal o terminal ya se encuentra en su respectiva lista y evitar duplicaciones.
    public boolean estaEnLaLista(String elementoAComparar, String lista){
        flag = true; // Colocamos la bandera en verdadero.
        switch(lista){ // Dependiendo si el elemento a comparar es un terminal o no terminal, se establece el primer nodo de su lista.
            case "No terminales":
                auxRecorrer = auxNoTerminales.inicio;
                break;
            case "Terminales":
                auxRecorrer = auxTerminales.inicio;
                break;
        }
        
        /* Se recorre la lista de los terminales o no terminales sea el caso. Si hay un elemento en la lista que equivale
        al elemento nuevo entonces la bandera se pone en falso y no es posible agregar el elemento a la lista, pues ya está repetido*/
        while(auxRecorrer != null){
            if(elementoAComparar.equals(auxRecorrer.follow)){
                flag = false; break;
            }
            auxRecorrer = auxRecorrer.siguiente;
        } return flag;
    }
    
    // Método para generar la lista de producciones.
    public void generarListaProducciones(){
        for (int j = 0; j < arrLadosIzquierdos.length; j++) {
            Nodo p = new Nodo((j+1),arrLadosIzquierdos[j][1],arrLadoDerecho[j][1]);
            listaProducciones.agregar(p);
        }
    }
    
    // Método para obtener la lista de producciones.
    public Lista getProducciones(){
        return listaProducciones;
    }
    
    // Se instancia los arreglos mediante las listas obtenidas de lados derechos, lados izquierdos, símbolos terminales y no terminales.
    public void instanciarArreglos(){
        // Se estable una matriz de [número de elementos en lista][2] para almacenar elemento y número (que nos ayudará con la matriz predictivs)
        arrLadoDerecho = new String[auxLadoDerechos.getContadorElementos()][2];
        arrLadosIzquierdos = new String[auxLadoIzquierdos.getContadorElementos()][2];
        arrNoTerminales = new String[auxNoTerminales.getContadorElementos()][2];
        arrTerminales = new String[auxTerminales.getContadorElementos()][2];
    }
    
    // Se guardan las listas en arreglos
    public void guardarListaEnArreglo(String tipoLista){        
        switch(tipoLista){
            case "terminales":
                recorrerLista(auxTerminales, arrTerminales);
                break;
            case "noTerminales":
                recorrerLista(auxNoTerminales, arrNoTerminales);
            break;
            case "ladosDerechos":
                recorrerLista(auxLadoDerechos, arrLadoDerecho);
            break;
            case "ladosIzquierdos":
                recorrerLista(auxLadoIzquierdos, arrLadosIzquierdos);
            break;
        }
    }
    
    // Se recorren las listas para rellenar las matrices con elementos y números
    public void recorrerLista(Lista lista, String[][] arregloLlenar){
        auxRecorrer = lista.inicio; contadorListas = 0;
        while (auxRecorrer != null) {
            arregloLlenar[contadorListas][1] = auxRecorrer.follow;
            arregloLlenar[contadorListas][0] = contadorListas+"";
            contadorListas++;
            auxRecorrer = auxRecorrer.siguiente;
        }
    }
    
    // Se imprimen los elementos generados.
    public void imprimir(){
        System.out.println("No terminales");
        for (int i = 0; i < arrNoTerminales.length; i++) {
            System.out.println(Arrays.toString(arrNoTerminales[i]));
        }
        System.out.println(auxNoTerminales.getContadorElementos());
        
        System.out.println("\nTerminales");
        for (int i = 0; i < arrTerminales.length; i++) {
            System.out.println(Arrays.toString(arrTerminales[i]));
        }
        System.out.println(auxTerminales.getContadorElementos());
        
        System.out.println("\nLados derechos");
        for (int i = 0; i < arrLadoDerecho.length; i++) {
            System.out.println(Arrays.toString(arrLadoDerecho[i]));
        }
        System.out.println(auxLadoDerechos.getContadorElementos());
        System.out.println("\nLados izquierdos");
        for (int i = 0; i < arrLadosIzquierdos.length; i++) {
            System.out.println(Arrays.toString(arrLadosIzquierdos[i]));
        }
        System.out.println(auxLadoIzquierdos.getContadorElementos());
    }

    // Método para obtener el arreglo de símbolos terminales.
    public String[][] getArrTerminales() {
        return arrTerminales;
    }

    // Método para obtener el arreglo de símbolos no terminales.
    public String[][] getArrNoTerminales() {
        return arrNoTerminales;
    }

    // Método para obtener el arreglo de lados derechos.
    public String[][] getArrLadoDerecho() {
        return arrLadoDerecho;
    }
    
    // Método para obtener el arreglo de lados izquierdos.
    public String[][] getArrLadosIzquierdos() {
        return arrLadosIzquierdos;
    }
}