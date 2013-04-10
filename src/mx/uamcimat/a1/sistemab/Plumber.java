package mx.uamcimat.a1.sistemab;

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
		FarenheitToCelsiusFilter tempFilter = new FarenheitToCelsiusFilter();
		FeetToMetersFilter altFilter = new FeetToMetersFilter();
		SinkFileFilter sinkFilter = new SinkFileFilter();
		SinkExtremeValuesFilter extremeValues = new SinkExtremeValuesFilter();


		/****************************************************************************
		* Aqui conectamos los filtros empezando con uno de los filtros pozo (sinkFilter) el cual
		* conectamos a una bifurcación con el altFilter que es el filtro del medio que hace las 
		* conversiones a metros. Posteriormente conectamos otro filtro pozo (extemeValues) a
		* otro puerto de salida del altFilter. Después conectamos el altFilter con el tempFilter
		* que hace las conversiones a Celsius y finalmente tempFilter 
		* al filtro fuente (filter1).
		****************************************************************************/

		sinkFilter.Connect(altFilter); 		//conectar el puerto de entrada del Sink a la salida del Filtro de altitud
		extremeValues.Connect(altFilter); 	//conectar el puerto de entrada del Sink2 a la salida del Filtro de altitud
		altFilter.Connect(tempFilter); 		//Conectar el puerto de entrada del Filtro de Altura con la salida del Filtro de Temperatura		
		tempFilter.Connect(filter1); 		//Conectar el puerto de entrada del Filtro de Temp a la salida del Source
				
		/****************************************************************************
		* Aqui arrancamos los filtros. En realidad,... es  un poco aburrido.
		****************************************************************************/

		filter1.start();		
		tempFilter.start();
		altFilter.start();
		sinkFilter.start();
		extremeValues.start();

	}

}
