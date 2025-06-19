package es.caib.ripea.service.intf.model.sse;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import es.caib.ripea.service.intf.dto.AvisDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AvisosActiusEvent implements Serializable {
	private static final long serialVersionUID = 5850266337948562607L;
	private final List<AvisDto> avisosUsuari;
    private final Map<String, List<AvisDto>> avisosAdmin;
}