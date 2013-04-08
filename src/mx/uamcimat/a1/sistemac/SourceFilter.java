package mx.uamcimat.a1.sistemac;
/******************************************************************************************************************
* File:SourceFilter.java
* Project: Assignment 1,
* Copyright: Equipo Zac
* Versions:
*	1.0 Marzo 2013.
*
* Descripcion:
* Esta clase hereda de la superclase FilterFramework.
* Esta clase sirve para crear un filtro fuente. Este filtro particular es un filtro fuente que lee datos de un
* archivo y escribe los datos en el flujo.
*
* Atributos:
* 	fileName:							Este el nombre del archivo que contiene los datos. Este recibe el nombre
* 										del archivo por medio del constructor SourceFilter(String file).
* Costructores:
* 	public SourceFilter(String file):	Este costructor recibe como parametros un dato de tipo String donde 
* 										se indican el nombre del archivo que contiene los datos.
* Metodos:
*	public void run():					Es el ciclo principal de lectura-escritura para leer datos de 
*										alguna fuente y escribir al puerto de salida del filtro.
******************************************************************************************************************/

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.EOFException;
import java.io.IOException;

public class SourceFilter extends FilterFramework
{
	
	String fileName;	// Archivo de datos de entrada.
	
	/*
	 * Este contructor recibe como parametro el nombre del archivo y lo almacena
	 * en el atributo fileName
	 */
	public SourceFilter(String file)
	{
		fileName = file;
	}
	
	public void run()
    {

		
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

			while(true)
			{
				databyte = in.readByte();
				bytesread++;
				WriteFilterOutputPort(databyte);
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