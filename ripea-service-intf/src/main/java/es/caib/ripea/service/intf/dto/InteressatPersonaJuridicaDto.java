/**
 * 
 */
package es.caib.ripea.service.intf.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Informació d'un interessat de tipus ciutadà.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatPersonaJuridicaDto extends InteressatDto {

	protected String raoSocial;

	@Override
	public InteressatTipusEnumDto getTipus() {
		return InteressatTipusEnumDto.PERSONA_JURIDICA;
	}
	
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}

	public String getNomComplet() {
		return raoSocial;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
