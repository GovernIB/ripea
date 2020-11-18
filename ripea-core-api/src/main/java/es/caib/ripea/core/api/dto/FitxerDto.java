/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Data;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class FitxerDto implements Serializable {

	private String nom;
	private String nomFitxerFirmat;
	private String contentType;
	private byte[] contingut;
	private long tamany;
	/*private String firmaNom;
	private String firmaContentType;
	private byte[] contingutFirma;
	private long firmaTamany;*/
	
	public FitxerDto() { }
	
	public FitxerDto(String nom, String contentType, byte[] contingut) {
		super();
		this.nom = nom;
		this.contentType = contentType;
		this.setContingut(contingut);
	}

	
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
		if (contingut != null)
			this.tamany = contingut.length;
		else
			this.tamany = 0;
	}
	
	public String getExtensio() {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}
	
	public String getExtensioFitxerFirmat() {
		int indexPunt = nomFitxerFirmat.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nomFitxerFirmat.length() - 1) {
			return nomFitxerFirmat.substring(indexPunt + 1);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	private static final long serialVersionUID = -139254994389509932L;

}
