/**
 * 
 */
package es.caib.ripea.back.command;

import es.caib.ripea.back.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.EsborratFiltreDto;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

/**
 * Command per al filtre d'elements esborrats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EsborratFiltreCommand {

	private String nom;
	private String usuariCodi;
	private Date dataInici;
	private Date dataFi;



	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi != null ? usuariCodi.trim() : null;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	public static EsborratFiltreCommand asCommand(EsborratFiltreDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				EsborratFiltreCommand.class);
	}
	public static EsborratFiltreDto asDto(EsborratFiltreCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				EsborratFiltreDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
