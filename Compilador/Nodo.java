package Compilador;

/*
    Proyecto: Fase de análisis de compilador.
    Clase para crear nodos en listas.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class Nodo {
    String follow, first, noTerminal, atributoArb, tipoAtrArb, triada, operando,operador1,operador2;
    String lexema, descripcion, accion, token, tipo, linea, ladoI, ladoD, idError;
    int numRep, lineaCodigo, atributo, numP, produccion, valor;
    float atributoReal, valorReal;
    Nodo siguiente; // Para la creacion de un nuevo nodo
    
    /*
        En esta clase hay varios constructores sobrecargados, cada uno tiene la función propia:
        para la creación de tabla dentro del análisis léxico, sintáticto y semántico
    */
    
    //Nodo para guardar Follows de tabla predictiva
    Nodo(String follow){
        this.follow = follow; // se crea y se le asigna el valor al nodo
        siguiente = null; // El siguiente de este es null porque no hay nada despues
    }
    
    // Nodo para almacenar una producción por partes
    Nodo(int num, String ladoI, String ladoD){
        this.numP = num; 
        this.ladoI = ladoI;
        this.ladoD = ladoD;
    }
    
    // Nodo para guardar información de árbol sintáctico abstracto
    Nodo(String atributo, String tipo){
        this.atributoArb = atributo;
        this.tipoAtrArb = tipo;
        siguiente = null;
    }
    
    // Nodo para guardar First de símbolos terminales
    Nodo(int produccion, String first){
        this.produccion = produccion;
        this.first = first;
        
        siguiente = null;
    }
    
    // Nodo para guardar First de símbolos no terminales
    Nodo(String noTerminal, int produccion, String first){
        this.noTerminal = noTerminal;
        this.produccion = produccion;
        this.first = first;
        
        siguiente = null;
    }
    
    // Nodo para tokens de tipo entero en la tabla de tokens
    Nodo(String lexema, String descripcion, String accion, int atributo, int lineaCodigo, int numRep){
        this.lexema = lexema;
        this.descripcion = descripcion;
        this.accion = accion;
        this.atributo = atributo;
        this.lineaCodigo = lineaCodigo;
        this.numRep = numRep;
        siguiente = null;
    }
    
    // Nodo para tokens de tipo real en la tabla de tokens
    Nodo(String lexema, String descripcion, String accion, float atributo, int lineaCodigo, int numRep){
        this.lexema = lexema;
        this.descripcion = descripcion;
        this.accion = accion;
        this.atributoReal = atributo;
        this.lineaCodigo = lineaCodigo;
        this.numRep = numRep;
        siguiente = null;   
    }
    
    // Nodo para tokens de tipo entero en la tabla de simbolos
    Nodo(String token, String tipo, int atributo, int numRep, String linea, int valor){
        this.token = token;
        this.tipo = tipo;
        this.atributo = atributo;
        this.numRep = numRep;
        this.linea = linea;
        this.valor = valor;
        siguiente = null;
    }
    
    // Nodo para tokens de tipo real en la tabla de simbolos
    Nodo(String token, String tipo, float atributo, int numRep, String linea, float valor){
        this.token = token;
        this.tipo = tipo;
        this.atributoReal = atributo;
        this.numRep = numRep;
        this.linea = linea;
        this.valorReal = valor;
        siguiente = null;
    }
    
    // Nodo para errores
    Nodo(String idError, String descripcion, String linea){
        this.idError = idError;
        this.descripcion = descripcion;
        this.linea = linea;
        siguiente = null;
    }

    // Métodos para actualizacion de datos en la clase de tabla de simbolos

    public void setNumRep(int numRep) {
        this.numRep = numRep;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public String getLinea() {
        return linea;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}