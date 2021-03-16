# AD-UD2-Conectores-JDBC-H2
Ejemplo de conexión JDBC a un DB H2 gestionando una base de datos de libros
Mejoras
1- La base de datos se crea con el IDLIBROS de tipo long 
2- La base de datos se crea con todos los campos NOT NULL
3- La transacción de la inserción de libros se realiza en la capa de operaciones de la base de datos (OperacionesBD.java)
4- Se ha eliminado de la clase Principal toda referencia a java.sql



En el patrón Singleton
1- Se analiza si la conexión está cerrada, en tal caso, también devuelve una nueva instancia

Estructura del proyecto
Se han estructurado las clases del proyecto siguiendo el patron MVC (vista, controlador, modelo)
La programación de software por capas es una arquitectura en la que buscamos separar el código o lógica que 
hace tareas de negocios (facturar, ventas) de la lógica de presentación gráfica y de datos. 
También le conocer como modelo MCV, (vista, controlador, modelo).

 

La ventaja de este estilo es que el desarrollo es la facilidad de reutilización y mantenimiento,
ya que en caso de algún cambio, solo se modifica la capa necesaria sin tener que revisar todo el código.

Capa lógica de presentación:
Hace referencia a como se va a presentar la información del programa al usuario. El objetivo es separar 
todo aquellos que se muestra al usuario, esta capa no tiene conexión a base de datos, ni realizar operaciones 
de ningún tipo solo muestra datos en pantalla, la capa de presentación solicita mediante funciones que se 
jecutan en la capa de la lógica de negocio.
 
Capa de lógica de negocio:
Aquí es donde se encuentran las funciones y  clases que serán invocados a través de la interfaz gráfica.
Recibe peticiones o eventos del usuario, procesa esas peticiones y luego envía la respuesta a la interfaz gráfica, 
si es necesario 
esta capa se comunicara con la capa de datos, pero la capa de negocios no se conecta a la base de datos, 
solo recibe datos o los procesa. Aquí se ejecutan e invocan reglas o funciones de negocios por ejemplo, facturar, 
listar productos, etc.

Capa de datos
Aquí tendremos clases y funciones que se conectan a la base de datos y es donde se realizan transacciones con sql 
para leer, insertar, modificar o eliminar información en la base de datos.
Aquí ejecutaremos consultas sql de forma que ninguna de las otras capas saben donde esta la base de datos, así la 
capa de presentación podría estar en un pc y las otras capas en un servidor como servicio se software Saas.

