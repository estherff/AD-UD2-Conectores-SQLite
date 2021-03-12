/*
 * This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS for A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

Capa de datos

Aquí tendremos clases y funciones que se conectan a la base de datos y es donde 
se realizan transacciones con sql para leer, insertar, modificar o eliminar 
información en la base de datos.
 

Aquí ejecutaremos consultas sql de forma que ninguna de las otras capas saben 
donde esta la base de datos, así la capa de presentación podría estar en un pc y
las otras capas en un servidor como servicio de software Saas

 */
package gal.teis.db.datos;

import gal.teis.negocio.Libro;
import gal.teis.excepciones.OperacionBaseDatosException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


/**
 *
 * @author Esther Ferreiro
 */
public class OperacionesBD {

    /**
     * Campos estáticos relacionados con la cadena de conexión de la base de
     * datos
     */
    public static String cadenaConexion = "jdbc:h2:.\\data\\biblioteca";
    public static String user = "admin";
    public static String pass = "admin";

    /**
     * ****************** CREAR BASE DE DATOS********************************
     */
    /**
     * Crea las tablas de la base de datos
     *
     * @throws OperacionBaseDatosException
     */
    public static void crearBD() throws OperacionBaseDatosException {

        try (Connection miConexion = ConexionSingleton.getConnection(cadenaConexion, user, pass);
                Statement elStatement = miConexion.createStatement()) {

            elStatement.execute("CREATE TABLE LIBROS "
                    + "(IDLIBRO LONG NOT NULL PRIMARY KEY AUTO_INCREMENT,"
                    + " TITULO  VARCHAR(50) NOT NULL,"
                    + " AUTOR  VARCHAR(50) NOT NULL,"
                    + " PRECIO  FLOAT NOT NULL);");

        } catch (SQLException ex) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            //El mensaje lo tomo hasta e primer \n pues es donde indica la información útil
            String mensaje = "No se ha podido realiazar la operación\n"
                    + ex.getMessage();
            throw new OperacionBaseDatosException(mensaje);
        }

    }

    /*
     * ****************** ALTAS EN LA BASE DE DATOS**************************
     */
    /**
     * Determina una transición para agregar varios registros a una base de
     * datos.
     *
     * @param listaLibrosInsertar ArrayList que contiene las instancias de la
     * clase Libros a añadir a la base de datos
     * @return String Mensaje con los IDs de los libros añadidos
     * @throws OperacionBaseDatosException
     */
    public static String altaListaLibros(ArrayList<Libro> listaLibrosInsertar) throws OperacionBaseDatosException {
        String mensaje = "";

        try (Connection miConexion = ConexionSingleton.getConnection(cadenaConexion, user, pass);) {
            try {
                //Se inicia una transación desactivando los commits automáticos
                miConexion.setAutoCommit(false);
                for (Libro auxLibro : listaLibrosInsertar) {
                    //Realizar el alta del libro
                    mensaje += OperacionesBD.altaLibro(miConexion, auxLibro.getTitulo(), auxLibro.getAutor(), auxLibro.getPrecio());
                }
                //Se ejecutan las operaciones de la transación
                miConexion.commit();
                //Se restaura el funcionamiento de los commits automáticos
                miConexion.setAutoCommit(true);

            } catch (SQLException ex) {
                try {
                    //En caso de que suceda algún error durante la insercción de datos, se desacen todas las operaciones
                    miConexion.rollback();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
                mensaje = "No se ha podido realizar la operación de alta solicitada\n";
                throw new OperacionBaseDatosException(mensaje + "\n" + ex.getMessage());
            }
        } catch (SQLException ex) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            mensaje = "No se ha podido establecer conexión con la base de datos\n";
            throw new OperacionBaseDatosException(mensaje + "\n" + ex.getMessage());
        }
        //Se devuelven todos los IDs de los registros insertados
        return mensaje;
    }

    /**
     * Da de alta libros en la tabla LIBROS
     *
     * @param laConexion Objeto Connection, conexión de la base de datos
     * @param titulo String, título del libro
     * @param autor String, autor del libro
     * @param precio double, precio del libro
     * @return String, mensaje mensaje de la operación
     * @throws java.sql.SQLException
     */
    public static String altaLibro(Connection laConexion, String titulo, String autor, double precio) throws SQLException {
        String mensaje="";

        // Preparamos la consulta
        // El parámetro Statement.RETURN_GENERATED_KEYS permite recuperar el ID autogenerado tras el INSERT
        try (PreparedStatement elPrepareStatement = laConexion.prepareStatement("INSERT INTO LIBROS (TITULO,AUTOR, PRECIO) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);) {
            //Asignamos los parámetros
            elPrepareStatement.setString(1, titulo);
            elPrepareStatement.setString(2, autor);
            elPrepareStatement.setDouble(3, precio);

            //Ejecutamos la operación de inserción en la tabla LIBROS
            int affectedRows = elPrepareStatement.executeUpdate();

            //Recupero los id's generados en las altas de los libros
            //Esta operación la puedo hacer debeido a utilizar el parámetro Statement.RETURN_GENERATED_KEYS 
            try (ResultSet elResultSet = elPrepareStatement.getGeneratedKeys()) {
                while (elResultSet.next()) {
                    mensaje = "\nID: " + elResultSet.getLong(1) + "- Titulo: " + titulo;
                }
            }

        }
        return mensaje;
    }

    /**
     * ****************** BAJAS EN LA BASE DEDATO******************************
     */
    /**
     * Permite dar de baja un libro conociendo el título
     *
     * @param titulo String, título del libro
     * @return String, mensaje mensaje de la operación
     * @throws OperacionBaseDatosException
     */
    public static String bajaLibrosPorTitulo(String titulo) throws OperacionBaseDatosException {
        String mensaje="";
        try (Connection miConexion = ConexionSingleton.getConnection(cadenaConexion, user, pass);) {
            //Asignamos los parámetros
            try ( // Preparamos la consulta
                    // El parámetro Statement.RETURN_GENERATED_KEYS permite recuperar el ID autogenerado tras el INSERT
                    PreparedStatement elPrepareStatement = miConexion.prepareStatement("DELETE FROM LIBROS WHERE TITULO = ?", Statement.RETURN_GENERATED_KEYS)) {
                //Asignamos los parámetros
                elPrepareStatement.setString(1, titulo);
                //Ejecutamos la operación de inserción en la tabla LIBROS
                int affectedRows = elPrepareStatement.executeUpdate();
            }
           
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            mensaje = "No se ha podido realizar la operación de alta solicitada\n";
            throw new OperacionBaseDatosException(mensaje + "\n" + e.getMessage());
        }
        
        mensaje = "Se ha eliminado el libro con el título " + titulo;
        return mensaje;

    }

    /**
     * ****************** CONSULTAS EN LA BASE DE DATOS************************
     */
    /**
     * Muestra los libros que se corresponden con un título determinado
     *
     * @param titulo String, título del libro, campo de búsqueda
     * @return String, mensaje con la información de los libros encontrados
     * @throws OperacionBaseDatosException
     */
    public static String consultaLibroPorTitulo(String titulo) throws OperacionBaseDatosException {
        String resultado = "";
        int count = 0;

        try (Connection miConexion = ConexionSingleton.getConnection(cadenaConexion, user, pass);
                PreparedStatement elPrepareStatement = miConexion.prepareStatement("SELECT * FROM LIBROS WHERE TITULO = ?");) {

            //Asignamos los parámetros
            elPrepareStatement.setString(1, titulo);
            //ejecuta la operación de inserción
            try (ResultSet elResultSet = elPrepareStatement.executeQuery();){

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
            } 
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            String mensaje = "Se ha producido un error al conectarse a la base de datos.\n Póngase en contacto con el/la responsable\n";
            throw new OperacionBaseDatosException(mensaje + "\n" + e.getMessage());
        }
        //Si todo ha salido bien se devuelve el mensaje de la consulta
        resultado += "Se han encontrado " + count + " libros";
        return resultado;

    }

    /**
     * Muestra los libros que se corresponden con un ID determinado
     *
     * @param id in, identificador único del libro, campo de búsqueda
     * @return String, mensaje con la información de los libros encontrados
     * @throws OperacionBaseDatosException
     */
    public static String consultaLibroPorId(long id) throws OperacionBaseDatosException {
        String resultado = "";
        int count = 0;

        try (Connection miConexion = ConexionSingleton.getConnection(cadenaConexion, user, pass);) {

            // Preparamos la consulta
            try (PreparedStatement elPrepareStatement = miConexion.prepareStatement("SELECT * FROM LIBROS WHERE IDLIBRO = ?");) {
                //Asignamos los parámetros
                elPrepareStatement.setLong(1, id);
                //ejecuta la operación de inserción
                try (ResultSet elResultSet = elPrepareStatement.executeQuery();) {

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
                } 
            } 
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            String mensaje = "Se ha producido un error al conectarse a la base de datos.\n Póngase en contacto con el/la responsable\n";
            throw new OperacionBaseDatosException(mensaje);
        }
        //Si todo ha salido bien se devuelve el mensaje de la consulta
        resultado += "Se han encontrado " + count + " libros";
        return resultado;

    }

    /**
     * Muestra los libros que se corresponden con un autor determinado
     *
     * @param autor String, autor del libro, campo de búsqueda
     * @return String, mensaje con la información de los libros encontrados
     * @throws OperacionBaseDatosException
     */
    public static String consultaLibroPorAutor(String autor) throws OperacionBaseDatosException {
        String resultado = "";
        int count = 0;

        try (Connection miConexion = ConexionSingleton.getConnection(cadenaConexion, user, pass);) {

            // Preparamos la consulta
            try (PreparedStatement elPrepareStatement = miConexion.prepareStatement("SELECT * FROM LIBROS WHERE AUTOR = ?");) {
                //Asignamos los parámetros
                elPrepareStatement.setString(1, autor);
                //ejecuta la operación de inserción
                try (ResultSet elResultSet = elPrepareStatement.executeQuery();) {

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
                } 
            } 
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            String mensaje = "Se ha producido un error al conectarse a la base de datos.\n Póngase en contacto con el/la responsable\n";
            throw new OperacionBaseDatosException(mensaje);
        }
        //Si todo ha salido bien se devuelve el mensaje de la consulta
        resultado += "Se han encontrado " + count + " libros";
        return resultado;

    }

    /**
     * Muestra los libros que se corresponden con un autor determinado
     *
     * @param precio double, precio del libro, campo de búsqueda
     * @return String, mensaje con la información de los libros encontrados
     * @throws OperacionBaseDatosException
     */
    public static String consultaLibroPorPrecio(double precio) throws OperacionBaseDatosException {
        String resultado = "";
        int count = 0;
        try (Connection miConexion = ConexionSingleton.getConnection(cadenaConexion, user, pass);) {

            // Preparamos la consulta
            try (PreparedStatement elPrepareStatement = miConexion.prepareStatement("SELECT * FROM LIBROS WHERE PRECIO = ?");) {
                //Asignamos los parámetros
                elPrepareStatement.setDouble(1, precio);
                //ejecuta la operación de inserción
                try (ResultSet elResultSet = elPrepareStatement.executeQuery();) {

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
                } 
            }   
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            String mensaje = "Se ha producido un error al conectarse a la base de datos.\n Póngase en contacto con el/la responsable\n";
            throw new OperacionBaseDatosException(mensaje);
        }
        //Si todo ha salido bien se devuelve el mensaje de la consulta
        resultado += "Se han encontrado " + count + " libros";
        return resultado;

    }

    /**
     * Muestra todos los libros de la tabla LIBROS
     *
     * @return String, mensaje con la información de los libros encontrados
     * @throws OperacionBaseDatosException
     */
    public static String consultaTodoLosLibros() throws OperacionBaseDatosException {
        String mensaje = "";
        int count = 0;

        try {
            try (Connection miConexion = ConexionSingleton.getConnection(cadenaConexion, user, pass);) {

                // Preparamos la consulta
                try (Statement elStatement = miConexion.createStatement()) {
                    String sql = "SELECT * FROM LIBROS";

                    try (ResultSet resul = elStatement.executeQuery(sql)) {

                        //Se utilizan metadatos para averiguar el nombre de los campos
                        for (int i = 1; i <= resul.getMetaData().getColumnCount(); i++) {
                            mensaje += resul.getMetaData().getColumnName(i) + "\t\t\t";
                        }

                        mensaje += "\n";

                        while (resul.next()) {
                            /*Se utilizan metadatos para recorrer la columnas del recordset
                    Se recoge todo como String por el el tipo que puede incluir a 
                    todos los tipos específicos de la tabla*/
                            for (int i = 1; i <= resul.getMetaData().getColumnCount(); i++) {
                                mensaje += resul.getString(i) + "\t\t\t";
                            }
                            mensaje += "\n";
                            count++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            //En el caso de que no se pueda ejecutar, lo más probable es que sea porque ya están creadas
            mensaje = "Se ha producido un error al conectarse a la base de datos.\n Póngase en contacto con el/la responsable\n";
            throw new OperacionBaseDatosException(mensaje + "\n" + e.getMessage());
        }
        //Si todo ha salido bien se devuelve el mensaje de la consulta
        mensaje += "Se han encontrado " + count + " libros";
        return mensaje;

    }

}
