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

Capa de lógica de negocio

Aquí es donde se encuentran las funciones y clases  
que serán invocados a través de la interfaz gráfica.

Recibe peticiones o eventos del usuario, procesa esas peticiones y luego envía 
la respuesta a la interfaz gráfica, si es necesario esta capa se comunicara con 
la capa de datos, pero la capa de negocios no se conecta a la base de datos, 
solo recibe datos o los procesa. Aquí se ejecutan e invocan reglas o funciones 
de negocios por ejemplo, facturar, listar productos, etc.
 */
package gal.teis.negocio;

import gal.teis.db.datos.OperacionesBD;
import gal.teis.excepciones.OperacionBaseDatosException;
import gal.teis.presentacion.Presentacion;
import java.util.ArrayList;



/**
 *
 * @author Esther Ferreiro
 */
public class Principal {

    /**
     * @param args the command line arguments
     */


    static ArrayList<Libro> losLibros = new ArrayList<Libro>();

    public static void main(String[] args) {
        //Para finalizar el menú 
        boolean finalizar = false;
        //Donde se almacena la cadena resultado de la operación de base de datos
        String resultado;
        //Para almacenar campos de la base de datos para distintas operaciones.
        String titulo, autor;
        double precio;
        //Cadena de conexión

        //Establece la conexión con una base de datos SQLite.
        //No es necesario cerrar la conexión al usar try con recursos
        do {
            switch (Presentacion.pintarMenu()) {
                case 1://CREAR BASE DE DATOS
                    try {
                       OperacionesBD.crearBD();
                       Presentacion.mostrarCreacionCorrecta();
                    } catch (OperacionBaseDatosException ex) {
                       Presentacion.mostrarError(ex.getMessage());
                    }
                break;

                case 2://ALTAS EN LA TABLA LIBROS
                    /*
                    Los libros se agregarán a un ArrayList<Libros> y cuando 
                    finalice la introducción de la información de los libros a 
                    agregar, entonces se insertarán en la base de datos
                     */
                    do {
                        //Introduzco los datos del libro
                        titulo = Presentacion.leerTitulo();
                        autor = Presentacion.leerAutor();
                        precio = Presentacion.leerPrecio();
                      
                        /*El campo ID no se introduce ya que se calcula de 
                            forma automatica, según se ha indicado en la orden 
                            SQL en el momento de la creación de la tabla LIBROS 
                            (PRIMARY KEY AUTO_INCREMENT)*/

                        //Creo una instancia del libro
                        Libro unLibro = new Libro(titulo, autor, precio);
                        //Agrego el libro a una lista
                        losLibros.add(unLibro);
                        //Pregunto si quieren agreagar otro libro
                    } while (Presentacion.otraOperacionSN().equals("S"));

                    try {
                        resultado = OperacionesBD.altaListaLibros(losLibros);
                        Presentacion.insertarLibrosCorrecta(resultado);
                    } catch (OperacionBaseDatosException ex) {
                         Presentacion.mostrarMensaje(ex.getMessage());
                    }
                    break;

                case 3://BAJAS
                    titulo = Presentacion.leerTitulo();
                    try {
                        resultado = OperacionesBD.bajaLibrosPorTitulo(titulo);
                        /*Este mensaje solo se muestra si no se produce una 
                            excepción en el método bajaLibrosPorTitulo()*/
                        System.out.println(resultado);
                    } catch (OperacionBaseDatosException ex) {
                        //Se lanza esta excepción desde el método crearBD()
                        Presentacion.mostrarMensaje(ex.getMessage());
                    }
                    break;
                case 4://CONSULTAS
                    /*Se muestar un menú para buscar en la tabla LIBROS por diferentes campos*/
                    switch (Presentacion.pintarSubMenuConsultas()) {
                        case 1: //Buscar por TITULO del libro
                            titulo = Presentacion.leerTitulo();
                           
                            try {
                                resultado = OperacionesBD.consultaLibroPorTitulo(titulo);
                                Presentacion.mostrarMensaje(resultado);
                            } catch (OperacionBaseDatosException ex) {
                                 Presentacion.mostrarMensaje(ex.getMessage());
                            }

                            break;

                        case 2: //Buscar por ID del libro
                            //Pedir los datos
                            long id = Presentacion.leerId();
                            
                            try {
                                resultado = OperacionesBD.consultaLibroPorId(id);
                                Presentacion.mostrarMensaje(resultado);
                            } catch (OperacionBaseDatosException ex) {
                                 Presentacion.mostrarMensaje(ex.getMessage());
                            }

                            break;
                        case 3: //Buscar libros por AUTOR
                            //Pedir los datos                      
                            autor =  Presentacion.leerAutor();
                            
                            try {
                                resultado = OperacionesBD.consultaLibroPorAutor(autor);
                                Presentacion.mostrarMensaje(resultado);
                            } catch (OperacionBaseDatosException ex) {
                                Presentacion.mostrarMensaje(ex.getMessage());
                            }

                            break;
                        case 4: //Buscar libros por PRECIO
                            //Pedir los datos
                            precio = Presentacion.leerPrecio();
                            
                            try {
                                resultado = OperacionesBD.consultaLibroPorPrecio(precio);
                                Presentacion.mostrarMensaje(resultado);
                            } catch (OperacionBaseDatosException ex) {
                                Presentacion.mostrarMensaje(ex.getMessage());
                            }

                            break;
                        case 5: {
                            try {
                                //Ver todos los libros de la tabla LIBROS
                                resultado = OperacionesBD.consultaTodoLosLibros();
                                Presentacion.mostrarMensaje(resultado);
                            } catch (OperacionBaseDatosException ex) {
                                Presentacion.mostrarMensaje(ex.getMessage());
                            }
                        }
                        break;
                    }
                    break;
                case 5:
                    Presentacion.mostrarMensaje("Hasta luego!!!");
                    finalizar = true;
            }
        } while (!finalizar);

    }
}
