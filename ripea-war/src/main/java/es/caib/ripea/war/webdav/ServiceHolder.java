/**
 * 
 */
package es.caib.ripea.war.webdav;

import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.DocumentService;

/**
 * Classe per a poder accedir als serveis des dels resources
 * generats pel servidor WEBDAV.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServiceHolder {

	private ContingutService contenidorService;
	private DocumentService documentService;
	private CarpetaService carpetaService;

	public ServiceHolder(
			ContingutService contenidorService,
			DocumentService documentService,
			CarpetaService carpetaService) {
		this.contenidorService = contenidorService;
		this.documentService = documentService;
		this.carpetaService = carpetaService;
	}

	public ContingutService getContenidorService() {
		return contenidorService;
	}
	public DocumentService getDocumentService() {
		return documentService;
	}
	public CarpetaService getCarpetaService() {
		return carpetaService;
	}

}
