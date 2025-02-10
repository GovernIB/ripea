/**
 * 
 */
package es.caib.ripea.core.persistence.entity;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.service.intf.dto.ViaFirmaCallbackEstatEnumDto;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * Classe del model de dades que representa un enviament d'una versió
 * d'un document al portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class DocumentViaFirmaEntity extends DocumentEnviamentEntity {

	@Column(name = "vf_codi_usuari", length = 64)
	String codiUsuari;
	@Column(name = "vf_contrasenya_usuari", length = 64)
	String contrasenyaUsuariViaFirma;
	@Column(name = "vf_titol", length = 256)
	String titol;
	@Column(name = "vf_descripcio", length = 256)
	String descripcio;
	@Column(name = "vf_codi_dispositiu", length = 64)
	String codiDispositiu;
	@Column(name = "vf_lectura_obligatoria")
	boolean lecturaObligatoria;
	@Column(name = "vf_message_code", length = 64, unique = true)
	private String messageCode;
	@Column(name = "vf_callback_estat")
	private ViaFirmaCallbackEstatEnumDto callbackEstat;
	
	@Column(name = "vf_signant_nif")
	private String signantNif;
	@Column(name = "vf_signant_nom")
	private String signantNom;
	@Column(name = "vf_observacions")
	private String observacions;
	
	@Column(name = "vf_validate_code_enabled")
	private Boolean validateCodeEnabled;
	@Column(name = "vf_validate_code")
	private String validateCode;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vf_viafirma_dispositiu")
	@ForeignKey(name = BaseConfig.DB_PREFIX + "document_enviament_dis_fk")
	protected DispositiuEnviamentEntity dispositiuEnviament;
	
	@Column(name = "vf_rebre_correu")
	private Boolean rebreCorreu;
	
	@Column(name = "firma_parcial")
	private Boolean firmaParcial;
	
	public String getCodiUsuari() {
		return codiUsuari;
	}
	public String getContrasenyaUsuariViaFirma() {
		return contrasenyaUsuariViaFirma;
	}
	public String getTitol() {
		return titol;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public String getCodiDispositiu() {
		return codiDispositiu;
	}
	public boolean isLecturaObligatoria() {
		return lecturaObligatoria;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public ViaFirmaCallbackEstatEnumDto getCallbackEstat() {
		return callbackEstat;
	}
	public DispositiuEnviamentEntity getDispositiuEnviament() {
		return dispositiuEnviament;
	}
	public String getSignantNif() {
		return signantNif;
	}
	public String getSignantNom() {
		return signantNom;
	}
	public String getObservacions() {
		return observacions;
	}
	public void updateMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public Boolean isFirmaParcial() {
		return firmaParcial != null ? firmaParcial : false;
	}
	public Boolean isValidateCodeEnabled() {
		return validateCodeEnabled != null ? validateCodeEnabled : false;
	}
	public String getValidateCode() {
		return validateCode;
	}
	public Boolean isRebreCorreu() {
		return rebreCorreu != null ? rebreCorreu : false;
	}
	
	public void updateEnviat(
			Date enviatData,
			String messageCode) {
		super.updateEnviat(enviatData);
		this.messageCode = messageCode;
	}

	public void updateCallbackEstat(
			ViaFirmaCallbackEstatEnumDto callbackEstat) {
		this.callbackEstat = callbackEstat;
	}

	public static Builder getBuilder(
			DocumentEnviamentEstatEnumDto estat,
			String codiUsuari,
			String contrasenyaUsuariViaFirma,
			String titol,
			String descripcio,
			String codiDispositiu,
			String signantNif,
			String signantNom,
			String observacions,
			DispositiuEnviamentEntity dispositiuEnviament,
			boolean lecturaObligatoria,
			ExpedientEntity expedient,
			DocumentEntity document,
			boolean firmaParcial,
			boolean validateCodeEnabled,
			String validateCode,
			boolean rebreCorreu) {
		return new Builder(
				estat,
				codiUsuari,
				contrasenyaUsuariViaFirma,
				titol,
				descripcio,
				codiDispositiu,
				signantNif,
				signantNom,
				observacions,
				dispositiuEnviament,
				lecturaObligatoria,
				expedient,
				document,
				firmaParcial,
				validateCodeEnabled,
				validateCode,
				rebreCorreu);
	}

	public static class Builder {
		DocumentViaFirmaEntity built;
		Builder(
				DocumentEnviamentEstatEnumDto estat,
				String codiUsuari,
				String contrasenyaUsuariViaFirma,
				String titol,
				String descripcio,
				String codiDispositiu,
				String signantNif,
				String signantNom,
				String observacions,
				DispositiuEnviamentEntity dispositiuEnviament,
				boolean lecturaObligatoria,
				ExpedientEntity expedient,
				DocumentEntity document,
				boolean firmaParcial,
				boolean validateCodeEnabled,
				String validateCode,
				boolean rebreCorreu) {
			built = new DocumentViaFirmaEntity();
			built.inicialitzar();
			built.assumpte = titol;
			built.estat = estat;
			built.codiUsuari = codiUsuari;
			built.contrasenyaUsuariViaFirma = contrasenyaUsuariViaFirma;
			built.titol = titol;
			built.descripcio = descripcio;
			built.codiDispositiu = codiDispositiu;
			built.signantNif = signantNif;
			built.signantNom = signantNom;
			built.observacions = observacions;
			built.dispositiuEnviament = dispositiuEnviament;
			built.lecturaObligatoria = lecturaObligatoria;
			built.expedient = expedient;
			built.document = document;
			built.firmaParcial = firmaParcial;
			built.validateCodeEnabled = validateCodeEnabled;
			built.validateCode = validateCode;
			built.rebreCorreu = rebreCorreu;
		}
		public Builder observacions(String observacions) {
			built.observacions = observacions;
			return this;
		}
		public DocumentViaFirmaEntity build() {
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
		result = prime * result + ((codiUsuari == null) ? 0 : codiUsuari.hashCode());
		result = prime * result + ((titol == null) ? 0 : titol.hashCode());
		result = prime * result + ((descripcio == null) ? 0 : descripcio.hashCode());
		result = prime * result + ((codiDispositiu == null) ? 0 : codiDispositiu.hashCode());
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
		DocumentViaFirmaEntity other = (DocumentViaFirmaEntity) obj;
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
		if (codiUsuari == null) {
			if (other.codiUsuari != null)
				return false;
		} else if (!codiUsuari.equals(other.codiUsuari))
			return false;
		if (titol == null) {
			if (other.titol != null)
				return false;
		} else if (!titol.equals(other.titol))
			return false;
		if (descripcio == null) {
			if (other.descripcio != null)
				return false;
		} else if (!descripcio.equals(other.descripcio))
			return false;
		if (codiDispositiu != other.codiDispositiu)
			return false;
		return true;
	}

	private static final long serialVersionUID = -4663407359007476544L;

}
