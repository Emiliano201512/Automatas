package Compilador;

/*
    Proyecto: Fase de análisis de compilador.
    Clase genérica de estructura de datos: lista.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class Lista {
    Nodo inicio;
    Nodo fin;
    Nodo anterior, temporal, recorrer;
    private int contadorElementos;
    private String nombreLista;
    
    // Constructor para crear una lista simple
    public Lista(){ 
        inicio = fin = null; //Principio de la lista
        contadorElementos = 0; // Variable para obtener el tamaño de la lista para poder generar estructura de arreglos dinamicas
    }
    
    // Constructor para crear una lista con nombre
    public Lista(String nombreLista){ 
        this.nombreLista = nombreLista; //variable para guardar el nombre de la lista
        inicio = fin = null;
    }
    
    // Método para agregar elementos a la lista mediante nodos
    public void agregar(Nodo nuevo){
        if(inicio == null){ // Si está vacía el nuevo nodo pasa a ser el inicio y el fin de la lista y se incrementa el contador de elementos
            inicio = fin = nuevo;
            contadorElementos++;
        }else{ // Si no lo está:
            fin.siguiente = nuevo; // Crear un nodo nuevo después del fin y asignarle el valor del nuevo nodo       
            fin = nuevo; // Como el nodo nuevo es el último, a este se le asigna como el nodo final 
            contadorElementos++;
        }
    }
    
    // Método para saber si la lista está vacía
    public boolean estaVacia(){
        return inicio == null;
    }

    // Método para saber el tamaño de la lista
    public int getContadorElementos() {
        return contadorElementos;
    }
    
    // Método para obtener el nombre de la lista
    public String getNombreLista() {
        return nombreLista;
    }
}
