/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una MetaDada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaDadaDto implements Serializable {

	private Long id;
	private String codi;
	private String nom;
	private String descripcio;
	private MetaDadaTipusEnumDto tipus;
	private boolean globalExpedient;
	private boolean globalDocument;
	private MultiplicitatEnumDto globalMultiplicitat;
	private boolean globalReadOnly;
	private boolean activa;

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
	public MetaDadaTipusEnumDto getTipus() {
		return tipus;
	}
	public boolean isGlobalExpedient() {
		return globalExpedient;
	}
	public void setGlobalExpedient(boolean globalExpedient) {
		this.globalExpedient = globalExpedient;
	}
	public boolean isGlobalDocument() {
		return globalDocument;
	}
	public void setGlobalDocument(boolean globalDocument) {
		this.globalDocument = globalDocument;
	}
	public MultiplicitatEnumDto getGlobalMultiplicitat() {
		return globalMultiplicitat;
	}
	public void setGlobalMultiplicitat(MultiplicitatEnumDto globalMultiplicitat) {
		this.globalMultiplicitat = globalMultiplicitat;
	}
	public boolean isGlobalReadOnly() {
		return globalReadOnly;
	}
	public void setGlobalReadOnly(boolean globalReadOnly) {
		this.globalReadOnly = globalReadOnly;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public void setTipus(MetaDadaTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetaDadaDto other = (MetaDadaDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
