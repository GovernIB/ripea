/**
 * 
 */
package es.caib.ripea.plugin.caib.dadesext;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.dadesext.ComunitatAutonoma;
import es.caib.ripea.plugin.dadesext.DadesExternesPlugin;
import es.caib.ripea.plugin.dadesext.Municipi;
import es.caib.ripea.plugin.dadesext.Pais;
import es.caib.ripea.plugin.dadesext.Provincia;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Client de test per al servei de dades externes DIR3.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesExternesPluginDir3Test {

	private static final String ENDPOINT_ADDRESS = "https://proves.caib.es/dir3caib/ws/Dir3CaibObtenerCatalogos";
	private static final String USERNAME = "$ripea_dir3caib";
	private static final String PASSWORD = "ripea_dir3caib";

	private DadesExternesPlugin plugin;



	@Before
	public void setUp() throws Exception {
		PropertiesHelper.getProperties().setLlegirSystem(false);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.dadesext.dir3.service.url",
				ENDPOINT_ADDRESS);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.dadesext.dir3.service.username",
				USERNAME);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.dadesext.dir3.service.password",
				PASSWORD);
		plugin = new DadesExternesPluginDir3();
	}

	@Test
	public void test() throws SistemaExternException, JsonProcessingException {
		List<Pais> paisos = plugin.paisFindAll();
		imprimirJson("Països", paisos);
		List<ComunitatAutonoma> comunitatsAutonomes = plugin.comunitatFindAll();
		imprimirJson("Comunitats autònomes", comunitatsAutonomes);
		List<Provincia> provincies = plugin.provinciaFindAll();
		imprimirJson("Províncies", provincies);
		List<Municipi> municipisIb = plugin.municipiFindByProvincia("7");
		imprimirJson("Municipis IB", municipisIb);
	}

	private void imprimirJson(
			String descripcio,
			List<?> objectes) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		System.out.println(">>> " + descripcio + ": [");
		boolean first = true;
		for (Object obj: objectes) {
			if (first) {
				first = false;
			} else {
				System.out.print(",");
			}
			System.out.println(objectMapper.writeValueAsString(obj));
		}
		System.out.println("]");
	}
	
}
