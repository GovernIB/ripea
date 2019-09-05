/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * Informació d'una notificació d'un document a un ciutadà.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentNotificacioDto extends DocumentEnviamentDto {

	private DocumentNotificacioTipusEnumDto tipus;
	private Date dataProgramada;
	private Integer retard;
	private Date dataCaducitat;


	private String enviamentIdentificador;
	private String enviamentReferencia;
	private List<Long> interessatsIds = new ArrayList<Long>();
	
	private ServeiTipusEnumDto serveiTipusEnum;
	
	private Boolean entregaPostal;
	private List<InteressatDto> interessats = new ArrayList<InteressatDto>();
	
	public DocumentNotificacioTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(DocumentNotificacioTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Date getDataProgramada() {
		return dataProgramada;
	}
	public void setDataProgramada(Date dataProgramada) {
		this.dataProgramada = dataProgramada;
	}
	public Integer getRetard() {
		return retard;
	}
	public void setRetard(Integer retard) {
		this.retard = retard;
	}
	public Date getDataCaducitat() {
		return dataCaducitat;
	}
	public void setDataCaducitat(Date dataCaducitat) {
		this.dataCaducitat = dataCaducitat;
	}
	public String getEnviamentIdentificador() {
		return enviamentIdentificador;
	}
	public void setEnviamentIdentificador(String enviamentIdentificador) {
		this.enviamentIdentificador = enviamentIdentificador;
	}
	public String getEnviamentReferencia() {
		return enviamentReferencia;
	}
	public void setEnviamentReferencia(String enviamentReferencia) {
		this.enviamentReferencia = enviamentReferencia;
	}
	public ServeiTipusEnumDto getServeiTipusEnum() {
		return serveiTipusEnum;
	}
	public void setServeiTipusEnum(ServeiTipusEnumDto serveiTipusEnum) {
		this.serveiTipusEnum = serveiTipusEnum;
	}	

	@Override
	public String getDestinatari() {
		return null;
	}
	@Override
	public String getDestinatariAmbDocument() {
		return null;
	}
	public Boolean getEntregaPostal() {
		return entregaPostal;
	}
	public void setEntregaPostal(Boolean entregaPostal) {
		this.entregaPostal = entregaPostal;
	}
	public List<Long> getInteressatsIds() {
		return interessatsIds;
	}
	public void setInteressatsIds(List<Long> interessatsIds) {
		this.interessatsIds = interessatsIds;
	}
	public List<InteressatDto> getInteressats() {
		return interessats;
	}
	public void setInteressats(List<InteressatDto> interessats) {
		this.interessats = interessats;
	}

}
