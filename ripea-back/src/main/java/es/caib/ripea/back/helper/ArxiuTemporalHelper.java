/**
 * 
 */
package es.caib.ripea.back.helper;

import es.caib.ripea.service.intf.dto.FitxerDto;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.*;

/**
 * Utilitat per a gestionar arxius temporals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ArxiuTemporalHelper {

	public String crearFitxer(
			ServletContext context,
			FitxerDto fitxer) throws IOException {
		File tempDir = (File)context.getAttribute("javax.servlet.context.tempdir");
		byte[] contingut = fitxer.getContingut();
		fitxer.setContingut(null);
		File tempFileH = File.createTempFile("RIPEA_", "_SCAN", tempDir);
		tempFileH.deleteOnExit();
		saveObject(tempFileH, fitxer);
		File tempFileC = new File(tempDir, tempFileH.getName() + "_CONTENT");
		tempFileC.deleteOnExit();
		FileUtils.writeByteArrayToFile(tempFileC, contingut);
		fitxer.setContingut(contingut);
		return tempFileH.getName();
	}

	public FitxerDto llegirFitxerSenseContingut(
			ServletContext context,
			String name) throws IOException, ClassNotFoundException {
		try {
			File tempDir = (File)context.getAttribute("javax.servlet.context.tempdir");
			File tempFile = new File(tempDir, name);
			return (FitxerDto)readObject(tempFile);
		} catch (FileNotFoundException ex) {
			return null;
		}
	}

	public FitxerDto llegirFitxerAmbContingut(
			ServletContext context,
			String name) throws IOException, ClassNotFoundException {
		try {
			File tempDir = (File)context.getAttribute("javax.servlet.context.tempdir");
			File tempFileH = new File(tempDir, name);
			FitxerDto fitxer = (FitxerDto)readObject(tempFileH);
			File tempFileC = new File(tempDir, name + "_CONTENT");
			byte[] contingut =  FileUtils.readFileToByteArray(tempFileC);
			fitxer.setContingut(contingut);
			return fitxer;
		} catch (FileNotFoundException ex) {
			return null;
		}
	}



	private void saveObject(
			File f,
			Object obj) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(obj);
		oos.close();
	}

	private Object readObject(
			File f) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(f);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object obj = ois.readObject();
		ois.close();
		return obj;
	}
}
