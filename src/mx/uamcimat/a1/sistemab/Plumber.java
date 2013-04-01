package mx.uamcimat.a1.sistemab;

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

		SourceFilter filter1 = new SourceFilter();
		FarenheitToCelsiusFilter tempFilter = new FarenheitToCelsiusFilter();
		FeetToMetersFilter altFilter = new FeetToMetersFilter();
		SinkFileFilter sinkFilter = new SinkFileFilter();
		SinkFile2Filter sinkFilter2 = new SinkFile2Filter();


		/****************************************************************************
		* Aqui conectamos los filtros empezando con el filtro pozo (filter3) el cual
		* conectamos al filter2 que es el filtro del medio. Posteriormente conectamos filter2
		* al filtro fuente (filter1).
		****************************************************************************/

		sinkFilter.Connect(altFilter); //conectar el puerto de entrada del Sink a la salida del Filtro de altitud
		sinkFilter2.Connect(altFilter); //conectar el puerto de entrada del Sink2 a la salida del Filtro de altitud
		altFilter.Connect(tempFilter); //Conectar el puerto de entrada del Filtro de Altura con la salida del Filtro de Temperatura		
		tempFilter.Connect(filter1); 	//Conectar el puerto de entrada del Filtro de Temp a la salida del Source
				
		/****************************************************************************
		* Aqui arrancamos los filtros. En realidad,... es  un poco aburrido.
		****************************************************************************/

		filter1.start();		
		tempFilter.start();
		altFilter.start();
		sinkFilter.start();
		sinkFilter2.start();

	}

}
