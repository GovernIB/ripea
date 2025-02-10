/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutFiltreDto implements Serializable {

	private String nom;
	private String creador;
	private ContingutTipusEnumDto tipus;
	private Long metaNodeId;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private Date dataEsborratInici;
	private Date dataEsborratFi;
	private boolean mostrarEsborrats;
	private boolean mostrarNoEsborrats;
	private Long expedientId;


	public ContingutTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ContingutTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCreador() {
		return creador;
	}
	public void setCreador(String creador) {
		this.creador = creador;
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
	public Date getDataEsborratInici() {
		return dataEsborratInici;
	}
	public void setDataEsborratInici(Date dataEsborratInici) {
		this.dataEsborratInici = dataEsborratInici;
	}
	public Date getDataEsborratFi() {
		return dataEsborratFi;
	}
	public void setDataEsborratFi(Date dataEsborratFi) {
		this.dataEsborratFi = dataEsborratFi;
	}
	public boolean isMostrarEsborrats() {
		return mostrarEsborrats;
	}
	public void setMostrarEsborrats(boolean mostrarEsborrats) {
		this.mostrarEsborrats = mostrarEsborrats;
	}
	public boolean isMostrarNoEsborrats() {
		return mostrarNoEsborrats;
	}
	public void setMostrarNoEsborrats(boolean mostrarNoEsborrats) {
		this.mostrarNoEsborrats = mostrarNoEsborrats;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
