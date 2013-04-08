package mx.uamcimat.a1.sistemaa;

/******************************************************************************************************************
* File:FeetToMetersFilter.java
* Project: Assignment 1,
* Copyright: Equipo Zac
* Versions:
*	1.0 Marzo 2013.
*
* Description:
*
* Esta clase realiza la conversión de Altitud de Pies a Metros, Ademas de trasmitir los datos
* transformados y los demas datos al siguiente filtro 
* 
* Atributos:
* Costructores:
* Metodos:
*	public void run():					Es el ciclo principal de lectura-escritura para leer datos de 
*										alguna fuente y escribir al puerto de salida del filtro.
*
*
******************************************************************************************************************/

public class FeetToMetersFilter extends FilterFramework {

public void run(){
		
		int MeasurementLength = 8;		// Esta es la longitud de todas las mediciones (incluyendo el tiempo) en bytes
		int IdLength = 4;				// Esta es la longitud de los DIs en el flujo de bytes

		byte databyte = 0;				// Este es el byte leido del flujo		
		int bytesread = 0;				// Este es el numero de bytes leido del flujo
		int byteswritten = 0;				// Cantidad de bytes escritos

		long measurement;				// Esta es la palabra usada para almacenar todas las medicionesT, se muestran conversiones
		
		int id;							// Este es el id de medicion
		int i;							// Este es el contador del ciclo
		
		double feets = 0, meters = 0; //Variables para almacenar los pies y la conversion a metros		
			    
		/*************************************************************
		*	Primero le anunciamos al mundo que estamos vivos
		**************************************************************/
		
		System.out.print( "\n" + this.getName() + "::Conversion to meters Reading ");
		
		while(true){
			try{
				
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
				
				/**
				 * Se hace la comparación con 2, ya que se trata del Id de la Altitud,
				 * se hace la conversión a Metros
				 */
				
				if ( id == 2 )
				{
					feets = Double.longBitsToDouble(measurement); 	//Almacenar el valor de measurment a double
					meters = feets / 3.2808;						//meters para guardar la conversión
					measurement = Double.doubleToLongBits(meters);	//Para tratarlo como long
					
				} // if
				
				sendIDToOutput(id, IdLength, databyte); 	//Se envían los datos al puerto de salida
				byteswritten += IdLength;					//Los bytes escritos es igual a la longitud del ID
				
				sendMeasurementToOutput(measurement, MeasurementLength, databyte); 	//Se envían los datos al puert de salida.
				byteswritten += MeasurementLength;									//Los bytes escritos es igual a la longitud de Measurement
				
			}catch(EndOfStreamException e){
				ClosePorts();
				System.out.print( "\n" + this.getName() + "::Conversion of Altitude Exiting; bytes read: " + bytesread +"; bytes written: " + byteswritten );
				break;
			}//catch
		}//while
	}//run

	
}// feetToMetersFilter
