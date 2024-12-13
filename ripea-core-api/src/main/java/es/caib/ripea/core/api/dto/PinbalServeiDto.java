package es.caib.ripea.core.api.dto;

import java.util.List;

import es.caib.ripea.core.api.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PinbalServeiDto {

    private Long id;
	private List<PinbalServeiDocPermesEnumDto> pinbalServeiDocsPermesos;
    private String codi;
    private String nom;
    private boolean actiu;
    
    public String getCodiNom() {
    	return codi + " - " + nom;
    }
    
    public String getNomAmbActiu() {
    	if (actiu) {
    		return getCodiNom();
    	} else {
    		return "(INACTIU) " + getCodiNom();
    	}
    }
    
    public String getDocPermesosString() {
    	String docsString = "";
    	if (Utils.isNotEmpty(pinbalServeiDocsPermesos)) {
    		for (PinbalServeiDocPermesEnumDto doc : pinbalServeiDocsPermesos) {
    			docsString += doc + ", ";
			}
    		docsString = docsString.substring(0, docsString.length() - 2); // remove last ,
			
		} 
    	return docsString;
	}
}