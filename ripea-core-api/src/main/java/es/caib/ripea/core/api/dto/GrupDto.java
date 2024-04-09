package es.caib.ripea.core.api.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GrupDto {

    private Long id;
    private String codi;
    private String descripcio;
    
    private Long entitatId;
    
    private OrganGestorDto organGestor;
    
    private Long organGestorId;
    
    private boolean perDefecte;

	private List<PermisDto> permisos;
    
	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	public String getCodiDescripcioIsEntitat() {
		String result = "";
		if (organGestor == null) {
			result = "isEntitat";
		}
		result += codi + " - " + descripcio;
		return result;
	}
}
