/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public String getNomFitxerFirmat() {
		return nomFitxerFirmat;
	}
	public void setNomFitxerFirmat(String nomFitxerFirmat) {
		this.nomFitxerFirmat = nomFitxerFirmat;
	}
	/*public String getFirmaNom() {
		return firmaNom;
	}
	public void setFirmaNom(String firmaNom) {
		this.firmaNom = firmaNom;
	}
	public String getFirmaContentType() {
		return firmaContentType;
	}
	public void setFirmaContentType(String firmaContentType) {
		this.firmaContentType = firmaContentType;
	}
	public byte[] getContingutFirma() {
		return contingutFirma;
	}
	public void setContingutFirma(byte[] contingutFirma) {
		this.contingutFirma = contingutFirma;
	}
	public long getFirmaTamany() {
		return firmaTamany;
	}
	public void setFirmaTamany(long firmaTamany) {
		this.firmaTamany = firmaTamany;
	}*/
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
		if (contingut != null)
			this.tamany = contingut.length;
		else
			this.tamany = 0;
	}
	public long getTamany() {
		return tamany;
	}
	public void setTamany(long tamany) {
		this.tamany = tamany;
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
