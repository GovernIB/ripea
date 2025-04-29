/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un contingut emmagatzemada a l'arxiu.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ArxiuDetallDto extends ArxiuContingutDto {

	private String eniVersio;
	private String eniIdentificador;
	private NtiOrigenEnumDto eniOrigen;
	private Date eniDataObertura;
	private String eniClassificacio;
	private ExpedientEstatEnumDto eniEstat;
	private List<String> eniOrgans;
	private List<String> eniInteressats;
	private Date eniDataCaptura;
	private DocumentNtiEstadoElaboracionEnumDto eniEstatElaboracio;
	private String eniTipusDocumental;
	private String eniTipusDocumentalAddicional;
	private String eniFormat;
	private String eniDocumentOrigenId;
	private String serieDocumental;

	private Map<String, Object> metadadesAddicionals;

	private String contingutTipusMime;
	private String contingutArxiuNom;
	
	private List<ArxiuFirmaDto> firmes;
	private List<ArxiuContingutDto> fills;

	private ArxiuEstatEnumDto arxiuEstat;

	public Map<String, Object> getMetadadesAddicionals(Map<String, Object> originalMap) {
        Map<String, Object> updatedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            // Reemplazar ':' por '_' porque sino no encuentra la clave en el fichero de traducciones.
            String newKey = entry.getKey().replace(":", "_");
            updatedMap.put(newKey, entry.getValue());
        }
        return updatedMap;
    } 
	
	private static final long serialVersionUID = -2124829280908976623L;
}