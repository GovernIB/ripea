/**
 * 
 */
package es.caib.ripea.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.UnitatsFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

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
		this.codi = codi.trim();
	}
	public String getNivellAdministracio() {
		return nivellAdministracio;
	}
	public void setNivellAdministracio(String nivellAdministracio) {
		this.nivellAdministracio = nivellAdministracio.trim();
	}
	public String getComunitat() {
		return comunitat;
	}
	public void setComunitat(String comunitat) {
		this.comunitat = comunitat.trim();
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia.trim();
	}
	public String getLocalitat() {
		return localitat;
	}
	public void setLocalitat(String localitat) {
		this.localitat = localitat.trim();
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
		this.denominacio = denominacio.trim();
	}

}
