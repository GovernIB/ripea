/**
 * 
 */
package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe del model de dades que representa un enviament d'un document
 * i controla l'estat en el qual es troba.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "document_enviament")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EntityListeners(AuditingEntityListener.class)
public abstract class DocumentEnviamentEntity extends RipeaAuditable<Long> { //TODO: shouldn't be one table, brings confusion

	@Column(name = "estat", nullable = false)
	@Enumerated(EnumType.STRING)
	protected DocumentEnviamentEstatEnumDto estat;
	@Column(name = "assumpte", length = 256, nullable = false)
	protected String assumpte;
	@Column(name = "observacions", length = 256)
	protected String observacions;
	@Column(name = "enviat_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date enviatData;
	@Column(name = "processat_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date processatData;
	@Column(name = "cancelat_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date cancelatData;
	@Column(name = "error")
	protected boolean error;
	@Column(name = "error_desc", length = ERROR_DESC_TAMANY)
	protected String errorDescripcio;
	@Column(name = "intent_num")
	protected int intentNum;
	@Column(name = "intent_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date intentData;
	@Column(name = "intent_proxim_data")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date intentProximData;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "expedient_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_docenv_fk")
	protected ExpedientEntity expedient;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "document_id")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "document_docenv_fk")
	protected DocumentEntity document;
	@ManyToMany(
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY)
	@JoinTable(
			name = BaseConfig.DB_PREFIX + "document_enviament_doc",
			joinColumns = {
					@JoinColumn(name = "document_enviament_id", referencedColumnName="id")},
			inverseJoinColumns = {
					@JoinColumn(name = "document_id")})
	@ForeignKey(
			name = BaseConfig.DB_PREFIX + "docenv_docenvdoc_fk",
			inverseName = "BaseConfig.DB_PREFIX + \"document_docenvdoc_fk")
	protected List<DocumentEntity> annexos = new ArrayList<DocumentEntity>();
	
	
	

	
	
	@Version
	private long version = 0;

	public void addAnnex(DocumentEntity annex) {
		annexos.add(annex);
	}

	public void updateEnviat(
			Date enviatData) {
		this.estat = DocumentEnviamentEstatEnumDto.ENVIAT;
		this.enviatData = enviatData;
		this.error = false;
		this.errorDescripcio = null;
		this.intentNum = 0;
		this.intentData = null;
		this.intentProximData = null;
	}
	public void updateEnviatError(
			String errorDescripcio,
			Date intentProximData) {
		this.estat = DocumentEnviamentEstatEnumDto.PENDENT;
		this.error = true;
		this.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_TAMANY);
		this.enviatData = null;
		this.intentNum = intentNum++;
		this.intentData = new Date();
		this.intentProximData = intentProximData;
	}

	public void updateProcessat(
			boolean processat,
			Date processatData) {
		this.estat = (processat) ? DocumentEnviamentEstatEnumDto.PROCESSAT : DocumentEnviamentEstatEnumDto.REBUTJAT;
		this.processatData = processatData;
		this.error = false;
		this.errorDescripcio = null;
		this.intentNum = 0;
		this.intentData = null;
		this.intentProximData = null;
	}
	public void updateProcessatError(
			String errorDescripcio,
			Date intentProximData) {
		this.estat = DocumentEnviamentEstatEnumDto.ENVIAT;
		this.error = true;
		this.errorDescripcio = StringUtils.abbreviate(errorDescripcio, ERROR_DESC_TAMANY);
		/*if (this.errorDescripcio != null) {
			System.out.println(">>> error: " + this.errorDescripcio);
			System.out.println(">>> length: " + this.errorDescripcio.length());
			System.out.println(">>> length: " + this.errorDescripcio.getBytes().length);
		}*/
		this.processatData = null;
		this.intentNum = intentNum++;
		this.intentData = new Date();
		this.intentProximData = intentProximData;
	}

	public void updateCancelat(
			Date cancelatData) {
		this.estat = DocumentEnviamentEstatEnumDto.CANCELAT;
		this.cancelatData = cancelatData;
		this.error = false;
		this.errorDescripcio = null;
		this.intentNum = 0;
		this.intentData = null;
		this.intentProximData = null;
	}

	protected void inicialitzar() {
		this.estat = DocumentEnviamentEstatEnumDto.PENDENT;
		this.enviatData = null;
		this.processatData = null;
		this.cancelatData = null;
		this.error = false;
		this.errorDescripcio = null;
		this.intentNum = 0;
		this.intentData = null;
		this.intentProximData = null;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	private static final int ERROR_DESC_TAMANY = 2000;
	private static final long serialVersionUID = -2299453443943600172L;

}
