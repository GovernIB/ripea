/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.List;

import es.caib.ripea.core.api.utils.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'una dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class OrganGestorDto extends AuditoriaDto {
    private Long id;
    private String codi;
    private String nom;
    private String entitatId;
    private String entitatCodi;
    private String pareCodi;
    private Long pareId;
    private String pareNom;
    private String entitatNom;
    private List<PermisDto> permisos;
    private boolean gestioDirect;
    private OrganEstatEnumDto estat;
    private String estatMessage;
    private TipusTransicioEnumDto tipusTransicio;
    private String cif;
    private boolean utilitzarCifPinbal;
    private boolean permetreEnviamentPostal;
    private boolean permetreEnviamentPostalDescendents;

    public int getPermisosCount() {
        if (permisos == null)
            return 0;
        else
            return permisos.size();
    }
    
    public String getCodiINom() {
    	return codi + " - " + nom;
    }

    public String getCodiAmbEstatINom() {
    	if (OrganEstatEnumDto.E.equals(this.estat) || OrganEstatEnumDto.A.equals(this.estat)) {
        	String txtObsolet = "";
        	if (Utils.isNotEmpty(this.estatMessage)) {
        		txtObsolet = "title='"+this.estatMessage+"'";
        	}
    		return codi + " <span class='fa fa-warning text-danger' "+txtObsolet+" style='margin-top: 3px;'></span> - " + nom;
    	} else {
    		return codi + " - " + nom;
    	}
    }
    
    public String getNomICodi() {
		return nom + " (" + codi + ")";
    }
    
    public String getPareCodiNom() {
    	if (pareCodi == null) 
    		return null;
    	return pareCodi + " - " + pareNom;
    }
    
    public String getCodiINomIEntitat() {
		return codi + " - " + nom + " (" + entitatCodi + ")";
    }
	
	public boolean isObsolet() {
		return estat != OrganEstatEnumDto.V;
	}
}