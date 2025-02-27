/**
 * 
 */
package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.InteressatAdministracioDto;
import es.caib.ripea.service.intf.dto.InteressatDto;
import es.caib.ripea.service.intf.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.service.intf.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a gestionar els interessats dels expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientInteressatService {

	/**
	 * Crea un nou interessat i l'associa a un expedient.
	 * 
	 * @param entitatId   Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param expedientId Atribut id de l'expedient al qual s'associarà
	 *                    l'interessat.
	 * @param interessat  Dades de l'interessat que es vol crear.
	 * @param rolActual TODO
	 * @return L'interessat creat.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public InteressatDto create(Long entitatId, Long expedientId, InteressatDto interessat, String rolActual) throws NotFoundException;

	/**
	 * Crea un nou interessat i l'associa a un expedient.
	 * @param expedientId  Atribut id de l'expedient al qual s'associarà
	 *                     l'interessat.
	 * @param representant Dades del representant que es vol crear.
	 * @param rolActual TODO
	 * @param entitatId    Atribut id de l'entitat a la qual pertany l'expedient.
	 * 
	 * @return L'interessat creat.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public InteressatDto create(
			Long id,
			Long expedientId,
			Long interessatId,
			InteressatDto representant,
			boolean propagarArxiu, String rolActual) throws NotFoundException;

	/**
	 * Modifica un representant associat a un interessat.
	 * 
	 * @param entitatId    Atribut id de l'entitat a la qual pertany l'interessat.
	 * @param expedientId  Atribut id de l'expedient al qual s'associarà
	 *                     l'interessat.
	 * @param rolActual TODO
	 * @param interessatId Atribut id de linteressat al qual s'associarà
	 *                     l'interessat.
	 * @return L'interessat modificat.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public InteressatDto update(Long entitatId, Long expedientId, InteressatDto interessat, String rolActual) throws NotFoundException;

	/**
	 * Modifica un representant associat a un interessat.
	 * 
	 * @param entitatId    Atribut id de l'entitat a la qual pertany el
	 *                     representant.
	 * @param expedientId  Atribut id de l'expedient al qual s'associarà el
	 *                     representant.
	 * @param interessatId Atribut id de linteressat al qual s'associarà el
	 *                     representant.
	 * @param representant Dades del representant que es vol modificar.
	 * @param rolActual TODO
	 * @return L'interessat modificat.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public InteressatDto update(Long entitatId, Long expedientId, Long interessatId, InteressatDto representant, String rolActual);

	/**
	 * elimina un interessat existent en un expedient.
	 * 
	 * @param entitatId    Atribut id de l'entitat a la qual pertany l'expedient.
	 * @param expedientId  Atribut id de l'expedient al qual s'associarà
	 *                     l'interessat.
	 * @param interessatId Atribut id de l'interessat que es vol afegir.
	 * @param rolActual TODO
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public void delete(Long entitatId, Long expedientId, Long interessatId, String rolActual);

	/**
	 * elimina un interessat existent en un expedient.
	 * 
	 * @param entitatId      Atribut id de l'entitat a la qual pertany el
	 *                       representant.
	 * @param expedientId    Atribut id de l'expedient al qual s'associarà el
	 *                       representant.
	 * @param interessatId   Atribut id de l'interessat al qual pertany el
	 *                       representant.
	 * @param representantId Atribut id del representant que es vol esborrar.
	 * @param rolActual TODO
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public void deleteRepresentant(Long entitatId, Long expedientId, Long interessatId, Long representantId, String rolActual);

	/**
	 * Consulta l'interessat donat el seu id.
	 * 
	 * @param id Atribut id de l'interessat que es vol trobar.
	 * @param consultarDadesExternes Indicar si consultar el plugin de dades externes per obtenir la informació restatnt de la direcció (nom país, nom província, nom municipi)
	 * @return l'interessat.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public InteressatDto findById(Long id, boolean consultarDadesExternes) throws NotFoundException;

	/**
	 * Consulta l'interessat donat el seu id.
	 * 
	 * @param interessatId Atribut interessatId de l'interessat al que pertany el
	 *                     representant.
	 * @param id           Atribut id del representant que es vol trobar.
	 * @return l'interessat.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	InteressatDto findRepresentantById(Long interessatId, Long id);

	/**
	 * Consulta dels interessats associats a un expedient.
	 * 
	 * @param entitatId   Atribut id de l'entitat a la qual pertany l'interessat.
	 * @param expedientId Atribut id de l'interessat que es vol trobar.
	 * @return Els insteressats associats a l'expedient.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<InteressatDto> findByExpedient(
			Long entitatId,
			Long expedientId,
			boolean nomesAmbNotificacioActiva) throws NotFoundException;

	/**
	 * Consulta el nombre d'interessats associats a un expedient.
	 * 
	 * @param entitatId   Atribut id de l'entitat a la qual pertany l'interessat.
	 * @param expedientId Atribut id de l'interessat que es vol trobar.
	 * @return El nombre d'insteressats associats a l'expedient.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public long countByExpedient(Long entitatId, Long expedientId) throws NotFoundException;

	/**
	 * Consulta dels interessats associats a un document per a fer notificacions. El
	 * document ha d'estar forçosament associat a un expedient.
	 * 
	 * @param entitatId  Atribut id de l'entitat a la qual pertany l'interessat.
	 * @param documentId Atribut id del document.
	 * @return Els insteressats associats a l'expedient.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<InteressatDto> findAmbDocumentPerNotificacio(Long entitatId, Long documentId) throws NotFoundException;

	/**
	 * Consulta els interessats per nom i identificador.
	 * 
	 * @param nom         Nom de l'interessat per a la consulta.
	 * @param nif         NIF de l'interessat per a la consulta.
	 * @param llinatges   Llinatges de l'interessat per a la consulta.
	 * @param expedientId Id del expedient del interessat
	 * @return La llista d'interessats trobats.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<InteressatPersonaFisicaDto> findByFiltrePersonaFisica(
			String documentNum,
			String nom,
			String llinatge1,
			String llinatge2,
			Long expedientId) throws NotFoundException;

	/**
	 * Consulta els interessats per nom i identificador.
	 * 
	 * @param nom         Nom de l'interessat per a la consulta.
	 * @param nif         NIF de l'interessat per a la consulta.
	 * @param llinatges   Llinatges de l'interessat per a la consulta.
	 * @param expedientId Id del expedient del interessat
	 * @return La llista d'interessats trobats.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<InteressatPersonaJuridicaDto> findByFiltrePersonaJuridica(
			String documentNum,
			String raoSocial,
			Long expedientId) throws NotFoundException;

	/**
	 * Consulta els interessats per nom i identificador.
	 * 
	 * @param organCodi   codi de l'organ de l'administració per a la consulta.
	 * @param expedientId Id del expedient del interessat
	 * @return La llista d'interessats trobats.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<InteressatAdministracioDto> findByFiltreAdministracio(
			String organCodi,
			Long expedientId) throws NotFoundException;

	/**
	 * Consulta l'interessat d'un expedient amb un determinat número de document
	 * 
	 * @param documentNum Número del document de l'interessat
	 * @param expedientId Id del expedient del interessat
	 * @return La llista d'interessats trobats.
	 * @throws NotFoundException Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public InteressatDto findByExpedientAndDocumentNum(
			String documentNum,
			Long expedientId) throws NotFoundException;

	/**
	 * 
	 * @param text
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("isAuthenticated()")
	public List<InteressatDto> findByText(String text);

	/**
	 * 
	 * @param documentNum
	 * @return
	 * @throws NotFoundException
	 */
	@PreAuthorize("isAuthenticated()")
	public InteressatDto findByDocumentNum(String documentNum) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public Exception guardarInteressatsArxiu(Long expId);

	@PreAuthorize("isAuthenticated()")
    public Long findExpedientIdByInteressat(Long interessatId);

	@PreAuthorize("isAuthenticated()")
	public InteressatDto createRepresentant(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto interessat,
			boolean propagarArxiu,
			String rolActual);

	@PreAuthorize("isAuthenticated()")
	public String importarInteressats(Long entitatId, Long expedientId, String rolActual, List<InteressatDto> interessats, List<Long> seleccionats);
}
