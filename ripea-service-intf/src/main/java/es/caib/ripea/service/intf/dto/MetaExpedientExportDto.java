/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Informació d'un MetaExpedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@SuppressWarnings("serial")
public class MetaExpedientExportDto extends MetaNodeAmbMetaDadesDto implements Serializable {

	private TipusProcedimentServeiEnum tipusProcedimentServei;
    private TipusClassificacioEnumDto tipusClassificacio;
    private String classificacio;
    private String serieDocumental;
    private String expressioNumero;
    private boolean notificacioActiva;
    private Long pareId;
    private int expedientEstatsCount;
    private int expedientTasquesCount;
    private int expedientDominisCount;
    private int grupsCount;
    private boolean permetMetadocsGenerals;
    private OrganGestorDto organGestor;    
    private boolean gestioAmbGrupsActiva;
    private boolean permisDirecte;
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	
	private List<MetaDocumentDto> metaDocuments;
	//private List<MetaDadaDto> metaDades; (Es troba al pare MetaNodeAmbMetaDadesDto)
	private Set<ExpedientEstatDto> estats;
    private Set<MetaExpedientTascaDto> tasques;
    private List<GrupDto> grups; //Opcional
    private List<MetaExpedientCarpetaMinDto> carpetes; //Opcional

    private boolean interessatObligatori;
	
	public boolean isComu() {
		if (organGestor == null) {
			return true;
		} else {
			return false;
		}
	}
    
    public int getMetaDocumentsCount() {
        if (metaDocuments == null)
            return 0;
        else
            return metaDocuments.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MetaExpedientExportDto other = (MetaExpedientExportDto) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    

}
