/**
 * 
 */
package es.caib.ripea.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * Classe del model de dades que representa una notificació d'un document
 * a un dels interessats d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "document_enviament_dis")
@EntityListeners(AuditingEntityListener.class)
public class DispositiuEnviamentEntity  extends RipeaAuditable<Long> {

	@Column(name = "codi")
	private String codi;
	@Column(name = "codi_aplicacio")
	private String codiAplicacio;
	@Column(name = "codi_usuari")
	private String codiUsuari;
	@Column(name = "descripcio")
	private String descripcio;
	@Column(name = "locale")
	private String local;
	@Column(name = "estat")
	private String estat;
	@Column(name = "token")
	private String token;
	@Column(name = "identificador")
	private String identificador;
	@Column(name = "tipus")
	private String tipus;
	@Column(name = "email_usuari")
	private String emailUsuari;
	@Column(name = "identificador_nac")
	private String identificadorNacional;
	
	public String getCodi() {
		return codi;
	}
	public String getCodiAplicacio() {
		return codiAplicacio;
	}
	public String getCodiUsuari() {
		return codiUsuari;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public String getLocal() {
		return local;
	}
	public String getEstat() {
		return estat;
	}
	public String getToken() {
		return token;
	}
	public String getIdentificador() {
		return identificador;
	}
	public String getTipus() {
		return tipus;
	}
	public String getEmailUsuari() {
		return emailUsuari;
	}
	public String getIdentificadorNacional() {
		return identificadorNacional;
	}
	
	public static Builder getBuilder(
			String codi,
			String codiAplicacio,
			String descripcio,
			String local,
			String estat,
			String token,
			String identificador,
			String tipus,
			String emailUsuari,
			String codiUsuari,
			String identificadorNacional) {
		return new Builder(
				codi,
				codiAplicacio,
				descripcio,
				local,
				estat,
				token,
				identificador,
				tipus,
				emailUsuari,
				codiUsuari,
				identificadorNacional);
	}

	public static class Builder {
		DispositiuEnviamentEntity built;
		Builder(
				String codi,
				String codiAplicacio,
				String descripcio,
				String local,
				String estat,
				String token,
				String identificador,
				String tipus,
				String emailUsuari,
				String codiUsuari,
				String identificadorNacional) {
			built = new DispositiuEnviamentEntity();
			built.codi = codi;
			built.codiAplicacio = codiAplicacio;
			built.descripcio = descripcio;
			built.local = local;
			built.estat = estat;
			built.token = token;
			built.identificador = identificador;
			built.tipus = tipus;
			built.emailUsuari = emailUsuari;
			built.codiUsuari = codiUsuari;
			built.identificadorNacional = identificadorNacional;
			
		}
		public DispositiuEnviamentEntity build() {
			return built;
		}
	}
	
	private static final long serialVersionUID = -8233056997778309655L;	
}
