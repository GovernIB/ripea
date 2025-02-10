package es.caib.ripea.core.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;
import es.caib.ripea.plugin.notificacio.NotificacioEstat;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe del model de dades que representa una notificació d'un document
 * a un dels interessats d'un expedient.
 * Notib Notificacio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class DocumentNotificacioEntity extends DocumentEnviamentEntity {

	@Column(name = "not_tipus")
	private DocumentNotificacioTipusEnumDto tipus;
	@Column(name = "not_data_prog")
	@Temporal(TemporalType.DATE)
	private Date dataProgramada;
	@Column(name = "not_retard")
	private Integer retard;
	@Column(name = "not_data_caducitat")
	@Temporal(TemporalType.DATE)
	private Date dataCaducitat;
	@Column(name = "not_env_id", length = 100)
	private String notificacioIdentificador;
	@Column(name = "not_env_cert_arxiuid", length = 50)
	private String enviamentCertificacioArxiuId;
	@Enumerated(EnumType.STRING)
	@Column(name = "servei_tipus", length = 10)
	private ServeiTipusEnumDto serveiTipusEnum;
	@Column(name="entrega_postal")
	private Boolean entregaPostal;
	@Column(name="not_env_registre_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date registreData;
	@Column(name="not_env_registre_numero", length = 19)
	private Integer registreNumero;
	@Column(name="not_env_registre_num_formatat", length = 50)
	private String registreNumeroFormatat;
	@ManyToOne(optional = true)
	@JoinColumn(name = "not_emisor_id")
	private OrganGestorEntity emisor;
	@Column(name = "not_data_enviada")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataEnviada;
	@Column(name = "not_data_finalitzada")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataFinalitzada;
	@OneToMany(
			mappedBy = "notificacio",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	private Set<DocumentEnviamentInteressatEntity> documentEnviamentInteressats = new HashSet<DocumentEnviamentInteressatEntity>();
	@Column(name = "notificacio_estat")
	@Enumerated(EnumType.STRING)
	protected DocumentNotificacioEstatEnumDto notificacioEstat;

	public void update(
			DocumentNotificacioEstatEnumDto notificacioEstat,
			String assumpte,
			String observacions) {
		this.notificacioEstat = notificacioEstat;
		this.assumpte = assumpte;
		this.observacions = observacions;
	}

	public void updateEnviat(
			Date enviatData,
			NotificacioEstat estat,
			String notificacioIdentificador) {
		super.updateEnviat(enviatData);
		this.notificacioIdentificador = notificacioIdentificador;
		this.enviatData = enviatData;
		this.notificacioEstat = estat != null ? DocumentNotificacioEstatEnumDto.valueOf(estat.toString()) : null;
	}
	
	public void updateEnviatError(
			String errorDescripcio,
			String notificacioIdentificador) {
		this.notificacioIdentificador = notificacioIdentificador;
		super.updateEnviatError(
				errorDescripcio,
				null);
	}

	public void updateNotificacioEstat(
			NotificacioEstat estat,
			Date estatData,
			boolean error,
			String errorDescripcio,
			Date dataEnviada, 
			Date dataFinalitzada) {
		this.error = error;
		this.errorDescripcio = errorDescripcio;
		this.notificacioEstat = estat != null ? DocumentNotificacioEstatEnumDto.valueOf(estat.toString()) : null;
		this.processatData = estatData;
		this.dataEnviada = dataEnviada;
		this.dataFinalitzada = dataFinalitzada;
	}
	
	public void updateNotificacioInfoRegistre(
			Date registreData,
			Integer numeroRegistre,
			String numeroRegistreFormatat) {
		this.registreData = registreData;
		this.registreNumero = numeroRegistre;
		this.registreNumeroFormatat = numeroRegistreFormatat;
	}
	
	public static Builder getBuilder(
			DocumentNotificacioEstatEnumDto notificacioEstat,
			String assumpte,
			DocumentNotificacioTipusEnumDto tipus,
			Date dataProgramada,
			Integer retard,
			Date dataCaducitat,
			ExpedientEntity expedient,
			DocumentEntity document,
			ServeiTipusEnumDto serveiTipusEnum,
			Boolean entregaPostal) {
		return new Builder(
				notificacioEstat,
				assumpte,
				tipus,
				dataProgramada,
				retard,
				dataCaducitat,
				expedient,
				document,
				serveiTipusEnum,
				entregaPostal);
	}

	public static class Builder {
		DocumentNotificacioEntity built;
		Builder(
				DocumentNotificacioEstatEnumDto notificacioEstat,
				String assumpte,
				DocumentNotificacioTipusEnumDto tipus,
				Date dataProgramada,
				Integer retard,
				Date dataCaducitat,
				ExpedientEntity expedient,
				DocumentEntity document,
				ServeiTipusEnumDto serveiTipusEnum,
				Boolean entregaPostal) {
			built = new DocumentNotificacioEntity();
			built.inicialitzar();
			built.notificacioEstat = notificacioEstat;
			built.assumpte = assumpte;
			built.tipus = tipus;
			built.dataProgramada = dataProgramada;
			built.retard = retard;
			built.dataCaducitat = dataCaducitat;
			built.expedient = expedient;
			built.document = document;
			built.serveiTipusEnum = serveiTipusEnum;
			built.entregaPostal = entregaPostal;
			
		}

		public Builder annexos(List<DocumentEntity> annexos) {
			built.annexos = annexos;
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder emisor(OrganGestorEntity emisor) {
			built.emisor = emisor;
			return this;
		}
		public DocumentNotificacioEntity build() {
			return built;
		}
	}

	public boolean isNotificacioFinalitzada() {
		List<DocumentNotificacioEstatEnumDto> estatsFinals = new ArrayList<DocumentNotificacioEstatEnumDto>(Arrays.asList(
				DocumentNotificacioEstatEnumDto.FINALITZADA, 
				DocumentNotificacioEstatEnumDto.PROCESSADA));
		return estatsFinals.contains(this.getNotificacioEstat());
	}
	
	public boolean isCaducada() {
		return (this.getDataCaducitat()!=null && this.getDataCaducitat().before(Calendar.getInstance().getTime()));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expedient == null) ? 0 : expedient.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((enviatData == null) ? 0 : enviatData.hashCode());
		result = prime * result + ((processatData == null) ? 0 : processatData.hashCode());
		result = prime * result + ((tipus == null) ? 0 : tipus.hashCode());
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
		DocumentNotificacioEntity other = (DocumentNotificacioEntity) obj;
		if (expedient == null) {
			if (other.expedient != null)
				return false;
		} else if (!expedient.equals(other.expedient))
			return false;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
			return false;
		if (enviatData == null) {
			if (other.enviatData != null)
				return false;
		} else if (!enviatData.equals(other.enviatData))
			return false;
		if (processatData == null) {
			if (other.processatData != null)
				return false;
		} else if (!processatData.equals(other.processatData))
			return false;
		if (tipus == null) {
			if (other.tipus != null)
				return false;
		} else if (!tipus.equals(other.tipus))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;
}