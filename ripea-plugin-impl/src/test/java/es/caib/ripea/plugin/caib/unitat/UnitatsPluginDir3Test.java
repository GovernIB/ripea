package es.caib.ripea.plugin.caib.unitat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.unitat.UnitatOrganitzativa;
import es.caib.ripea.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.plugin.PropertiesHelper;

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
		PropertiesHelper.getProperties().setProperty(PropertyConfig.DIR3_PLUGIN_URL, ENDPOINT_ADDRESS);
		PropertiesHelper.getProperties().setProperty(PropertyConfig.DIR3_PLUGIN_USER, USERNAME);
		PropertiesHelper.getProperties().setProperty(PropertyConfig.DIR3_PLUGIN_PASS, PASSWORD);
		PropertiesHelper.getProperties().setProperty(PropertyConfig.DIR3_PLUGIN_DEBUG, "true");
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