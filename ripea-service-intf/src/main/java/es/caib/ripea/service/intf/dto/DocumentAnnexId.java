package es.caib.ripea.service.intf.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentAnnexId implements Serializable {
	private static final long serialVersionUID = 5529816596655582719L;
	private Long document_enviament_id;
	private Long document_id;
}
