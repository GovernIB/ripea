/**
 * 
 */
package es.caib.ripea.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.MassiuAnnexProcesarFiltreDto;
import es.caib.ripea.core.api.dto.MassiuAnnexEstatProcessamentEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;


public class MassiuAnnexProcesarFiltreCommand {

	private String nom;
	private String numero;
	private Date dataInici;
	private Date dataFi;	
	private MassiuAnnexEstatProcessamentEnumDto estatProcessament;
	
	
	
	public static MassiuAnnexProcesarFiltreCommand asCommand(MassiuAnnexProcesarFiltreDto dto) {
		MassiuAnnexProcesarFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				MassiuAnnexProcesarFiltreCommand.class);
		return command;
	}
	public static MassiuAnnexProcesarFiltreDto asDto(MassiuAnnexProcesarFiltreCommand command) {
		MassiuAnnexProcesarFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				MassiuAnnexProcesarFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
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
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public MassiuAnnexEstatProcessamentEnumDto getEstatProcessament() {
		return estatProcessament;
	}
	public void setEstatProcessament(MassiuAnnexEstatProcessamentEnumDto estatProcessament) {
		this.estatProcessament = estatProcessament;
	}
	
	
}
