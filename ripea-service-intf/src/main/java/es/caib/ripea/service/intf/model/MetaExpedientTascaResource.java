package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Informació d'una aplicació a monitoritzar.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class MetaExpedientTascaResource extends BaseAuditableResource<Long> {

    @NotNull private String codi;
    @NotNull private String nom;
    @NotNull private String descripcio;
    private ResourceReference<UsuariResource, String> responsable;
    private boolean activa;
    private Date dataLimit;
    @SuppressWarnings("unused")
    private String dataLimitString;
    private Integer duracio = 10;
    @SuppressWarnings("unused")
    private String duracioFormat;
    private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;

    private ResourceReference<MetaExpedientEstatResource, Long> estatCrearTasca;
    private ResourceReference<MetaExpedientEstatResource, Long> estatFinalitzarTasca;
    private ResourceReference<MetaExpedientResource, Long> metaExpedient;

}
