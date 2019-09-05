/**
 * 
 */
package es.caib.ripea.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.audit.RipeaAuditable;

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

}
