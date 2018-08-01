/**
 * 
 */
package es.caib.ripea.plugin.portafirmes;

/**
 * Informació sobre un tipus de document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesDocumentTipus {

	private long id;
	private String codi;
	private String nom;



	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}

}
