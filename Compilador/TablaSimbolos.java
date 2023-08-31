package Compilador;

/*
    Proyecto: Fase de análisis de compilador.
    Fase de análisis: análisis léxico - sintáctico - semántico.
    Entrada: Lista de tokens. Salida: Tabla de símbolos.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class TablaSimbolos extends AnalizadorSintactico{
    private Lista tablaDeSimbolos;
    private Nodo recorrerLista, recorrerListaAux;
    private boolean flag, flagNombreP;
    private String auxLinea;
    private int auxRepeticion;

    public TablaSimbolos() {
        flagNombreP = false;
        tablaTokens = aLex.getTokens(); //variable para guarda la tabla de simbolos
        tablaDeSimbolos = new Lista(); //variable para creacion de tabla de simbolos
        run();
    }
    
    //Método para funcionamiento y ejecución de la clases
    public final void run(){
        generarTablaSimbolos();
        asignarTipo();
    }
    
    // Método para generar la tabla de símbolos
    public void generarTablaSimbolos(){
        auxLinea = "";
        recorrerLista = tablaTokens.inicio; //recorremos la tabla de tokens
        while(recorrerLista != null){
            switch(recorrerLista.descripcion){
                case "identificador": //en caso de encontrar un identificador
                    if(!flagNombreP){
                        if(estaEnTabla(recorrerLista.lexema)){ //si no esta en la tabla, lo agregamos
                            //el nodo que estamos utilizando, nos pide 6 atributos: token, tipo, atributo, repeticion, linea de codigo y valor
                            tablaDeSimbolos.agregar(new Nodo(recorrerLista.lexema, "", recorrerLista.atributo, 1,  Integer.toString(recorrerLista.lineaCodigo), 0));
                        }else{ //si esta, actualizamos los datos del token en la tabla de simboles
                            actulizarDatos(recorrerLista.lexema);
                        }
                    }else{
                        flagNombreP = false;
                    }
                    break;
                case "entero": //en caso de encontrar un numero entero
                    if(estaEnTabla(recorrerLista.lexema)){ //si no esta lo agregamos
                        tablaDeSimbolos.agregar(new Nodo(recorrerLista.lexema, "entero", recorrerLista.atributo, 1,  Integer.toString(recorrerLista.lineaCodigo), recorrerLista.atributo));
                    }else{ //si esta actualizamos los datos
                        actulizarDatos(recorrerLista.lexema);
                    }
                    break;
                case "real": //en caso de encontrar un numero real
                    if(estaEnTabla(recorrerLista.lexema)){ //si no esta lo agregamos
                        if(recorrerLista.accion.equals("real")){ 
                            tablaDeSimbolos.agregar(new Nodo(recorrerLista.lexema, "real", recorrerLista.atributoReal, 1,  Integer.toString(recorrerLista.lineaCodigo), recorrerLista.atributoReal));
                        }else{
                            tablaDeSimbolos.agregar(new Nodo(recorrerLista.lexema, "real", recorrerLista.atributo, 1,  Integer.toString(recorrerLista.lineaCodigo), recorrerLista.atributoReal));
                        }
                    }else{ //Si esta, unicamente actualizamos datos
                        actulizarDatos(recorrerLista.lexema);
                    }
                    break;
                case "Palabra reservada": //en caso de encontrar una palabra reservadad
                    if(recorrerLista.lexema.equals("programa")){
                        flagNombreP = true;
                    }
            }
            recorrerLista = recorrerLista.siguiente;
        }
    }
    
    public void asignarTipo(){
        recorrerLista = tablaTokens.inicio; //para la asignacion de tipos recorreremos la tabla de tokens
        boolean flagEntero = false,flagReal = false;
        
        while(recorrerLista != null){
            if(recorrerLista.lexema.equals("entero")) flagEntero = true; //si el apuntador apunta a un lexema que sea} entero, levantamos la bandera de entero
            else if(recorrerLista.lexema.equals("real")) flagReal = true; //si no la de numero real
            
            if(flagEntero){
                if(recorrerLista.descripcion.equals("identificador")){ //las palabra siguientes del lexema entero sera su tipo, siempre y cuando sean identificadores
                    actualizarTipo(recorrerLista.lexema, "entero");
                }else if(recorrerLista.lexema.equals(";")){ //nos detenemos cuando nos encontramos el delimitador de linea
                    flagEntero = false; //y bajamos la bandera
                }
            }else if(flagReal){ //de mismo modo para los numeros reales
                if(recorrerLista.descripcion.equals("identificador")){
                    actualizarTipo(recorrerLista.lexema, "real");
                }else if(recorrerLista.lexema.equals(";")){
                    flagReal = false;
                }
            }
            recorrerLista = recorrerLista.siguiente;
        }
    }
    
    //Metodo para validar si un lexema esta en la tabla
    public boolean estaEnTabla(String token){
        recorrerListaAux = tablaDeSimbolos.inicio;
        flag = true; //regresa la bandera en true
        
        while(recorrerListaAux != null){
            if(token.equals(recorrerListaAux.token)){ //la bandera se baja cuando se encuentra un token igual
                flag = false;
                break;
            }
            recorrerListaAux = recorrerListaAux.siguiente;
        }
        
        return flag;
    }    
    
    //Metodo para actualizar datos
    public void actulizarDatos(String token){
        recorrerListaAux = tablaDeSimbolos.inicio;
        
        while(recorrerListaAux != null){
            if(token.equals(recorrerListaAux.token)){ //recorremos la tabla de simbolos para encontrar el token que vamos a actualizar
                auxRepeticion = recorrerListaAux.numRep + 1; //actualizamos el numero de repeticiones con el anterior que tenia aumentando en 1 
                recorrerListaAux.setNumRep(auxRepeticion);
                auxLinea = recorrerListaAux.getLinea() + ", " + recorrerLista.lineaCodigo; //concatenamos la linea de codigo nueva, con la anteriores que ya existian
                recorrerListaAux.setLinea(auxLinea);
                break;
            }
            recorrerListaAux = recorrerListaAux.siguiente;
        }
    }
    
    //metodo para acutalizar tipos
    public void actualizarTipo(String token, String tipo){
        recorrerListaAux = tablaDeSimbolos.inicio;
        
        while(recorrerListaAux != null){
            if(token.equals(recorrerListaAux.token)){
                if(recorrerListaAux.tipo.equals("")){ //si no tiene tipo se puede actualizar
                    recorrerListaAux.setTipo(tipo);
                    break;
                }else{ //de caso contrario tenemos un error semantico
                    AnalizadorLexico.tablaErrores.agregar(new Nodo(Integer.toString(AnalizadorLexico.idError), "La variable " + token + " ya fue declarada", recorrerListaAux.linea));
                }
            }
            recorrerListaAux = recorrerListaAux.siguiente;
        }
    }
    
    public void imprimirErrores(){
        recorrerLista = AnalizadorLexico.tablaErrores.inicio;
        while(recorrerLista != null){
            System.out.println("Se produjo un error en la linea: " + recorrerLista.linea + " " + recorrerLista.descripcion + ". Id de error: " + recorrerLista.idError);
            recorrerLista = recorrerLista.siguiente;
        }
    }
    
    public void imprimir(){
        recorrerListaAux = tablaDeSimbolos.inicio;
        System.out.println("\n   Token"+"\tTipo"+"\tAtributo"+"  Repeticiones"+"\t\tLinea\t Valor");
        while(recorrerListaAux != null){
            if(recorrerListaAux.tipo.equals("real") && recorrerListaAux.atributo==0){
                System.out.printf("%10s%10s%10s%10s%20s%10s%n",recorrerListaAux.token , recorrerListaAux.tipo , recorrerListaAux.atributoReal , recorrerListaAux.numRep , recorrerListaAux.linea,recorrerListaAux.valorReal);
            }else{
                System.out.printf("%10s%10s%10s%10s%20s%10s%n",recorrerListaAux.token , recorrerListaAux.tipo , recorrerListaAux.atributo, recorrerListaAux.numRep ,recorrerListaAux.linea , recorrerListaAux.valor);
            }
            recorrerListaAux = recorrerListaAux.siguiente;
        }
    }
    
    public Lista getTablaSimbolos(){
        return tablaDeSimbolos;
    }
    
    public Lista getTablaTokens(){
        return aLex.getTokens();
    }

}