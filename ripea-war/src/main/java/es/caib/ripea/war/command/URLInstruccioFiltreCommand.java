package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.URLInstruccioFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;

@Getter
public class URLInstruccioFiltreCommand {

	private String codi;
	private String nom;
	private String descripcio;
	

	public URLInstruccioFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				URLInstruccioFiltreDto.class);
	}

	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio != null ? descripcio.trim() : null;
	}
	
}