package mx.uamcimat.a1.plantillas;
/******************************************************************************************************************
* File:SinkFilterTemplate.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Descripcion:
*
* Esta clase sirve como plantilla para crear filtros pozo. Los detalles de hilado, y salida de datos de las conexiones
* estan contenidos en la clase de base FilterFramework. Para usar esta plantilla, el programa debe renombrar la clase.
* La plantilla incluye el metodo run() que se ejecuta cuando el filtro arranca.
* El metodo run() contiene las 'tripas' del filtro y es donde el programador debe poner el codigo especifico de su filtro.
* En la plantilla hay un ciclo principal de lectura-escritura para leer del puerto de entrada del filtro. El programador
* es responsable de escribir datos a un archivo o algun dispositivo. Esta plantilla supone que el filtro es un filtro pozo
* que lee datos de un archivo de entrada y escribe la salida del filtro a un archivo o dispositivo de algun tipo. En este
* caso, solo el puerto de entrada es usado por el filtro. En casos donde el filtro es un filtro estandar o un filtro fuente,
* uno debe usar FilterTemplate.java o SourceFilterTemplate.java como punto de partida para crer filtro fuente o 
* estandar.
* 
*
******************************************************************************************************************/

public class SinkFilterTemplate extends FilterFramework
{
	public void run()
    {
		byte databyte = 0;


		/*************************************************************
		 * Este es el cliclo principal de procesamiento para el filtro. Dado
		 * que es un filtro pozo, leemos hasta que no haya mas datos en el 
		 * puerto de entrada. 
		 ***************************************************************/

		while (true)
		{
			try
			{
				
				/*************************************************************
				 * Aqui leemos un byte del puerto de entrada. Notes que
				 * independietemente de la manera como se escriben los datos,
				 * los datos tienen que ser leidos un byte a la vez. Esto se
				 * hace para adherir al paradigma de Pipe and Filter y provee
				 * un alto grado de portabilidad entre filtros. Sin embargo,
				 * tu mismo debes convertir los datos de salidac conforme
				 * los necesites. 
				 **************************************************************/

				databyte = readFilterInputPort();

				/*************************************************************
				*	El programador puede inserta codigo para las operaciones de filtro
				*	aqui, incluyendo el escribir los datos a algun archivo
				*	o dispositivo.
				**************************************************************/

			} // try

			/***************************************************************
			 * Cuando se llega al final del flujo de entrada, una excepcion es
			 * lanzada como se muestra abajo. En este punto se debe terminar
			 * todo procesamiento, cerrar los puertos y salir.
			 ***************************************************************/


			catch (EndOfStreamException e)
			{
				closePorts();
				break;

			} // catch

		} // while

   } // run

} // FilterTemplate