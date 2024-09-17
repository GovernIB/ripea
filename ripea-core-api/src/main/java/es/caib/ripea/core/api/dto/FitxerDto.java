package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class FitxerDto implements Serializable {

	private String nom;
	private String contentType;
	private byte[] contingut;
	private Long tamany;
	/*private String firmaNom;
	private String firmaContentType;
	private byte[] contingutFirma;
	private long firmaTamany;*/
	private Long id;
	
	public FitxerDto() { }
	
	public FitxerDto(String nom, String contentType, byte[] contingut) {
		this.nom = nom;
		this.contentType = contentType;
		this.setContingut(contingut);
	}
	public FitxerDto(String nom, String contentType, Long tamany) {
		this.nom = nom;
		this.contentType = contentType;
		this.tamany = tamany;
	}
	
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
		if (contingut != null)
			this.tamany = Long.valueOf(contingut.length);
		else
			this.tamany = 0l;
	}
	
	public String getExtensio() {
		if (nom == null)
			return null;

		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		//java.lang.OutOfMemoryError: Java heap space at java.util.Arrays.copyOfRange(Arrays.java:2694)
		//return ToStringBuilder.reflectionToString(this);
		return "FitxerDto --> nom: "+this.nom + ", type: "+this.contentType+ ", tamany: "+this.tamany+ ", id: "+this.id;
	}

	private static final long serialVersionUID = -139254994389509932L;
}