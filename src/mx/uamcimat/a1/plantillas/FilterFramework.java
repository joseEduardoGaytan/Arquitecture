package mx.uamcimat.a1.plantillas;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/******************************************************************************************************************
* Archivo:FilterFramework.java
* Curso: 17655
* Proyecto: Tarea 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Descripcion:
*
* Esta clase de base define el esqueleto de un framework de filtros que define un filtro en termino de 
* puertos de entrada y salida. Todos los filtros deben ser definidos en base a este framework, esto es que
* los filtros deben extender esta clase para ser considerados como filtros validos del sistema. Los filtros
* se ejecutan en hilos individuales hasta que el puerto de entrada (inputport) ya no tiene datos - en ese
* momento el filtro termina cualquier trabajo pendiente y termina.
* 
* 
*
* Atributos:
*
* InputReadPort:	Este es el puerto de entrada del filtro. Este puerto se conecta al puerto de salida de otro filtro.
* 					Todos los filtros se conectan a otros filtros mediante conexiones de los puertos de entrada a los
* 					puertos de salida de los otros filtros. Esto es manejado por el metodo Connect()
*
* OutputWritePort:	Este es el puerto de salida del filtro. En esencia, el trabajo del filtro es de leer data del puerto
* 					de entrada, realizar alguna operacion sobre los datos, y escribir los datos transformados al puerto
* 					de salida.	
* 
* InputFilter:		Esta es una referencia al filtro que esta conectado al puerto de entrada. Esta referencia sirve para
* 					determinar cuando termina de enviar datos el filtro conectado al puerto de entrada.
*
* Metodos:
*
*	public void Connect( FilterFramework Filter )
*	protected byte ReadFilterInputPort()
*	protected void WriteFilterOutputPort(byte datum)
*	protected boolean EndOfInputStream()
*
******************************************************************************************************************/

public class FilterFramework extends Thread
{
	// Puertos de entrada y salida del filtro

	private PipedInputStream inputReadPort = new PipedInputStream();
	private PipedOutputStream outputWritePort = new PipedOutputStream();

	// La referencia siguiente a un filtro es usada por que los pipes de java permiten
	// detectar de manera confiable cuando termina el flujo el puerto de entrada del filtro.
	// Esta referencia apunta al filtro previo en la red y cuando muere sabemos que ha
	// terminado el envio de datos.

	private FilterFramework inputFilter;


	/**
	 * Excepcion de fin de flujo de datos
	 * 
	 *
	 */
	class EndOfStreamException extends Exception {

		EndOfStreamException () { super(); }

		EndOfStreamException(String s) { super(s); }

	} // class



	/**	  
	 * Este metodo conecta a los filtros entre ellos. Todas las conexiones
	 * se hacen a traves del puerto de entrada de cada filtro, esto es que el inputport
	 * es conectado al puerto de salida de otro filtro a traves de este metodo.
	 * 
	 * @param previousFilter
	 * @return void
	 * @exception IOException
	 */
	public void connect( FilterFramework previousFilter )
	{
		try
		{
			// Conecta la entrada de este filtro a la salida del filtro previo

			inputReadPort.connect( previousFilter.outputWritePort );
			inputFilter = previousFilter;

		}
		catch( Exception ex )
		{
			System.out.println( "\n" + this.getName() + " FilterFramework error connecting::"+ ex );

		}

	}

	/***************************************************************************
	* CONCRETE METHOD:: ReadFilterInputPort
	* Purpose: This method reads data from the input port one byte at a time.
	*
	* Arguments: void
	*
	* Returns: byte of data read from the input port of the filter.
	*
	* Exceptions: IOExecption, EndOfStreamException (rethrown)
	*
	****************************************************************************/

