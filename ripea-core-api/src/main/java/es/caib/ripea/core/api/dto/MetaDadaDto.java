/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
	private MetaDadaTipusEnumDto tipus;
	private String descripcio;
	private MultiplicitatEnumDto multiplicitat;
	private boolean readOnly;
	private int ordre;
	private boolean activa;
	
	
	
	private Long valorSencer;
	private Double valorFlotant;
	private BigDecimal valorImport;
	private Date valorData; 
	private Boolean valorBoolea;
	private String valorString;
	
	

	public Long getValorSencer() {
		return valorSencer;
	}
	public void setValorSencer(
			Long valorSencer) {
		this.valorSencer = valorSencer;
	}
	public Double getValorFlotant() {
		return valorFlotant;
	}
	public void setValorFlotant(
			Double valorFlotant) {
		this.valorFlotant = valorFlotant;
	}
	public BigDecimal getValorImport() {
		return valorImport;
	}
	public void setValorImport(
			BigDecimal valorImport) {
		this.valorImport = valorImport;
	}
	public Date getValorData() {
		return valorData;
	}
	public void setValorData(
			Date valorData) {
		this.valorData = valorData;
	}
	public Boolean getValorBoolea() {
		return valorBoolea;
	}
	public void setValorBoolea(
			Boolean valorBoolea) {
		this.valorBoolea = valorBoolea;
	}
	public String getValorString() {
		return valorString;
	}
	public void setValorString(
			String valorString) {
		this.valorString = valorString;
	}
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
	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}
	public void setMultiplicitat(MultiplicitatEnumDto multiplicitat) {
		this.multiplicitat = multiplicitat;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
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