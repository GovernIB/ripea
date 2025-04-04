package es.caib.ripea.persistence.entity.resourceentity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;

import es.caib.ripea.persistence.base.entity.BaseAuditableEntity;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.model.DocumentEnviamentResource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "document_enviament")
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorColumn(name="dtype")
public abstract class DocumentEnviamentResourceEntity<R extends DocumentEnviamentResource> extends BaseAuditableEntity<R> {

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
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "expedient_docenv_fk")
    protected ExpedientResourceEntity expedient;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "document_docenv_fk")
    protected DocumentResourceEntity document;

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
	
    private static final int ERROR_DESC_TAMANY = 2000;
}