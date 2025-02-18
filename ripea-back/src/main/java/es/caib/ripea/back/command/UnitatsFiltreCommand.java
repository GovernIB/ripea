/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.UnitatsFiltreDto;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Command per al manteniment de permisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsFiltreCommand {

	private String denominacio;
	private String codi;
	private String nivellAdministracio;
	private String comunitat;
	private String provincia;
	private String localitat;
	private boolean unitatArrel;

	public static UnitatsFiltreCommand asCommand(UnitatsFiltreDto dto) {
		UnitatsFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				UnitatsFiltreCommand.class);
		return command;
	}
	public static UnitatsFiltreDto asDto(UnitatsFiltreCommand command) {
		UnitatsFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				UnitatsFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi != null ? codi.trim() : null;
	}
	public String getNivellAdministracio() {
		return nivellAdministracio;
	}
	public void setNivellAdministracio(String nivellAdministracio) {
		this.nivellAdministracio = nivellAdministracio != null ? nivellAdministracio.trim() : null;
	}
	public String getComunitat() {
		return comunitat;
	}
	public void setComunitat(String comunitat) {
		this.comunitat = comunitat != null ? comunitat.trim() : null;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia != null ? provincia.trim() : null;
	}
	public String getLocalitat() {
		return localitat;
	}
	public void setLocalitat(String localitat) {
		this.localitat = localitat != null ? localitat.trim() : null;
	}
	public boolean isUnitatArrel() {
		return unitatArrel;
	}
	public void setUnitatArrel(boolean unitatArrel) {
		this.unitatArrel = unitatArrel;
	}
	public String getDenominacio() {
		return denominacio;
	}
	public void setDenominacio(String denominacio) {
		this.denominacio = denominacio != null ? denominacio.trim() : null;
	}

}
