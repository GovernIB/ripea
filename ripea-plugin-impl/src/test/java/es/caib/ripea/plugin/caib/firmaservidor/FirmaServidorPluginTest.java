package es.caib.ripea.plugin.caib.firmaservidor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.ripea.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/** Classe de test per provar el plugin de signatura en el servidor de RIPEA.
 * Les implementacions conegudes del plugin són l'API del Portafib i la
 * implmentació mock. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaServidorPluginTest {
	
	@Before
	public void setUp() throws Exception {
		// Carrega les propietats de test
		PropertiesHelper.getProperties().setLlegirSystem(false);
		PropertiesHelper.getProperties().load(ClassLoader.getSystemResourceAsStream("test.properties"));
	}

	//@Test
	public void signarDocumentPortafibCorrecte() throws Throwable {
		String nom = "";
		String motiu = "prova signatura";
		byte[] contingut = this.obtenirContingutPerFirmar();
		FirmaServidorPlugin signaturaPlugin = new FirmaServidorPluginPortafib();
		try {
			byte[] signatura = signaturaPlugin.firmar(
					nom,
					motiu,
					contingut,
					TipusFirma.PADES,
					"ca");
			assertNotNull("La firma retornada no pot ser nul·la", signatura);
		} catch (Exception ex) {
			System.err.println("Excepció obtinguda signant: " + ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}
	}

	@Test
	public void signarPluginMock() throws SistemaExternException {
		String nom = "";
		String motiu = "prova signatura";
		TipusFirma tipusFirma = TipusFirma.CADES;
		byte[] contingut = new byte[0];
		FirmaServidorPlugin signaturaPlugin = new FirmaServidorPluginMock();
		byte[] signatura = signaturaPlugin.firmar(nom, motiu, contingut, tipusFirma, "ca");
		assertNotNull("La firma retornada no pot ser nul·la", signatura);
	}

	@Test
	public void signarPluginMockException() throws SistemaExternException {
		String nom = "";
		String motiu = "e"; // motiu per provocar una excepció
		TipusFirma tipusFirma = TipusFirma.CADES;
		byte[] contingut = new byte[0];
		FirmaServidorPlugin signaturaPlugin = new FirmaServidorPluginMock();
		try {
			signaturaPlugin.firmar(nom, motiu, contingut, tipusFirma, "ca");
			fail("S'esperava una excepció quan el motiu és \"e\".");
		} catch (Exception e) {
		}
	}

	/** Retorna el contingut de l'arxiu per signar.
	 * 
	 * @return 
	 * @throws Throwable 
	 */
	private byte[] obtenirContingutPerFirmar() throws Throwable {
		byte[] contingut = IOUtils.toByteArray(this.getClass().getResourceAsStream("/es/caib/ripea/plugin/caib/firma_test.pdf")); 
		return contingut;
	}

}