	/**
	 * Este metodo lee datos del puerto de entrada un byte a la vez
	 * 
	 * @return
	 * @throws EndOfStreamException
	 */
	protected byte readFilterInputPort() throws EndOfStreamException
	{
		byte datum = 0;

		/*
		 * Puesto que puede haber retrasos en filtros previos, primero esperamos
		 * hasta que no haya datos disponibles en el puerto de entrada. Checamos,
		 * si no hay datos disponibles en el puerto de entrada, esperamos por un
		 * cuarto de segundo y checamos de nuevo. Notese que no hay un timeout y
		 * si el filtro previo esta bloqueado, esto puede resultar en esperas 
		 * infinitas dentro de este ciclo. Es necesario checar si estamos al final
		 * de un flujo de datos en el ciclo de espera por que es posible que el filtro
		 * previo complete mientras estamos esperando. Si esto ocurre y no verificamos
		 * que sea el final de un flujo de datos, podriamos esperar para siempre
		 * a un filtro previo que ya termino anteriormente. Por desgracia, los
		 * pipes de java no envian excepciones cuando la tuberia de entrada se rompe. 
		 * Lo que hacemos aqui es ver si el filtro previo esta vivo. Si lo
		 * esta, asumimos que la tuberia sigue sigue abierta y se estan enviando datos.
		 * Si el filtro no esta vivo, entonces asumimos que el fin del flujo ha
		 * sido alcanzado.
		 */	
		

		try
		{
			while (inputReadPort.available()==0 )
			{
				if (endOfInputStream())
				{
					throw new EndOfStreamException("End of input stream reached");

				} //if

				sleep(250);

			} // while

		} // try

		catch( EndOfStreamException Error )
		{
			throw Error;

		} // catch

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " Error in read port wait loop::" + Error );

		} // catch

		/*
		 * Si, finalmente, un byte de datos esta disponible en la tuberia de entrada
		 * podemos leerlo. Leemos y escribimos un byte de los puertos
		 */
		try
		{
			datum = (byte)inputReadPort.read();
			return datum;

		} // try

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " Pipe read error::" + Error );
			return datum;

		} // catch

	} // ReadFilterPort

	/**
	 * Este metodo escribe datos al puerto de salida un byte a la vez
	 * 
	 * @param datum el byte que sera escrito
	 */
	protected void writeFilterOutputPort(byte datum)
	{
		try
		{
            outputWritePort.write((int) datum );
		   	outputWritePort.flush();

		} // try

		catch( Exception Error )
		{
			System.out.println("\n" + this.getName() + " Pipe write error::" + Error );

		} // catch

		return;

	} // WriteFilterPort

	/**
	 * Este metodo es usado de forma interna al framework por lo cual es privdo. Regresa
	 * un valor verdadero cuando no hay mas datos a leer en el puerto de entrada del filtro.
	 * Lo que hace en realidad es cecar si el filtro previo sigue vivo. Esto se hace pues java
	 * no maneja de manera confiable las tuberias de entrada y frecuentemente sigue leyendo
	 * (basura) de una tuberia de entrada rota
	 * 
	 * @return true si el filtro previo ha dejado de enviar datos false de lo contrario
	 */
	private boolean endOfInputStream()
	{
		if (inputFilter.isAlive())
		{
			return false;

		} else {

			return true;

		} // if

	} // EndOfInputStream

	/***************************************************************************
	* CONCRETE METHOD:: ClosePorts
	* Purpose: This method is used to close the input and output ports of the
	* filter. It is important that filters close their ports before the filter
	* thread exits.
	*
	* Arguments: void
	*
	* Returns: void
	*
	* Exceptions: IOExecption
	*
	****************************************************************************/

	/**
	 * Este metodo es usado para cerrar los puertos de entrada y salida del filtro.
	 * Es importante que los filtros cierren sus puertos antes de que el hilo
	 * del filtro salga.
	 */
	protected void closePorts()
	{
		try
		{
			inputReadPort.close();
			outputWritePort.close();

		}
		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " ClosePorts error::" + Error );

		} // catch

	} // ClosePorts


	/**
	 * Este es un metodo abstracto definido por Thread. Es llamado cuando el hilo
	 * arranca mediante una llamada al metodo Thread.start(). En este caso, el metodo
	 * run() debe ser sobreescrito por el programador de filtros
	 */
	public void run()
    {
		// Este metodo debe ser sobreescrito por la subclase, ver las aplicaciones
		// de ejemplo para mas detalles 

	} // run

} // FilterFramework class