package es.caib.ripea.core.api.service;

import java.util.List;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.OrganGestorFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a la gestió de meta-expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganGestorService {

	/**
	 * Obté una llista amb tots els organs gestors de la base de dades
	 * 
	 * @return Llistat de tots els organs gestors
	 */
	public List<OrganGestorDto> findAll();
	
	/**
	 * Obté l'element amb l'identificador donat.
	 * 
	 * @param id Identificador de l'element a consultar
	 * 
	 * @return L'objecte del registre consultat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	public OrganGestorDto findItem(Long id) throws NotFoundException;
	
	/**
	 * Obté una llista amb tots els organs gestors de l'entitat especificada
	 * per paràmetre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return Llistat dels organs gestors de l'entitat
	 */
	public List<OrganGestorDto> findByEntitat(Long entitatId);

	/**
	 * Consulta tots els organs gestors de l'entitat actual de forma paginada 
	 * i aplicant el filtre.
	 * 
	 * @param entitatId Identificador de l'entitat actual
	 * @param filtre Filtre a aplicar als resultats
	 * @param paginacioParams
	 * 		Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina amb els organs gestors
	 */
	public PaginaDto<OrganGestorDto> findOrgansGestorsAmbFiltrePaginat(Long entitatId, OrganGestorFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	
	public boolean syncDir3OrgansGestors(Long entitatId);
	
}
