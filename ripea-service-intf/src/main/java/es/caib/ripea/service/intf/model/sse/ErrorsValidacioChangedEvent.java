package es.caib.ripea.service.intf.model.sse;

import java.io.Serializable;
import java.util.List;

import es.caib.ripea.service.intf.dto.ValidacioErrorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ErrorsValidacioChangedEvent implements Serializable {
	private static final long serialVersionUID = -4396198585239253576L;
	private final Long expedientId;
	private final List<ValidacioErrorDto> errorsValidacio;
}
