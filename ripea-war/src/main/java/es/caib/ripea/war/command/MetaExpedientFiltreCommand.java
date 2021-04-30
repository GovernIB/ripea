package es.caib.ripea.war.command;

import es.caib.ripea.core.api.dto.MetaExpedientActiuEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientAmbitEnumDto;
import es.caib.ripea.core.api.dto.MetaExpedientFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import lombok.Getter;

@Getter
public class MetaExpedientFiltreCommand {

	private String codi;
	private String nom;
	private String classificacioSia;
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
	public void setClassificacioSia(String classificacioSia) {
		this.classificacioSia = classificacioSia != null ? classificacioSia.trim() : null;
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
