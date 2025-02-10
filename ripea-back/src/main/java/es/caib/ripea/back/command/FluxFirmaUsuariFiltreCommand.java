package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.FluxFirmaUsuariFiltreDto;
import lombok.Getter;

@Getter
public class FluxFirmaUsuariFiltreCommand {

	private String nom;
	private String descripcio;
	

	public FluxFirmaUsuariFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				FluxFirmaUsuariFiltreDto.class);
	}

	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio != null ? descripcio.trim() : null;
	}
	
}
