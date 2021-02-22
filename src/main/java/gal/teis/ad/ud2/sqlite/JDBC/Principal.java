package gal.teis.ad.ud2.sqlite.JDBC;

import gal.teis.excepciones.NumeroFueraRangoException;
import gal.teis.libreriadam.ControlData;
import gal.teis.libreriadam.Menu;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Esther Ferreiro URL de consulta
 * https://serprogramador.es/como-conectar-y-utilizar-java-con-sqlite/
 */
public class Principal {

    /**
     * @param args the command line arguments
     */
    //Variable que representa la entrada de datos estándar
    static Scanner sc;

    public static void main(String[] args) {
        //Para finalizar el menú 
        boolean finalizar = false;
        sc = new Scanner(System.in); //creo una instancia de la clase Scanner para la introdución de datos por teclado
        String cadenaConexion = "jdbc:sqlite:"
                + "D:\\IES\\6_Teis\\Modulo-Acceso a datos-DAM2\\2 AVALIACION\\UD2\\SQLite\\matriculas.db";

        //Establece la conexión con una base de datos SQLite.
        //No es necesario cerrar la conexión al usar try con recursos
        try (Connection laConexion = ConexionSingleton.getConnection(cadenaConexion)) {
            do {
                switch (pintarMenu()) {
                    case 1:

                        System.out.println("\n************************************************************\n"
                                + "LAS TABLAS DE LA BASE DE DATOS ABIERTA SON: ");
                        verTablas(laConexion);

                        break;

                    case 2:
                        System.out.println("Introduce el nombre de la tabla de la que ver su contenido");
                        String nombreTabla = ControlData.lerString(sc);
                        verContenidoTabla(nombreTabla, laConexion);

                        break;
                    case 9:
                        System.out.println("Hasta luego!!!");
                        finalizar = true;
                }
            } while (!finalizar);

        } catch (SQLException e) {
            System.out.println("Se ha producido una excepcion " + e.getMessage()
                    + " El programa se cerrará. /n Consulte con el responsable");
        } finally {//Cerrar conexión y flujo de entrada estándar

            sc.close();
        }

    }

    /**
     * Dibuja un menú en la consola a partir con unas opciones determinadas
     */
    static byte pintarMenu() {
        byte opcion = 0;
        boolean correcta;
        System.out.println("\n\n*******************************************************************************************************");
        /* Solo sale del While cuando se selecciona una opción correcta en rango y tipo*/
        do {
            ArrayList<String> misOpciones = new ArrayList<String>() {
                {
                    add("Mostrar las tablas de la base de datos");
                    add("Mostrar todos los datos de las tablas");

                    add("Finalizar");
                }
            };

            /*La clase Menu permite imprimir el menú a partir de los datos de un ArrayList<String>
            y utilizar métodos para control de rango*/
            Menu miMenu = new Menu(misOpciones);
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

    static void verTablas(Connection miConexion) throws SQLException {
        // Preparamos la consulta
        try (Statement elStatement = miConexion.createStatement()) {
            String sql = "SELECT * FROM sqlite_master WHERE type = \'table\'";
            // Recorremos el resultado para visualizar cada fila
            // Se hace un bucle mientras haya registros y se van visualizando
            try (ResultSet resul = elStatement.executeQuery(sql)) {
                // Recorremos el resultado para visualizar cada fila
                // Se hace un bucle mientras haya registros y se van visualizando
                while (resul.next()) {
                    /*Para saber qué getString(i)*/
                    System.out.printf("%s %n", resul.getString(2));
                }
            }
        }
    }

    static void verContenidoTabla(String nombreTabla, Connection miConexion) throws SQLException {
        // Preparamos la consulta
        try (Statement elStatement = miConexion.createStatement()) {
            String sql = "SELECT * FROM " + nombreTabla;
            // Recorremos el resultado para visualizar cada fila
            // Se hace un bucle mientras haya registros y se van visualizando
            try (ResultSet resul = elStatement.executeQuery(sql)) {
                // Recorremos el resultado para visualizar cada fila
                // Se hace un bucle mientras haya registros y se van visualizando
                while (resul.next()) {
                    /*Para saber qué getString(i)*/
                    System.out.printf("%s %n", resul.getString(2));
                }
            }
        }
    }

}
