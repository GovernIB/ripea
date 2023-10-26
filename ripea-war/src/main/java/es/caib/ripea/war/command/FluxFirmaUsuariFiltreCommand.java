package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.FluxFirmaUsuariFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
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
