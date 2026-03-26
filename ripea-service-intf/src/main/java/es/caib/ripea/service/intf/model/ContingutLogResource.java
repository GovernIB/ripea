package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.LogContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.service.intf.dto.LogTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@NoArgsConstructor
public class ContingutLogResource extends BaseAuditableResource<Long> {

    private LogTipusEnumDto tipus;
    private String objecteId;
    private LogObjecteTipusEnumDto objecteTipus;
    private LogTipusEnumDto objecteLogTipus;
    private String param1;
    private String param2;

    private Long contingutId;
    private LogContingutTipusEnumDto contingutTipus;
//    private ResourceReference<ContingutResource, Long> contingut;
//    private ResourceReference<ContingutMovimentResource, Long> contingutMoviment;
//    private ResourceReference<ContingutLogResource, Long> pare;

    @Transient private ContingutMovimentResource moviment;
    @Transient private ContingutLogResource pare;
    @Transient private String mssg;
    @Transient private String tipusString;
    @Transient private String objecteNom;

    public boolean isSecundari() {
        return tipus.equals(LogTipusEnumDto.MODIFICACIO) && objecteId != null;
    }
}