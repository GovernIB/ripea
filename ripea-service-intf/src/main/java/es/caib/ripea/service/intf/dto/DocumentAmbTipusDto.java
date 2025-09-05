package es.caib.ripea.service.intf.dto;

import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DocumentAmbTipusDto {
	private FileReference fitxer;
	private ResourceReference<MetaDocumentResource, Long> tipusDocument;
}