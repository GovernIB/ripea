/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.InteressatDocumentTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.core.audit.RipeaAuditable;

/**
 * Classe del model de dades que representa un interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(	name = "ipa_interessat",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {
						"expedient_id",
						"document_num",
						"nom",
						"llinatge1",
						"llinatge2",
						"rao_social",
						"organ_codi"})})
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

	@Column(name = "document_tipus", length = 1)
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
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "representant_id")
	@ForeignKey(name = "ipa_represent_interessat_fk")
	protected InteressatEntity representant;
	@Transient
	private Long representantId;
	@Transient
	private Long representantIdentificador;
	@Version
	private long version = 0;
	
	@Column(name = "entrega_deh")
	protected Boolean entregaDeh;

	@Column(name = "entrega_deh_obligat")
	protected Boolean entregaDehObligat;
	
	@Column(name = "incapacitat")
	protected Boolean incapacitat;
	
	

	@OneToMany(
			mappedBy = "interessat",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	private Set<DocumentEnviamentInteressatEntity> documentEnviamentInteressats = new HashSet<DocumentEnviamentInteressatEntity>();
	
	

	public Set<DocumentEnviamentInteressatEntity> getDocumentEnviamentInteressats() {
		return documentEnviamentInteressats;
	}
	public InteressatDocumentTipusEnumDto getDocumentTipus() {
		return documentTipus;
	}
	public String getDocumentNum() {
		return documentNum;
	}
	public String getPais() {
		return pais;
	}
	public String getProvincia() {
		return provincia;
	}
	public String getMunicipi() {
		return municipi;
	}
	public String getAdresa() {
		return adresa;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public String getEmail() {
		return email;
	}
	public String getTelefon() {
		return telefon;
	}
	public String getObservacions() {
		return observacions;
	}
	public InteressatIdiomaEnumDto getPreferenciaIdioma() {
		return preferenciaIdioma;
	}
	public boolean isNotificacioAutoritzat() {
		return notificacioAutoritzat;
	}
	public boolean isEsRepresentant() {
		return esRepresentant;
	}
	public ExpedientEntity getExpedient() {
		return expedient;
	}
	public InteressatEntity getRepresentant() {
		return representant;
	}
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
	public Boolean getEntregaDeh() {
		return entregaDeh;
	}
	public void updateEntregaDeh(Boolean entregaDeh) {
		this.entregaDeh = entregaDeh;
	}
	public Boolean getEntregaDehObligat() {
		return entregaDehObligat;
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
	public Boolean getIncapacitat() {
		return incapacitat != null ? incapacitat : false;
	}
	public abstract String getIdentificador();



	private static final long serialVersionUID = -2299453443943600172L;

}
