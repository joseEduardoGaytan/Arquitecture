package mx.uamcimat.a1.sistemaa;


/******************************************************************************************************************
* File:FilterFramework.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*	2.0 Marzo 2013 - Equipo Zac Cimat A.C. 
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
* InputReadPort:	Esta es una coleccion HashMap "Key, Value".
* 					Donde:
* 					Key es la referencia al fitro predecesor del cual se obtienen datos.
* 					Value es el puerto de entrada que utiliza el fitro para recibir datos.
* 					Este puerto se conecta al puerto de salida de otro filtro.
* 					Todos los filtros se conectan a otros filtros mediante conexiones de los puertos de entrada a los
* 					puertos de salida de los otros filtros. Esto es manejado por el metodo Connect()
*
* OutputWritePort:	Esta es una coleccion HashMap "Key, Value".
* 					Donde:
* 					Key es la referencia al fitro sucesor al cual se le envian los datos.
* 					Value es el puerto de salida que utiliza el fitro para mandar datos.
* 					Este es un puerto de salida del filtro. En esencia, el trabajo del filtro es de leer data del puerto
* 					de entrada, realizar alguna operacion sobre los datos, y escribir los datos transformados al puerto
* 					de salida.	
* 
* InputFilter:		Este es un ArratList que contiene la referencia de los filtros que esta conectado a algún puerto de entrada. Esta referencia sirve para
* 					determinar cuando termina de enviar datos el filtro conectado al puerto de entrada.
*
* Metodos:
*
*	public void Connect( FilterFramework Filter )
*	protected byte ReadFilterInputPort()
*	protected void WriteFilterOutputPort(byte datum)
*	protected boolean EndOfInputStream()
*	sendIDToOutput(int id, int IdLength, byte databyte)
*	sendMeasurementToOutput(long measurement, int MeasurementLength, byte databyte)
*
******************************************************************************************************************/

import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;



public class FilterFramework extends Thread
{
		/*****************************************************************************
		 * Se definen los HashMap que contendran los filtros predecesores y sucerores 
		 * a si como su respectivo puerto de entrada o salida
		 ******************************************************************************/

		private HashMap<FilterFramework, PipedInputStream> InputReadPorts = new HashMap<FilterFramework, PipedInputStream>();
		private HashMap<FilterFramework, PipedOutputStream> OutputWritePorts = new HashMap<FilterFramework, PipedOutputStream>();
		
		
		// The following reference to a filter is used because java pipes are able to reliably
		// detect broken pipes on the input port of the filter. This variable will point to
		// the previous filter in the network and when it dies, we know that it has closed its
		// output pipe and will send no more data.
		// se creo un arreglo de Filtros que contiene la referencia a otros filtros para determinar
		// si se a terminado de enviar datos.
		private ArrayList<FilterFramework> InputFilters = new ArrayList<FilterFramework>();
		
	/***************************************************************************
	* InnerClass:: EndOfStreamExeception
	* Purpose: This
	*
	*
	*
	* Arguments: none
	*
	* Returns: none
	*
	* Exceptions: none
	*
	****************************************************************************/

	public class EndOfStreamException extends Exception {

		EndOfStreamException () { super(); }

		EndOfStreamException(String s) { super(s); }

	} // class


	/***************************************************************************
	* CONCRETE METHOD:: Connect
	* Purpose: This method connects filters to each other. All connections are
	* through the inputport of each filter. That is each filter's inputport is
	* connected to another filter's output port through this method.
	*
	* Arguments:
	* 	FilterFramework - this is the filter that this filter will connect to.
	*
	* Returns: void
	*
	* Exceptions: IOException
	*
	****************************************************************************/

