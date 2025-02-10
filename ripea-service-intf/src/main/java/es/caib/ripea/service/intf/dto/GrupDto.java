package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class GrupDto {

    private Long id;
    private String codi;
    private String descripcio;
    private Long entitatId;
    private OrganGestorDto organGestor;
    private Long organGestorId;
    private boolean perDefecte;
	//Quant filtres per organ gestor (ets admin d'organ), tendras permisos sobre organ + fills.
	//Els grups que no vagin per organ, es mostrarán deshabilitats.
	private boolean editableUsuari;
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
