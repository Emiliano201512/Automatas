package Compilador;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/*
    Proyecto: Fase de análisis de compilador.
    Clase genérica de estructura de archivo.
    Entrada: Nombre de archivo de texto. Salida: Lectura y cierre de archivo.
    Autores: Corralejo Alamilla Evelyn Rocio, Díaz Rodríguez Fabián Emiliano, Valadez Molina Carlos Humberto.
*/

public class Archivo {
    private String filename; //Guardar direccion de archivo
    private Scanner lector; //Encargado de leer el archivo
   
    //Constructor para recibir la dirección de archivo desde otra clase.
    public Archivo(String filename){
        this.filename = filename;
    }
    
    //Todo archivo realiza dos acciones: abrir, leer y cerrar
    public void abrir(){
        //Para evitar problemas al abrir el archivo
        try{
            //Al lector se le aasigna el archivo que leera
            lector = new Scanner(new File(filename));
        }catch(IOException e){
            System.err.println("Problema con el archivo " + filename + e.getMessage());
            System.exit(0);
        }
    }
    
    //Al leer un archivo regeresamos lo leído línea por línea
    public String leer(){
        //Si el lector esta lleno y existe línea que leer, regresamos la siguiente línea
        if(lector != null && lector.hasNextLine())
            return lector.nextLine();
        //Si no la hay no regresamos nada y cerramos el archivo
        else{
            cerrar();
            return null;            
        }
    }
    
    public void cerrar(){
        lector.close();
    }
}