	public void Connect( FilterFramework Filter)
	{
		try
		{
			// Connect this filter's input to the upstream pipe's output stream
			/*
			 * se crean un PipedInputStream que sera el puerto de entrada al filtro
			 * que se esta conectando.
			 */
			
			PipedInputStream in = new PipedInputStream();
			
			/*
			 * se crean un PipedOutputStream que sera el puerto de salida al filtro
			 * que se esta conectando.
			 */
			PipedOutputStream out = new PipedOutputStream();
			
			
			/*
			 * se agrega el filtro al arreglo de filtros para posteriormente
			 * verificar si a terminado de enviar datos
			 */
			InputFilters.add(Filter);
			
			
			//se obtiene el indice en el cual fue agregado el filtro al arreglo
			int index = InputFilters.indexOf(Filter);
			
			//se agrega el puerto de salida y el filtro al que pertenece en el OutputWritePorts  
			InputFilters.get(index).OutputWritePorts.put(this, out);
			
			//se agrega el puerto de entrada y el filtro al que pertenece en el InputReadPorts 
			InputReadPorts.put(Filter, in);
			// Conecta la entrada de este filtro a la salida del filtro previo
			InputReadPorts.get(Filter).connect(InputFilters.get(index).OutputWritePorts.get(this));

		} // try

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " FilterFramework error connecting::"+ Error );

		} // catch

	} // Connect

	/***************************************************************************
	* CONCRETE METHOD:: ReadFilterInputPort
	* Purpose: This method reads data from the input port one byte at a time.
	*
	* Arguments: 
	* 	index - este indica la posicion del filtro el el arreglo InputFilters
	* 			de donde se obtienen los datos
	*
	* Returns: byte of data read from the input port of the filter.
	*
	* Exceptions: IOExecption, EndOfStreamException (rethrown)
	*
	****************************************************************************/

	protected byte ReadFilterInputPort(int index) throws EndOfStreamException
	{
		byte datum = 0;
		/*
		 * con el parametro index se obtiene el filtro del cual se desea obtener datos
		 * despues de conocer el filtro se obtiene el puerto de entrada utilizado por dicho filtro 
		 */
		FilterFramework input = InputFilters.get(index);
		PipedInputStream InputReadPort = InputReadPorts.get(input);
		

		/***********************************************************************
		* Since delays are possible on upstream filters, we first wait until
		* there is data available on the input port. We check,... if no data is
		* available on the input port we wait for a quarter of a second and check
		* again. Note there is no timeout enforced here at all and if upstream
		* filters are deadlocked, then this can result in infinite waits in this
		* loop. It is necessary to check to see if we are at the end of stream
		* in the wait loop because it is possible that the upstream filter completes
		* while we are waiting. If this happens and we do not check for the end of
		* stream, then we could wait forever on an upstream pipe that is long gone.
		* Unfortunately Java pipes do not throw exceptions when the input pipe is
		* broken.
		***********************************************************************/

		try
		{
			
			while(InputReadPort.available() == 0)
			{
				if ( EndOfInputStream(input) )
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

		/***********************************************************************
		* If at least one byte of data is available on the input
		* pipe we can read it. We read and write one byte to and from ports.
		***********************************************************************/

		try
		{
						
			datum = (byte)InputReadPort.read();			
			return datum;			

		} // try

		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " Pipe read error::" + Error );
			return datum;

		} // catch

	} // ReadFilterPort

	/***************************************************************************
	* CONCRETE METHOD:: WriteFilterOutputPort
	* Purpose: This method writes data to the output port one byte at a time.
	*			Este metodo escribe datos a todos los puertos de salida un byte a la vez.
	* Arguments:
	* 	byte datum - This is the byte that will be written on the output port.of
	*	the filter.
	*
	* Returns: void
	*
	* Exceptions: IOException
	*
	****************************************************************************/

	protected void WriteFilterOutputPort(byte datum)
	{
		try
		{
			/*
			 * se crea un ciclo for que recora el OutputWritePorts para enviar los datos a todos los filtros conectados 
			 */
			for(Entry<FilterFramework, PipedOutputStream> entry : OutputWritePorts.entrySet())
			{
				entry.getValue().write((int) datum);
				entry.getValue().flush();				
			}
			
		} // try

		catch( Exception Error )
		{
			System.out.println("\n" + this.getName() + " Pipe write error::" + Error );

		} // catch

		return;

	} // WriteFilterPort

	/***************************************************************************
	* CONCRETE METHOD:: EndOfInputStream
	* Purpose: This method is used within this framework which is why it is private
	* It returns a true when there is no more data to read on the input port of
	* the instance filter. What it really does is to check if the upstream filter
	* is still alive. This is done because Java does not reliably handle broken
	* input pipes and will often continue to read (junk) from a broken input pipe.
	*
	* Arguments: 
	* 		InputFilter - recibe la referencia de algun Filtro predecesor
	*
	* Returns: A value of true if the previous filter has stopped sending data,
	*		   false if it is still alive and sending data.
	*
	* Exceptions: none
	*
	****************************************************************************/

	private boolean EndOfInputStream(FilterFramework InputFilter)
	{

		if (InputFilter.isAlive())
		{
			return false;
		}			
	
		return true;

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

	protected void ClosePorts()
	{
		try
		{
			/*
			 * se crea un ciclo for que recora el OutputWritePorts para cerrar todos los puertos de salida 
			 */
			for(Entry<FilterFramework, PipedOutputStream> entry : OutputWritePorts.entrySet())
			{
				entry.getValue().close();				
			}
			
			/*
			 * se crea un ciclo for que recora el InputReadPorts para cerrar todos los puertos de entrada 
			 */
			for(PipedInputStream InputReadPort: InputReadPorts.values())
			{			
			InputReadPort.close();
			}
			
		}
		catch( Exception Error )
		{
			System.out.println( "\n" + this.getName() + " ClosePorts error::" + Error );

		} // catch

	} // ClosePorts

	/***************************************************************************
	* CONCRETE METHOD:: run
	* Purpose: This is actually an abstract method defined by Thread. It is called
	* when the thread is started by calling the Thread.start() method. In this
	* case, the run() method should be overridden by the filter programmer using
	* this framework superclass
	*
	* Arguments: void
	*
	* Returns: void
	*
	* Exceptions: IOExecption
	*
	****************************************************************************/

	public void run()
    {
		// The run method should be overridden by the subordinate class. Please
		// see the example applications provided for more details.

	} // run
	
	/**
	 * El código utilizado en varias partes se decidio hacerlos métodos con el fin de promover reuso
	 * Envía el ID al puerto de salida del Filtro, se utiliza un objeto del tipo FilterFramework, con el
	 * fin de saber de que objeto es el método WriteFilterOutputPort
	 * @param id
	 * @param IdLength
	 * @param databyte
	 * @param filtro
	 */
	
	public void sendIDToOutput(int id, int IdLength, byte databyte)
	{
		
		for(int i = IdLength-1 ; i >= 0; i--){	
		    
		    databyte = (byte)(id >>> (8*i));		//para convertir a byte byte por byte contenido en el long en incrementos de 8 measurement >>> 0, 8, 16, 24, 32, 40, 48, 56
		   
		    WriteFilterOutputPort(databyte);			

		}//for
		
	}// sendIDTiOutput
	
	/**
	 * El código utilizado en varias partes se decidio hacerlos métodos con el fin de promover reuso
	 * Envía la medida al puerto de salida del Filtro. Se utiliza un objeto del tipo FilterFramework, con el
	 * fin de saber de que objeto es el método WriteFilterOutputPort
	 * @param measurement
	 * @param MeasurementLength
	 * @param databyte
	 * @param filtro
	 */
	
	public void sendMeasurementToOutput(long measurement, int MeasurementLength, byte databyte)
	{
		
		for(int i = MeasurementLength-1; i >= 0  ; i--){
		    
		    databyte = (byte)(measurement >>> (8*i));		//para convertir a byte byte por byte contenido en el long en incrementos de 8 measurement >>> 0, 8, 16, 24, 32, 40, 48, 56
		    							
			WriteFilterOutputPort(databyte);

		}//for
		
	}//sendMeasurementToOutput

} // FilterFramework class