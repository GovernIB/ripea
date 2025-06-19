package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TasquesPendentsEvent implements Serializable {
	private static final long serialVersionUID = 3036674169368032751L;
	private Map<String, Long> tasquesPendentsUsuaris; 
}
