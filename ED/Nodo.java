package ED;

/*
    Proyecto: Fase de análisis de compilador.
    Clase genérica de estructura de datos: nodo.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class Nodo<T>{
    private T elemento;
    private Nodo<T>siguiente;
    
    // Constructor de clase Nodo que tiene un elemento T y un Nodo siguiente
    public Nodo(T elemento,Nodo<T>siguiente){
        this.elemento=elemento ;
        this.siguiente=siguiente;
    } 

    // Clase para obtener el elemento T del nodo.
    public T getElemento(){
        return elemento;
    }

    // Clase para colocar el elemento T del nodo.
    public void setElemento(T elemento){
        this.elemento = elemento;
    }

    // Clase para obtener el nodo siguiente del presente nodo.
    public Nodo<T> getSiguiente(){
        return siguiente;
    }

    // Clase para colocar el nodo siguiente del presente nodo.
    public void setSiguiente(Nodo<T> siguiente){
        this.siguiente=siguiente;
    }

    // Clase para escribir el elemento T del nodo.
    @Override
    public String toString(){
        return elemento+"\n";
    }  
}
