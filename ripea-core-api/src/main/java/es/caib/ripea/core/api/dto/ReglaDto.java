/**
 * 
 */
package es.caib.ripea.core.api.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'una regla per a gestionar anotacions de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglaDto extends AuditoriaDto {

	private Long id;
	private String nom;
	private String descripcio;
	private ReglaTipusEnumDto tipus;
	private String assumpteCodi;
	private String unitatCodi;
	private Long metaExpedientId;
	private Long arxiuId;
	private Long bustiaId;
	private BackofficeTipusEnumDto backofficeTipus;
	private String backofficeUrl;
	private String backofficeUsuari;
	private String backofficeContrasenya;
	private Integer backofficeIntents;
	private Integer backofficeTempsEntreIntents;
	private int ordre;
	private boolean activa;



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
		this.nom = nom;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
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
		this.assumpteCodi = assumpteCodi;
	}
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public void setUnitatCodi(String unitatCodi) {
		this.unitatCodi = unitatCodi;
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
		this.backofficeUrl = backofficeUrl;
	}
	public String getBackofficeUsuari() {
		return backofficeUsuari;
	}
	public void setBackofficeUsuari(String backofficeUsuari) {
		this.backofficeUsuari = backofficeUsuari;
	}
	public String getBackofficeContrasenya() {
		return backofficeContrasenya;
	}
	public void setBackofficeContrasenya(String backofficeContrasenya) {
		this.backofficeContrasenya = backofficeContrasenya;
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
	public int getOrdre() {
		return ordre;
	}
	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
