package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.dto.DocumentPublicacioTipusEnumDto;
import es.caib.ripea.service.intf.resourcevalidation.InteressatValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom"
)
public class DocumentPublicacioResource extends DocumentEnviamentResource {

    @NotNull
    private DocumentPublicacioTipusEnumDto tipus;
    private Date dataPublicacio;

}