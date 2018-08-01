/**
 * 
 */
package es.caib.ripea.plugin.caib.portafirmes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Classe de proves pel portafirmes de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesPluginCwsTest {

	private PortafirmesPlugin plugin;

	private PortafirmesDocument uploadDocument;
	private String documentId;



	@Before
	public void setUp() throws Exception {
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.portafirmes.service.url",
				"https://proves.caib.es/portafirmasws/web/services/CWS");
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.portafirmes.username",
				"HELIUM");
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.portafirmes.password",
				"HELIUM");
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.portafirmes.check.cert",
				"false");
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.portafirmes.signatura.tipus",
				"1");
		plugin = new PortafirmesPluginCwsJaxws();
		uploadDocument = new PortafirmesDocument();
		uploadDocument.setTitol("(RIP) Document per firmar");
		uploadDocument.setArxiuNom("document_firma.pdf");
		uploadDocument.setArxiuContingut(
			IOUtils.toByteArray(getClass().getResourceAsStream(
	        		"/es/caib/ripea/plugin/caib/document_firma.pdf")));
		uploadDocument.setFirmat(false);
	}

	@Test
	public void upload() throws Exception {
		/*List<PortafirmesFluxBloc> passos = new ArrayList<PortafirmesFluxBloc>();
		PortafirmesFluxBloc pas = new PortafirmesFluxBloc();
		pas.setMinSignataris(1);
		pas.setDestinataris(new String[] {"37340638T"});
		passos.add(pas);
		documentId = plugin.upload(
				uploadDocument,
				new Integer(999),
				"Prova d'enviament RIPEA",
				"Aplicació RIPEA",
				PortafirmesPrioritatEnum.NORMAL,
				null,
				passos,
				null,
				null,
				false);*/
		//documentId = 30117;
		System.out.println(">>> Document id pujat: " + documentId); // 30117
	}

	@Test
	public void download() throws Exception {
		System.out.println(">>> Document id per descarregar: " + documentId);
		documentId = "30078";
		PortafirmesDocument downloadDocument = plugin.download(documentId);
		assertThat(
				downloadDocument.getTitol(),
				is(uploadDocument.getTitol()));
		assertThat(
				downloadDocument.getArxiuNom(),
				is(uploadDocument.getArxiuNom()));
		assertThat(
				downloadDocument.getArxiuContingut().length,
				is(uploadDocument.getArxiuContingut().length));
		assertThat(
				downloadDocument.isFirmat(),
				is(true));
	}

}
