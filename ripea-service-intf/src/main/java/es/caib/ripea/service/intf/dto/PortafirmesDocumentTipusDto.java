/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * Informació d'un tipus de document de portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesDocumentTipusDto implements Serializable {

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
	
	/** Mètode per retornar la concatenació del codi i del nom si el codi és diferent de null;
	 * 
	 * @return Codi - Nom o Nom
	 */
	public String getCodiNom() {
		if(this.codi != null) {
			return new StringBuilder(this.codi)
					.append(" - ")
					.append(this.nom)
					.toString();
		} else {
			return this.nom;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
