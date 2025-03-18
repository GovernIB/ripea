/**
 * 
 */
package es.caib.ripea.plugin.gesdoc;

/**
 * Arxiu emmagatzemat a dins la gestió documental.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GestioDocumentalArxiu {

	private String fileName;
	private byte[] content;

	public GestioDocumentalArxiu(
			String fileName,
			byte[] content) {
		super();
		this.fileName = fileName;
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}

}
