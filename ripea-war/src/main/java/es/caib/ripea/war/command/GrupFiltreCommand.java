package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.GrupFiltreDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;

@Getter
public class GrupFiltreCommand {

	private String codi;
	private String descripcio;
	private Long organGestorId;	
	private Long organGestorAscendentId;

	public GrupFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				GrupFiltreDto.class);
	}

	public void setCodi(String codi) {
		this.codi = Utils.trim(codi);
	}
	public void setOrganGestorId(Long organGestorId) {
		this.organGestorId = organGestorId;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public void setOrganGestorAscendentId(Long organGestorAscendentId) {
		this.organGestorAscendentId = organGestorAscendentId;
	}

	
}
