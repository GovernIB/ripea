package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "id, titol" }, descriptionField = "titol")
public class ExpedientTascaResource extends BaseAuditableResource<Long> {

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
