/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;

/**
 * Informació d'un expedient peticio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientPeticioDto {


	private Long id;
	private String identificador;
	private String clauAcces;
	private Date dataAlta;
	private ExpedientPeticioEstatEnumDto estat;
	private RegistreDto registre;
	private String metaExpedientNom;
	private ExpedientPeticioAccioEnumDto expedientPeticioAccioEnumDto;
	private String notificaDistError;
	
	

	public String getNotificaDistError() {
		return notificaDistError;
	}
	public void setNotificaDistError(String notificaDistError) {
		this.notificaDistError = notificaDistError;
	}
	public String getMetaExpedientNom() {
		return metaExpedientNom;
	}
	public void setMetaExpedientNom(
			String metaExpedientNom) {
		this.metaExpedientNom = metaExpedientNom;
	}
	public Long getId() {
		return id;
	}
	public void setId(
			Long id) {
		this.id = id;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(
			String identificador) {
		this.identificador = identificador;
	}
	public String getClauAcces() {
		return clauAcces;
	}
	public void setClauAcces(
			String clauAcces) {
		this.clauAcces = clauAcces;
	}
	public Date getDataAlta() {
		return dataAlta;
	}
	public void setDataAlta(
			Date dataAlta) {
		this.dataAlta = dataAlta;
	}
	public ExpedientPeticioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(
			ExpedientPeticioEstatEnumDto estat) {
		this.estat = estat;
	}
	public RegistreDto getRegistre() {
		return registre;
	}
	public void setRegistre(
			RegistreDto registre) {
		this.registre = registre;
	}
	public ExpedientPeticioAccioEnumDto getExpedientPeticioAccioEnumDto() {
		return expedientPeticioAccioEnumDto;
	}
	public void setExpedientPeticioAccioEnumDto(
			ExpedientPeticioAccioEnumDto expedientPeticioAccioEnumDto) {
		this.expedientPeticioAccioEnumDto = expedientPeticioAccioEnumDto;
	}
	
	

}
