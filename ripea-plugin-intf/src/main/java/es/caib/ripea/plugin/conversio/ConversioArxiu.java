/**
 * 
 */
package es.caib.ripea.plugin.conversio;

/**
 * Arxiu per al plugin de conversió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConversioArxiu {

	private String arxiuNom;
	private byte[] arxiuContingut;

	public ConversioArxiu() {}
	public ConversioArxiu(
			String arxiuNom,
			byte[] arxiuContingut) {
		super();
		this.arxiuNom = arxiuNom;
		this.arxiuContingut = arxiuContingut;
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

	public String getArxiuExtensio() {
		int index = arxiuNom.lastIndexOf(".");
		if (index != -1) {
			return arxiuNom.substring(index + 1);
		} else {
			return "";
		}
	}

}
