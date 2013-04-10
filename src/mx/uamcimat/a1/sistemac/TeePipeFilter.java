package mx.uamcimat.a1.sistemac;

/******************************************************************************************************************
* File:TeePipeFilter.java
* Project: Assignment 1, Sistem C
* Copyright: Equipo Zac
* Versions:
*	1.0 November 2013.
*
* Description:
*
* Esta clase sirve para la lectura de dos puertos de entrada
* 
* 
*
******************************************************************************************************************/


import java.io.IOException;
import java.util.Map.Entry;

public class TeePipeFilter extends FilterFramework {

	public void run() 
	{
		int MeasurementLength = 8;		// Esta es la longitud de todas las mediciones (incluyendo el tiempo) en bytes
		int IdLength = 4;				// Esta es la longitud de los DIs en el flujo de bytes

		byte databyte = 0;				// Este es el byte leido del flujo		
		int bytesread = 0;				// Este es el numero de bytes leido del flujo
		int byteswritten = 0;				// Cantidad de bytes escritos

		long measurement;				// Esta es la palabra usada para almacenar todas las medicionesT, se muestran conversiones
		
		int id;							// Este es el id de medicion
		int i;							// Este es el contador del ciclo
		
		int portNumber = 0;
		
		boolean isReady = false;		//Con el fin de verificar si los datos ya pueden ser enviados al output
		
		int [] ids = new int[2];			//se almacenan los ids de cada puerto de entrada, en este caso 2 puertos de entrada
		long[] measurements = new long[2];	//se almacenan las medidas de cada puerto de entrada, en este caso 2 puertos de entrada
		
		boolean portsClosed = false;	//para verificar si todos los puertos estan inactivos o ya rotos
				
		/*************************************************************
		*	Primero le anunciamos al mundo que estamos vivos
		**************************************************************/
		
		System.out.print( "\n" + this.getName() + "::Joint Reading ");
		
		while(true){
			try{
				
				/***************************************************************************
				 * Sabemos que el primer dato que entra al filtro va a ser un ID de longitud
				 * idLength. Primero obtenemos los bytes del ID				
				 ***************************************************************************/
				
					id = 0;
				
					ids[portNumber] = 0;					
					
					try
					{
						for (i=0; i<IdLength; i++ )
						{
							databyte = ReadFilterInputPort(portNumber);	// Aqui leemos el byte del flujo	
		
							ids[portNumber] = ids[portNumber] | (databyte & 0xFF);		// Adjuntamos el byte al ID
		
							if (i != IdLength-1)				// Si este no es el ultimo byte, se hace un corrimiento del byte que se adjunto 
							{									// un byte a la izquierda
								ids[portNumber] = ids[portNumber] << 8;					// para hacer lugar para el proximo byte que adjuntamos al id
		
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
						measurements[portNumber] = 0;
		
						for (i=0; i<MeasurementLength; i++ )
						{
							databyte = ReadFilterInputPort(portNumber);
							measurements[portNumber] = measurements[portNumber] | (databyte & 0xFF);	// Adjuntamos el byte a la medicion...
		
							if (i != MeasurementLength-1)					// Si este no es el ultimo byte, recorremos el byte
							{												// previamente adjuntado a la izquierda por un byte
								measurements[portNumber] = measurements[portNumber] << 8;				// para hacer lugar para el proximo byte que adjuntamos a la
																			// medicion
							} // if
		
							bytesread++;									// Incrementamos el conteo de bytes
		
						} // for
					
						/**********************************************************************
						 * Se atrapa un EndOfStreamException, señal que un puerto de entrada 
						 * esta roto, pero no necesariamente los otros puertos de entrada 
						 * están rotos
						***********************************************************************/
					}catch(EndOfStreamException e){ 								
						measurements[portNumber] = Double.doubleToLongBits(100000);	//Con esto la medida del puerto que esta leyendo se hace una cantidad enorme, haciendo que con las comparaciónes ya no se vuelva a tomar ese puerto
						
						portsClosed = true;											//Se ha cerrado un puerto y con el fin de que se haga la evaluación en el siguiente for
						
						for(i = 0; i < measurements.length; i++)
							portsClosed = portsClosed && (measurements[i] == Double.doubleToLongBits(100000));	// primer pipe roto =>	true && true && false = false, segundo pipe roto => true && true && true = true 
						
						if(portsClosed)												//Si todos los puertos están cerrados se manda otra excepción al try principal para que cierre los puertos y finalice el thread
							throw new EndOfStreamException("End of input stream reached");
						
					}//catch
				
				if(!isReady && bytesread > 23) 				//Se emplea la variable isReady con el fin de saber si los datos están listos para escritura en el output port y bytesread es para saber que ya el arreglo measurement[1] tiene datos  
				{
					if(measurements[0] < measurements[1]) 	//se hacen comparaciones, si el puerto 0 es menor que el primero los datos van a ser leídos desde el puerto 0, las medidas son las de tiempo, esta comparación es solamente para 2 puertos de entrada
					{
						portNumber = 0;					  //se van a seguir leyendo los datos restantes de la entrada desde el puerto 0	
						isReady = true;						// Para mandar escribir directamente y no tener que pasar por las comparaciones, pues solamente es necesario comparar con el tiempo
					}
					else
					{
						portNumber = 1;					//se van a seguir leyendo los datos restantes de la entrada desde el puerto 1 en caso de que el puerto 1 sea el menor			
						isReady = true;
					}
				}
				else if(!isReady && bytesread < 23)
					portNumber = 1;						//para que lea del siguiente puerto, pues solamente es en etapa temprana y solo hay datos leidos desde un puerto
				
				id = ids[portNumber];					//el id de los datos que resultaron ser menores se almacena para posteriormente enviarlo a las salidas
				measurement = measurements[portNumber]; //la medida de los datos que resultaron ser menores se almacena para posteriormente enviarlo a las salidas
								
				if(isReady)	//los datos han sido comparados y deben enviarse al output
				{
					
					sendIDToOutput(id, IdLength, databyte);	//Se envían los datos al puert de salida.Se manda la referencia de este objeto, con el fin de hacer un delegado de la función WriteToOutputPort
					byteswritten += IdLength;						//Los bytes escritos es igual a la longitud del ID
					
					sendMeasurementToOutput(measurement, MeasurementLength, databyte); //Se envían los datos al puert de salida.Se manda la referencia de este objeto, con el fin de hacer un delegado de la función WriteToOutputPort
					byteswritten += MeasurementLength;				//Los bytes escritos es igual a la longitud de Measurement
					
					if(id == 5)
					{
												
						isReady = false;	//se ha llegado al fin de todos los datos, ahora es necesario comparar el tiempo y no escribirlo directamente al puerto de salida
						
					}//if
					
				}//if	
				
			}catch(EndOfStreamException e){
				ClosePorts();
				System.out.print( "\n" + this.getName() + "::Conversion of Temperature Exiting; bytes read: " + bytesread +"; bytes written: " + byteswritten );
				break;
			}//catch
		}//while
	}

}
