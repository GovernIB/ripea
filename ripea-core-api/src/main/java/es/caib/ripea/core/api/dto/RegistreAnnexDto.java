/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;


/**
 * Informació d'un registre annex.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnnexDto {

	private Long id;
	private String firmaPerfil;
	private String firmaTipus;
	private Date ntiFechaCaptura;
	
	private String ntiOrigen;
	private String ntiTipoDocumental;
	private String ntiEstadoElaboracion;
	private String observacions;
	private String sicresTipoDocumento;
	private String sicresValidezDocumento;
	private long tamany;
	private String tipusMime;
	private String titol;
	private String uuid;

	private String nom;


	private Date createdDate;
	private String registreNumero;
	private Long expedientId;
	private String expedientNumeroNom;
	private Date expedientCreatedDate;

	private Long documentId;
	private RegistreAnnexEstatEnumDto estat;
	private String error;
	private Long expedientPeticioId;
	
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
	public Long getId() {
		return id;
	}
	public void setId(
			Long id) {
		this.id = id;
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
	public String getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(String ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public boolean isAmbFirma() {
		return firmaTipus != null;
	}

	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getRegistreNumero() {
		return registreNumero;
	}
	public void setRegistreNumero(String registreNumero) {
		this.registreNumero = registreNumero;
	}
	public Long getExpedientId() {
		return expedientId;
	}
	public void setExpedientId(Long expedientId) {
		this.expedientId = expedientId;
	}
	public String getExpedientNumeroNom() {
		return expedientNumeroNom;
	}
	public void setExpedientNumeroNom(String expedientNumeroNom) {
		this.expedientNumeroNom = expedientNumeroNom;
	}
	public Date getExpedientCreatedDate() {
		return expedientCreatedDate;
	}
	public void setExpedientCreatedDate(Date expedientCreatedDate) {
		this.expedientCreatedDate = expedientCreatedDate;
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public boolean isRowSelectable() {
		return this.documentId != null;
	}
	public Long getExpedientPeticioId() {
		return expedientPeticioId;
	}
	public void setExpedientPeticioId(Long expedientPeticioId) {
		this.expedientPeticioId = expedientPeticioId;
	}
	
}
