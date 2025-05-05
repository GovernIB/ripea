package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ContingutMovimentResource extends BaseAuditableResource<Long> {

//    private Long origenId;
//    private Long destiId;
    private String comentari;

    private ResourceReference<ContingutResource, Long> contingut;
    private ResourceReference<ContingutResource, Long> origen;
    private ResourceReference<ContingutResource, Long> desti;
    private ResourceReference<UsuariResource, String> remitent;
}