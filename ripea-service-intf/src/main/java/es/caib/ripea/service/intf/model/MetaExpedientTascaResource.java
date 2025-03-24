package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Informació d'una aplicació a monitoritzar.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "codi", "nom" }, descriptionField = "nom")
public class MetaExpedientTascaResource extends BaseAuditableResource<Long> {

    private String codi;
    private String nom;
    private String descripcio;
    private ResourceReference<UsuariResource, String> responsable;
    private boolean activa;
    private Date dataLimit;
    @SuppressWarnings("unused")
    private String dataLimitString;
    private Integer duracio = 10;
    @SuppressWarnings("unused")
    private String duracioFormat;
    private PrioritatEnumDto prioritat = PrioritatEnumDto.B_NORMAL;
    private Long estatIdCrearTasca;
    private String estatNomCrearTasca;
    private String estatColorCrearTasca;
    private Long estatIdFinalitzarTasca;
    private String estatNomFinalitzarTasca;
    private String estatColorFinalitzarTasca;

    private ResourceReference<ExpedientEstatResource, Long> estatCrearTasca;
    private ResourceReference<ExpedientEstatResource, Long> estatFinalitzarTasca;
    private ResourceReference<MetaExpedientResource, Long> metaExpedient;

}
