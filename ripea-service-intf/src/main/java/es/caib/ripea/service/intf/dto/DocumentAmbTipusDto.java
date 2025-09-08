package es.caib.ripea.service.intf.dto;

import es.caib.ripea.service.intf.base.model.FileReference;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DocumentAmbTipusDto {
	private FileReference fitxer;
	private Long tipusDocument;
}