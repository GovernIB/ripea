/**
 * 
 */
package es.caib.ripea.core.api.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.DocumentPortafirmesDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaDocumentFirmaFluxTipusEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesPrioritatEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.exception.ValidationException;


/**
 * Declaració dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DocumentService {

	/**
	 * Crea un nou document a dins un contenidor.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param pareId
	 *            Atribut id del contenidor a on es vol crear el document.
	 * @param document
	 *            Informació del document que es vol crear.
	 * @return El document creat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("hasRole('tothom')")
	public DocumentDto create(
			Long entitatId,
			Long pareId,
			DocumentDto document) throws NotFoundException, ValidationException;

	/**
	 * Modifica un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document que es vol modificar.
	 * @param document
	 *            Informació del document que es vol crear.
	 * @return El document modificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws ValidationException
	 *             Si el nom del contenidor conté caràcters invàlids.
	 */
	@PreAuthorize("hasRole('tothom')")
	public DocumentDto update(
			Long entitatId,
			Long id,
			DocumentDto document) throws NotFoundException, ValidationException;

	/**
	 * Consulta un document donat el seu id.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document que es vol trobar.
	 * @return El document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public DocumentDto findById(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta les versions d'un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es volen recuperar les versions.
	 * @return La llista de versions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	/*@PreAuthorize("hasRole('tothom')")
	public List<DocumentVersioDto> findVersionsByDocument(
			Long entitatId,
			Long id) throws NotFoundException;*/

	/**
	 * Consulta la darrera versió del document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @return la darrera versió del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	/*@PreAuthorize("hasRole('tothom')")
	public DocumentVersioDto findDarreraVersio(
			Long entitatId,
			Long id) throws NotFoundException;*/

	/**
	 * Consulta una versió del document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @param versio
	 *            El número de versió del document que es vol descarregar.
	 * @return la versió del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	/*@PreAuthorize("hasRole('tothom')")
	public DocumentVersioDto findVersio(
			Long entitatId,
			Long id,
			int versio) throws NotFoundException;*/

	/**
	 * Consulta els documents d'un expedient.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @return la llistat de documents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public List<DocumentDto> findAmbExpedientIPermisRead(
			Long entitatId,
			Long expedientId) throws NotFoundException;

	/**
	 * Recupera el contingut d'un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @param versio
	 *            El número de versió del document que es vol descarregar.
	 * @return el fitxer amb el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto infoDocument(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException;

	/**
	 * Consulta el detall de les signatures del document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @param versio
	 *            El número de versió del document que es vol descarregar.
	 * @return la informació de les signatures del document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'informacio amb el contingut en bytes especificat.
	 */
	public List<ArxiuFirmaDetallDto> getDetallSignants(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException;

	/**
	 * Descarrega el contingut d'un document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document del qual es vol descarregar el contingut.
	 * @param versio
	 *            El número de versió del document que es vol descarregar.
	 * @return el fitxer amb el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto descarregar(
			Long entitatId,
			Long id,
			String versio) throws NotFoundException;

	/**
	 * Envia un document a firmar al portafirmes.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol enviar a firmar.
	 * @param assumpte
	 *            L'assumpte de l'enviament.
	 * @param prioritat
	 *            La prioritat de l'enviament.
	 * @param dataCaducitat
	 *            La data màxima per a firmar el document.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws IllegalStateException
	 *             Si hi ha enviaments a portafirmes pendents per aquest document.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void portafirmesEnviar(
			Long entitatId,
			Long documentId,
			String assumpte,
			PortafirmesPrioritatEnumDto prioritat,
			Date dataCaducitat,
			String[] portafirmesResponsables,
			MetaDocumentFirmaFluxTipusEnumDto portafirmesFluxTipus) throws NotFoundException, IllegalStateException, SistemaExternException;

	/**
	 * Cancela l'enviament d'un document a firmar al portafirmes.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document que es vol enviar a firmar.
	 * @param versio
	 *            El número de versió del document que es vol enviar a firmar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws IllegalStateException
	 *             Si no s'ha trobat l'enviament al portafirmes pel document.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void portafirmesCancelar(
			Long entitatId,
			Long documentId) throws NotFoundException, IllegalStateException, SistemaExternException;

	/**
	 * Processa una petició del callback de portafirmes.
	 * 
	 * @param documentId
	 *            Atribut id del document del portafirmes.
	 * @param estat
	 *            Nou estat del document.
	 * @return null si tot ha anat bé o una excepció si s'ha produit algun error
	 *            al processar el document firmat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public Exception portafirmesCallback(
			long documentId,
			PortafirmesCallbackEstatEnumDto estat) throws NotFoundException;

	/**
	 * Reintenta la custòdia d'un document firmat amb portafirmes que ha donat
	 * error al custodiar.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol custodiar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb la custòdia.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void portafirmesReintentar(
			Long entitatId,
			Long documentId) throws NotFoundException, SistemaExternException;

	/**
	 * Retorna la informació del darrer enviament a portafirmes del document.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param documentId
	 *            Atribut id del document que es vol convertir.
	 * @return la informació de l'enviament a portafirmes.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public DocumentPortafirmesDto portafirmesInfo(
			Long entitatId,
			Long documentId) throws NotFoundException;

	/**
	 * Converteix el document a format PDF per a firmar-lo al navegador.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document que es vol convertir.
	 * @return el fitxer convertit.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb el portafirmes.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto convertirPdfPerFirmaClient(
			Long entitatId,
			Long id) throws NotFoundException, SistemaExternException;

	/**
	 * Genera un identificador del document per firmar en el navegador
	 * del client.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contenidor.
	 * @param id
	 *            Atribut id del document que es vol convertir.
	 * @return l'identificador generat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb la custòdia.
	 */
	@PreAuthorize("hasRole('tothom')")
	public String generarIdentificadorFirmaClient(
			Long entitatId,
			Long id) throws NotFoundException, SistemaExternException;

	/**
	 * Envia a custòdia un document firmat al navegador.
	 * 
	 * @param identificador
	 *            Identificador del document generat amb anterioritat.
	 * @param arxiuNom
	 *            Nom de l'arxiu firmat.
	 * @param arxiuContingut
	 *            Contingut de l'arxiu firmat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 * @throws SistemaExternException
	 *             Hi ha hagut algun error en la comunicació amb la custòdia.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void processarFirmaClient(
			String identificador,
			String arxiuNom,
			byte[] arxiuContingut) throws NotFoundException, SistemaExternException;

	FitxerDto descarregarImprimible(Long entitatId, Long id, String versio);
	

	void notificacioActualitzarEstat(String identificador,
			String referencia);


}
