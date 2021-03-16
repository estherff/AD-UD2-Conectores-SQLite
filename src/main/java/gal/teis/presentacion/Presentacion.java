/*
 * Capa lógica de presentación

Hace referencia a como se va a presentar la información del programa al usuario. 
El objetivo es separar todo aquellos que se muestra al usuario, esta capa no 
tiene conexión a base de datos, ni realizar operaciones de ningún tipo solo
muestra datos en pantalla, la capa de presentación solicita mediante funciones 
que se ejecutan en la capa de la lógica de negocio.
 */
package gal.teis.presentacion;

import gal.teis.excepciones.NumeroFueraRangoException;
import gal.teis.libreriadam.ControlData;
import gal.teis.libreriadam.Menu;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Esther Ferreiro
 */
public class Presentacion {

    //Variable que representa la entrada de datos estándar
    static Scanner sc = new Scanner(System.in);

    /**
     * Mensajes informativo de un error
     */
    public static void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
    
    public static void mostrarError(String mensaje) {
        System.out.println(mensaje);
    }

    /**
     * Mensajes informativo del exito de la operación de creación de la base de
     * datos
     */
    public static void mostrarCreacionCorrecta() {
        System.out.println("La tabla LIBROS se ha creado con éxito");
    }

    /**
     * Leer el título
     *
     * @return String titulo
     */
    public static String leerTitulo() {
        String titulo;
        System.out.println("Introduce el TÍTULO del libro");
        titulo = ControlData.lerString(sc);
        return titulo;
    }

    /**
     * Leer el autor
     *
     * @return String autor
     */
    public static String leerAutor() {
        String autor;
        System.out.println("Introduce el/la AUTOR/A del libro");
        autor = ControlData.lerString(sc);
        return autor;
    }
      /**
     * Leer el precio
     *
     * @return Double precio
     */
    public static Double leerPrecio() {
        Double precio;
        System.out.println("Introduce el PRECIO del libro");
        precio = ControlData.lerDouble(sc);
        return precio;
    }

    /**
     * Leer el id
     *
     * @return Long id
     */
    public static Long leerId() {
        Long id;
        System.out.println("Introduce el ID del libro");
        id = ControlData.lerLong(sc);
        return id;
    }



/************ALTAS***************/
public static String otraOperacionSN (){
         String otro;
         System.out.println("¿Desea dar de ALTA otro libro (S/N)?");
         otro = ControlData.lerNome(sc).toUpperCase();
         return otro;
     }
     public static void insertarLibrosCorrecta(String resultado){
         System.out.println("Se han insertado los siguientes libros: " + resultado);
     }
     

/****************MENUS************************/
/**
     * Dibuja un menú en la consola a partir con unas opciones determinadas
     */
   public static byte pintarMenu() {
        byte opcion = 0;
        boolean correcta;

        ArrayList<String> misOpciones;
        misOpciones = new ArrayList<String>() {
            {
                add("CREAR BASE DE DATOS");
                add("ALTAS");
                add("BAJAS");
                add("CONSULTAS");
                add("FINALIZAR");
            }
        };

        /*La clase Menu permite imprimir el menú a partir de los datos de un ArrayList<String>
            y utilizar métodos para control de rango*/
        Menu miMenu = new Menu(misOpciones);

        System.out.println("\n\n*************************GESTION DE BIBLIOTECA*************************************");
        /* Solo sale del While cuando se selecciona una opción correcta en rango y tipo*/
        do {
            miMenu.printMenu();

            /*La clase ControlData permite hacer un control de tipo leído*/
            try {
                opcion = ControlData.lerByte(sc);
                /*miMenu.rango() lanza una excepción propia en el caso de que 
                el parámetro opcion esté fuera del rango posible */
                miMenu.rango(opcion);
                correcta = true;
            } catch (NumeroFueraRangoException e) {//Excepción personalizada
                System.out.println(e.getMessage());
                correcta = false;
            }

        } while (!correcta);

        return opcion;
    }

   public static byte pintarSubMenuConsultas() {
        byte opcion = 0;
        boolean correcta;

        ArrayList<String> misOpciones;
        misOpciones = new ArrayList<String>() {
            {
                add("Ver Libro por TITULO");
                add("Ver Libro por ID");
                add("Ver Libros por AUTOR");
                add("Ver Libros por PRECIO");
                add("Ver todos los libros");
                add("Finalizar");
            }
        };

        /*La clase Menu permite imprimir el menú a partir de los datos de un ArrayList<String>
            y utilizar métodos para control de rango*/
        Menu miMenu = new Menu(misOpciones);

        System.out.println("\n\n*********************BÚSQUEDAS EN BIBLIOTECA*******************************");
        /* Solo sale del While cuando se selecciona una opción correcta en rango y tipo*/
        do {
            miMenu.printMenu();

            /*La clase ControlData permite hacer un control de tipo leído*/
            try {
                opcion = ControlData.lerByte(sc);
                /*miMenu.rango() lanza una excepción propia en el caso de que 
                el parámetro opcion esté fuera del rango posible */
                miMenu.rango(opcion);
                correcta = true;
            } catch (NumeroFueraRangoException e) {//Excepción personalizada
                System.out.println(e.getMessage());
                correcta = false;
            }

        } while (!correcta);

        return opcion;
    }
   
   public static void cerrarelScanner(){
       sc.close();
   }
}