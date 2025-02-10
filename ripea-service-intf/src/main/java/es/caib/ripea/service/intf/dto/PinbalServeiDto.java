package es.caib.ripea.service.intf.dto;

import es.caib.ripea.service.intf.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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