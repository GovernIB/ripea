/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Informació d'un MetaExpedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@SuppressWarnings("serial")
public class MetaExpedientDto extends MetaNodeAmbMetaDadesDto implements Serializable {

    private String classificacioSia;
    private String serieDocumental;
    private String expressioNumero;
    private boolean notificacioActiva;
    private Long pareId;
    private int expedientEstatsCount;
    private int expedientTasquesCount;
    private int expedientDominisCount;
    private List<MetaDocumentDto> metaDocuments;
    private boolean permetMetadocsGenerals;
    private OrganGestorDto organGestor;

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
        MetaExpedientDto other = (MetaExpedientDto) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
