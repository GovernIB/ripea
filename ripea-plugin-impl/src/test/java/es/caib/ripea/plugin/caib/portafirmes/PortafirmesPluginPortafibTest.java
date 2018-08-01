/**
 * 
 */
package es.caib.ripea.plugin.caib.portafirmes;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.plugin.portafirmes.PortafirmesDocument;
import es.caib.ripea.plugin.portafirmes.PortafirmesDocumentTipus;
import es.caib.ripea.plugin.portafirmes.PortafirmesFluxBloc;
import es.caib.ripea.plugin.portafirmes.PortafirmesPlugin;
import es.caib.ripea.plugin.portafirmes.PortafirmesPrioritatEnum;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/**
 * Classe de proves pel portafirmes Portafib.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesPluginPortafibTest {

	private static final String BASE_URL = "https://proves.caib.es/portafib";
	private static final String USERNAME = "$ripea_portafib";
	private static final String PASSWORD = "ripea_portafib";
	private static final String DESTINATARI = "43110511R";

	private PortafirmesPlugin plugin;

	private PortafirmesDocument uploadDocument;



	@Before
	public void setUp() throws Exception {
		PropertiesHelper.getProperties().setLlegirSystem(false);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.base.url",
				BASE_URL);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.username",
				USERNAME);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.password",
				PASSWORD);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.ripea.plugin.portafirmes.portafib.log.actiu",
				"true");
		plugin = new PortafirmesPluginPortafib();
		uploadDocument = new PortafirmesDocument();
		uploadDocument.setTitol("(RIP) Document per firmar");
		uploadDocument.setArxiuNom("document_firma.pdf");
		uploadDocument.setArxiuContingut(
			IOUtils.toByteArray(getClass().getResourceAsStream(
	        		"/es/caib/ripea/plugin/caib/document_firma.pdf")));
		uploadDocument.setFirmat(false);
	}

	//@Test
	public void findTipusDocument() throws Exception {
		List<PortafirmesDocumentTipus> tipus = plugin.findDocumentTipus();
		assertTrue(tipus != null);
		assertTrue(tipus.size() > 0);
		/*for (PortafirmesDocumentTipus t: tipus)
			System.out.println(">>> " + t.getId() + ", " + t.getNom());*/
		/*long instanciaFluxId = ((PortafirmesPluginPortafib)plugin).instanciaFluxFirma(
				new Long(353));
		System.out.println(">>> instanciaFluxId: " + instanciaFluxId);*/
	}

	//@Test
	public void uploadAndDelete() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		List<PortafirmesFluxBloc> flux = new ArrayList<PortafirmesFluxBloc>();
		PortafirmesFluxBloc bloc = new PortafirmesFluxBloc();
		bloc.setDestinataris(new String[] {DESTINATARI});
		bloc.setMinSignataris(1);
		bloc.setObligatorietats(new boolean[] {true});
		flux.add(bloc);
		String documentId = plugin.upload(
				uploadDocument,
				"99",
				"Prova d'enviament RIPEA",
				"Aplicació RIPEA",
				PortafirmesPrioritatEnum.NORMAL,
				cal.getTime(),
				flux,
				null,
				null,
				false);
		plugin.delete(documentId);
	}

	@Test
	public void download() throws Exception {
		long documentId = 29079;
		PortafirmesDocument downloadDocument = plugin.download(
				new Long(documentId).toString());
		/*assertEquals(
				uploadDocument.getTitol(),
				downloadDocument.getTitol());
		assertEquals(
				uploadDocument.getArxiuNom(),
				downloadDocument.getArxiuNom());
		assertEquals(
				uploadDocument.getArxiuContingut().length,
				downloadDocument.getArxiuContingut().length);*/
		assertTrue(downloadDocument.isFirmat());
	}

}
