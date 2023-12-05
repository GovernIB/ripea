package es.caib.ripea.core.api.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GrupDto {

    private Long id;
    private String rol;
    private String descripcio;
    
    private Long entitatId;
    
    private boolean relacionat;
    
	private String codi;

	private List<PermisDto> permisos;
    
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

}
