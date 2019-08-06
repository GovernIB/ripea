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

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.ViaFirmaCallbackEstatEnumDto;

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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vf_viafirma_dispositiu")
	@ForeignKey(name = "ipa_document_enviament_dis_fk")
	protected DispositiuEnviamentEntity dispositiuEnviament;
	
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
	
	public void updateMessageCode(String messageCode) {
		this.messageCode = messageCode;
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
			DispositiuEnviamentEntity dispositiuEnviament,
			boolean lecturaObligatoria,
			ExpedientEntity expedient,
			DocumentEntity document) {
		return new Builder(
				estat,
				codiUsuari,
				contrasenyaUsuariViaFirma,
				titol,
				descripcio,
				codiDispositiu,
				dispositiuEnviament,
				lecturaObligatoria,
				expedient,
				document);
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
				DispositiuEnviamentEntity dispositiuEnviament,
				boolean lecturaObligatoria,
				ExpedientEntity expedient,
				DocumentEntity document) {
			built = new DocumentViaFirmaEntity();
			built.inicialitzar();
			built.assumpte = titol;
			built.estat = estat;
			built.codiUsuari = codiUsuari;
			built.contrasenyaUsuariViaFirma = contrasenyaUsuariViaFirma;
			built.titol = titol;
			built.descripcio = descripcio;
			built.codiDispositiu = codiDispositiu;
			built.dispositiuEnviament = dispositiuEnviament;
			built.lecturaObligatoria = lecturaObligatoria;
			built.expedient = expedient;
			built.document = document;
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
