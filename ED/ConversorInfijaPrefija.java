package ED;

/*
    Proyecto: Fase de análisis de compilador.
    Fase de análisis: análisis semántico.
    Entrada: Expresión (triada) infija. Salida: Expresión (triada) prefija.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class ConversorInfijaPrefija {
    private String prefija, infija, temp, expresionInv;
    private Pila<Character> p, poperadores;
    private Pila<String> pInvertir;
    private char caracter;
    private char[] operadores; 
    
    public ConversorInfijaPrefija(){
        p = new Pila<>(); // Creamos una nueva pila para almacenar los caracteres.
        poperadores = new Pila<>(); // Creamos una nueva pila para almacenar los operadores.
        pInvertir = new Pila<>(); // Creamos una nueva pila para almacenar los operadores.
        prefija = temp = expresionInv = "";
        operadores = new char[]{'^','/','*','+','-','='}; // Arreglo de operadores permitidos por el lenguaje del compilador.
    }
    
    //Método para convertir una expresión infija en expresión prefija.
    public String convertir(String inf){
        infija = inf;
        prefija = ""; // Inicializamos la cadena que contendrá la expresión prefija en blanco.
        for(int i = infija.length()-1; i >= 0; i--){
            // Recorremos la expresión infija que se ha pasado por el parámetro 
            caracter = infija.charAt(i); // Caracter será el char que encontremos analizando de la expresión infija.
            if(esOperador(caracter)){ // Si el carácter es operador y la pila está vacía, apilamos en la pila de carácteres el operador.
                if(p.estaVacia()){
                    p.apilar(caracter);
                }else{ // Si no está vacía, checamos la prioridad del operador contra la prioridad de la pila del tope
                    if(prioridad(caracter) > prioridadPila(p.tope())){
                        p.apilar(caracter); // Si es mayor, apilamos el caracter en la pila con la expresión prefija.
                    }else{ // Si no es mayor, desapilamos lo que hay en la pila, lo almacenamos en la expresión prefija y apilamos el carácter.
                        prefija += p.desapilar();
                        p.apilar(caracter);
                    }
                }
            }else if(caracter == ' '){ // Si el caracter equivale a un blanco, desapilamos la pila de operadores
                while(!poperadores.estaVacia()){
                    prefija += poperadores.desapilar();
                }prefija += " ";
            }else{ // Si no es ni blanco ni operador, lo apilamos en la pila de operadores
                poperadores.apilar(caracter);
            }
        }
        // Una vez terminado el análisis de cada carácter, desapilamos lo que quedo en la pila y lo almacenamos en la cadena que tiene la expresión prefija.
        while (!p.estaVacia()) {
            prefija += p.desapilar();
        } // Retornamos la expresión prefija
        return prefija;
    }
    
    // Método para determinar si un char es operador recorriendo el arreglo de operadores y devolviendo un booleano.
    public boolean esOperador(char c){
        for(int i = 0; i < operadores.length; i++){
            if(c == operadores[i])
                return true;
        }
        return false;
    }
    
    // Método para determinar la prioridad de los operadores
    public int prioridad(char op){
        switch (op) {
            case '=': return 1;
            case '/':
            case '*': return 3;
            case '-':
            case '+': return 2;
            default:  return 0;
        }
    }
    
    // Método para determinar la prioridad de los operadores en pila de operadores
    public int prioridadPila(char op) {
        switch (op) {
            case '=': return 1;
            case '/':
            case '*': return 3;
            case '-':
            case '+': return 2;
            default:  return 0;
        }
    }
    
    public String invertir(){
        temp = "";
        for (int i = 0; i < prefija.length(); i++) {
            if(prefija.charAt(i)== ' ' && !temp.isEmpty()){
                pInvertir.apilar(temp);
                temp = "";
            }else{
                temp += prefija.charAt(i);
            }
        }
        pInvertir.apilar(prefija.charAt(prefija.length()-1)+"");
        
        expresionInv = pInvertir.getContenidoPila();
        return expresionInv;
    }
    
}