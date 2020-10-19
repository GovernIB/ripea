package es.caib.ripea.core.api.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.HistoricDto;
import es.caib.ripea.core.api.dto.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.HistoricUsuariDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;

/**
 * Declaració dels mètodes per a la consulta de l'històric
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HistoricService {

	/**
	 * Consulta l'històric dels expedients d'una entitat concreta dins un rang de
	 * dates definit.
	 * 
	 * @param entitatId      Identificador de l'entitat consultada
	 * @param dataInici      Data d'inici de les dades a consultar
	 * @param dataFi         Data final de les dates a consultar
	 * @param metaExpedients Tipus d'expedients consultats
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta l'històric dels expedients d'una entitat concreta dins un rang de
	 * dates definit.
	 * 
	 * @param entitatId      Identificador de l'entitat consultada
	 * @param dataInici      Data d'inici de les dades a consultar
	 * @param dataFi         Data final de les dates a consultar
	 * @param metaExpedients Tipus d'expedients consultats
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	List<HistoricExpedientDto> getDadesEntitat(Long entitatId, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'un conjunt d'òrgans gestors dins un
	 * rang de dates definit.
	 * 
	 * @param organGestors   Llistat d'òrgans gestors que es volen consultar
	 * @param dataInici      Data d'inici de les dades a consultar
	 * @param dataFi         Data final de les dates a consultar
	 * @param metaExpedients Tipus d'expedients consultats
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	Map<OrganGestorDto, List<HistoricExpedientDto>> getDadesOrgansGestors(
			HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'un usuari concret dins un rang de dates
	 * definit.
	 * 
	 * @param usuariCodi     Codi de l'usuari que es vol consultar
	 * @param dataInici      Data d'inici de les dades a consultar
	 * @param dataFi         Data final de les dates a consultar
	 * @param metaExpedients Tipus d'expedients consultats
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, HistoricFiltreDto filtre);

	
	/**
	 * Consulta l'històric dels expedients de l'usuari actual dins un rang de dates
	 * definit.
	 * 
	 * @param dataInici      Data d'inici de les dades a consultar
	 * @param dataFi         Data final de les dates a consultar
	 * @param metaExpedients Tipus d'expedients consultats
	 * 
	 * @return
	 */
//	@PreAuthorize("hasRole('IPA_ADMIN')")
//	List<HistoricUsuariDto> getDadesUsuariActual(HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'un interessat concret dins un rang de
	 * dates definit.
	 * 
	 * @param interessatDocNum Número del document de l'interessat que es vol
	 *                         consultar.
	 * @param dataInici        Data d'inici de les dades a consultar
	 * @param dataFi           Data final de les dates a consultar
	 * @param metaExpedients   Tipus d'expedients consultats
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	List<HistoricDto> getDadesInteressat(String interessatDocNum, HistoricFiltreDto filtre);
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, HistoricFiltreDto filtre);
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	Map<OrganGestorDto, HistoricExpedientDto> getDadesActualsOrgansGestors(HistoricFiltreDto filtre);
	

}
