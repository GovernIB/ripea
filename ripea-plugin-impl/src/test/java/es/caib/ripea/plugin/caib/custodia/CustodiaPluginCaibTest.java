/**
 * 
 */
package es.caib.ripea.plugin.caib.custodia;

import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.plugin.custodia.CustodiaPlugin;
import es.caib.ripea.plugin.PropertiesHelper;

/**
 * Classe de proves per la custòdia documental.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CustodiaPluginCaibTest {

	private CustodiaPlugin plugin;



	@Before
	public void setUp() throws Exception {
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.custodia.caib.service.url",
				"https://proves.caib.es/signatura/services/CustodiaDocumentos");
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.custodia.caib.username",
				"HELIUM");
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.custodia.caib.password",
				"HELIUMABC");
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.custodia.caib.verificacio.base.url",
				"https://proves.caib.es/signatura/sigpub/");
		plugin = new CustodiaPluginCaib();
	}

	@Test
	public void findTipusDocument() throws Exception {
		String url = plugin.reservarUrl(
				"12345678");
		System.out.println(">>> URL reservada: " + url);
	}

}
