/**
 * 
 */
package es.caib.ripea.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.plugin.notificacio.EnviamentEstat;
import es.caib.ripea.service.intf.config.BaseConfig;

/**
 * Notib Enviament
 * Equivalent to Notib es.caib.notib.core.entity.NotificacioEnviamentEntity
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "document_enviament_inter")
@EntityListeners(AuditingEntityListener.class)
public class DocumentEnviamentInteressatEntity extends RipeaAuditable<Long> {

	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "interessat_id")
	protected InteressatEntity interessat;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "document_enviament_id")
	protected DocumentNotificacioEntity notificacio;
	@Column(name = "not_env_ref", length = 100)
	private String enviamentReferencia;
	@Column(name = "not_env_dat_estat", length = 20)
	private String enviamentDatatEstat; //Notib: notificaEstat
	@Column(name = "not_env_dat_data")
	private Date enviamentDatatData; //Notib: notificaEstatData
	@Column(name = "not_env_dat_orig", length = 20)
	private String enviamentDatatOrigen;
	@Column(name = "not_env_cert_data")
	@Temporal(TemporalType.DATE)
	private Date enviamentCertificacioData; //Notib: notificaCertificacioData
	@Column(name = "not_env_cert_orig", length = 20)
	private String enviamentCertificacioOrigen;
	@Column(name="not_env_registre_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date registreData;
	@Column(name="not_env_registre_numero", length = 19)
	private Integer registreNumero;
	@Column(name="not_env_registre_num_formatat", length = 50)
	private String registreNumeroFormatat;
	@Column(name = "error")
	protected Boolean error;
	@Column(name = "error_desc", length = ERROR_DESC_TAMANY)
	protected String errorDescripcio;

	public static Builder getBuilder(
			InteressatEntity interessat,
			DocumentNotificacioEntity notificacio) {
		return new Builder(
				interessat,
				notificacio);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Josep Gayà
	 */
	public static class Builder {
		DocumentEnviamentInteressatEntity built;
		Builder(
				InteressatEntity interessat,
				DocumentNotificacioEntity notificacio) {
			built = new DocumentEnviamentInteressatEntity();
			built.interessat = interessat;
			interessat.getDocumentEnviamentInteressats().add(built);
			built.notificacio = notificacio;
			notificacio.getDocumentEnviamentInteressats().add(built);
		}
		public DocumentEnviamentInteressatEntity build() {
			return built;
		}

	}
	
	public boolean isFinalitzat() {
		if (enviamentDatatEstat != null && (
				enviamentDatatEstat.equals("ABSENT") || 
				enviamentDatatEstat.equals("ADRESA_INCORRECTA") || 
				enviamentDatatEstat.equals("ERROR_ENTREGA") || 
				enviamentDatatEstat.equals("EXPIRADA") || 
				enviamentDatatEstat.equals("EXTRAVIADA") || 
				enviamentDatatEstat.equals("MORT") || 
				enviamentDatatEstat.equals("LLEGIDA") || 
				enviamentDatatEstat.equals("NOTIFICADA") || 
				enviamentDatatEstat.equals("REBUTJADA"))) {
			return true;
		} else {
			return false;
		}
	}
	
	public void updateEnviamentCertificacioData(Date enviamentCertificacioData) {
		this.enviamentCertificacioData = enviamentCertificacioData;
	}
	
	public void updateEnviamentError(
			EnviamentEstat enviamentEstat,
			Date enviamentDatatData,
			String enviamentDatatOrigen,
			String errorDescripcio) {
		this.enviamentDatatEstat	= enviamentEstat!=null?enviamentEstat.name():EnviamentEstat.DESCONEGUT.name();
		this.enviamentDatatData		= enviamentDatatData;
		this.enviamentDatatOrigen	= enviamentDatatOrigen;
		this.error 					= true;
		this.errorDescripcio 		= errorDescripcio;
	}
	
	public void updateEnviamentEstat(
			EnviamentEstat enviamentDatatEstat,
			Date enviamentDatatData,
			String enviamentDatatOrigen,
			Date enviamentCertificacioData,
			String enviamentCertificacioOrigen) {
		this.enviamentDatatEstat = enviamentDatatEstat != null ? enviamentDatatEstat.name() : null;
		this.enviamentDatatData = enviamentDatatData;
		this.enviamentDatatOrigen = enviamentDatatOrigen;
		this.enviamentCertificacioData = enviamentCertificacioData;
		this.enviamentCertificacioOrigen = enviamentCertificacioOrigen;
		this.error = false;
		this.errorDescripcio = null;
	}
	
	public void updateEnviamentInfoRegistre(
			Date registreData,
			Integer numeroRegistre,
			String numeroRegistreFormatat) {
		this.registreData = registreData;
		this.registreNumero = numeroRegistre;
		this.registreNumeroFormatat = numeroRegistreFormatat;
	}
	
	public InteressatEntity getInteressat() {
		return interessat;
	}

	public DocumentNotificacioEntity getNotificacio() {
		return notificacio;
	}

	public String getEnviamentReferencia() {
		return enviamentReferencia;
	}

	public void updateEnviamentReferencia(String enviamentReferencia) {
		this.enviamentReferencia = enviamentReferencia;
	}	
	
	public String getEnviamentCertificacioOrigen() {
		return enviamentCertificacioOrigen;
	}

	public void setEnviamentCertificacioOrigen(String enviamentCertificacioOrigen) {
		
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

	public Boolean isError() {
		return error;
	}

	public String getErrorDescripcio() {
		return errorDescripcio;
	}


	public Date getRegistreData() {
		return registreData;
	}



	public void setRegistreData(Date registreData) {
		this.registreData = registreData;
	}



	public Integer getRegistreNumero() {
		return registreNumero;
	}



	public void setRegistreNumero(Integer registreNumero) {
		this.registreNumero = registreNumero;
	}



	public String getRegistreNumeroFormatat() {
		return registreNumeroFormatat;
	}



	public void setRegistreNumeroFormatat(String registreNumeroFormatat) {
		this.registreNumeroFormatat = registreNumeroFormatat;
	}

	public String getNomCompletAmbDocument() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getInteressat().getNomComplet());
		if (this.notificacio!=null && this.notificacio.getDocument()!=null) {
			if (this.notificacio.getDocument().getNumeroRegistre() != null) {
				sb.append(this.notificacio.getDocument().getNumeroRegistre());
				sb.append(" - ");
			}
			if (this.notificacio.getDocument().getTipus() != null) {
				sb.append(this.notificacio.getDocument().getTipus().name());
				sb.append(": ");
			}
			sb.append(this.notificacio.getDocument().getNom());
		}
		return sb.toString();
	}

	private static final int ERROR_DESC_TAMANY = 2000;

}
