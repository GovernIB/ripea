/**
 * 
 */
package es.caib.ripea.plugin.caib.unitat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsPluginDir3Test {

	private static final String ENDPOINT_ADDRESS = "http://dev.caib.es/dir3caib";
	private static final String USERNAME = "$ripea_dir3caib";
	private static final String PASSWORD = "ripea_dir3caib";
	private static final String UNITAT_ARREL = "A04003003"; // real:"A04003003";

	private UnitatsOrganitzativesPlugin plugin;



	@Before
	public void setUp() throws Exception {
		PropertiesHelper.getProperties().setLlegirSystem(false);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.unitats.organitzatives.dir3.service.url",
				ENDPOINT_ADDRESS);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.unitats.organitzatives.dir3.service.username",
				USERNAME);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.unitats.organitzatives.dir3.service.password",
				PASSWORD);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.unitats.organitzatives.dir3.service.log.actiu",
				"true");
		plugin = new UnitatsOrganitzativesPluginDir3();
	}

	@Test
	public void test() throws SistemaExternException {
		List<UnitatOrganitzativa> unitats = plugin.findAmbPare(UNITAT_ARREL);
		for (UnitatOrganitzativa unitat: unitats) {
			System.out.println(">>> [" + unitat.getCodi() + "] " + unitat.getDenominacio());
		}
	}

}
