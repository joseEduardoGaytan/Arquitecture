package PruebaJoin;


/******************************************************************************************************************
* File:FilterFramework.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Description:
*
* This superclass defines a skeletal filter framework that defines a filter in terms of the input and output
* ports. All filters must be defined in terms of this framework - that is, filters must extend this class
* in order to be considered valid system filters. Filters as standalone threads until the inputport no longer
* has any data - at which point the filter finishes up any work it has to do and then terminates.
*
* Parameters:
*
* InputReadPort:	This is the filter's input port. Essentially this port is connected to another filter's piped
*					output steam. All filters connect to other filters by connecting their input ports to other
*					filter's output ports. This is handled by the Connect() method.
*
* OutputWritePort:	This the filter's output port. Essentially the filter's job is to read data from the input port,
*					perform some operation on the data, then write the transformed data on the output port.
*
* FilterFramework:  This is a reference to the filter that is connected to the instance filter's input port. This
*					reference is to determine when the upstream filter has stopped sending data along the pipe.
*
* Internal Methods:
*
*	public void Connect( FilterFramework Filter )
*	public byte ReadFilterInputPort()
*	public void WriteFilterOutputPort(byte datum)
*	public boolean EndOfInputStream()
*
******************************************************************************************************************/

import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

//import PruebaFork.FilterFramework;

public class FilterFramework extends Thread
{
	// Define filter input and output ports
		
	//private PipedInputStream InputReadPort = new PipedInputStream();	
		private HashMap<FilterFramework, PipedInputStream> InputReadPorts = new HashMap<FilterFramework, PipedInputStream>();
		
		private HashMap<FilterFramework, PipedOutputStream> OutputWritePorts = new HashMap<FilterFramework, PipedOutputStream>();

		// The following reference to a filter is used because java pipes are able to reliably
		// detect broken pipes on the input port of the filter. This variable will point to
		// the previous filter in the network and when it dies, we know that it has closed its
		// output pipe and will send no more data.

		//private FilterFramework InputFilter;
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
									
			PipedOutputStream out = new PipedOutputStream();
			PipedInputStream in = new PipedInputStream();
			
			//InputFilter = Filter;
			InputFilters.add(Filter);
			
			int index = InputFilters.indexOf(Filter);
			
			//InputFilter.OutputWritePorts.put(this, out);
			InputFilters.get(index).OutputWritePorts.put(this, out);
			
			//InputReadPort.connect(InputFilter.OutputWritePorts.get(this));
			InputReadPorts.put(Filter, in);
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
	* Arguments: void
	*
	* Returns: byte of data read from the input port of the filter.
	*
	* Exceptions: IOExecption, EndOfStreamException (rethrown)
	*
	****************************************************************************/

	protected byte ReadFilterInputPort(int index) throws EndOfStreamException
	{
		byte datum = 0;

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
			
			for(PipedInputStream input: InputReadPorts.values())
			{
				
			//while (InputReadPort.available()==0 )
			while(input.available() == 0)
			{
				if (EndOfInputStream())
				{					
					throw new EndOfStreamException("End of input stream reached");

				} //if

				sleep(250);

			} // while
			}
			

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
			PipedInputStream InputReadPort = InputReadPorts.get(InputFilters.get(index));
			
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
	*
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
	* Arguments: void
	*
	* Returns: A value of true if the previous filter has stopped sending data,
	*		   false if it is still alive and sending data.
	*
	* Exceptions: none
	*
	****************************************************************************/

	private boolean EndOfInputStream()
	{
		for(FilterFramework InputFilter: InputFilters)
		{
		if (InputFilter.isAlive())
		{
			return false;
		}			
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
			for(Entry<FilterFramework, PipedOutputStream> entry : OutputWritePorts.entrySet())
			{
				entry.getValue().close();				
			}
			
			for(PipedInputStream InputReadPort: InputReadPorts.values())
			{			
			InputReadPort.close();
			}
			/*
			OutputWritePort.close();
			*/

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