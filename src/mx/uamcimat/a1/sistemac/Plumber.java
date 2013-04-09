package mx.uamcimat.a1.sistemac;


//import mx.uamcimat.a1.ejemplo.SourceFilter; // se importa la clase SourceFilter con el fin de no crearla de nueva cuenta

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
				
		//JoinSinkFileFilter joinFilter = new JoinSinkFileFilter();

		/****************************************************************************
		* Aqui conectamos los filtros empezando con el filtro pozo (filter3) el cual
		* conectamos al filter2 que es el filtro del medio. Posteriormente conectamos filter2
		* al filtro fuente (filter1).
		****************************************************************************/

		//Para el join
		
		sinkInclPressFilter.Connect(tempFilter);
		sinkAltPressFilter.Connect(tempFilter);
		tempFilter.Connect(altFilter);
		altFilter.Connect(jointFilter);	
		jointFilter.Connect(filter1);
		jointFilter.Connect(filter2);		
		
		/*sinkAltPressFilter.Connect(jointFilter);
		//tempFilter.Connect(altFilter);
		//altFilter.Connect(jointFilter);
		jointFilter.Connect(filter1);
		jointFilter.Connect(filter2);*/
		
		/*joinFilter.Connect(altFilter);
		joinFilter.Connect(tempFilter);
		altFilter.Connect(filter1);
		tempFilter.Connect(filter1);*/	
		
		//Para el sequential
		/*sinkFilter.Connect(tempFilter);
		tempFilter.Connect(altFilter);
		altFilter.Connect(filter1);*/
		
		//Para el Fork
		/*sinkFilter2.Connect(tempFilter);
		sinkFilter.Connect(altFilter); //conectar el puerto de entrada del Sink a la salida del Filtro de altitud
		tempFilter.Connect(filter1); 	//Conectar el puerto de entrada del Filtro de Temp a la salida del Source
		altFilter.Connect(filter1);*/ //Conectar el puerto de entrada del Filtro de Altura con la salida del Filtro de Temperatura		
		
		
		/*sinkFilter2.Connect(tempFilter);
		sinkFilter.Connect(altFilter); //conectar el puerto de entrada del Sink a la salida del Filtro de altitud
		tempFilter.Connect(filter1); 	//Conectar el puerto de entrada del Filtro de Temp a la salida del Source
		altFilter.Connect(filter1); //Conectar el puerto de entrada del Filtro de Altura con la salida del Filtro de Temperatura
		*/
		
						
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
		/*altFilter.start();
		tempFilter.start();		
		joinFilter.start();*/
		//sinkFilter.start();
		//sinkFilter2.start();
		

	}

}
