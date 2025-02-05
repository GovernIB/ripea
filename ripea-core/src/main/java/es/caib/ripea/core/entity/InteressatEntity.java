/**
 * 
 */
package es.caib.ripea.core.entity;

import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;
import lombok.Getter;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe del model de dades que representa un interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(	name = "ipa_interessat",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {
						"expedient_id",
						"document_num"})})
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@EntityListeners(AuditingEntityListener.class)
public abstract class InteressatEntity extends RipeaAuditable<Long> {

//	CAMP					TIPUS INTERESSAT	DESCRIPCIÓ
//	------------------------------------------------------------------------------------------------------------------------------------
//	tipus: 					COMÚ				tipus d’interessat (persona física, persona jurídica o administració pública)
//	documentTipus: 			COMÚ				tipus de document d’identitat.
//	documentNum: 			COMÚ				número del document d’identitat.
//	nom: 					FÍSICA				nom de l’interessat.
//	llinatge1: 				FÍSICA				primer llinatge de l’interessat.
//	llinatge2: 				FÍSICA				segon llinatge de l’interessat.
//	raoSocial: 				JURÍDICA			nom de l’empresa en cas de persona jurídica.
//	organCodi: 				ADMINISTRACIÓ		codi DIR3 de l’òrgan en cas de que l’interessat sigui del tipus administració pública.
//	país: 					COMÚ				país de l’interessat.
//	provincia: 				COMÚ				província de l’interessat.
//	municipi: 				COMÚ				municipi de l’interessat.
//	adresa: 				COMÚ				adreça de l’interessat.
//	codiPostal: 			COMÚ				codi postal de l’interessat.
//	email: 					COMÚ				adreça electonica de contacte.
//	telefon: 				COMÚ				telèfon de l’interessat
//	observacions: 			COMÚ				observacions de l’interessat.
//	notificacioIdioma: 		COMÚ				per emmagatzemar l’idioma desitjat per a les notificacions.
//	NotificacioAutoritzat: 	COMÚ				per indicar si l’interessat ha autoritzat la recepció de notificacions en format electrònic.

	@Column(name = "document_tipus", length = 40)
	@Enumerated(EnumType.STRING)
	protected InteressatDocumentTipusEnumDto documentTipus;
	@Column(name = "document_num", length = 17)
	protected String documentNum;
	@Column(name = "pais", length = 4)
	protected String pais;
	@Column(name = "provincia", length = 2)
	protected String provincia;
	@Column(name = "municipi", length = 5)
	protected String municipi;
	@Column(name = "adresa", length = 160)
	protected String adresa;
	@Column(name = "codi_postal", length = 5)
	protected String codiPostal;
	@Column(name = "email", length = 160)
	protected String email;
	@Column(name = "telefon", length = 20)
	protected String telefon;
	@Column(name = "observacions", length = 160)
	protected String observacions;
	@Column(name = "not_idioma", length = 2)
	@Enumerated(EnumType.STRING)
	protected InteressatIdiomaEnumDto preferenciaIdioma;
	@Column(name = "not_autoritzat")
	protected boolean notificacioAutoritzat;
	@Column(name = "es_representant")
	protected boolean esRepresentant;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = "ipa_expedient_interessat_fk")
	protected ExpedientEntity expedient;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "representant_id")
	@ForeignKey(name = "ipa_represent_interessat_fk")
	protected InteressatEntity representant;
	@Transient
	private Long representantId;
	@Transient
	private Long representantIdentificador;
	
