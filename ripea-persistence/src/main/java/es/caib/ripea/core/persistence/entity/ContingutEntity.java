/**
 * 
 */
package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.*;

/**
 * Classe del model de dades que representa un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "contingut")
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class ContingutEntity extends RipeaAuditable<Long> {

	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;
	@Column(name = "tipus", nullable = false)
	protected ContingutTipusEnumDto tipus;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "pare_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "pare_contingut_fk")
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
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "esborrat_data")
	protected Date esborratData;
	@Column(name = "arxiu_uuid", length = 36)
	protected String arxiuUuid;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arxiu_data_act")
	protected Date arxiuDataActualitzacio;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arxiu_intent_data")
	protected Date arxiuIntentData;
	@Column(name = "arxiu_reintents")
	protected int arxiuReintents;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_contingut_fk")
	protected EntitatEntity entitat;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_contingut_fk")
	protected ExpedientEntity expedient;
	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "contmov_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "contmov_contingut_fk")
	protected ContingutMovimentEntity darrerMoviment;
	@OneToMany(
			mappedBy = "contingut",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	@OrderBy("createdDate ASC")
	protected List<AlertaEntity> alertes = new ArrayList<AlertaEntity>();
	
	@Column(name = "ordre")
	protected int ordre;
	
	@Column(name = "numero_registre")
	protected String numeroRegistre;

	@Column(name = "arxiu_propagat")
	protected boolean arxiuPropagat;
	
	@Version
	private long version = 0;

	public void updateEsborratData(Date esborratData) {
		this.esborratData = esborratData;
	}

	public ExpedientEntity getExpedientPare() {
		if (this instanceof ExpedientEntity) {
			return (ExpedientEntity) this;
		} else {
			return this.getExpedient();
		}
	}
	public ContingutMovimentEntity getDarrerMoviment() {
		return darrerMoviment;
	}
	
	public void addFill(ContingutEntity fill) {
		this.fills.add(fill);
	}

	public void updateNom(String nom) {
		this.nom = nom;
	}
	public void updatePare(ContingutEntity pare) {
		this.pare = pare;
	}
	public void updateExpedient(ExpedientEntity expedient) {
		this.expedient = expedient;
	}
	public void updateEsborrat(int esborrat) {
		this.esborrat = esborrat;
	}
	public void updateDarrerMoviment(ContingutMovimentEntity darrerMoviment) {
		this.darrerMoviment = darrerMoviment;
	}
	public void updateOrdre(int ordre) {
		this.ordre = ordre;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public void updateArxiu(
			String arxiuUuid) {
		if (arxiuUuid != null) {
			this.arxiuUuid = arxiuUuid;
		}
        this.arxiuPropagat = true;
		this.arxiuDataActualitzacio = new Date();
	}
	public void updateArxiuEsborrat() {
		this.arxiuUuid = null;
		this.arxiuDataActualitzacio = null;
	}

	public void updateArxiuIntent(boolean arxiuPropagat) {
        this.arxiuPropagat = arxiuPropagat;
		this.arxiuIntentData = new Date();
		this.arxiuReintents++;
	}
	
	public void updateNumeroRegistre(String numeroRegistre) {
		this.numeroRegistre = numeroRegistre;
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

	@Override
	public String toString() {
		return "ContingutEntity: [" +
				"id: " + this.getId() + ", " +
				"nom: " + this.nom + ", " +
				"tipus: " + this.tipus + ", " +
				"esborrat: " + this.esborrat + ", " +
				"expedient: " + (this.expedient != null ? this.expedient.toString() : "NULL") + ", " +
				"entitat: " + (this.entitat != null ? this.entitat.toString() : "NULL") + ", " +
				"pare: " + (this.pare != null ? this.pare.toString() : "NULL") + "]";
	}
	
	private static final long serialVersionUID = -2299453443943600172L;

}
