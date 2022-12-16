/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EqualsAndHashCode
@Getter
@Setter
public class OrganGestorDto extends AuditoriaDto {
    private Long id;
    private String codi;
    private String nom;
    private String entitatId;
    private String pareCodi;
    private Long pareId;
    private String pareNom;
    private String entitatNom;
    private List<PermisDto> permisos;
    private boolean gestioDirect;
    private OrganEstatEnumDto estat;
    private TipusTransicioEnumDto tipusTransicio;
    
    private String cif;
    
    public int getPermisosCount() {
        if (permisos == null)
            return 0;
        else
            return permisos.size();
    }
    
    public String getCodiINom() {
    	return codi + " - " + nom;
    }
    
    public String getNomICodi() {
		return nom + " (" + codi + ")";
    }
    
    public String getPareCodiNom() {
    	if (pareCodi == null) 
    		return null;
    	return pareCodi + " - " + pareNom;
    }
    
	
	public boolean isObsolet() {
		return estat != OrganEstatEnumDto.V;
	}
       
}