//	@OneToMany(
//			mappedBy = "representant_id",
//			fetch = FetchType.LAZY)
//	private List<InteressatEntity> representats = new ArrayList<InteressatEntity>();
	
	@Version
	private long version = 0;
	
	@Column(name = "entrega_deh")
	protected Boolean entregaDeh;

	@Column(name = "entrega_deh_obligat")
	protected Boolean entregaDehObligat;
	
	@Column(name = "incapacitat")
	protected Boolean incapacitat;
	
	@Column(name = "arxiu_propagat")
	protected boolean arxiuPropagat;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arxiu_intent_data")
	protected Date arxiuIntentData;
	@Column(name = "arxiu_reintents")
	protected int arxiuReintents;

	@OneToMany(mappedBy = "interessat", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<DocumentEnviamentInteressatEntity> documentEnviamentInteressats = new HashSet<DocumentEnviamentInteressatEntity>();

	@Transient private String paisNom;
	@Transient private String provinciaNom;
	@Transient private String municipiNom;

	public Long getRepresentantId() {
		Long representantId = null;
		if (representant != null) {
			representantId = representant.getId();
		}
		return representantId;
	}
	public String getRepresentantIdentificador() {
		String representantIdentificador = "";
		if (representant != null) {
			representantIdentificador = representant.getIdentificador();
		}
		return representantIdentificador;
	}

	public void updateEntregaDeh(Boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}

	public void updateEntregaDehObligat(Boolean entregaDehObligat) {
		this.entregaDehObligat = entregaDehObligat;
	}

	public void updateEsRepresentant(boolean esRepresentant) {
		this.esRepresentant = esRepresentant;
	}
	public void updateRepresentant(InteressatEntity representant) {
		this.representant = representant;
	}
	public void setPaisNom(String paisNom) {
		this.paisNom = paisNom;
	}
	public void setProvinciaNom(String provinciaNom) {
		this.provinciaNom = provinciaNom;
	}
	public void setMunicipiNom(String municipiNom) {
		this.municipiNom = municipiNom;
	}
	
	public Boolean getIncapacitat() {
		return incapacitat != null ? incapacitat : false;
	}
	public abstract String getIdentificador();

	public String getNom() {
		if (this instanceof InteressatAdministracioEntity) {
			return ((InteressatAdministracioEntity) this).getOrganNom();
		} else if (this instanceof InteressatPersonaFisicaEntity) {
			InteressatPersonaFisicaEntity fis = (InteressatPersonaFisicaEntity) this;
			return fis.getNom() + " " + fis.getLlinatge1() + " " + fis.getLlinatge2();
		} else if (this instanceof InteressatPersonaJuridicaEntity) {
			return ((InteressatPersonaJuridicaEntity) this).getRaoSocial();
		}
		return null;
	}

	public void updateArxiuIntent(boolean arxiuPropagat) {
		this.arxiuPropagat = arxiuPropagat;
		this.arxiuReintents++;
		this.arxiuIntentData = new Date();
	}

	public void update(InteressatDto dto) {
		this.documentTipus = dto.getDocumentTipus();
		this.documentNum = dto.getDocumentNum();
		this.pais = dto.getPais();
		this.provincia =  dto.getProvincia();
		this.municipi =  dto.getMunicipi();
		this.adresa =  dto.getAdresa();
		this.codiPostal =  dto.getCodiPostal();
		this.email =  dto.getEmail();
		this.telefon =  dto.getTelefon();
		this.observacions =  dto.getObservacions();
		this.preferenciaIdioma =  dto.getPreferenciaIdioma();
		this.entregaDeh = dto.getEntregaDeh();
		this.entregaDehObligat = dto.getEntregaDehObligat();
		this.incapacitat = dto.getIncapacitat();
	}
	
	public void merge(InteressatDto dto) {
		if (dto.getDocumentTipus()!=null) { this.documentTipus = dto.getDocumentTipus(); }
		if (dto.getDocumentNum()!=null) { this.documentNum = dto.getDocumentNum(); }
		if (dto.getPais()!=null) { this.pais = dto.getPais(); }
		if (dto.getProvincia()!=null) { this.provincia =  dto.getProvincia(); }
		if (dto.getMunicipi()!=null) { this.municipi =  dto.getMunicipi(); }
		if (dto.getAdresa()!=null) { this.adresa =  dto.getAdresa(); }
		if (dto.getCodiPostal()!=null) { this.codiPostal =  dto.getCodiPostal(); }
		if (dto.getEmail()!=null) { this.email =  dto.getEmail(); }
		if (dto.getTelefon()!=null) { this.telefon =  dto.getTelefon(); }
		if (dto.getObservacions()!=null) { this.observacions =  dto.getObservacions(); }
		if (dto.getPreferenciaIdioma()!=null) { this.preferenciaIdioma =  dto.getPreferenciaIdioma(); }
		if (dto.getEntregaDeh()!=null) { this.entregaDeh = dto.getEntregaDeh(); }
		if (dto.getEntregaDehObligat()!=null) { this.entregaDehObligat = dto.getEntregaDehObligat(); }
		if (dto.getIncapacitat()!=null) { this.incapacitat = dto.getIncapacitat(); }
	}
	
	public boolean isNotificableTelematicament() {
		if (this.getRepresentant()==null) {
			if (this.getDocumentTipus()!=null && InteressatDocumentTipusEnumDto.isNotificableTelematic(this.getDocumentTipus())) {
				return true;
			}
		} else {
			if (this.getRepresentant().getDocumentTipus()!=null && InteressatDocumentTipusEnumDto.isNotificableTelematic(this.getRepresentant().getDocumentTipus())) {
				return true;
			}
		}
		return false;
	}

	public static Builder getBuilder(
			InteressatDto dto,
			ExpedientEntity expedient,
			InteressatEntity representant) {
		if (dto.isPersonaFisica()) {
			return InteressatPersonaFisicaEntity.getBuilder((InteressatPersonaFisicaDto)dto, expedient, representant);
		} else if (dto.isPersonaJuridica()) {
			return InteressatPersonaJuridicaEntity.getBuilder((InteressatPersonaJuridicaDto)dto, expedient, representant);
		} else if (dto.isAdministracio()) {
			return InteressatAdministracioEntity.getBuilder((InteressatAdministracioDto)dto, expedient, representant);
		}
		return null;
	}

	public abstract InteressatTipusEnumDto getTipus();

//	public InteressatEntity getRepresentat() {
//		return !CollectionUtils.isEmpty(representats) ? representats.get(0) : null;
//	}

	public void setRepresentant(InteressatEntity representant) {
		this.representant = representant;
	}

	public abstract static class Builder {
		public abstract InteressatEntity build();
	}
	private static final long serialVersionUID = -2299453443943600172L;

}
