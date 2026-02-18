package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;

import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CreacioFluxFinalitzatEvent implements Serializable {
	private static final long serialVersionUID = -7159538246036007850L;
	private Long expedientId;
	private Long metaDocumentId;
	private String entitatCodi;
	private String usuariCodi;
	private PortafirmesFluxRespostaDto fluxCreat;
	
	public CreacioFluxFinalitzatEvent(Long expedientId, PortafirmesFluxRespostaDto fluxCreat) {
		super();
		this.expedientId = expedientId;
		this.fluxCreat = fluxCreat;
		this.metaDocumentId = null;
		this.entitatCodi = null;
		this.usuariCodi = null;
	}
}
