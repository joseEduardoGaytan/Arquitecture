package mx.uamcimat.a1.ejemplo;
/******************************************************************************************************************
* File:MiddleFilter.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Sample Pipe and Filter code (ajl).
*
* Description:
*
* Esta clase sirve como ejemplo de como usar el FilterTemplate para crear un filtro estandar. Este ejemplo en
* particular es un filtro simple "pass-through" que lee datos del puerto de entrada y escribe los datos al
* puerto de salida.
******************************************************************************************************************/

public class MiddleFilter extends FilterFramework
{
	public void run()
    {
		int bytesread = 0;					// Cantidad de bytes leidos
		int byteswritten = 0;				// Cantidad de bytes escritos
		byte databyte = 0;					// El byte leido de la entrada

		// A continuacion un mensaje a la terminal para avisar al mundo que estamos vivo...

		System.out.print( "\n" + this.getName() + "::Middle Reading ");

		while (true)
		{
			/*************************************************************
			*	Aqui leemos un byte y escribimos un byte
			*************************************************************/

			try
			{
				databyte = ReadFilterInputPort();
				bytesread++;
				WriteFilterOutputPort(databyte);
				byteswritten++;

			} // try

			catch (EndOfStreamException e)
			{
				ClosePorts();
				System.out.print( "\n" + this.getName() + "::Middle Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
				break;

			} // catch

		} // while

   } // run

} // MiddleFilter