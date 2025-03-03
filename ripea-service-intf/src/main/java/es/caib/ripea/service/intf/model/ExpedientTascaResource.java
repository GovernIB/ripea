package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.annotation.ResourceConfigArtifact;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceArtifactType;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "id, titol" },
        descriptionField = "titol",
        artifacts = {
                @ResourceConfigArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ExpedientTascaResource.PERSPECTIVE_RESPONSABLES_CODE)
        })
public class ExpedientTascaResource extends BaseAuditableResource<Long> {

    public static final String PERSPECTIVE_RESPONSABLES_CODE = "RESPONSABLES_RESUM";

    private Long id;
    private Date dataInici;
    private Date dataFi;
    private TascaEstatEnumDto estat;
//    private Long metaExpedientTascaId;
    private String motiuRebuig;
    private Date dataLimit;
//    @SuppressWarnings("unused")
//    private String dataLimitString;
    private boolean shouldNotifyAboutDeadline;
//    @SuppressWarnings("unused")
//    private boolean dataLimitExpirada;
    private String comentari;
    private long numComentaris;

    private boolean usuariActualResponsable;
    private boolean usuariActualObservador;
    private boolean usuariActualDelegat;

    private String titol;
    private String observacions;
    private Integer duracio;
//    @SuppressWarnings("unused")
//    private String duracioFormat;
    private PrioritatEnumDto prioritat;
    private String responsablesStr;

    private ResourceReference<ExpedientResource, Long> expedient;
    private ResourceReference<MetaExpedientTascaResource, Long> metaExpedientTasca;
    private ResourceReference<UsuariResource, String> responsableActual;
    private ResourceReference<UsuariResource, String> delegat;

}
