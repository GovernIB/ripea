/**
 * 
 */
package es.caib.ripea.core.api.dto;

import java.util.Date;
import java.util.List;

/**
 * Informació d'un enviament d'un document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class DocumentEnviamentDto extends AuditoriaDto {

	private Long id;
	private DocumentEnviamentEstatEnumDto estat;
	private String assumpte;
	private String observacions;
	private Date enviatData;
	private Date processatData;
	private Date cancelatData;
	private boolean error;
	private String errorDescripcio;
	private int intentNum;
	private Date intentData;
	private Date intentProximData;
	private DocumentDto document;
	private List<DocumentDto> annexos;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public DocumentEnviamentEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(DocumentEnviamentEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getAssumpte() {
		return assumpte;
	}
	public void setAssumpte(String assumpte) {
		this.assumpte = assumpte;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public Date getEnviatData() {
		return enviatData;
	}
	public void setEnviatData(Date enviatData) {
		this.enviatData = enviatData;
	}
	public Date getProcessatData() {
		return processatData;
	}
	public void setProcessatData(Date processatData) {
		this.processatData = processatData;
	}
	public Date getCancelatData() {
		return cancelatData;
	}
	public void setCancelatData(Date cancelatData) {
		this.cancelatData = cancelatData;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public int getIntentNum() {
		return intentNum;
	}
	public void setIntentNum(int intentNum) {
		this.intentNum = intentNum;
	}
	public Date getIntentData() {
		return intentData;
	}
	public void setIntentData(Date intentData) {
		this.intentData = intentData;
	}
	public Date getIntentProximData() {
		return intentProximData;
	}
	public void setIntentProximData(Date intentProximData) {
		this.intentProximData = intentProximData;
	}
	public DocumentDto getDocument() {
		return document;
	}
	public void setDocument(DocumentDto document) {
		this.document = document;
	}
	public Long getDocumentId() {
		return document != null ? document.id : null;
	}
	public List<DocumentDto> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<DocumentDto> annexos) {
		this.annexos = annexos;
	}

	public boolean isNotificacio() {
		return this instanceof DocumentNotificacioDto;
	}
	public boolean isPublicacio() {
		return this instanceof DocumentPublicacioDto;
	}
	public boolean isPortafirmes() {
		return this instanceof DocumentPortafirmesDto;
	}

	public abstract String getDestinatari();
	public abstract String getDestinatariAmbDocument();

}
