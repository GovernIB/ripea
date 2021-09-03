/**
 * 
 */
package es.caib.ripea.plugin.caib.registre;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.plugin.registre.RegistreAnotacioResposta;
import es.caib.ripea.plugin.registre.RegistrePlugin;
import es.caib.ripea.plugin.PropertiesHelper;

/**
 * Classe de proves pel registre regweb3.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistrePluginRegweb3Test {

	private static final String BASE_URL = "https://proves.caib.es/regweb3";
	private static final String USERNAME = "$ripea_regweb";
	private static final String PASSWORD = "ripea_regweb";
	private static final String USUARI = "e43110511r";
	private static final String REGISTRE_NUM = "L19E2/2016";
	private static final String ENTITAT = "A04003003";

	private RegistrePlugin plugin;



	@Before
	public void setUp() throws Exception {
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.registre.regweb3.base.url",
				BASE_URL);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.registre.regweb3.username",
				USERNAME);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.registre.regweb3.password",
				PASSWORD);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.registre.regweb3.log.actiu",
				"true");
		plugin = new RegistrePluginRegweb3();
	}

	@Test
	public void entradaConsultar() throws Exception {
		RegistreAnotacioResposta anotacio = plugin.entradaConsultar(
				REGISTRE_NUM,
				USUARI,
				ENTITAT);
		assertTrue(anotacio != null);
	}

}
