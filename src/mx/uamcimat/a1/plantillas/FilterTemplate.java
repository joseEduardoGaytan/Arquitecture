package mx.uamcimat.a1.plantillas;
/******************************************************************************************************************
* File:FilterTemplate.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Description:
*
* Esta clase sirve como plantilla para crear filtros. Los detalles del manejo de hilos, filtrado de conexiones, entrada y salida
* estan contenidos en la clase de base FilterFramework. Para usar esta plantilla, el programa debe renombrar la clase.
* Esta plantilla incluye el metodo run() que se ejecuta cuando el filtro arranca.
* El metodo run() contiene las 'tripas' del filtro y es donde el programador debe poner su codigo especifico.
* En la plantilla hay un ciclo principal de escritura para leer del puerto de entrada del filtro y escribir al puerto
* de salida del filtro. Esta plantilla supone que el filtro es "normal" en el sentido de que tanto lee como escribe datos.
* Lo anterior significa que tanto los puertos de entrada como de salida son utilizados - el puerto de entrada esta conectado
* a una tuberia de un filtro previo y el puerto de salida esta conectado a una tuberia de un filtro subsecuente. En casos
* donde el filtro es una fuente o un pozo de datos, se deben usar las plantillas SourceFilterTemplate.java y SinkFilterTemplate.java
* para crear filtros fuente o pozos.
*
*
******************************************************************************************************************/

public class FilterTemplate extends FilterFramework
{
	public void run()
    {
		byte databyte;

		while (true)
		{

			/* El programa puede insertar codigo para las operaciones de filtrado aqui.
			 * Notese que los datos deben ser recibidos y enviados un byte a la vez.
			 * Esto se ha hecho de esta forma para adherir al paradigma Pipe and Filter
			 * y proveer un alto grado de portabilidad entre filtros. Sin embargo, tu
			 * mismo debes reconstruir los datos. Primero leemos un byte del flujo de entrada... 
			 * 
			 */

			try
			{
				databyte = readFilterInputPort();

				/* Aqui podriamos insertar codigo que opere en el flujo de entrada...
				 * Posteriormente escribiemos un byte al puerto de salida
				 */

           		writeFilterOutputPort(databyte);

			} // try


			/* Cuando se llega al final del flujo de entrada se envia una
			 * excepcion que es mostrada abajo. En este punto se debe
			 * terminar todo procesamiento, cerrar los puertos y salir.
			 */
			

			catch (EndOfStreamException e)
			{
				closePorts();
				break;

			} // catch

		} // while

   } // run

} // FilterTemplate