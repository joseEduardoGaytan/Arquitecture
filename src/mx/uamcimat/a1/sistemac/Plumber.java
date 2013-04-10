package mx.uamcimat.a1.sistemac;

/**
 * Como se modifico la Superclase FilterFramework, es necesario utilizar de nueva cuenta el archivo
 * SourceFilter
 * @author Equipo Zac
 *
 */

public class Plumber {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/****************************************************************************
		* Aqui instanciamos los filtros
		****************************************************************************/

		SourceFilter filter1 = new SourceFilter(args[0]);
		SourceFilter filter2 = new SourceFilter(args[1]);
		TeePipeFilter jointFilter = new TeePipeFilter();
		FeetToMetersFilter altFilter = new FeetToMetersFilter();
		FarenheitToCelsiusFilter tempFilter = new FarenheitToCelsiusFilter();
		SinkAltPressionFilter sinkAltPressFilter = new SinkAltPressionFilter();
		SinkInclPressionFilter sinkInclPressFilter = new SinkInclPressionFilter();
		
		
		/****************************************************************************
		* Aqui conectamos los filtros empezando con un filtro pozo (sinkInclPressFilter) 
		* el cual conectamos al tempFilter que es el filtro que hace la conversión a 
		* grados Celsius. Posteriormente conectamos otro filtro pozo sinkAltPressFilter
		* con otro puerto de salida del tempFilter, el tempFilter a su vez esta conectado
		* a altFilter que hace la conversion de datos a metros. El altFilter se conecta con
		* el filtro encargado de leer dos archivos y alinear los datos (jointFilter),
		* jointFilter esta conectado a dos filtros fuente: filter1 y filter2
		****************************************************************************/

		//Para el join
		
		sinkInclPressFilter.Connect(tempFilter);	//Conectar uno de los filtros de entrada de un filtro pozo a una de las salidas del altFilter
		sinkAltPressFilter.Connect(tempFilter);		//Conectar uno de los filtros de entrada de un filtro pozo a una de las salidas del altFilter
		tempFilter.Connect(altFilter);				//Conectar uno de los filtros de entrada del tempFilter a la salida del altFilter 
		altFilter.Connect(jointFilter);				//Conectar uno de los filtros de entrada del altFilter a la salida del jointFilter
		jointFilter.Connect(filter1);				//Conectar uno de los filtros de entrada del jointFilter a la salida de un filtro fuente
		jointFilter.Connect(filter2);				//Conectar uno de los filtros de entrada del jointFilter a la salida de un filtro fuente 	
						
		/****************************************************************************
		* Aqui arrancamos los filtros. En realidad,... es  un poco aburrido.
		****************************************************************************/

		filter1.start();
		filter2.start();
		jointFilter.start();
		altFilter.start();
		tempFilter.start();
		sinkAltPressFilter.start();
		sinkInclPressFilter.start();
		
	}

}
