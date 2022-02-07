/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;

/**
 * Informació d'un expedient peticio per llistat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientPeticioListDto {

	private Long id;
	private String identificador;
	private String clauAcces;
	private Date dataAlta;
	private ExpedientPeticioEstatEnumDto estat;
	private RegistreDto registre;
	private Long metaExpedientId;
	private String metaExpedientNom;
	private ExpedientPeticioAccioEnumDto accio;
	private String notificaDistError;

	@SuppressWarnings("incomplete-switch")
	public ExpedientPeticioEstatViewEnumDto getEstatView() {
		ExpedientPeticioEstatViewEnumDto estatView = null;
		if (estat != null) {
			switch (estat) {
			case PENDENT:
				estatView = ExpedientPeticioEstatViewEnumDto.PENDENT;
				break;
			case PROCESSAT_PENDENT:
			case PROCESSAT_NOTIFICAT:
				estatView = ExpedientPeticioEstatViewEnumDto.ACCEPTAT;
				break;
			case REBUTJAT:
				estatView = ExpedientPeticioEstatViewEnumDto.REBUTJAT;
				break;
			}
		}
		return estatView;
	}
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
	public ExpedientPeticioAccioEnumDto getAccio() {
		return accio;
	}
	public void setAccio(ExpedientPeticioAccioEnumDto accio) {
		this.accio = accio;
	}
	public Long getMetaExpedientId() {
		return metaExpedientId;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	

}
