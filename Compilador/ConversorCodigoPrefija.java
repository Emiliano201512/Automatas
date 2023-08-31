package Compilador;

import java.io.FileWriter;
import java.io.PrintWriter;

public final class ConversorCodigoPrefija {

    private String lineaCodigo, nuevaExpresion;
    private String[] variables, expresiones;
    private int contador;
    private Lista auxLista;
    private FileWriter fichero;
    private Nodo auxRecorrer;
    private boolean continuar;

    public ConversorCodigoPrefija(String[] expresiones) {
        this.expresiones = expresiones;
        auxLista = new Lista();
        contador = 1;
        run();
        imprimir();
        guardar();
    }

    //Optimización global. Eliminar espacios en blanco 
    public void run() {
        AnalizadorLexico.archivo.abrir(); // Se abre el archivo
        do {
            lineaCodigo = AnalizadorLexico.archivo.leer();
            if (lineaCodigo != null && !lineaCodigo.equals("")) {
                nuevaExpresion = lineaCodigo;
                eliminarSangria();
                nuevaExpresion = nuevaExpresion.replace(";", "");
                nuevaExpresion = nuevaExpresion.replace("(", " ");
                nuevaExpresion = nuevaExpresion.replace(")", "");
                if (nuevaExpresion.contains(",")) {
                    analizarComa();
                } else if (lineaCodigo.contains("=")) {
                    auxLista.agregar(new Nodo(expresiones[contador - 1]));
                    contador++;
                } else {
                    auxLista.agregar(new Nodo(nuevaExpresion));
                }
            }
        } while (lineaCodigo != null);
    }

    public void eliminarSangria() {
        continuar = true;
        for (int i = 0; i < nuevaExpresion.length(); i++) {
            switch (nuevaExpresion.charAt(i)) {
                case ' ':
                    if (continuar) {
                        i++;
                    }
                    break;
                default:
                    if (continuar) {
                        nuevaExpresion = nuevaExpresion.substring(i, nuevaExpresion.length());
                        continuar = false;
                    }
                    break;
            }
        }
    }

    public void analizarComa() {
        variables = nuevaExpresion.split(",");
        if (nuevaExpresion.contains("entero")) {
            sustituirDeclaracion("entero");
        } else if (nuevaExpresion.contains("real")) {
            sustituirDeclaracion("real");
        }
    }

    // Optimización global. Espacios en blanco.
    public void sustituirDeclaracion(String tipo) {
        for (int i = 0; i < variables.length; i++) {
            if (variables[i].contains(tipo)) {
                variables[i] = variables[i].replace(tipo, "");
                variables[i] = variables[i].replace(" ", "");
                auxLista.agregar(new Nodo(tipo + " " + variables[i]));
            } else {
                variables[i] = variables[i].replace(",", "");
                variables[i] = variables[i].replace(" ", "");
                auxLista.agregar(new Nodo(tipo + variables[i]));
            }
        }
    }

    public void imprimir() {
        System.out.println("\n\nCódigo en formato prefijo\n");
        auxRecorrer = auxLista.inicio;

        while (auxRecorrer != null) {
            System.out.println(auxRecorrer.follow);
            auxRecorrer = auxRecorrer.siguiente;
        }
    }

    public void guardar() {
        auxRecorrer = auxLista.inicio;

        try {
            fichero = new FileWriter("src/Archivos/CodigoPrefija.txt");
            PrintWriter pw = new PrintWriter(fichero);
            while (auxRecorrer != null) {
                if (auxRecorrer.follow != null) {
                    pw.print(auxRecorrer.follow + "\n");
                }
                auxRecorrer = auxRecorrer.siguiente;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
