package es.caib.ripea.core.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.ripea.core.firma.DocumentFirmaServidorFirma;
import es.caib.ripea.plugin.PropertiesHelper;
import es.caib.ripea.plugin.caib.firmaservidor.FirmaSimpleServidorPluginPortafib;
import es.caib.ripea.plugin.firmaservidor.SignaturaResposta;


public class RemoveSignaturesPdfTest {
	
	private static final String FILE_NUMBER = "1";
	
	@Before
	public void setUp() throws Exception {
		// Carrega les propietats de test
		PropertiesHelper.getProperties().load(ClassLoader.getSystemResourceAsStream("test.properties"));
	}



	@Test
	public void test() throws Throwable {


		byte[] fileContent = null;
		

		fileContent = removeSignatures(true);
		
		fileContent = signInServidor(true, fileContent);
		
		
		
	}
	
	public byte[] removeSignatures(boolean writeInDisk) throws Throwable{
		DocumentFirmaServidorFirma documentFirmaServidorFirma = new DocumentFirmaServidorFirma();
		byte[] fileWithRemovedSignatures = documentFirmaServidorFirma.removeSignaturesPdfUsingPdfWriterCopyPdf(obtenirContingut(null), "application/pdf");
		if (writeInDisk) {
			Path path = Paths.get(System.getProperty("user.home") + "\\Desktop\\signature_removed" + FILE_NUMBER + ".pdf");
			Files.write(path, fileWithRemovedSignatures);
		}
		return fileWithRemovedSignatures;
	}
	
	public byte[] signInServidor(boolean writeInDisk, byte[] content) throws Throwable{
		if (content == null) {
			content = Files.readAllBytes(Paths.get(System.getProperty("user.home") + "\\Desktop\\signature_removed" + FILE_NUMBER + ".pdf"));
		}

		FirmaSimpleServidorPluginPortafib firmaSimpleServidorPluginPortafib = new FirmaSimpleServidorPluginPortafib("es.caib.ripea.", PropertiesHelper.getProperties());
		SignaturaResposta signaturaResposta = firmaSimpleServidorPluginPortafib.firmar("name.pdf", "123", content, "ca");
		byte[] fileSigned = signaturaResposta.getContingut();
		if (writeInDisk) {
			Path path1 = Paths.get(System.getProperty("user.home") + "\\Desktop\\signedAfterSignatureRemoved" + FILE_NUMBER + ".pdf");
			Files.write(path1, fileSigned);
		}
		return fileSigned;
		
	}



	private byte[] obtenirContingut(String fileName) throws Throwable {
		byte[] contingut = null;
		if (fileName != null) {
			contingut = Files.readAllBytes(Paths.get(System.getProperty("user.home") + "\\Desktop\\" + fileName));
		} else {
			contingut = IOUtils.toByteArray(this.getClass().getResourceAsStream("/es/caib/ripea/core/sample_invalid_firmes/invalid_firma" + FILE_NUMBER + ".pdf")); 
		}

		return contingut;
	}

}
