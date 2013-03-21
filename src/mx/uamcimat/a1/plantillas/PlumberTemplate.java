package mx.uamcimat.a1.plantillas;
/******************************************************************************************************************
* File:PlumberTemplate.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Descripcion:
* 
* Esta clase sirve como plantilla para crear un hilo principal que crea instancias y conecta un conjunto de filtros.
* Los detalles de la operacion de los filtros estan totalmente autocontenidos dentro de los filtros, el "plumber" (plomero)
* se encarga de arrancar los filtros y conectar a los filtros entre ellos. Los detalles de como conectar
* filtros son responsabilidad del FilterFramework. Para usar esta plantilla, se debe renombrar la clase.
* La plantilla incluye el metodo runFilter() que es ejecutado cuando el filtro arranca. Aunque sencillo, existe
* una semantica para instanciar, conectar y arrancar los filtros:
* 
* Paso 1:	Instanciar los filtros ocmo se muestra en el ejemplo de abajo. Se deben crear los filtros usando las
* 		  	plantillas provistas, y se debe usar el FilterFramework como clase de base para todos los filtros. Toda
* 		  	red de filtros y tuberias debe tener un filtro fuente en donde se originan los datos y un filtro pozo en
*  		  	donde todo el flujo de datos termine.
*  
* Paso 2:	Conectar los filtros. Empezar desde el pozo e ir en sentido inverso hasta la fuente. En esencia, se esta
* 			conectando la entrada de cada filtro a la salida del filtro previo hasta que se llega al filtro fuente.
* 			Los filtros tienen un metodo connect() que acepta una referencia de tipo FilterFramework. Este metodo
* 			conecta el puerto de entrada de quien llama al metodo al puerto de salida del filtro que se recibe como
* 			parametro. El ejemplo mas adelante ilustra como se hace esto.
* 
* Paso 3:	Arrancar los filtros usando el metodo start() 
* 
* Una vez que los filtros son arrancados, el hilo principal muere y la red de filtros y tuberias procesa datos hasta
* que ya no haya mas movimientos de datos de la fuente. Cada filtro se apaga cuando ya no hay datos disponibles
* (suponiendo que se siguio la semantica de lectura descrita en la plantilla de filtro).
*
******************************************************************************************************************/
public class PlumberTemplate
{
   public static void main( String argv[])
   {
		/****************************************************************************
		* A continuacion se crean tres instancias de filtro
		****************************************************************************/
		/*

		SourceFilter filter1 = new SourceFilter();	// Este es un filtro fuente - ver SourceFilterTemplate.java
		MiddleFilter filter2 = new MiddleFilter();	// Este es un filtro estandar - ver FilterTemplate.java
		SinkFilter filter3 = new SinkFilter();		// This es un filtro pozo - ver SinkFilterTemplate.java

		*/
	   
	    /****************************************************************************
	     * Aqui conectamos los filtros arrancando con el filtro pozo (filter3) que
	     * conectamos al filtro medio filter2. Posteriormente conectamos filter2 al
	     * filtro fuente filter1. Se deben conectar los filtros comenzando con el filtro
	     * pozo y avanzar hasta llegar al filtro fuente como se muestra aqui.
	     ****************************************************************************/
		
		/*

		filter3.Connect(filter2); // Esto significa "conectar el puerto de entrada de filter3 al puerto de salida de filter2"
		filter2.Connect(filter1); // Esto significa "conectar el puerto de entrada de filter2 al puerto de salida de filter1"

		*/
		/****************************************************************************
		* Aqui arrancamos los filtros.
		****************************************************************************/
		/*

		filter1.start();
		filter2.start();
		filter3.start();

		*/
   } // main

} // PlumberTemplate