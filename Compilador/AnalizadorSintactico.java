package Compilador;

import ED.Pila;

/*
    Proyecto: Fase de análisis de compilador.
    Fase de análisis: análisis sintáctico descendente.
    Entrada: Tabla de tokens. Salida: Análisis aceptado o análisis erróneo.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class AnalizadorSintactico {
    private Pila<String>  pila;
    private String nextT, xTemp, ladosDT[], tokenEnviar;
    protected AnalizadorLexico aLex;
    private Gramatica gramatica;
    private MatrizPredictiva matrizP;
    private int columna, fila, lineaCodigo;
    protected Lista tablaTokens;
    private Nodo recorrerLista, auxRecorrer;

    public AnalizadorSintactico(){
        pila = new Pila<>(); // Instanciamos pila que simula el proceso de recursividad existente en las derivaciones.
        aLex = new AnalizadorLexico(); // Instanciamos analizador léxico
        matrizP = new MatrizPredictiva(); // Instanciamos matriz predictiva que dicta las reglas de sintaxis
        gramatica = new Gramatica(); // Instanciamos gramática
        nextT = xTemp = tokenEnviar = ""; 
        columna = fila = lineaCodigo = 0;
        this.analizar(matrizP.getMatrizPredictiva()); //Iniciamos proceso de análisis sintáctico
    }
    
    //Método para análisis sintáctico obteniendo como parámetro la matriz predictiva que se ha calculado en la instancia de la misma.
    public final void analizar(int[][] matriz){
        //Realizamos análisis léxico y guardamos la lista de tokens generada.
        aLex.run(); aLex.guardarLista();
        tablaTokens = aLex.getTokens();
        // Iniciamos a recorrer la lista de tokens para analizar los tokens dirigidos por la gramática
        recorrerLista = tablaTokens.inicio;
        // Apilamos el no terminal que determina la estructura de nuestra gramática
        pila.apilar("<system_goal>");
        // El símbolo temporal será el tope de la pila
        xTemp = pila.tope();
        // Obtenemos el token de nuestra lista de tokens y el token a analizar corresponde a lo obtenido por el método antes llamado
        obtenerToken(); nextT = tokenEnviar;
        // Mientras la pila se encuentre vacía, analizamos token por token.
        while(!pila.estaVacia()){
            // Si el token a analizar (nextT) equivale a un terminal, se obtiene el número de la columna que le corresponde en la matriz predictiva
            compararTokenTerminales();
            // Si el tope (xTemp) equivale a un no terminal, entonces, se obtiene el número de la fila que le corresponde en la matriz predictiva:
            if(buscarTopeNT()){
                // Si existe una producción (es decir, en la matriz hay un número distinto de 0)
                if(matriz[fila][columna]!=0){
                    // Buscamos la producción y determinamos sus lados derechos
                    buscarProduccion(matriz);
                    // Desapilamos el tope
                    pila.desapilar();
                    // Por cada uno de los lados derechos de la producción (a excepción de ε), apilamos
                    for (int j = ladosDT.length - 1; j >= 0; j--) {
                        if (!"ε".equals(ladosDT[j])) {  
                            pila.apilar(ladosDT[j]);
                        }
                    } // El símbolo temporal se convierte en el tope
                    xTemp = pila.tope();
                }else{ // Si no existe la producción (en la matriz el número es 0), se agrega a la tabla de errores y se detiene el análisis
                    AnalizadorLexico.tablaErrores.agregar(new Nodo(Integer.toString(AnalizadorLexico.idError), "Error de sintaxis, se esperaba: " + xTemp, Integer.toString(this.lineaCodigo)));
                    AnalizadorLexico.idError++;
                    System.out.println("Error de sintaxis, se esperaba: " + xTemp);
                    break;
                }
            }else{ // Si el tope no es terminal (y por tanto, es terminal, un token) se compara el tope con el token a analizar
                if(xTemp.equals(nextT)){ //Si equivalen, se desapilan y se pide un nuevo token a la lista de tokens
                    pila.desapilar();
                    obtenerToken(); 
                    nextT = tokenEnviar;
                    // El nuevo xTemp es el tope de la pila tras haberse desapilado
                    xTemp = pila.tope();
                } else if(xTemp.equals("$")){ //Si el tope de la pila es igual que la marca de fin de archivo ($), el analizador sintáctico no tiene más elemento por analizar
                    pila.desapilar(); // Se desapila la marca del fin de archivo ($) y se acepta el programa en el análisis sintáctico.
                    System.out.println("Programa aceptado en análisis sintáctico.");break;
                }else{ // Caso contraro, se agrega a la tabla de errores y el análisis se detiene
                    AnalizadorLexico.tablaErrores.agregar(new Nodo(Integer.toString(AnalizadorLexico.idError), "Error de sintaxis se esperaba: "  + xTemp, Integer.toString(this.lineaCodigo)));
                    AnalizadorLexico.idError++;
                    break;
                }
            }
        }
    }
    
    // Método para comparar el token solicitado con la lista de terminales y determinar su número de columna dentro de la matriz predictiva.
    public void compararTokenTerminales(){
        for (String[] terminales : gramatica.getArrTerminales()) {
            if (nextT.equalsIgnoreCase(terminales[1])) {
                columna = Integer.parseInt(terminales[0]); break;
            }
        }
    }
    
    // Método para comparar el tope de la pila con la lista de no terminales y determinar su número de fila dentro de la matriz predictiva.
    public boolean buscarTopeNT(){
        for (String[] noTerminales : gramatica.getArrNoTerminales()) {
            if (xTemp.equals(noTerminales[1])) {
                fila = Integer.parseInt(noTerminales[0]);
                return true;
            }
        }return false;
    }
    
    // Método para buscar la producción en la matriz y determinar sus lados derechos para apilarlos en la pila
    public void buscarProduccion(int[][] matriz){
        // Comenzamos a recorrer la lista de producciones.
        auxRecorrer = gramatica.getProducciones().inicio;
        while(auxRecorrer != null){
            if(matriz[fila][columna]==auxRecorrer.numP){
                ladosDT = auxRecorrer.ladoD.split(" ");break;
            }
            auxRecorrer = auxRecorrer.siguiente;
        }
    }
    
    // Método para ir obteniendo tokens de la lista de tokens.
    public void obtenerToken(){
        while(recorrerLista != null){
            // Determinamos la línea de código del token que estamos recorriendo para guardar el error con su línea de código en caso de que sea necesario
            lineaCodigo = recorrerLista.lineaCodigo;
            /* Debido a que la gramática no cuenta con lexemas como tal, sino con 'identificadores', si encontramos que la descripción de
            un lexema equivale a identificar, ese será el token a analizar. De la misma manera con 'litreal' y 'litentero' */
            if("identificador".equals(recorrerLista.descripcion)){
                tokenEnviar = "identificador";break;
            }else if("real".equals(recorrerLista.descripcion)){
                tokenEnviar = "litreal";break;
            }else if("entero".equals(recorrerLista.descripcion)){
                tokenEnviar = "litentero";break;
            }else{ // Caso contrario, el token a analizar es el lexema
                tokenEnviar = recorrerLista.lexema;break;
            }
        }if(recorrerLista!=null)recorrerLista = recorrerLista.siguiente;
    }
}