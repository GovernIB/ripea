package es.caib.ripea.service.intf.base.util;

import java.io.Serializable;
import java.util.List;
import es.caib.ripea.service.intf.dto.AvisDto;
import lombok.Data;

@Data
public class SyncStoredSessionData implements Serializable {
	private static final long serialVersionUID = 8838499584224320097L;
	private List<AvisDto> avisos;
}