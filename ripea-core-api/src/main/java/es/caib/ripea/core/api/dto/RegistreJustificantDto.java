/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;


/**
 * Informació d'un registre justificant.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreJustificantDto {

	private String firmaPerfil;
	private String firmaTipus;
	private Date ntiFechaCaptura;
	private String ntiOrigen;
	private String ntiTipoDocumental;
	private String observacions;
	private String sicresTipoDocumento;
	private String sicresValidezDocumento;
	private long tamany;
	private String tipusMime;
	private String titol;
	private String uuid;

	private RegistreAnnexEstatEnumDto estat;
	private String error;

	
	public RegistreAnnexEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(RegistreAnnexEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getFirmaPerfil() {
		return firmaPerfil;
	}
	public void setFirmaPerfil(
			String firmaPerfil) {
		this.firmaPerfil = firmaPerfil;
	}
	public String getFirmaTipus() {
		return firmaTipus;
	}
	public void setFirmaTipus(
			String firmaTipus) {
		this.firmaTipus = firmaTipus;
	}
	public Date getNtiFechaCaptura() {
		return ntiFechaCaptura;
	}
	public void setNtiFechaCaptura(
			Date ntiFechaCaptura) {
		this.ntiFechaCaptura = ntiFechaCaptura;
	}
	public String getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(
			String ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	public String getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(
			String ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(
			String observacions) {
		this.observacions = observacions;
	}
	public String getSicresTipoDocumento() {
		return sicresTipoDocumento;
	}
	public void setSicresTipoDocumento(
			String sicresTipoDocumento) {
		this.sicresTipoDocumento = sicresTipoDocumento;
	}
	public String getSicresValidezDocumento() {
		return sicresValidezDocumento;
	}
	public void setSicresValidezDocumento(
			String sicresValidezDocumento) {
		this.sicresValidezDocumento = sicresValidezDocumento;
	}
	public long getTamany() {
		return tamany;
	}
	public void setTamany(
			long tamany) {
		this.tamany = tamany;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(
			String tipusMime) {
		this.tipusMime = tipusMime;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(
			String titol) {
		this.titol = titol;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(
			String uuid) {
		this.uuid = uuid;
	}
	
}