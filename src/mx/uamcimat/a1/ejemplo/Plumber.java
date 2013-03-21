package mx.uamcimat.a1.ejemplo;
/******************************************************************************************************************
* File:Plumber.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
*
* Esta clase sirve como un ejemplo para ilustrar como usar el PlumberTemplate para crear un hilo principal que 
* instancia y contecta un conjunto de filtros. El ejemplo consiste de tres filtros: una fuente, un filtro medio
* que actua como pass-through (no hace nada a los datos), y un filtro pozo que ilustra toda clase de cosas
* utiles que se pueden hacer con el flujo de entrada de datos.
******************************************************************************************************************/
public class Plumber
{
   public static void main( String argv[])
   {
		/****************************************************************************
		* Aqui instanciamos los filtros
		****************************************************************************/

		SourceFilter filter1 = new SourceFilter();
		MiddleFilter filter2 = new MiddleFilter();
		SinkFilter filter3 = new SinkFilter();

		/****************************************************************************
		* Aqui conectamos los filtros empezando con el filtro pozo (filter3) el cual
		* conectamos al filter2 que es el filtro del medio. Posteriormente conectamos filter2
		* al filtro fuente (filter1).
		****************************************************************************/

		filter3.Connect(filter2); // Esto significa "conectar el puerto de entrada de filter3 al puerto de salida de filter2"
		filter2.Connect(filter1); // Esto significa "conectar el puerto de entrada de filter2 al puerto de salida de filter1"

		/****************************************************************************
		* Aqui arrancamos los filtros. En realidad,... es  un poco aburrido.
		****************************************************************************/

		filter1.start();
		filter2.start();
		filter3.start();

   } // main

} // Plumber