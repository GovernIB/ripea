/**
 * 
 */
package es.caib.ripea.plugin.arxiu;

/**
 * Estructura que representa un contingut d'un document de l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuDocumentContingut {

	private ArxiuContingutTipusEnum tipus;
	private String contentType;
	private byte[] contingut;

	public ArxiuDocumentContingut(
			ArxiuContingutTipusEnum tipus,
			String contentType,
			byte[] contingut) {
		super();
		this.tipus = tipus;
		this.contentType = contentType;
		this.contingut = contingut;
	}

	public ArxiuContingutTipusEnum getTipus() {
		return tipus;
	}
	public String getContentType() {
		return contentType;
	}
	public byte[] getContingut() {
		return contingut;
	}

}
