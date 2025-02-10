/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Informació d'una domini d'un meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaExpedientDominiDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -2352701224683446854L;

}
