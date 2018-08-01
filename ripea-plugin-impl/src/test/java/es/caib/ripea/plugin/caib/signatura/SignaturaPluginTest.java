package es.caib.ripea.plugin.caib.signatura;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.signatura.SignaturaPlugin;
import es.caib.ripea.plugin.utils.PropertiesHelper;

/** Classe de test per provar el plugin de signatura en el servidor de RIPEA.
 * Les implementacions conegudes del plugin són l'API del Portafib i la
 * implmentació mock. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaPluginTest {
	
	@Before
	public void setUp() throws Exception {
		// Carrega les propietats de test
		PropertiesHelper.getProperties().setLlegirSystem(false);
		PropertiesHelper.getProperties().load(ClassLoader.getSystemResourceAsStream("test.properties"));
		// Carrega el magatzem de certificats de confiança
		String trustStoreFilePath = null;
		if(trustStoreFilePath == null)
			trustStoreFilePath = getTrustStoreFilePath();
		System.setProperty("javax.net.ssl.trustStore", trustStoreFilePath);
	}

	@Test
	public void signarDocumentPortafibCorrecte() throws Throwable {
		
		String id = "0";
		String nom = "";
		String motiu = "prova signatura";
		String tipusFirma = FileInfoSignature.SIGN_TYPE_CADES;
		byte[] contingut = this.obtenirContingutPerFirmar();
		SignaturaPlugin signaturaPlugin = new SignaturaPluginPortafib();
		try {
			byte[] signatura = signaturaPlugin.signar(id, nom, motiu, tipusFirma, contingut);
			assertNotNull("La firma retornada no pot ser nul·la", signatura);
		} catch ( Exception e ) {
			System.err.println("Excepció obtinguda signant: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void signarPluginMock() throws SistemaExternException {
		
		String id = "0";
		String nom = "";
		String motiu = "prova signatura";
		String tipusFirma = FileInfoSignature.SIGN_TYPE_CADES;
		byte[] contingut = new byte[0];
		SignaturaPlugin signaturaPlugin = new SignaturaPluginMock();
		byte[] signatura = signaturaPlugin.signar(id, nom, motiu, tipusFirma, contingut);
		assertNotNull("La firma retornada no pot ser nul·la", signatura);
	}

	@Test
	public void signarPluginMockException() throws SistemaExternException {
		
		String id = "e"; // id per provocar una excepció
		String nom = "";
		String motiu = "prova signatura";
		String tipusFirma = FileInfoSignature.SIGN_TYPE_CADES;
		byte[] contingut = new byte[0];
		SignaturaPlugin signaturaPlugin = new SignaturaPluginMock();
		try {
			signaturaPlugin.signar(id, nom, motiu, tipusFirma, contingut);
			fail("S'esperava una excepció quan l'id és \"e\".");
		} catch (Exception e) {
			
		}
	}

	/** Retorna el contingut de l'arxiu per signar.
	 * 
	 * @return 
	 * @throws Throwable 
	 */
	private byte[] obtenirContingutPerFirmar() throws Throwable {
		byte[] contingut = IOUtils.toByteArray(this.getClass().getResourceAsStream("/es/caib/ripea/plugin/caib/document_test.odt")); 
		return contingut;
	}


	/** Retorna el path cap al truststore.jks. Primer mira si existeix, si no el guarda des del
	 * classpath cap al directori temporal.
	 * 
	 * @return
	 */
	protected String getTrustStoreFilePath() {
		
		// Ruta destí a la carpeta selenium del directori temporal
		String folderPath = System.getProperty("java.io.tmpdir") + "ripea";
		String filePath = folderPath + File.separator + "truststore.jks";
		// Mira si existeix, si no el crea
		File file = new File(filePath);
		if (!file.exists()) {
			// Mira si crear el directori
			File folder = new File(folderPath);
			if (!folder.exists())
				folder.mkdirs();
			// Copia el contingut de l'arxiu del .jar al temporal
			InputStream stream = null;
	        OutputStream resStreamOut = null;
	        try {
	    		String resourceName = "/truststore.jks";
	            stream = this.getClass().getResourceAsStream(resourceName);
	            if(stream == null) {
	            	fail("No es pot llegir el fitxer de proves del .jar " + resourceName);
	            }
	            int readBytes;
	            byte[] buffer = new byte[4096];
	            resStreamOut = new FileOutputStream(filePath);
	            while ((readBytes = stream.read(buffer)) > 0) {
	                resStreamOut.write(buffer, 0, readBytes);
	            }
	        } catch (Exception e) {
	            fail("Error creant el fitxer truststore.jdk de proves al directori temporal: " + e.getMessage());
	        } finally {
	        	try {
	        		if (stream != null)
	        			stream.close();
	        		if (resStreamOut != null)
	        			resStreamOut.close();
	        	} catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        }     
	    }
		return filePath;		
	}	
}
