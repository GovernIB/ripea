/**
 * 
 */
package es.caib.ripea.plugin.notificacio;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.ripea.core.api.dto.ServeiTipusEnumDto;

/**
 * Informació d'una notificació per al seu enviament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class Notificacio {

	private String emisorDir3Codi;
	private EnviamentTipus enviamentTipus;
	private String concepte;
	private String descripcio;
	private Date enviamentDataProgramada;
	private Integer retard;
	private Date caducitat;
	private String documentArxiuNom;
	private byte[] documentArxiuContingut;
	private String procedimentCodi;
	private List<Enviament> enviaments;
	private String usuariCodi;
	private ServeiTipusEnumDto serveiTipusEnum;
	
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getEmisorDir3Codi() {
		return emisorDir3Codi;
	}
	public void setEmisorDir3Codi(String emisorDir3Codi) {
		this.emisorDir3Codi = emisorDir3Codi;
	}
	public EnviamentTipus getEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(EnviamentTipus enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public String getConcepte() {
		return concepte;
	}
	public void setConcepte(String concepte) {
		this.concepte = concepte;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public Date getEnviamentDataProgramada() {
		return enviamentDataProgramada;
	}
	public void setEnviamentDataProgramada(Date enviamentDataProgramada) {
		this.enviamentDataProgramada = enviamentDataProgramada;
	}
	public Integer getRetard() {
		return retard;
	}
	public void setRetard(Integer retard) {
		this.retard = retard;
	}
	public Date getCaducitat() {
		return caducitat;
	}
	public void setCaducitat(Date caducitat) {
		this.caducitat = caducitat;
	}
	public String getDocumentArxiuNom() {
		return documentArxiuNom;
	}
	public void setDocumentArxiuNom(String documentArxiuNom) {
		this.documentArxiuNom = documentArxiuNom;
	}
	public byte[] getDocumentArxiuContingut() {
		return documentArxiuContingut;
	}
	public void setDocumentArxiuContingut(byte[] documentArxiuContingut) {
		this.documentArxiuContingut = documentArxiuContingut;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public List<Enviament> getEnviaments() {
		return enviaments;
	}
	public void setEnviaments(List<Enviament> enviaments) {
		this.enviaments = enviaments;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public ServeiTipusEnumDto getServeiTipusEnum() {
		return serveiTipusEnum;
	}
	public void setServeiTipusEnum(ServeiTipusEnumDto serveiTipusEnum) {
		this.serveiTipusEnum = serveiTipusEnum;
	}

}
