package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AnotacionsPendentsEvent implements Serializable {
	private static final long serialVersionUID = -1446325746388927516L;
	private Map<String, Long> anotacionsPendentsUsuaris; 
}
