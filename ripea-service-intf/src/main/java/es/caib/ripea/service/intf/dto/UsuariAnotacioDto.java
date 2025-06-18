package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UsuariAnotacioDto implements Serializable {

	private static final long serialVersionUID = 8606334604679512878L;
	private String codi;
	private String rolActual;
	private TipoUsuario tipusUsuari;
	private Long organId;
	private Long metaExpedientId;
	private Long entitatId;
	
	public UsuariAnotacioDto(String codi, String rolActual, Long organId, Long entitatId) {
		super();
		this.codi = codi;
		this.rolActual = rolActual;
		this.organId = organId;
		this.entitatId = entitatId;
	}
	
	public UsuariAnotacioDto(String codi, TipoUsuario tipusUsuari, Long organId, Long metaExpedientId) {
		super();
		this.codi = codi;
		this.tipusUsuari = tipusUsuari;
		this.organId = organId;
		this.metaExpedientId = metaExpedientId;
	}
	
	public enum TipoUsuario {
		ADMIN, ADM_ORG, ADM_ORG_COMUN, USER
	}
	
	public String getRolActual() {
		if (this.rolActual!=null) return this.rolActual;
		if (this.tipusUsuari!=null) {
			switch (this.tipusUsuari) {
				case ADMIN: return "IPA_ADMIN";
				case ADM_ORG: return "IPA_ORGAN_ADMIN";
				case ADM_ORG_COMUN: return "IPA_ORGAN_ADMIN";
				default: return "IPA_USER";
			}
		}
		return "IPA_USER";
	}
}