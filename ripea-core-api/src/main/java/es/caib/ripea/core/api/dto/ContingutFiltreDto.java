/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutFiltreDto implements Serializable {

	private String nom;
	private ContingutTipusEnumDto tipus;
	private Long metaNodeId;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private boolean mostrarEsborrats;
	private boolean mostrarNoEsborrats;



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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
