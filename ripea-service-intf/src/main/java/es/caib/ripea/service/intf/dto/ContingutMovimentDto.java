/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació del moviment d'un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutMovimentDto implements Serializable {

	private Long id;
	private Long contingutId;
	private Long origenId;
	private Long destiId;
	private Date data;
	private UsuariDto remitent;
	private String comentari;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getContingutId() {
		return contingutId;
	}
	public void setContingutId(Long contingutId) {
		this.contingutId = contingutId;
	}
	public Long getOrigenId() {
		return origenId;
	}
	public void setOrigenId(Long origenId) {
		this.origenId = origenId;
	}
	public Long getDestiId() {
		return destiId;
	}
	public void setDestiId(Long destiId) {
		this.destiId = destiId;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public UsuariDto getRemitent() {
		return remitent;
	}
	public void setRemitent(UsuariDto remitent) {
		this.remitent = remitent;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
