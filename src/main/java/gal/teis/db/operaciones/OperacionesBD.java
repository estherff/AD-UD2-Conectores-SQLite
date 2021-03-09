/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gal.teis.db.operaciones;

import gal.teis.excepciones.OperacionBaseDatosException;
import gal.teis.libreriadam.ControlData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DefaultStyledDocument;

/**
 *
 * @author Esther Ferreiro
 */
public class OperacionesBD {

    static Scanner sc = new Scanner(System.in);

    /**
     * ****************** CREAR BASE DE DATOS********************************
     */
    /**
     * Crea las tablas de la base de datos
     *
     * @param sentencia Statement
     * @throws gal.teis.excepciones.OperacionBaseDatosException
     */
    public static void crearBD(Statement sentencia) throws OperacionBaseDatosException {

        try {
            sentencia.execute("CREATE TABLE LIBROS "
                    + "(IDLIBRO INT NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + " TITULO  VARCHAR(50) NOT NULL,"
                    + " AUTOR  VARCHAR(50) NOT NULL,"
                    + " PRECIO  FLOAT);");

        } catch (SQLException ex) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            //El mensaje lo tomo hasta e primer \n pues es donde indica la información útil
            String mensaje = "No se ha podido realiazar la operación\n"
                    + ex.getMessage().substring(0, ex.getMessage().indexOf("\n"));
            throw new OperacionBaseDatosException(mensaje);
        }

    }

    /**
     * ****************** ALTAS EN LA BASE DE
     * DATOS********************************
     */
    public static String altaLibros(Connection laConexion, String titulo, String autor, double precio) throws OperacionBaseDatosException {
        String mensaje = "";
        try {
            // Preparamos la consulta
            // El parámetro Statement.RETURN_GENERATED_KEYS permite recuperar el ID autogenerado tras el INSERT
            PreparedStatement elPrepareStatement = laConexion.prepareStatement("INSERT INTO LIBROS (TITULO,AUTOR, PRECIO) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            //Asignamos los parámetros
            elPrepareStatement.setString(1, titulo);
            elPrepareStatement.setString(2, autor);
            elPrepareStatement.setDouble(3, precio);
            //Ejecutamos la operación de inserción en la tabla LIBROS
            int affectedRows = elPrepareStatement.executeUpdate();
            mensaje = "Se ha agregado " + affectedRows + " libro a la tabla LIBROS";
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            mensaje = "No se ha podido realizar la operación de alta solicitada\n";
            throw new OperacionBaseDatosException(mensaje + "\n" + e.getMessage());
        }
        return mensaje;
    }

    /**
     * ****************** BAJAS EN LA BASE DE
     * DATOS********************************
     */
    public static String bajaLibrosPorTitulo(Connection laConexion, String titulo) throws OperacionBaseDatosException {
        String mensaje = "";
        try {
            // Preparamos la consulta
            // El parámetro Statement.RETURN_GENERATED_KEYS permite recuperar el ID autogenerado tras el INSERT
            PreparedStatement elPrepareStatement = laConexion.prepareStatement("DELETE FROM LIBROS WHERE TITULO = ?", Statement.RETURN_GENERATED_KEYS);
            //Asignamos los parámetros
            elPrepareStatement.setString(1, titulo);
            //Ejecutamos la operación de inserción en la tabla LIBROS
            int affectedRows = elPrepareStatement.executeUpdate();
            mensaje = "Se ha eliminado " + affectedRows + " libro de la tabla LIBROS";
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            mensaje = "No se ha podido realizar la operación de alta solicitada\n";
            throw new OperacionBaseDatosException(mensaje + "\n" + e.getMessage());
        }
        return mensaje;

    }

    /**
     * ****************** CONSULTAS EN LA BASE DE
     * DATOS********************************
     */
    public static String consultaLibroPorTitulo(Connection laConexion, String titulo) throws OperacionBaseDatosException {

        PreparedStatement elPrepareStatement = null;
        String resultado = "";
        ResultSet elResultSet = null;
        int count = 0;
        try {
            // Preparamos la consulta
            elPrepareStatement = laConexion.prepareStatement("SELECT * FROM LIBROS WHERE TITULO = ?");
            //Asignamos los parámetros
            elPrepareStatement.setString(1, titulo);
            //ejecuta la operación de inserción
            elResultSet = elPrepareStatement.executeQuery();

            /*Recorre el ResultSet, conociendo en nombre y el tipo de los
                campos que se quieren leer*/
            while (elResultSet.next()) {
                resultado += " ID: " + elResultSet.getInt("IDLIBRO");
                resultado += " TITULO: " + elResultSet.getString("TITULO");
                resultado += " AUTOR: " + elResultSet.getString("AUTOR");
                resultado += " PRECIO: " + elResultSet.getDouble("PRECIO");
                resultado += "\n";
                count++;
            }
            resultado += "Se han encontrado " + count + " libros";
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            String mensaje = "Se ha producido un error desconocido en la base de datos.\n Póngase en contacto con el/la responsable\n";
            throw new OperacionBaseDatosException(mensaje);
        }
        return resultado;

    }

    public static String consultaLibroPorId(Connection laConexion, int id) throws OperacionBaseDatosException {

        PreparedStatement elPrepareStatement = null;
        String resultado = "";
        ResultSet elResultSet = null;
        int count = 0;
        try {
            // Preparamos la consulta
            elPrepareStatement = laConexion.prepareStatement("SELECT * FROM LIBROS WHERE IDLIBRO = ?");
            //Asignamos los parámetros
            elPrepareStatement.setInt(1, id);
            //ejecuta la operación de inserción
            elResultSet = elPrepareStatement.executeQuery();

            /*Recorre el ResultSet, conociendo en nombre y el tipo de los
                campos que se quieren leer*/
            while (elResultSet.next()) {
                resultado += " ID: " + elResultSet.getInt("IDLIBRO");
                resultado += " TITULO: " + elResultSet.getString("TITULO");
                resultado += " AUTOR: " + elResultSet.getString("AUTOR");
                resultado += " PRECIO: " + elResultSet.getDouble("PRECIO");
                resultado += "\n";
                count++;
            }
            resultado += "Se han encontrado " + count + " libros";
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            String mensaje = "Se ha producido un error desconocido en la base de datos.\n Póngase en contacto con el/la responsable\n";
            throw new OperacionBaseDatosException(mensaje);
        }
        return resultado;

    }
    
     public static String consultaLibroPorAutor(Connection laConexion, String autor) throws OperacionBaseDatosException {

        PreparedStatement elPrepareStatement = null;
        String resultado = "";
        ResultSet elResultSet = null;
        int count = 0;
        try {
            // Preparamos la consulta
            elPrepareStatement = laConexion.prepareStatement("SELECT * FROM LIBROS WHERE AUTOR = ?");
            //Asignamos los parámetros
            elPrepareStatement.setString(1, autor);
            //ejecuta la operación de inserción
            elResultSet = elPrepareStatement.executeQuery();

            /*Recorre el ResultSet, conociendo en nombre y el tipo de los
                campos que se quieren leer*/
            while (elResultSet.next()) {
                resultado += " ID: " + elResultSet.getInt("IDLIBRO");
                resultado += " TITULO: " + elResultSet.getString("TITULO");
                resultado += " AUTOR: " + elResultSet.getString("AUTOR");
                resultado += " PRECIO: " + elResultSet.getDouble("PRECIO");
                resultado += "\n";
                count++;
            }
            resultado += "Se han encontrado " + count + " libros";
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            String mensaje = "Se ha producido un error desconocido en la base de datos.\n Póngase en contacto con el/la responsable\n";
            throw new OperacionBaseDatosException(mensaje);
        }
        return resultado;

    }
      public static String consultaLibroPorPrecio(Connection laConexion, double precio) throws OperacionBaseDatosException {

        PreparedStatement elPrepareStatement = null;
        String resultado = "";
        ResultSet elResultSet = null;
        int count = 0;
        try {
            // Preparamos la consulta
            elPrepareStatement = laConexion.prepareStatement("SELECT * FROM LIBROS WHERE PRECIO = ?");
            //Asignamos los parámetros
            elPrepareStatement.setDouble(1, precio);
            //ejecuta la operación de inserción
            elResultSet = elPrepareStatement.executeQuery();

            /*Recorre el ResultSet, conociendo en nombre y el tipo de los
                campos que se quieren leer*/
            while (elResultSet.next()) {
                resultado += " ID: " + elResultSet.getInt("IDLIBRO");
                resultado += " TITULO: " + elResultSet.getString("TITULO");
                resultado += " AUTOR: " + elResultSet.getString("AUTOR");
                resultado += " PRECIO: " + elResultSet.getDouble("PRECIO");
                resultado += "\n";
                count++;
            }
            resultado += "Se han encontrado " + count + " libros";
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            String mensaje = "Se ha producido un error desconocido en la base de datos.\n Póngase en contacto con el/la responsable\n";
            throw new OperacionBaseDatosException(mensaje);
        }
        return resultado;

    }
      
      

    public static String consultaTodoLosLibros(Connection miConexion) throws OperacionBaseDatosException {
        String resultado = "";
        // Preparamos la consulta
        try (Statement elStatement = miConexion.createStatement()) {
            String sql = "SELECT * FROM LIBROS";

            try (ResultSet resul = elStatement.executeQuery(sql)) {

                //Se utilizan metadatos para averiguar el nombre de los campos
                for (int i = 1; i <= resul.getMetaData().getColumnCount(); i++) {
                    resultado += resul.getMetaData().getColumnName(i) + "\t\t\t";
                }

                resultado += "\n";

                while (resul.next()) {
                    /*Se utilizan metadatos para recorrer la columnas del recordset
                    Se recoge todo como String por el el tipo que puede incluir a 
                    todos los tipos específicos de la tabla*/
                    for (int i = 1; i <= resul.getMetaData().getColumnCount(); i++) {
                        resultado += resul.getString(i) + "\t\t\t";
                    }
                    resultado += "\n";
                }
            }
        } catch (SQLException e) {
            //Envio a la excepción propia el mensaje de la excepción que se produzca aquí
            throw new OperacionBaseDatosException(e.getMessage());
        }
        return resultado;
    }

}
