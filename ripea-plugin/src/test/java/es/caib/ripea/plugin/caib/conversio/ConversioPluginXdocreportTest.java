/**
 * 
 */
package es.caib.ripea.plugin.caib.conversio;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.plugin.conversio.ConversioArxiu;
import es.caib.ripea.plugin.conversio.ConversioPlugin;

/**
 * Classe de proves per la conversió de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConversioPluginXdocreportTest {

	private ConversioPlugin plugin;

	private ConversioArxiu original;



	@Before
	public void setUp() throws Exception {
		plugin = new ConversioPluginXdocreport();
		original = new ConversioArxiu();
		original.setArxiuNom("original_conversio.odt");
		original.setArxiuContingut(
			IOUtils.toByteArray(getClass().getResourceAsStream(
	        		"/es/caib/ripea/plugin/caib/original_conversio.odt")));
	}

	@Test
	public void convertirPdf() throws Exception {
		ConversioArxiu convertit = plugin.convertirPdf(original);
		assertThat(
				convertit.getArxiuNom(),
				is("original_conversio.pdf"));
		assertNotNull(convertit.getArxiuContingut());
		assertTrue(convertit.getArxiuContingut().length > 0);
		/*FileOutputStream fos = new FileOutputStream(
				convertit.getArxiuNom()); 
		fos.write(convertit.getArxiuContingut());
		fos.close();*/
	}

}
