package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "identificador" }, descriptionField = "identificador")
public class ExpedientPeticioResource extends BaseAuditableResource<Long> {

//    private Long id;
    private String identificador;
    private String clauAcces;
    private Date dataAlta;
    private ExpedientPeticioEstatEnumDto estat;
    private ExpedientPeticioAccioEnumDto accio;
    private String notificaDistError;
    private Date dataActualitzacio;
    private String usuariActualitzacio;
    private String observacions;

    private ResourceReference<RegistreResource, Long> registre;
    private ResourceReference<MetaExpedientResource, Long> metaExpedient;
    private ResourceReference<GrupResource, Long> grup;
    private ResourceReference<ExpedientResource, Long> expedient;
}
