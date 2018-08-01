/**
 * 
 */
package es.caib.ripea.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.DocumentNotificacioTipusEnumDto;
import es.caib.ripea.core.api.dto.InteressatIdiomaEnumDto;
import es.caib.ripea.plugin.notificacio.EnviamentEstat;

/**
 * Classe del model de dades que representa una notificació d'un document
 * a un dels interessats d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "not_interessat_id")
	@ForeignKey(name = "ipa_interessat_docenv_fk")
	private InteressatEntity interessat;
	@Column(name = "not_seu_idioma", length = 2)
	@Enumerated(EnumType.STRING)
	private InteressatIdiomaEnumDto seuIdioma;
	@Column(name = "not_seu_avis_titol", length = 256)
	private String seuAvisTitol;
	@Column(name = "not_seu_avis_text", length = 1024)
	private String seuAvisText;
	@Column(name = "not_seu_avis_textm", length = 200)
	private String seuAvisTextMobil;
	@Column(name = "not_seu_ofici_titol", length = 256)
	private String seuOficiTitol;
	@Column(name = "not_seu_ofici_text", length = 1024)
	private String seuOficiText;
	@Column(name = "not_env_id", length = 100)
	private String enviamentIdentificador;
	@Column(name = "not_env_ref", length = 100)
	private String enviamentReferencia;
	@Column(name = "not_env_dat_estat", length = 20)
	private String enviamentDatatEstat;
	@Column(name = "not_env_dat_data")
	private Date enviamentDatatData;
	@Column(name = "not_env_dat_orig", length = 20)
	private String enviamentDatatOrigen;
	@Column(name = "not_env_cert_data")
	@Temporal(TemporalType.DATE)
	private Date enviamentCertificacioData;
	@Column(name = "not_env_cert_orig", length = 20)
	private String enviamentCertificacioOrigen;
	@Column(name = "not_env_cert_arxiuid", length = 50)
	private String enviamentCertificacioArxiuId;

	public DocumentNotificacioTipusEnumDto getTipus() {
		return tipus;
	}
	public Date getDataProgramada() {
		return dataProgramada;
	}
	public Integer getRetard() {
		return retard;
	}
	public Date getDataCaducitat() {
		return dataCaducitat;
	}
	public InteressatEntity getInteressat() {
		return interessat;
	}
	public InteressatIdiomaEnumDto getSeuIdioma() {
		return seuIdioma;
	}
	public String getSeuAvisTitol() {
		return seuAvisTitol;
	}
	public String getSeuAvisText() {
		return seuAvisText;
	}
	public String getSeuAvisTextMobil() {
		return seuAvisTextMobil;
	}
	public String getSeuOficiTitol() {
		return seuOficiTitol;
	}
	public String getSeuOficiText() {
		return seuOficiText;
	}
	public String getEnviamentIdentificador() {
		return enviamentIdentificador;
	}
	public String getEnviamentReferencia() {
		return enviamentReferencia;
	}
	public String getEnviamentDatatEstat() {
		return enviamentDatatEstat;
	}
	public Date getEnviamentDatatData() {
		return enviamentDatatData;
	}
	public String getEnviamentDatatOrigen() {
		return enviamentDatatOrigen;
	}
	public Date getEnviamentCertificacioData() {
		return enviamentCertificacioData;
	}
	public String getEnviamentCertificacioOrigen() {
		return enviamentCertificacioOrigen;
	}
	public String getEnviamentCertificacioArxiuId() {
		return enviamentCertificacioArxiuId;
	}
	
	public void update(
			DocumentEnviamentEstatEnumDto estat,
			String assumpte,
			String observacions,
			InteressatEntity interessat) {
		this.estat = estat;
		this.assumpte = assumpte;
		this.observacions = observacions;
		this.interessat = interessat;
	}

	public void updateEnviat(
			Date enviatData,
			boolean enviat,
			String enviamentIdentificador,
			String enviamentReferencia) {
		super.updateEnviat(enviatData);
		this.enviamentIdentificador = enviamentIdentificador;
		this.enviamentReferencia = enviamentReferencia;
		if (!enviat) {
			this.enviatData = null;
			this.estat = DocumentEnviamentEstatEnumDto.PENDENT;
		}
	}

	public void updateEnviamentEstat(
			EnviamentEstat enviamentDatatEstat,
			Date enviamentDatatData,
			String enviamentDatatOrigen,
			Date enviamentCertificacioData,
			String enviamentCertificacioOrigen,
			String enviamentCertificacioArxiuId) {
		this.enviamentDatatEstat = enviamentDatatEstat.name();
		this.enviamentDatatData = enviamentDatatData;
		this.enviamentDatatOrigen = enviamentDatatOrigen;
		this.enviamentCertificacioData = enviamentCertificacioData;
		this.enviamentCertificacioOrigen = enviamentCertificacioOrigen;
		this.enviamentCertificacioArxiuId = enviamentCertificacioArxiuId;
		switch (enviamentDatatEstat) {
		case LLEGIDA:
		case NOTIFICADA:
			updateProcessat(true, enviamentDatatData);
			break;
		case EXPIRADA:
		case REBUTJADA:
			updateProcessat(false, enviamentDatatData);
			break;
		case NOTIB_ENVIADA:
			updateEnviat(enviamentDatatData);
			break;
		default:
			break;
		}
	}

	public static Builder getBuilder(
			DocumentEnviamentEstatEnumDto estat,
			String assumpte,
			DocumentNotificacioTipusEnumDto tipus,
			Date dataProgramada,
			Integer retard,
			Date dataCaducitat,
			InteressatEntity interessat,
			InteressatIdiomaEnumDto seuIdioma,
			String seuAvisTitol,
			String seuAvisText,
			String seuOficiTitol,
			String seuOficiText,
			ExpedientEntity expedient,
			DocumentEntity document) {
		return new Builder(
				estat,
				assumpte,
				tipus,
				dataProgramada,
				retard,
				dataCaducitat,
				interessat,
				seuIdioma,
				seuAvisTitol,
				seuAvisText,
				seuOficiTitol,
				seuOficiText,
				expedient,
				document);
	}

	public static class Builder {
		DocumentNotificacioEntity built;
		Builder(
				DocumentEnviamentEstatEnumDto estat,
				String assumpte,
				DocumentNotificacioTipusEnumDto tipus,
				Date dataProgramada,
				Integer retard,
				Date dataCaducitat,
				InteressatEntity interessat,
				InteressatIdiomaEnumDto seuIdioma,
				String seuAvisTitol,
				String seuAvisText,
				String seuOficiTitol,
				String seuOficiText,
				ExpedientEntity expedient,
				DocumentEntity document) {
			built = new DocumentNotificacioEntity();
			built.inicialitzar();
			built.estat = estat;
			built.assumpte = assumpte;
			built.tipus = tipus;
			built.dataProgramada = dataProgramada;
			built.retard = retard;
			built.dataCaducitat = dataCaducitat;
			built.interessat = interessat;
			built.seuIdioma = seuIdioma;
			built.seuAvisTitol = seuAvisTitol;
			built.seuAvisText = seuAvisText;
			built.seuOficiTitol = seuOficiTitol;
			built.seuOficiText = seuOficiText;
			built.expedient = expedient;
			built.document = document;
		}
		public Builder annexos(List<DocumentEntity> annexos) {
			built.annexos = annexos;
			return this;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public Builder seuAvisTextMobil(String seuAvisTextMobil) {
			built.seuAvisTextMobil = seuAvisTextMobil;
			return this;
		}
		public DocumentNotificacioEntity build() {
			return built;
		}
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
		result = prime * result + ((interessat == null) ? 0 : interessat.hashCode());
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
		if (interessat == null) {
			if (other.interessat != null)
				return false;
		} else if (!interessat.equals(other.interessat))
			return false;
		return true;
	}

	private static final long serialVersionUID = -2299453443943600172L;

}
