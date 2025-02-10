package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.MetaExpedientActiuEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientAmbitEnumDto;
import es.caib.ripea.service.intf.dto.MetaExpedientFiltreDto;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import lombok.Getter;

@Getter
public class MetaExpedientFiltreCommand {

	private String codi;
	private String nom;
	private String classificacio;
	private Long organGestorId;
	private MetaExpedientActiuEnumDto actiu;
	private MetaExpedientAmbitEnumDto ambit;
	private MetaExpedientRevisioEstatEnumDto revisioEstat;
	

	public MetaExpedientFiltreDto asDto() {
		return ConversioTipusHelper.convertir(
				this,
				MetaExpedientFiltreDto.class);
	}

	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public void setClassificacio(String classificacio) {
		this.classificacio = classificacio != null ? classificacio.trim() : null;
	}
	public void setOrganGestorId(Long organGestorId) {
		this.organGestorId = organGestorId;
	}
	public void setActiu(MetaExpedientActiuEnumDto actiu) {
		this.actiu = actiu;
	}
	public void setAmbit(MetaExpedientAmbitEnumDto ambit) {
		this.ambit = ambit;
	}
	public void setRevisioEstat(MetaExpedientRevisioEstatEnumDto revisioEstat) {
		this.revisioEstat = revisioEstat;
	}
	
}
