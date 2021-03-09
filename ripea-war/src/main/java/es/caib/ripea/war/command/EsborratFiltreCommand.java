/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.EsborratFiltreDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

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
		this.nom = nom.trim();
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi.trim();
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
