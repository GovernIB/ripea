/**
 * 
 */
package es.caib.ripea.plugin.caib.unitat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.junit.Test;

import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWs;
import es.caib.dir3caib.ws.api.unidad.Dir3CaibObtenerUnidadesWsService;
import es.caib.dir3caib.ws.api.unidad.UnidadTF;

/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatTest {

	private static final String ENDPOINT_ADDRESS = "http://dev.caib.es/dir3caib/ws/Dir3CaibObtenerUnidades";
	private static final String USERNAME = "$ripea_dir3caib";
	private static final String PASSWORD = "ripea_dir3caib";
	//private static final String UNITAT_ARREL = "A04006741";
	private static final String UNITAT_ARREL = "A04003003";



	@Test
	public void test() throws MalformedURLException {
		List<UnidadTF> unidades = getObtenerUnidadesService().obtenerArbolUnidades(
				UNITAT_ARREL,
				null,
				null);
		for (UnidadTF unidad: unidades) {
			System.out.println(">>> " + unidad.getCodigo());
		}
	}



	private Dir3CaibObtenerUnidadesWs getObtenerUnidadesService() throws MalformedURLException {
		Dir3CaibObtenerUnidadesWs client = null;
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		Dir3CaibObtenerUnidadesWsService service = new Dir3CaibObtenerUnidadesWsService(
				url,
				new QName(
						"http://unidad.ws.dir3caib.caib.es/",
						"Dir3CaibObtenerUnidadesWsService"));
		client = service.getDir3CaibObtenerUnidadesWs();
		BindingProvider bp = (BindingProvider)client;
		bp.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				ENDPOINT_ADDRESS);
		if (USERNAME != null) {
			bp.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					USERNAME);
			bp.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					PASSWORD);
		}
		return client;
	}

}
