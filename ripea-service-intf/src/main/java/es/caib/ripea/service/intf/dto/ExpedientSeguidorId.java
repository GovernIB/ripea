package es.caib.ripea.service.intf.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Embeddable
@Data
@NoArgsConstructor
public class ExpedientSeguidorId implements Serializable {
	private static final long serialVersionUID = -4255356809877307406L;
	private Long expedient_id;
    private String seguidor_codi;
}