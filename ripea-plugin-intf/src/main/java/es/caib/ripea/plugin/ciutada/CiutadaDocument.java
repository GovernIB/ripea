/**
 * 
 */
package es.caib.ripea.plugin.ciutada;

/**
 * Informació d'un document per a interactuar amb la zona personal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CiutadaDocument {

	private String titol;
	private String arxiuNom;
	private byte[] arxiuContingut;



	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}
	public byte[] getArxiuContingut() {
		return arxiuContingut;
	}
	public void setArxiuContingut(byte[] arxiuContingut) {
		this.arxiuContingut = arxiuContingut;
	}

	public String getArxiuNomSenseExtensio() {
		int indexPunt = arxiuNom.lastIndexOf(".");
		if (indexPunt != -1) {
			return arxiuNom.substring(0, indexPunt);
		} else {
			return arxiuNom;
		}
	}
	public String getArxiuExtensio() {
		int indexPunt = arxiuNom.lastIndexOf(".");
		if (indexPunt != -1) {
			return arxiuNom.substring(indexPunt + 1);
		} else {
			return "";
		}
	}

}
