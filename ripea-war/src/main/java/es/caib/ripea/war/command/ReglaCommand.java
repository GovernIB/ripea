/**
 * 
 */
package es.caib.ripea.war.command;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.ripea.core.api.dto.BackofficeTipusEnumDto;
import es.caib.ripea.core.api.dto.ReglaDto;
import es.caib.ripea.core.api.dto.ReglaTipusEnumDto;
import es.caib.ripea.war.helper.ConversioTipusHelper;

/**
 * Command per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglaCommand {

	private Long id;
	@NotEmpty @Size(max = 256)
	private String nom;
	@Size(max = 1024)
	private String descripcio;
	private ReglaTipusEnumDto tipus;
	@NotEmpty @Size(max = 16)
	private String assumpteCodi;
	@Size(max = 9)
	private String unitatCodi;
	private Long metaExpedientId;
	private Long arxiuId;
	private Long bustiaId;
	private BackofficeTipusEnumDto backofficeTipus;
	@Size(max = 256)
	private String backofficeUrl;
	@Size(max = 64)
	private String backofficeUsuari;
	@Size(max = 64)
	private String backofficeContrasenya;
	private Integer backofficeIntents;
	private Integer backofficeTempsEntreIntents;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom.trim();
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio.trim();
	}
	public ReglaTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ReglaTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public void setAssumpteCodi(String assumpteCodi) {
		this.assumpteCodi = assumpteCodi.trim();
	}
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public void setUnitatCodi(String unitatCodi) {
		this.unitatCodi = unitatCodi.trim();
	}
	public Long getMetaExpedientId() {
		return metaExpedientId;
	}
	public void setMetaExpedientId(Long metaExpedientId) {
		this.metaExpedientId = metaExpedientId;
	}
	public Long getArxiuId() {
		return arxiuId;
	}
	public void setArxiuId(Long arxiuId) {
		this.arxiuId = arxiuId;
	}
	public Long getBustiaId() {
		return bustiaId;
	}
	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}
	public BackofficeTipusEnumDto getBackofficeTipus() {
		return backofficeTipus;
	}
	public void setBackofficeTipus(BackofficeTipusEnumDto backofficeTipus) {
		this.backofficeTipus = backofficeTipus;
	}
	public String getBackofficeUrl() {
		return backofficeUrl;
	}
	public void setBackofficeUrl(String backofficeUrl) {
		this.backofficeUrl = backofficeUrl.trim();
	}
	public String getBackofficeUsuari() {
		return backofficeUsuari;
	}
	public void setBackofficeUsuari(String backofficeUsuari) {
		this.backofficeUsuari = backofficeUsuari.trim();
	}
	public String getBackofficeContrasenya() {
		return backofficeContrasenya;
	}
	public void setBackofficeContrasenya(String backofficeContrasenya) {
		this.backofficeContrasenya = backofficeContrasenya.trim();
	}
	public Integer getBackofficeIntents() {
		return backofficeIntents;
	}
	public void setBackofficeIntents(Integer backofficeIntents) {
		this.backofficeIntents = backofficeIntents;
	}

	public Integer getBackofficeTempsEntreIntents() {
		return backofficeTempsEntreIntents;
	}
	public void setBackofficeTempsEntreIntents(Integer backofficeTempsEntreIntents) {
		this.backofficeTempsEntreIntents = backofficeTempsEntreIntents;
	}
	public static ReglaCommand asCommand(ReglaDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ReglaCommand.class);
	}
	public static ReglaDto asDto(ReglaCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ReglaDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
