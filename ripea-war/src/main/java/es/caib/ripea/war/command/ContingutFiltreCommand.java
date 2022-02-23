/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ContingutFiltreDto;
import es.caib.ripea.core.api.dto.ContingutTipusEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutFiltreCommand {

	public enum ContenidorFiltreOpcionsEsborratEnum {
		NOMES_NO_ESBORRATS,
		NOMES_ESBORRATS,
		ESBORRATS_I_NO_ESBORRATS
	};

	private String nom;
	private String creador;
	private ContingutTipusEnumDto tipus;
	private Long metaNodeId;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private String usuariCreacio;
	private ContenidorFiltreOpcionsEsborratEnum opcionsEsborrat;



	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom != null ? nom.trim() : null;
	}
	public String getCreador() {
		return creador;
	}
	public void setCreador(String creador) {
		this.creador = creador != null ? creador.trim() : null;
	}
	public ContingutTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ContingutTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Long getMetaNodeId() {
		return metaNodeId;
	}
	public void setMetaNodeId(Long metaNodeId) {
		this.metaNodeId = metaNodeId;
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
	public String getUsuariCreacio() {
		return usuariCreacio;
	}
	public void setUsuariCreacio(String usuariCreacio) {
		this.usuariCreacio = usuariCreacio != null ? usuariCreacio.trim() : null;
	}
	public ContenidorFiltreOpcionsEsborratEnum getOpcionsEsborrat() {
		return opcionsEsborrat;
	}
	public void setOpcionsEsborrat(
			ContenidorFiltreOpcionsEsborratEnum opcionsEsborrat) {
		this.opcionsEsborrat = opcionsEsborrat;
	}

	public static ContingutFiltreCommand asCommand(ContingutFiltreDto dto) {
		ContingutFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				ContingutFiltreCommand.class);
		return command;
	}
	public static ContingutFiltreDto asDto(ContingutFiltreCommand command) {
		ContingutFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				ContingutFiltreDto.class);
		dto.setMostrarEsborrats(
				ContenidorFiltreOpcionsEsborratEnum.ESBORRATS_I_NO_ESBORRATS.equals(command.getOpcionsEsborrat()) ||
				ContenidorFiltreOpcionsEsborratEnum.NOMES_ESBORRATS.equals(command.getOpcionsEsborrat()));
		dto.setMostrarNoEsborrats(
				ContenidorFiltreOpcionsEsborratEnum.ESBORRATS_I_NO_ESBORRATS.equals(command.getOpcionsEsborrat()) ||
				ContenidorFiltreOpcionsEsborratEnum.NOMES_NO_ESBORRATS.equals(command.getOpcionsEsborrat()));
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
