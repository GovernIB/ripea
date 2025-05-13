package es.caib.ripea.service.intf.model.sse;

import es.caib.ripea.service.intf.dto.AvisDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
public class AvisosActiusEvent {

    private final List<AvisDto> avisosUsuari;
    private final Map<String, List<AvisDto>> avisosAdmin;

}
