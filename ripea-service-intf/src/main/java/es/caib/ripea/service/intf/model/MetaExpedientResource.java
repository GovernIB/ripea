package es.caib.ripea.service.intf.model;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.dto.MetaExpedientAmbitEnumDto;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Sort;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.CrearReglaDistribucioEstatEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@ResourceConfig(
		quickFilterFields = { "codi", "nom" },
		descriptionField = "nomClassificacio",
        artifacts = {
				@ResourceConfigArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = MetaExpedientResource.PERSPECTIVE_AUDIT_CODE),        		
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = MetaExpedientResource.FILTER_REVISIO_CODE,
                        formClass = MetaExpedientResource.GestioRevisioFormFilter.class),
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = MetaExpedientResource.FILTER_GESTIO_CODE,
                        formClass = MetaExpedientResource.GestioRevisioFormFilter.class),
        },
		defaultSortFields = { @ResourceConfig.ResourceSort(field = "nom", direction = Sort.Direction.ASC) }
)
public class MetaExpedientResource extends MetaNodeResource {

    public static final String FILTER_REVISIO_CODE = "FILTER_REVISIO";
    public static final String FILTER_GESTIO_CODE = "FILTER_GESTIO";
    public static final String PERSPECTIVE_AUDIT_CODE = "AUDITORIA";

	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@Size(max = 4000)
	private String descripcio;
	private boolean actiu = true;
	@NotNull
	@Size(max = 64)
	private String codiPropi;
	@NotNull
	private TipusClassificacioEnumDto tipusClassificacio;
	@NotNull
	@Size(max = 30)
	private String classificacio;
	@NotNull
	@Size(max = 30)
	private String serieDocumental;
	@Size(max = 100)
	private String expressioNumero;
	private boolean notificacioActiva;
	private boolean permetMetadocsGenerals;
	private boolean gestioAmbGrupsActiva;
	private boolean permisDirecte = false;
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	@Size(max = 1024)
	private String revisioComentari;
	private CrearReglaDistribucioEstatEnumDto crearReglaDistribucioEstat;
	@Size(max = 1024)
	private String crearReglaDistribucioError;
	private boolean organNoSincronitzat;
	private boolean interessatObligatori;

	private ResourceReference<EntitatResource, Long> entitat;
	private ResourceReference<MetaExpedientResource, Long> pare;
	private ResourceReference<OrganGestorResource, Long> organGestor;
	private ResourceReference<GrupResource, Long> grupPerDefecte;

    @Transient int numComentaris;
    @Transient boolean procedimentComu;
	@Transient private List<ResourceReference<ExpedientEstatResource, Long>> estats;
	@Transient private List<ResourceReference<MetaExpedientOrganGestorResource, Long>> metaExpedientOrganGestors;

    public boolean isComu() {
        if (organGestor == null) {
            return true;
        } else {
            return false;
        }
    }

    public String getNomClassificacio() {
        return nom + " (" + classificacio +")";
    }

    @Getter
    @Setter
    public static class GestioRevisioFormFilter implements Serializable {
        private String codi;
        private String classificacio;
        private String nom;
        private MetaExpedientRevisioEstatEnumDto revisioEstat;
        private ResourceReference<OrganGestorResource, Long> organGestor;

        private Boolean actiu;
        private MetaExpedientAmbitEnumDto ambit;
        private boolean permisDirecte;
    }
    
    private static final long serialVersionUID = -7526532893601431955L;
}