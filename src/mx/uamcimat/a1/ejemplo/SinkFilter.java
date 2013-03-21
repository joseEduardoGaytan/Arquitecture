package mx.uamcimat.a1.ejemplo;

/******************************************************************************************************************
* File:SinkFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
*
* Esta clase sirve como ejemplo para usar el SinkFilterTemplate para crear un filtro pozo. Este filtro particular
* lee una entrada del puerto de entrada del filtro y hace lo siguiente:
* 
* 1) Interpreta el flujo de entrada y obtiene del ID de medicion
* 2) Interpreta el flujo de entrada en busqueda de mediciones y las almacena en un long word.
* 
* Este filtro ilustra como convertir el fluje de bytes de datos del filtro previo en datos que pueden ser utilizados
* y que se encuentran en el flujo, en este caso: el tiempo (tipo long) y mediciones (tipo double).
*
******************************************************************************************************************/

import java.util.Calendar;
import java.text.SimpleDateFormat;		// Esta clase se usa para formatear y escribir el tiempo en formato de cadena

public class SinkFilter extends FilterFramework
{
	public void run()
    {
		/************************************************************************************
		 * Timestamp se usa para calcular el tiempo usando la clase java.util.Calendar
		 * TimeStampFormat se usa para formatear el valor de tiempo para que pueda ser
		 * facilmente mostrado en la terminal. 
		*************************************************************************************/

		Calendar TimeStamp = Calendar.getInstance();
		SimpleDateFormat TimeStampFormat = new SimpleDateFormat("yyyy MM dd::hh:mm:ss:SSS");

		int MeasurementLength = 8;		// Esta es la longitud de todas las mediciones (incluyendo el tiempo) en bytes
		int IdLength = 4;				// Esta es la longitud de los DIs en el flujo de bytes

		byte databyte = 0;				// Este es el byte leido del flujo		
		int bytesread = 0;				// Este es el numero de bytes leido del flujo

		long measurement;				// Esta es la palabra usada para almacenar todas las medicionesT, se muestran conversiones
		
		int id;							// Este es el id de medicion
		int i;							// Este es el contador del ciclo

		/*************************************************************
		*	Primero le anunciamos al mundo que estamos vivos
		**************************************************************/

		System.out.print( "\n" + this.getName() + "::Sink Reading ");

		while (true)
		{
			try
			{
				/***************************************************************************
				 * Sabemos que el primer dato que entra al filtro va a ser un ID de longitud
				 * idLength. Primero obtenemos los bytes del ID				
				 ***************************************************************************/

				id = 0;

				for (i=0; i<IdLength; i++ )
				{
					databyte = ReadFilterInputPort();	// Aqui leemos el byte del flujo	

					id = id | (databyte & 0xFF);		// Adjuntamos el byte al ID

					if (i != IdLength-1)				// Si este no es el ultimo byte, se hace un corrimiento del byte que se adjunto 
					{									// un byte a la izquierda
						id = id << 8;					// para hacer lugar para el proximo byte que adjuntamos al id

					} // if

					bytesread++;						// Se incrementa el conteo de bytes

				} // for

				/****************************************************************************
				 * Aqui leemos mediciones. Todos los datos de medicion se leen como un flujo de bytes
				 * y se almacenan como un valor long. Esto nos permite hacer manipulaciones a nivel bit
				 * que son necesarias para convertir el flujo de bytes en varias palabras da datos. Notese que
				 * las manipulaciones de bits no estan permitidas en tipos de punto flotante en java.
				 * Si el id = 0, entonces este es un valor de tiempo y por ello es un valor long - no
				 * hay problema. Sin embargo, si el id es algo distinto a cero, entonces los bits
				 * en el valor long son realmente de tipo double y necesitamos convertir el valor usando
				 * Double.longBitsToDouble(long val) para hacer la conversion, lo cual se muestra
				 * abajo.
				 *****************************************************************************/

				measurement = 0;

				for (i=0; i<MeasurementLength; i++ )
				{
					databyte = ReadFilterInputPort();
					measurement = measurement | (databyte & 0xFF);	// Adjuntamos el byte a la medicion...

					if (i != MeasurementLength-1)					// Si este no es el ultimo byte, recorremos el byte
					{												// previamente adjuntado a la izquierda por un byte
						measurement = measurement << 8;				// para hacer lugar para el proximo byte que adjuntamos a la
																	// medicion
					} // if

					bytesread++;									// Incrementamos el conteo de bytes

				} // if

				/****************************************************************************
				 * Aqui buscamos un ID de 0 que indica que esta es una medicion de tiempo.
				 * Cada marco (frame) empieza con un ID de 0 seguido de una estampa de tiempo
				 * que correlaciona con el tiempo en que se registro la medicion. El tiempo es almacenado
				 * en milisegundos desde Epoch. Esto nos permite usar la clase calendar de Java para
				 * recuperar el tiempo y tambien usar clases de formateo de texto para dar formato
				 * a la salida en un formato legible para los humanos. Esto provee gran flexibilidad
				 * en terminos de lidiar con el tiempo de forma aritmetica para propositos de 
				 * despliegue de cadenas. Esto se ilustra abajo.
				 ****************************************************************************/

				if ( id == 0 )
				{
					TimeStamp.setTimeInMillis(measurement);

				} // if

				/****************************************************************************
				 * Aqui tomamos una medicion (ID = 4 en este caso), pero se puede tomar cualquier
				 * medicion que se quiera. Todas las mediciones en el flujo son recuperadas
				 * por esta clase. Notese que todas las mediciones son de tipo double.
				 * Esto ilustra como convertir los bytes leidos del flujo en un tipo double. 
				 * Esto es bastante simple usando Double.longBitsToDouble(long value). Aqui
				 * imprimimos la estampa de tiempo asociada con el ID en el que estamos interesados.
				 ****************************************************************************/

				if ( id == 2 )
				{
					System.out.print( TimeStampFormat.format(TimeStamp.getTime()) + " -- ID = " + id + " " + Double.longBitsToDouble(measurement) );

				} // if

				System.out.print( "\n" );

			} // try


			/*******************************************************************************			 * 
			 *  La excepcion EndOfStreamExeception abajo es enviada cuando se alcanza el
			 *  final del flujo de entrada (obvio). En este punto, los puertos del filtro
			 *  son cerrados y un mensaje es escrito permitiendo al usuario saber que sucede.
			 ********************************************************************************/

			catch (EndOfStreamException e)
			{
				ClosePorts();
				System.out.print( "\n" + this.getName() + "::Sink Exiting; bytes read: " + bytesread );
				break;

			} // catch

		} // while

   } // run

} // SingFilter