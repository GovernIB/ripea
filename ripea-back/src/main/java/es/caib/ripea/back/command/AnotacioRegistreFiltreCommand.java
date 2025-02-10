/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.AnotacioRegistreFiltreDto;
import es.caib.ripea.service.intf.registre.RegistreProcesEstatEnum;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

/**
 * Command per al filtre del localitzador d'anotacions de registre
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioRegistreFiltreCommand {

	private String unitatOrganitzativa;
	private String bustia;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private RegistreProcesEstatEnum estat;

	public String getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}
	public void setUnitatOrganitzativa(String unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa != null ? unitatOrganitzativa.trim() : null;
	}
	public String getBustia() {
		return bustia;
	}
	public void setBustia(String bustia) {
		this.bustia = bustia != null ? bustia.trim() : null;
	}
	public Date getDataCreacioInici() {
		return dataCreacioInici;
	}
	public void setDataCreacioInici(Date dataCreacioInici) {
		this.dataCreacioInici = dataCreacioInici;
	}
	public Date getDataCreacioFi() {
		return dataCreacioFi;
	}
	public void setDataCreacioFi(Date dataCreacioFi) {
		this.dataCreacioFi = dataCreacioFi;
	}
	public RegistreProcesEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(RegistreProcesEstatEnum estat) {
		this.estat = estat;
	}
	
	public static AnotacioRegistreFiltreCommand asCommand(AnotacioRegistreFiltreDto dto) {
		AnotacioRegistreFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				AnotacioRegistreFiltreCommand.class);
		return command;
	}
	public static AnotacioRegistreFiltreDto asDto(AnotacioRegistreFiltreCommand command) {
		AnotacioRegistreFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				AnotacioRegistreFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
