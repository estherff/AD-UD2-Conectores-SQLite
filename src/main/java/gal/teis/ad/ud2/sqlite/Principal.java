package gal.teis.ad.ud2.sqlite;

import gal.teis.conexion.ConexionSingleton;
import gal.teis.excepciones.NumeroFueraRangoException;
import gal.teis.libreriadam.ControlData;
import gal.teis.libreriadam.Menu;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        String nombreTabla;
        sc = new Scanner(System.in); //creo una instancia de la clase Scanner para la introdución de datos por teclado
        String cadenaConexion = "jdbc:sqlite:bdSQLite\\agenda.db";

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

                    case 2://Ver contenido de una tabla
                        System.out.println("Introduce el nombre de la tabla de la que ver su contenido");
                        nombreTabla = ControlData.lerString(sc);
                        verContenidoTabla(nombreTabla, laConexion);
                        break;
                    case 3://Ver campos de la tabla
                        System.out.println("Introduce el nombre de la tabla de la que quieres ver sus campos");
                        nombreTabla = ControlData.lerString(sc);
                        System.out.println("Los campos de la tabla son");
                        mostrarCamposTabla(nombreTabla, laConexion);
                        break;
                    case 4://Buscar un contacto por nombre
                        System.out.println("Introduce el NICK del contacto para ver la información");
                        String nombreContacto = ControlData.lerString(sc);
                        buscarRegistroTabla(laConexion, nombreContacto);
                        break;
                    case 5://Inserta un registro
                        insertarRegistroTabla(laConexion);
                        break;
                    case 6:
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

        ArrayList<String> misOpciones = new ArrayList<String>() {
            {
                add("Mostrar TODAS LAS TABLAS de la base de datos");
                add("Mostrar TODO EL CONTENIDO de una tabla");
                add("Ver TODOS LOS CAMPOS de una tabla");
                add("Buscar en la tabla CONTACTOS por NICK");
                add("Insertar un registro en la tabla CONTACTOS");
                add("Finalizar");
            }
        };

        /*La clase Menu permite imprimir el menú a partir de los datos de un ArrayList<String>
            y utilizar métodos para control de rango*/
        Menu miMenu = new Menu(misOpciones);

        System.out.println("\n\n*******************************************************************************************************");
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

    /**
     * Muestra todas las tablas de la base de datos
     */
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

    /**
     * Muestra el contenido de la tabla que se pasa por parámetro
     *
     * @param nombreTabla
     * @param miConexion
     * @throws SQLException
     */
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
                    for (int i = 1; i <= resul.getMetaData().getColumnCount(); i++) {
                        System.out.printf("%s ", resul.getString(i));
                    }
                    System.out.println();
                }
            }
        }
    }

    /**
     * Muestra los campos de la tabla que se pasa por parámetro
     *
     * @param nombreTabla
     * @param miConexion
     * @throws SQLException
     */
    static void mostrarCamposTabla(String nombreTabla, Connection miConexion) throws SQLException {
        // Preparamos la consulta
        try (Statement elStatement = miConexion.createStatement()) {
            String sql = "SELECT * FROM " + nombreTabla;

            try (ResultSet resul = elStatement.executeQuery(sql)) {

                for (int i = 1; i <= resul.getMetaData().getColumnCount(); i++) {
                    System.out.print(resul.getMetaData().getColumnName(i) + "\t");
                }
                System.out.println();

            }
        }
    }
    
    
    /**
     * Bucar registros en la tabla contacto con un NICK. Utilizamos parámetros en sentencia SQL 
     * @param miConexion
     * @param nick
     * @throws SQLException 
     */
    static void buscarRegistroTabla(Connection miConexion, String nick) throws SQLException{
            // Preparamos la consulta
            PreparedStatement elPrepareStatement = miConexion.prepareStatement("SELECT * FROM CONTACTOS WHERE NICK = ?");
            elPrepareStatement.setString(1, nick);
            ResultSet resul = elPrepareStatement.executeQuery();
            while (resul.next()) {
                    for (int i = 1; i <= resul.getMetaData().getColumnCount(); i++) {
                        System.out.printf("%s ", resul.getString(i));
                    }
                    System.out.println();
                }
    }

    /**
     * Usaremos la sentencia insert con parámetros, introduciendo en el
     * sentencia SQL interrogaciones allí donde queremos que los datos sean
     * variables. Para ello necesitamos la clase PreparedStatement después
     * podemos utilizar setString(indiceCampo, valor) para dar valor a esos
     * parámetros
     *
     * @param nombreTabla
     * @param miConexion
     * @throws SQLException
     */
    static void insertarRegistroTabla(Connection miConexion) throws SQLException {   
        System.out.println("Introduce el nick");
        String nick = ControlData.lerString(sc);
        System.out.println("Introduce el nombre del contacto ");
        String nombre = ControlData.lerString(sc);
        System.out.println("Introduce los apellidos del contacto ");
        String apellidos = ControlData.lerString(sc);
        System.out.println("Introduce la dirección ");
        String direccion = ControlData.lerString(sc);
        System.out.println("Introduce un nº de teléfono");
        String telefono1 = ControlData.lerString(sc);
        System.out.println("Introduce otro nº de teléfono");
        String telefono2 = ControlData.lerString(sc);

            // Preparamos la consulta
            PreparedStatement elPrepareStatement = miConexion.prepareStatement("INSERT INTO CONTACTOS VALUES (?,?,?,?,?,?)");

            elPrepareStatement.setString(1, nick);
            elPrepareStatement.setString(2, nombre);
            elPrepareStatement.setString(3, apellidos);
            elPrepareStatement.setString(4, direccion);
            elPrepareStatement.setString(5, telefono1);
            elPrepareStatement.setString(6, telefono2);
            /*
        Cuando la sentencia a ejecutar no devuelve un conjunto de resultados no 
        deberemos de usar executeQuery(), sino que deberemos de utilizar executeUpdate(). 
        Esto es aplicable a INSERT, UPDATE y DELETE.
             */
            int registrosInsertados = elPrepareStatement.executeUpdate();

            if (registrosInsertados == 1) {
                System.out.println("Registro insertado");
            } else {
                System.out.println("La operación no se ha llevado a cabo");
            }
    }

}
