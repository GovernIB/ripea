/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_contingut",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "ipa_contingut_mult_uk",
						columnNames = {
								"nom",
								"tipus",
								"pare_id",
								"entitat_id",
								"esborrat"})})
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class ContingutEntity extends RipeaAuditable<Long> {

	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	@Column(name = "tipus", nullable = false)
	protected ContingutTipusEnumDto tipus;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	@ForeignKey(name = "ipa_pare_contingut_fk")
	protected ContingutEntity pare;
	@OneToMany(
			mappedBy = "pare",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	protected Set<ContingutEntity> fills = new HashSet<ContingutEntity>();
	/*
	 * Per a que hi pugui haver el mateix contenidor esborrat
	 * i sense esborrar.
	 */
	@Column(name = "esborrat")
	protected int esborrat = 0;
	@Column(name = "arxiu_uuid", length = 36)
	protected String arxiuUuid;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arxiu_data_act")
	protected Date arxiuDataActualitzacio;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_contingut_fk")
	protected EntitatEntity entitat;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "ipa_expedient_contingut_fk")
	protected ExpedientEntity expedient;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "contmov_id")
	@ForeignKey(name = "ipa_contmov_contingut_fk")
	protected ContingutMovimentEntity darrerMoviment;
	@OneToMany(
			mappedBy = "contingut",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	@OrderBy("createdDate ASC")
	protected List<AlertaEntity> alertes = new ArrayList<AlertaEntity>();
	@Version
	private long version = 0;

	public String getNom() {
		return nom;
	}
	public ContingutTipusEnumDto getTipus() {
		return tipus;
	}
	public ContingutEntity getPare() {
		return pare;
	}
	public Set<ContingutEntity> getFills() {
		return fills;
	}
	public int getEsborrat() {
		return esborrat;
	}
	public String getArxiuUuid() {
		return arxiuUuid;
	}
	public Date getArxiuDataActualitzacio() {
		return arxiuDataActualitzacio;
	}
	public EntitatEntity getEntitat() {
		return entitat;
	}
	public ExpedientEntity getExpedient() {
		return expedient;
	}
	public ContingutMovimentEntity getDarrerMoviment() {
		return darrerMoviment;
	}
	public List<AlertaEntity> getAlertes() {
		return alertes;
	}

	public void update(String nom) {
		this.nom = nom;
	}
	public void updatePare(ContingutEntity pare) {
		this.pare = pare;
	}
	public void updateEsborrat(int esborrat) {
		this.esborrat = esborrat;
	}
	public void updateDarrerMoviment(ContingutMovimentEntity darrerMoviment) {
		this.darrerMoviment = darrerMoviment;
	}
	public void updateArxiu(
			String arxiuUuid) {
		if (arxiuUuid != null) {
			this.arxiuUuid = arxiuUuid;
		}
		this.arxiuDataActualitzacio = new Date();
	}
	public void updateArxiuEsborrat() {
		this.arxiuUuid = null;
		this.arxiuDataActualitzacio = null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + esborrat;
		result = prime * result + ((pare == null) ? 0 : pare.hashCode());
		result = prime * result + ((tipus == null) ? 0 : tipus.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContingutEntity other = (ContingutEntity) obj;
		if (esborrat != other.esborrat)
			return false;
		if (pare == null) {
			if (other.pare != null)
				return false;
		} else if (!pare.equals(other.pare))
			return false;
		if (tipus != other.tipus)
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
