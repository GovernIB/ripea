/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;

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
	private Long interessatId;
	private InteressatDto interessat;

	private String enviamentIdentificador;
	private String enviamentReferencia;
	
	private ServeiTipusEnumDto serveiTipusEnum;
	
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
	public Long getInteressatId() {
		return interessatId;
	}
	public void setInteressatId(Long interessatId) {
		this.interessatId = interessatId;
	}
	public InteressatDto getInteressat() {
		return interessat;
	}
	public void setInteressat(InteressatDto interessat) {
		this.interessat = interessat;
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
		return interessat.getNomComplet();
	}
	@Override
	public String getDestinatariAmbDocument() {
		return interessat.getNomCompletAmbDocument();
	}

}
