/**
 * 
 */
package es.caib.ripea.core.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Informació d'un MetaExpedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@SuppressWarnings("serial")
public class MetaExpedientDto extends MetaNodeAmbMetaDadesDto implements Serializable {

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
	@JsonIgnore
    private List<MetaDocumentDto> metaDocuments;
    private boolean permetMetadocsGenerals;
    private OrganGestorDto organGestor;
    
    private boolean gestioAmbGrupsActiva;
    
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	private String revisioComentari;
	private long numComentaris;
	
	private boolean crearReglaDistribucio;
	private CrearReglaDistribucioEstatEnumDto crearReglaDistribucioEstat;
	private String crearReglaDistribucioError;
	private CrearReglaResponseDto crearReglaResponse;

    private boolean organNoSincronitzat;
    
    private OrganEstatEnumDto organEstat;
    private TipusTransicioEnumDto organTipusTransicio;
    private List<OrganGestorDto> organsNous;
    
    private boolean procedimentComu;
	
    private boolean interessatObligatori;
    
    public String getCodiSiaINom() {
    	if(this.classificacio!=null && !"".equals(classificacio)) {
    		return classificacio + " - " + nom;
    	} else {
    		return nom;
    	}
    }
    
    public String getNomICodiSia() {
    	if(this.classificacio!=null && !"".equals(classificacio)) {
    		return nom + " (" + classificacio +")";
    	} else {
    		return nom;
    	}
    }
	
	public boolean isComu() {
		if (organGestor == null) {
			return true;
		} else {
			return false;
		}
	}
	
    List<ArbreJsonDto> estructuraCarpetes;
    
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
    
//	@Data
//	public class MetaExpedientFiltreDto {
//
//		private String codi;
//		private String nom;
//		private Long organGestorId;
//		private Boolean veureTots = true;
//		private MetaExpedientActiuEnumDto actiu;
//
//	}

}
