/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

import lombok.Data;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class FitxerTemporalDto implements Serializable {

	private String nom;
	private String contentType;
	private File file;

	public FitxerTemporalDto(
			String nom,
			String contentType,
			byte[] contingut) {
		
	    try {
			file = File.createTempFile("temp", null);
			file.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(contingut);
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	    
		this.nom = nom;
		this.contentType = contentType;
	}

	public void delete() {
		file.delete();
	}

	public byte[] getBytes() {
		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
