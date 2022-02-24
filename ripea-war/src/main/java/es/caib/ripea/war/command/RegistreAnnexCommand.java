/**
 * 
 */
package es.caib.ripea.war.command;

/**
 * Informació d'un registre annex.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnnexCommand {

	private Long id;

	private String titol;
	private String uuid;
	
	private Long metaDocumentId;
	


	public Long getId() {
		return id;
	}
	public void setId(
			Long id) {
		this.id = id;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(
			String titol) {
		this.titol = titol;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(
			String uuid) {
		this.uuid = uuid;
	}
	public Long getMetaDocumentId() {
		return metaDocumentId;
	}
	public void setMetaDocumentId(Long metaDocumentId) {
		this.metaDocumentId = metaDocumentId;
	}
	

}
