package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuariAnotacioDto implements Serializable {

	private static final long serialVersionUID = 8606334604679512878L;
	private String codi;
	private TipoUsuario tipusUsuari;
	private Long organId;
	private Long metaExpedientId;
	
	public enum TipoUsuario {
		ADMIN, ADM_ORG, ADM_ORG_COMUN, USER
	}
}