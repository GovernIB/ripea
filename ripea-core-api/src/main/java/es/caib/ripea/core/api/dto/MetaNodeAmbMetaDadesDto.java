/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.List;

/**
 * Informació d'un meta-node amb meta-dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class MetaNodeAmbMetaDadesDto extends MetaNodeDto {

	private List<MetaDadaDto> metaDades;

	public List<MetaDadaDto> getMetaDades() {
		return metaDades;
	}
	public void setMetaDades(List<MetaDadaDto> metaDades) {
		this.metaDades = metaDades;
	}

	public int getMetaDadesCount() {
		if  (metaDades == null)
			return 0;
		else
			return metaDades.size();
	}

	private static final long serialVersionUID = -139254994389509932L;

}
