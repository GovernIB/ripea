package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.BaseAuditableResource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.MetaDadaTipusEnumDto;
import es.caib.ripea.service.intf.dto.MultiplicitatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(quickFilterFields = { "metaDada", "ordre", "valor" }, descriptionField = "metaDadaOrdreValor")
public class DadaResource extends BaseAuditableResource<Long> {

    @NotNull
    protected Object valor;
    protected Integer ordre;

    @NotNull
    private ResourceReference<MetaDadaResource, Long> metaDada;
    @NotNull
    protected ResourceReference<NodeResource, Long> node;

    public String getMetaDadaOrdreValor(){
        return metaDada.getDescription() + " - Nº" + ordre + " ( " + valor + " )";
    }
}