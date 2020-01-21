/**
 * 
 */
package es.caib.ripea.core.entity;

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

import es.caib.ripea.core.audit.RipeaAuditable;
import es.caib.ripea.plugin.notificacio.EnviamentEstat;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table( name = "ipa_document_enviament_inter")
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
				enviamentDatatEstat.equals("ADRESA_INCORRECTA ") || 
				enviamentDatatEstat.equals("ERROR_ENTREGA ") || 
				enviamentDatatEstat.equals("EXPIRADA ") || 
				enviamentDatatEstat.equals("EXTRAVIADA ") || 
				enviamentDatatEstat.equals("MORT ") || 
				enviamentDatatEstat.equals("LLEGIDA ") || 
				enviamentDatatEstat.equals("NOTIFICADA ") || 
				enviamentDatatEstat.equals("REBUTJADA"))) {
			return true;
		} else {
			return false;
		}
	}
	
	
	
	public void updateEnviamentEstat(
			EnviamentEstat enviamentDatatEstat,
			Date enviamentDatatData,
			String enviamentDatatOrigen,
			Date enviamentCertificacioData,
			String enviamentCertificacioOrigen,
			Boolean error,
			String errorDescripcio) {
		this.enviamentDatatEstat = enviamentDatatEstat.name();
		this.enviamentDatatData = enviamentDatatData;
		this.enviamentDatatOrigen = enviamentDatatOrigen;
		this.enviamentCertificacioData = enviamentCertificacioData;
		this.enviamentCertificacioOrigen = enviamentCertificacioOrigen;

		this.error = error;
		this.errorDescripcio = errorDescripcio;
//		switch (enviamentDatatEstat) {
//		case LLEGIDA:
//		case NOTIFICADA:
//			updateProcessat(true, enviamentDatatData);
//			break;
//		case EXPIRADA:
//		case REBUTJADA:
//			updateProcessat(false, enviamentDatatData);
//			break;
//		case NOTIB_ENVIADA:
//			updateEnviat(enviamentDatatData);
//			break;
//		default:
//			break;
//		}
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


	private static final int ERROR_DESC_TAMANY = 2000;

}
