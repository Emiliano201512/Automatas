package ED;

/*
    Proyecto: Fase de análisis de compilador.
    Clase genérica de estructura de datos: pila.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class Pila <T>{
    private Nodo<T> tope;
    private int tamaño;
    private String desapilado;
    
    // Constructor de pila con un tope nulo y tamaño 0.
    public Pila(){
        tope = null;
        this.tamaño = 0;
        desapilado = "";
    }
    
    // Método para apilar elemento T
    public void apilar(T elemento){
        Nodo<T> aux = new Nodo<>(elemento, tope);
        tope = aux;
        this.tamaño++;
    }
    
    // Método para desapilar elemento T, actualizar tope y reducir tamaño
    public T desapilar(){
        if(!estaVacia()){
            T elemento = tope.getElemento(); 
            Nodo<T> aux = tope.getSiguiente();
            tope = aux;
            this.tamaño--;
            return elemento;
        }else return null;
    }
    
    // Método para determinar si la pila está vacía
    public boolean estaVacia(){
        return tope == null;
    }
    
    // Método que regresa tope de la pila.
        public T tope(){
        return tope.getElemento();
    }
    
    // Método que regresa tamaño de la pila.
    public int tamañoPila(){
        return tamaño;
    }
    
    // Método para vaciar pila.
    public String getContenidoPila(){
        desapilado = "";
        while(!estaVacia()){
            desapilado += desapilar() + " ";
        }
        return desapilado;
    }
    
    // Método para vaciar pila.
    public void vaciarPila(){
        while(!estaVacia()){
            desapilar();
        }
    }
}
