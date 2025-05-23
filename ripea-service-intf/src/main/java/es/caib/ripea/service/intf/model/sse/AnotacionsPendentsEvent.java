package es.caib.ripea.service.intf.model.sse;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AnotacionsPendentsEvent {
	private Map<String, Long> anotacionsPendentsUsuaris; 
}
