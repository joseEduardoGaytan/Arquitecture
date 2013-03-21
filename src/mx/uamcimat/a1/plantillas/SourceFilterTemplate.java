package mx.uamcimat.a1.plantillas;
/******************************************************************************************************************
* File:SourceFilterTemplate.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Description:
*
* Esta clase sirve como plantilla para crear filtros fuente. Los detalles de manejo de hilos y escritura hacia
* conexiones de salida estan contenidos en la clase de base FilterFramework. Para usar esta plantilla, el programa
* debe renombrar la clase. La plantilla incluye el metodo run() que se ejecuta cuando el filtro arranca. El metodo
* run contiene las 'tripas' del filtro y es donde el programador debe poner el codigo especifico del filtro. El
* metodo run() es el ciclo principal de lectura-escritura para leer datos de alguna fuente y escribir al puerto de
* salida del filtro. Esta plantilla asume que el filtro es un filtro fuente que lee datos de un archivo, 
* dispositivo (sensor), o genera los datos de forma interna, y escribe datos al puerto de salida. En este caso, solo el
* puerto de salida es usado. En casos donde el filtro es un filtro estandar o un filtro pozo, se deben usar los
* archivos FilterTemplate.java o SinkFilterTemplate.java como punto de partida para crear filtros estandar o filtros pozo.
******************************************************************************************************************/

public class SourceFilterTemplate extends FilterFramework
{
	public void run()
    {

		byte databyte = 0;

		/*************************************************************
		 * Este es el ciclo principal de procesamiento para el filtro.
		 * Puesto que es un filtro fuente, el programador debe determinar
		 * cuando acaba el ciclo.
		 **************************************************************/

		while (true)
		{

			/*************************************************************
			 * El programador puede insertar codigo para las operaciones del filtro
			 * aqui incluyendo la lectura de datos de algun archivo o dispositivo.
			 * Notese que independientemente de la manera en que se leen los datos,
			 * los datos deben ser enviados un byte a la vez a la tuberia de salida. Esto se ha
			 * hecho para adherir al paradigma de filtros y tuberias y
			 * proveer un alto grado de portabilidad entre filtros.
			 * Sin embargo, tu debes convertir datos de entrada a algo de tipo byte por ti
			 * mismo. La siguiente linea de codigo escribe un byte de datos al puerto
			 * de salida del filtro. Si se sale del ciclo, se debe llamar closePorts() para
			 * cerrar los puertos del filtro de manera ordenada. Esto se muestra adelante
			 * aunque esta comentado. En donde se cierran los puertos depende de donde
			 * se termine el ciclo
			 **************************************************************/

           	writeFilterOutputPort(databyte);

		} // while

		/*
		ClosePorts();
		*/

   } // run

} // SourceFilterTemplate