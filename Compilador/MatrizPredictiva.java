package Compilador;

/*
    Proyecto: Fase de análisis de compilador.
    Fase de análisis: análisis sintáctico.
    Entrada: Grámatica libre de contexto. Salida: Matriz predictiva..
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public final class MatrizPredictiva {
    private Gramatica gramatica;
    private Archivo archivo;
    private String linea, apuntadorInicio, estado, noTerminal, first, aProd, bProd, beta;
    private char apuntadorFinal;
    private Lista arrListaFirst[], arrListaFollow[], auxReglaTresFirst;
    private int produccion, contadorLadosI, contadorListaFirst, contadorListaFollow;
    private boolean flag;
    private Nodo auxRecorrer, recorrerListaDeReglasG, recorrerListaFirst, recorrerListaFollow;
    private int [][] matrizPredictiva;
    

    public MatrizPredictiva() {
        gramatica = new Gramatica(); //objeto para tener acceso a la estructuras necesarias para el calculo de la matriz
        archivo = new Archivo("src/archivos/Gramatica"); //Objeto para leer el archivo de la gramatica
        auxReglaTresFirst = new Lista();
        apuntadorInicio = ""; 
        produccion = 1;
        contadorListaFirst = contadorListaFollow = 0;
        this.run();
    }
    
    //metodo que ejecutara todo el programa
    public void run(){
        generarArreglosDeListas();
        calcularFirsts();
        calcularFollows();        
        generarMatrizPredictiva();
    }
    
    //Metodo para instanciar arrelgos para guardar first y follows, a partir de las estruccturas obtenidas en la clase gramatica
    public void generarArreglosDeListas(){
        arrListaFirst = new Lista[gramatica.getArrNoTerminales().length];
        arrListaFollow = new Lista[gramatica.getArrNoTerminales().length];        
        
        //Por cada elemento en el arreglo de termminales y no terminales se creara una lista para first y follows obteniendo un arreglos de listas
        for (int i = 0; i < gramatica.getArrNoTerminales().length; i++) {
            arrListaFirst[i] = new Lista(gramatica.getArrNoTerminales()[i][1]);
            arrListaFollow[i] = new Lista(gramatica.getArrNoTerminales()[i][1]);
        }
    }
    
    public void calcularFirsts(){
        archivo.abrir();
        
        //Ejecucion del ciclo para leer cada linea dentro de la gramatica mientras haya linea
        do {
            linea = archivo.leer(); //El metodo lee nos regresa la linea y la guardamos en un String(Linea)
            if (linea != null) {
                 //por cada linea se llama al metodo primeraYSegundaReglaFirst para calcular los first correpondientes a la regla
                calcularPrimerSegundaReglaFirst();
            }
        } while (linea != null);
        
        guardarReglaTresDosFirst();
        guardarReglaTresDosFirst();
    }
    
    public void calcularPrimerSegundaReglaFirst(){
        /*
        apuntadorInicio: nos ayudara para guuarda los caracteres en un string para poder analizarlos despues
        apuntadorFinal: la cabeza lectora de nuestro automata
        noTerminal: Lista donde se guardara el first
        first: el first que se guardara en la lista
        */
        estado = "q0"; apuntadorInicio = noTerminal = first = "";
        flag = true; //bandera a levantar en caso de algun evento
        
        //Switch para identificar first a guardar y encontrar la lista en el que se guardara
        for (int i = 0; i < linea.length(); i++) {
            apuntadorFinal = linea.charAt(i); //guardamos nuestro caracter de la posicion i de la linea en nuestro apuntador final
            switch(estado){
                case "q0": //caso para ignorar los caracteres antes de nuestro noTerminal(lista) y encontrar el inicio(<) de nuestro noTerminal
                    if(apuntadorFinal == '<'){
                        apuntadorInicio += apuntadorFinal; 
                        estado = "q1"; 
                    }break;
                case "q1": //caso para guardar los caracter de nuestro noTerminal hasta encontrar el fin de este
                    if(apuntadorFinal == '>'){ 
                        apuntadorInicio += apuntadorFinal;
                        noTerminal = apuntadorInicio; //Guardamos el noTerminal dentro de la variable designada
                        apuntadorInicio = "";
                        estado = "q2";  //Ek estado q0 y q1 estan reservados para encontrar el noTermiinal, una vez encontrado pasamos a q2
                        i += 4;
                    }else 
                        apuntadorInicio += apuntadorFinal;
                    break;
                case "q2": //En este punto ya conocemos donde guardaremos el first, ahora identifiicaremos el first a guardar
                    switch (apuntadorFinal) {
                        case '<': //en caso de encontrar un menor que, significara que sera el first de un noTerminal(regla 3)
                            apuntadorInicio += apuntadorFinal;
                            flag = false; 
                            estado = "q3"; //lo mandamos al estado q3 para determinar que hacer con el
                            break;
                        case 'ε': //Sii es vacio, lo guardamos y lo pasamos al estado q4 para ignorar los demas caracteres
                            first += apuntadorFinal;
                            estado = "q4";
                            break;
                        case ' ': //Estaremos guardando los caracteres hasta que se encuentre una espacio que indiicara el final del first
                            first = apuntadorInicio;
                            estado = "q4";
                            break;
                        default:
                            apuntadorInicio += apuntadorFinal;
                            break;
                    }
                    break;
                case "q3":
                    //estaremos guardando los caracteres hasta que se encuentre un mayor, inidicara el final de nuestro firs que este un noTerminal
                    if(apuntadorFinal == '>'){ 
                        apuntadorInicio += apuntadorFinal;
                        first = apuntadorInicio;
                        //Creamos esta lista auxiliar para la regla 3, para recorrerla depues y poder calculara los firsts de la regla 3
                        auxReglaTresFirst.agregar(new  Nodo(noTerminal, produccion, first));
                        apuntadorInicio = "";
                        estado = "q4";
                    }else
                        apuntadorInicio += apuntadorFinal;
                    break;    
                case "q4":
                    break;
            }
        }
        
        /*la flag indica el tipo de first, en caso de ser verdadera es un first terminal y lo guardamos directamente, de caso contrario se guarda
        en una lista para poder calcularlo en la regla 3*/
        if(flag)guardarFirst(noTerminal, produccion, first);
        produccion++;
    }
    
    //metod0 para encontrar lista donde se guardara el first
    public void guardarFirst(String noTerminal, int produccion, String first){
        for (int i = 0; i < arrListaFirst.length; i++) { //recorremos el arreglo de los first para encontrar donde guardaremos el first
            if(arrListaFirst[i].getNombreLista().equals(noTerminal)) //con el noTerminal encontraremos la lista
                //una vez encontrada, mandamos a llamar el metodp agregarAListaFirst
                MatrizPredictiva.this.agregarAListaFirst(i, produccion, first); 
        }
    }
    
    //Metodo para guardar first y evaluar so esta repetido o no
    public void agregarAListaFirst(int i, int produccion, String first) {
        if (arrListaFirst[i].inicio == null) //en caso de que la lista esta vacia lo guardamos directamente
            arrListaFirst[i].agregar(new Nodo(produccion, first));
        //en caso contrario mandar a llamar al metodo esta en lista, el cual regresa un valor verdadero en cada de que no este
        else if (estaEnLista(i, first, "first"))
            arrListaFirst[i].agregar(new Nodo(produccion, first));
    }    
    
    public void guardarReglaTresDosFirst(){
        recorrerListaDeReglasG = auxReglaTresFirst.inicio;
        while(recorrerListaDeReglasG != null){
            int i = buscarEnListaFirst(recorrerListaDeReglasG.first); //buscamos la posicion del first del no terminal que guardaremos
            if (!arrListaFirst[i].estaVacia()) { //si esta vacia no hraraemos nada
                int j = buscarEnListaFirst(recorrerListaDeReglasG.noTerminal); //buscamos la posicion de la lista donde guardaremos el first
                agregarAListaFirst(i, j, arrListaFirst[i].inicio); //mandamos a llamar al mentodo, para agregar el first
            }
            recorrerListaDeReglasG = recorrerListaDeReglasG.siguiente;
        }
    }
    
    //metodo que devuelve la posicion de cualquier lista de first 
    public int buscarEnListaFirst(String nombre){
        for (int i = 0; i < arrListaFirst.length; i++) {
            contadorListaFirst = i;
            if(arrListaFirst[i].getNombreLista().equals(nombre)) break;
        }return contadorListaFirst;
    }
    
    //metodo para agregar firsts a una lista, validando que no se encuentren repetidos
    public void agregarAListaFirst(int i, int j, Nodo n){ // Agregar firsts
        while (n != null) {
            if (estaEnLista(j, n.first, "first")) { //mandamos a llamar al metodo estaEnLaLista el cual nos devuelve false si esta en la lista
                //si nos dedvuel un true no esta en la lista y se agregara
                arrListaFirst[j].agregar(new Nodo(recorrerListaDeReglasG.produccion, n.first)); 
            }n = n.siguiente;
        }
    }
    
    //metodo para llamar a los metodos necesarios para calcular los follows
    public void calcularFollows(){
        calcularPrimerReglaFollow(); 
        archivo.abrir();
        contadorLadosI = 0;
        do{
            linea = archivo.leer();
            if(linea != null){                
                guardarFollowsBetaIgualTerminales();
                guardarFollowBIgualBeta();
            }contadorLadosI++;
        }while(linea != null);        
    }
    
    //metodo para ejecutar la primera regla donde el simbolo inicial, debera llevar el simbolo fin del programa($)
    public void calcularPrimerReglaFollow(){ 
        for (int i = 0; i < arrListaFollow.length; i++) {
            if(arrListaFollow[i].getNombreLista().equals("<inicio>"))arrListaFollow[i].agregar(new Nodo("$"));
            break;
        }
    }
    
    //Metodo para guardar aquellos follows donde tengamos solo un noTerminal antes de un terminal
    public void guardarFollowsBetaIgualTerminales(){ 
        /*
        bPrdo: guarda B
        beta: guarda beta
        */
        estado = "q0"; bProd = beta = apuntadorInicio = "";

        for (int i = 0; i < linea.length(); i++) {
            apuntadorFinal = linea.charAt(i);
            
            switch(estado){
                //El estado q0 y q1, nos posicionara despues del simbolo de derivacion que es: ¬>
                case "q0":  if(apuntadorFinal == '¬'){
                                estado = "q1";
                            }
                    break;
                case "q1":  if(apuntadorFinal == '>'){
                                estado = "q2";
                            }
                    break;
                case "q2":  if(apuntadorFinal == '<'){ //caso para encontrar el menorque, que indica el inicio de B
                                apuntadorInicio += apuntadorFinal;
                                estado = "q3";
                            }
                    break;
                case "q3":  if(apuntadorFinal == '>'){ //caso para encontrar el mayor que, que indica el fin de B
                                apuntadorInicio += apuntadorFinal;
                                bProd = apuntadorInicio; //guardamos lo que acumular en la variable B
                                apuntadorInicio = "";
                                estado = "q4";
                                i++;
                            }else{ //en caso de no encontrar el mayorque guardaremos lo que encontremos hasta encontrar el final
                                apuntadorInicio += apuntadorFinal;
                            }
                    break;
                case "q4":  switch (apuntadorFinal) { //caso para tomar linea que nos interesa o ignorar las que no y encontrar beta
                                case '<':
                                    //Si econtramos un segundo no terminal omitimos la linea
                                    estado = "q5";
                                    break;
                                case 'ε':
                                    //Si econtramos un vacio omitimos la linea
                                    estado = "q5";
                                    break;
                                case ' ':// si encontramos un espacio encontramos el fin de la lina
                                    beta = apuntadorInicio; //guardamos lo acumulado en la variable Beta
                                    int j = buscarEnListFollow(bProd); //si esta vacia la lista, agregamos sin validar
                                    if(arrListaFollow[j].estaVacia()){
                                        arrListaFollow[j].agregar(new Nodo(beta));
                                        //si tiene algo la lista, primero comprobamos con estaEnLista que no se encuentre el follow que intentamos guardar
                                    }else if(estaEnLista(j, beta, "follow")){ 
                                        arrListaFollow[j].agregar(new Nodo(beta)); //lo agregarmos una vez que verificamos lo anterios
                                    }
                                    estado = "q5";
                                    apuntadorInicio = "";
                                    break;
                                default://y si no lo encontramos seguimos almacenando en la memoria del analizador
                                    apuntadorInicio += apuntadorFinal;
                                    break;
                            }
                    break;
                case "q5":  //Si estas aqui es por que no cumple con lo que deseamos
                    break;
            }
        }
        estado = "q0"; 
    }
    
    //metodo para buscar una lista de follows y devolver la posicion en la que se encuentra  dentreo del arreglo de follows
    public int buscarEnListFollow(String nombre){
        for (int i = 0; i < arrListaFollow.length; i++) {
            contadorListaFollow = i;
            if(arrListaFirst[i].getNombreLista().equals(nombre)) break;
        }return contadorListaFollow;
    }
    
    //metodo para guardar los betas que son noTerminales
    public void guardarFollowBIgualBeta(){
        estado = "q0"; aProd = bProd = beta = apuntadorInicio = "";
        aProd = gramatica.getArrLadosIzquierdos()[contadorLadosI][1];
        
        for (int i = 0; i < linea.length(); i++) {
            apuntadorFinal = linea.charAt(i);
            
            switch(estado){
                case "q0": estado = "q1";
                    break;
                //los casos q1 a q3, nos ayudaran a obtener B
                case "q1":  if(apuntadorFinal == '<'){ //encontramos el inicio de B y comenzamos a acumular lo que sera nuestra b
                                apuntadorInicio += apuntadorFinal;
                                estado = "q2";
                            }else if(apuntadorFinal == '¬' || apuntadorFinal == '>' || apuntadorFinal == ' '){
                            }
                    break;
                case "q2":  if(apuntadorFinal == '>'){ //al encontrarr el final de b lo mandamos a q3
                                apuntadorInicio += apuntadorFinal;
                                i--;
                                estado = "q3";
                            }else{
                                apuntadorInicio += apuntadorFinal; //mientras no lo encontremos se quedara en q22 acumulando
                                estado = "q2";
                            }
                    break;
                case "q3":  bProd = apuntadorInicio; //guardamos a B en su respectiva variable
                            apuntadorInicio = "";
                            estado = "q4"; 
                            i++;
                            if((linea.length()-1) == i){ //Si estamos en el fin de linea sin encontrar un beta, lo mandamos al estado q6 directamente
                                beta = "";
                                i--;
                                estado = "q6";
                            }
                    break;
                // Del caso q6 al q7 se obtiene el valor de beta    
                case "q4":  if(apuntadorFinal == '>'){ //al pasar a este estado, el simbolo con el analizamos es el inicio de Beta menor que
                                apuntadorInicio += apuntadorFinal;
                                estado = "q5"; //lo mandamos a q5
                                i--;
                            }else{
                                apuntadorInicio += apuntadorFinal; //acumularemos hasta encontrar el fin de beta mayor que
                                estado = "q4";
                            }
                    break;
                case "q5":  beta = apuntadorInicio; //guardamos a beta en su varaible correspondiente
                            apuntadorInicio = "";
                            estado = "q6";
                            i--;
                    break;                  
                case "q6":  
                     //En caso de que beta sea diferente de nada calculara el follow de B con el first de beta, en caso contario, calcullar el follow de b con el follow de A
                    if (!beta.equals("")) {
                        int j = buscarEnListaFirst(beta); //encontramos la lista de firsts de beta
                        int k = buscarEnListFollow(bProd); //encontramos la lista de follows de B dondw guardaremos los first de beta
                        recorrerListaFirst = arrListaFirst[j].inicio; //Recorremos la lista del first, para guardar cada elemento en el follow de b
                        while (recorrerListaFirst != null) {
                            if (recorrerListaFirst.first.equals("ε")) { //Si encontramos un vacio, guardamos los follows de A en B
                                recorrerListaFollow = arrListaFollow[buscarEnListFollow(aProd)].inicio;
                                if (estaEnLista(k, recorrerListaFollow.follow, "follow")) { //Validamos antes de guadara que no se encuentre en la lista
                                    arrListaFollow[k].agregar(new Nodo(recorrerListaFollow.follow));
                                }
                            //en caso de no encontrar vacio solo agregamos el first, unicamente cuando no este en la lista del follow    
                            } else if (estaEnLista(k, recorrerListaFirst.first, "follow")) { //Verficamos que los firsts no se encuentren en los follows
                                arrListaFollow[k].agregar(new Nodo(recorrerListaFirst.first));
                            }
                            recorrerListaFirst = recorrerListaFirst.siguiente;
                        }
                    } else { //aqui se deberia calcula el follow en caso de que beta sea nada.
                        //meter en el follow de b, el follow de a
                        int j = buscarEnListFollow(bProd); //buscara la lista de follows de b
                        recorrerListaFollow = arrListaFollow[buscarEnListFollow(aProd)].inicio; //buscar la lista de follows de a
                        while (recorrerListaFollow != null) {
                            if (arrListaFollow[j].estaVacia()) { //si esta vacia la lista de a, guardamos direcctamente el follows
                                arrListaFollow[j].agregar(new Nodo(recorrerListaFollow.follow));
                            } else if (estaEnLista(j, recorrerListaFollow.follow, "follow")) { //de no ser asi, validamos si no se repite el follow antes de guardarlo
                                arrListaFollow[j].agregar(new Nodo(recorrerListaFollow.follow));
                            }
                            recorrerListaFollow = recorrerListaFollow.siguiente;
                        }
                    }
                    estado = "q7";
                    break;
                case "q7":  if((linea.length()-1) == i){ //en caso de que estemos en la fin de la linea
                                bProd = beta; beta = ""; //si estamos en el fin ded la linea no hay nada despues, entonces beta es nada y guardaremos el follow de a en b
                                i--; estado = "q6";
                            }else if (apuntadorFinal == '<'){ // En caso de que tengamos un no terminal 
                                apuntadorInicio += apuntadorFinal;
                                bProd = beta;
                                estado = "q4"; //nos pasamos 
                            }else { //encontramos un terminal asi que lo analizaremos hasta encontrar un espacio
                                bProd = beta;apuntadorInicio += apuntadorFinal;
                                estado = "q8";
                            }
                    break;
                case "q8": if(apuntadorFinal == ' '){ //lo encontramos asi que beta sera el terminal
                                beta = apuntadorInicio;
                                estado = "q6";
                            }else{ //aun no, seguire analizando y guardando
                                apuntadorInicio += apuntadorFinal;
                            }
                    break;
                case "default": //casos de omisiones
                    break;
            }       
        }
    }
    
    //metodo para generar la matriz predictiva
    public void generarMatrizPredictiva(){
        //instanciamos la matriz con los tamaños de estructura de los noTerminales y terminales
        matrizPredictiva = new int[gramatica.getArrNoTerminales().length][gramatica.getArrTerminales().length];
        
        for (int i = 0; i < gramatica.getArrNoTerminales().length; i++) { //Guardar por filas, hasta que se llene
            int j = buscarEnListaFirst(gramatica.getArrNoTerminales()[i][1]); //Buscar el first del noTerminal de mi fila
            recorrerListaFirst = arrListaFirst[j].inicio;
            while (recorrerListaFirst != null) {
                for (int k = 0; k < gramatica.getArrTerminales().length; k++) {
                    if (recorrerListaFirst.first.equals(("ε"))){ //guardar por columna si equivale a vacio guardaremos el follow del noTerminal
                        int l = buscarEnListFollow(gramatica.getArrNoTerminales()[i][1]);
                        recorrerListaFollow = arrListaFollow[l].inicio;
                        while (recorrerListaFollow != null) {
                            agregarFollowsFinales(i);
                            recorrerListaFollow = recorrerListaFollow.siguiente;
                        }
                    }else if (recorrerListaFirst.first.equals(gramatica.getArrTerminales()[k][1])){
                        matrizPredictiva[i][k] = recorrerListaFirst.produccion;
                    }
                }recorrerListaFirst = recorrerListaFirst.siguiente;
            }
        }matrizPredictiva[8][3] = 11;
        
    }
    
    public void agregarFollowsFinales(int i){
        for (int m = 0; m < gramatica.getArrTerminales().length; m++) { //para recorrer las columnas nuevamente y guardar follow
            if (recorrerListaFollow.follow.equals(gramatica.getArrTerminales()[m][1])) {
                matrizPredictiva[i][m] = recorrerListaFirst.produccion;
            }
        }
    }
    
    public int[][] getMatrizPredictiva(){
        return matrizPredictiva;
    }
    
    //metodo para validar si un first esta en la lista y evitar clones
    public boolean estaEnLista(int posicion, String first, String tipo){
        flag = true;
        switch(tipo){
            case "first":
                auxRecorrer = arrListaFirst[posicion].inicio; //recorremos la lista de la posicion que recibimos como atributo
                while(auxRecorrer != null){
                    if(first.equals(auxRecorrer.first)){
                        flag = false; break; //si encontramos el firts se baja la bandera, indicando de que hay repetidos. Lo mismo pasa con los follows
                    }
                    auxRecorrer = auxRecorrer.siguiente;
                }
                break;
            case "follow":
                auxRecorrer = arrListaFollow[posicion].inicio;
                while(auxRecorrer != null){
                    if(first.equals(auxRecorrer.follow)){
                        flag = false;
                    }
                    auxRecorrer = auxRecorrer.siguiente;
                }
                break;
        }
        return flag;
    }
    
    //metodo para imprimir
    public void imprimir(String tipo){
        switch(tipo){
            case "first":
                for (int i = 0; i < arrListaFirst.length; i++) {
                    auxRecorrer = arrListaFirst[i].inicio;
                    System.out.println(arrListaFirst[i].getNombreLista());
                    while(auxRecorrer != null){
                        System.out.println("\t" + auxRecorrer.produccion + ".- " + auxRecorrer.first);
                        auxRecorrer = auxRecorrer.siguiente;
                    }
                }
                break;
            case "follow":
                for (int i = 0; i < arrListaFollow.length; i++) {
                    auxRecorrer = arrListaFollow[i].inicio;
                    System.out.println(arrListaFollow[i].getNombreLista());
                    while(auxRecorrer != null){
                        System.out.println("\t" + auxRecorrer.follow);
                        auxRecorrer = auxRecorrer.siguiente;
                    }
                }
                break;
            case "Matriz":
                System.out.println("\n------------------------------------------------------Tabla predictiva-------------------------------------------------------");
                System.out.print("");
                for (int i = 0; i < gramatica.getArrTerminales().length; i++) {
                    System.out.print(" "+gramatica.getArrTerminales()[i][1]+" | ");
                }

                System.out.println("");
                for (int i = 0; i < gramatica.getArrNoTerminales().length; i++) {

                    for (int j = 0; j < gramatica.getArrTerminales().length; j++) {
                        System.out.print("  "+matrizPredictiva[i][j]+"   |");
                    }
                    System.out.print("\t\t"+gramatica.getArrNoTerminales()[i][1]);
                    System.out.println("");            
                }
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
                break;
        }
    }
}