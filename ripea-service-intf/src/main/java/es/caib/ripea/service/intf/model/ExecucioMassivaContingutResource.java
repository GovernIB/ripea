package es.caib.ripea.service.intf.model;

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
public class ExecucioMassivaContingutResource extends BaseAuditableResource<Long> {

    private Date dataInici;
    private Date dataFi;
    private ExecucioMassivaEstatDto estat;
    private String error;
    private int ordre;
    private Long elementId;
    private String elementNom;
    private ElementTipusEnumDto elementTipus;
    private Throwable throwable;

    private ResourceReference<ExecucioMassivaResource, Long> execucioMassiva;
}