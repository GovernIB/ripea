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
    private MetaDocumentPinbalServeiEnumDto codi;
    private String nom;

    
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
