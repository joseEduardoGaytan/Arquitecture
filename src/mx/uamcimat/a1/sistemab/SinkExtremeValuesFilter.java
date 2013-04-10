package mx.uamcimat.a1.sistemab;

/******************************************************************************************************************
* File:SinkFileFilter.java
* Project: Assignment 1, Sistem A
* Copyright: Equipo Zac
* Versions:
*	1.0 November 2013.
*
* Description:
*
* Esta clase sirve como ejemplo para usar el SinkFilterTemplate para crear un filtro pozo. Este filtro particular
* lee una entrada del puerto de entrada del filtro y hace lo siguiente:
* 
* 1) Obtiene los datos desde el FeetToMeterFilter
* 2) Ya con los datos con las conversiones se dispone a construir una cadena, para estructurar la información
* 3) Una vez la información a sido estructurada se abre un archivo y se almacena la cadena. 
*
******************************************************************************************************************/


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.*;
import java.text.*;

import mx.uamcimat.a1.sistemab.FilterFramework.EndOfStreamException;


public class SinkExtremeValuesFilter extends FilterFramework {

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
		
		DecimalFormat formatoPresion = new DecimalFormat("##.#####");	// Para el formato para la Presión PP.ppppp
		
		FileWriter archivo = null;							//El archivo de salida, aquí solamente se declara como FileWriter
		
		//se utlizará una clase llamada Datos la cual tiene la estructura de los datos 
		Datos datos = new Datos();
		/*************************************************************
		*	Primero le anunciamos al mundo que estamos vivos
		**************************************************************/

		System.out.print( "\n" + this.getName() + "::SinkExtremeValues File Reading ");		
				
		try {
			archivo = new FileWriter("ExtremeValuesB.txt");
			
			archivo.write("Tiempo\t\t\t\tPresión(PSI)\r\n"); //Se empieza el encabezado del frame
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}					
		
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
					databyte = ReadFilterInputPort(0);	// Aqui leemos el byte del flujo	

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
					databyte = ReadFilterInputPort(0);
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
					//se almacena el dato de tiempo en la instancia del objeto Datos
					datos.setTiempo(measurement);

				} // if

				/****************************************************************************
				 * Aqui tomamos una medicion (ID = 3 en este caso), pero se puede tomar cualquier
				 * medicion que se quiera. Todas las mediciones en el flujo son recuperadas
				 * por esta clase. Notese que todas las mediciones son de tipo double.
				 * Esto ilustra como convertir los bytes leidos del flujo en un tipo double. 
				 * Esto es bastante simple usando Double.longBitsToDouble(long value). Se almacena 
				 * el valor en la variable presión con el fin de utilizarlo posteriormente.
				 ****************************************************************************/
				
				if ( id == 3 )
				{
					//se almacena el dato de Presion en la instancia del objeto Datos
					datos.setPresion(Double.longBitsToDouble(measurement));
					if (datos.getPresion() > 80 || datos.getPresion() < 50) {
						TimeStamp.setTimeInMillis(datos.getTiempo());
						archivo.write(TimeStampFormat.format(TimeStamp.getTime()) + "\t"+formatoPresion.format(datos.getPresion())+"\r\n"); 
						datos = new Datos();
					}
					
				} // if		
			
			} // try

			/*******************************************************************************			 * 
			 *  La excepcion EndOfStreamExeception abajo es enviada cuando se alcanza el
			 *  final del flujo de entrada (obvio). En este punto, los puertos del filtro
			 *  son cerrados y un mensaje es escrito permitiendo al usuario saber que sucede.
			 ********************************************************************************/

			catch (EndOfStreamException | IOException e)
			{
				ClosePorts();
				System.out.print( "\n" + this.getName() + "::SinkExtremeValues Exiting; bytes read: " + bytesread );
				break;

			} // catch

		} // while
		
		try{
			
			archivo.close();
			
		}catch(IOException e){
			System.out.print( "\n" + this.getName() + "::SinkExtremeValues File Filter Exiting; bytes read: " + bytesread +"; "  );
		}
		
   } // run
	
}
