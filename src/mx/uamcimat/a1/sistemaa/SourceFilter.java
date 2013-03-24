package mx.uamcimat.a1.sistemaa;
/******************************************************************************************************************
* File:SourceFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Descripcion:
*
* Esta clase sirve como ejemplo de como usar el SourceFilterTemplate para crear un filtro fuente. Este filtro
* particular es un filtro fuente que lee entrada del archivo FlightData.dat y escribe los datos en el flujo.
******************************************************************************************************************/

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SourceFilter extends FilterFramework
{
	public void run()
    {

		String fileName = "FlightData.dat";	// Archivo de datos de entrada.
		int bytesread = 0;										// Bytes leidos del archivo de entrada.
		int byteswritten = 0;									// Bytes escritos al flujo.
		DataInputStream in = null;								// Referencia al flujo del archivo.
		byte databyte = 0;										// Byte leido del archivo

		try
		{
			/***********************************************************************************
			* Aqui abrimos el archivo y escribimos un mensaje a la consola
			***********************************************************************************/

			in = new DataInputStream(new FileInputStream(fileName));
			System.out.println("\n" + this.getName() + "::Source reading file..." );

			/***********************************************************************************
			 *  Aqui leemos datos del archivo y los enviamos al puerto de salida del filtro
			 *  un byte a la vez. El ciclo termina cuando encuentra un EOFExecption.
			 ***********************************************************************************/
			/*
			 * se creo un For each para recorer los filtros conectados y obtener los puertos de cada uno
			 */
			while(true)
			{
				databyte = in.readByte();
				bytesread++;
				for(Entry<FilterFramework, PipedOutputStream> entry :  OutputWritePort.entrySet()) {
					WriteFilterOutputPort(databyte,entry.getKey());
				}
				byteswritten++;

			} // while

		} //try

		/***********************************************************************************
		* La siguiente excepcion es levantada cuando alcanzamos el final del archivo de entrada.
		* Una vez que se llega a este punto, se cierra el archivo de entrada, se cierran
		* los puerto del filtro y salimos.	
		***********************************************************************************/

		catch ( EOFException eoferr )
		{
			System.out.println("\n" + this.getName() + "::End of file reached..." );
			try
			{
				in.close();
				ClosePorts();
				System.out.println( "\n" + this.getName() + "::Read file complete, bytes read::" + bytesread + " bytes written: " + byteswritten );

			}
		/***********************************************************************************
		* La siguiente excepcion es levantada cuando tenemos un problema para cerrar el archivo	
		***********************************************************************************/
			catch (Exception closeerr)
			{
				System.out.println("\n" + this.getName() + "::Problem closing input data file::" + closeerr);

			} // catch

		} // catch

		/***********************************************************************************
		* La siguiente excepcion es levantada cuando tenemos un problema al abrir el archivo
		***********************************************************************************/

		catch ( IOException iox )
		{
			System.out.println("\n" + this.getName() + "::Problem reading input data file::" + iox );

		} // catch

   } // run

} // SourceFilter