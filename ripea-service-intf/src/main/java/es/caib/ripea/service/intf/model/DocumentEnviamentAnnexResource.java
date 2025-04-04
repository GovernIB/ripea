package es.caib.ripea.service.intf.model;

import es.caib.ripea.service.intf.base.annotation.ResourceConfig;
import es.caib.ripea.service.intf.base.model.Resource;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.DocumentAnnexId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom"
)
public class DocumentEnviamentAnnexResource implements Resource<DocumentAnnexId> {
	private static final long serialVersionUID = 3430795856567925817L;
	private DocumentAnnexId id;
    private ResourceReference<DocumentEnviamentResource, Long> documentEnviament;
    private ResourceReference<DocumentResource, Long> document;
}
