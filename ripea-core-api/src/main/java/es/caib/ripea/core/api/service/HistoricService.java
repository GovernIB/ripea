package es.caib.ripea.core.api.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricFiltreDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;

/**
 * Declaració dels mètodes per a la consulta de l'històric
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HistoricService {

	public void generateOldHistorics();
	
	/**
	 * Consulta l'històric dels expedients d'una entitat concreta dins un rang de
	 * dates definit. 
	 * Retorna els resultats paginats segons els valor del parèmetre paginacioParams.
	 * 
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param filtre         Configuració de la selecció de històrics a consultar
	 * @param paginacioParams Parametre amb la configuració de la paginació de la consulta.
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
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param filtre         Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_API_HIST') ")
	List<HistoricExpedientDto> getDadesEntitat(Long entitatId, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients agrupats per data i per organ gestor.
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_API_HIST') ")
	Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> getDadesOrgansGestors(HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'un conjunt d'òrgans gestors agrupats per organs gestors.
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN')")
	Map<OrganGestorDto, List<HistoricExpedientDto>> getHistoricsByOrganGestor(HistoricFiltreDto filtre);
	
	/**
	 * Consulta l'històric dels expedients d'un usuari concret.
	 * 
	 * @param usuariCodi     Codi de l'usuari que es vol consultar
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_API_HIST') ")
	List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'un interessat concret.
	 * 
	 * @param interessatDocNum Número del document de l'interessat que es vol
	 *                         consultar.
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_API_HIST') ")
	List<HistoricInteressatDto> getDadesInteressat(String interessatDocNum, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'una entitat concreta per el dia d'avui.
	 * 
	 * @param interessatDocNum Número del document de l'interessat que es vol
	 *                         consultar.
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_API_HIST') ")
	List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients del dia d'avui agrupats per òrgans gestors.
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_API_HIST') ")
	Map<OrganGestorDto, HistoricExpedientDto> getDadesActualsOrgansGestors(HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'una entitat concreta per el dia d'avui.
	 * 
	 * @param usuariCodi     Codi de l'usuari que es vol consultar
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_API_HIST') ")
	List<HistoricUsuariDto> getDadesActualsUsuari(String codiUsuari, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'un usuari concret per el dia d'avui.
	 * 
	 * @param interessatDocNum	Nombre del document de l'interessat a consultar.
	 * @param filtre    		Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("hasRole('IPA_ADMIN') or hasRole('IPA_API_HIST') ")
	List<HistoricInteressatDto> getDadesActualsInteressat(String interessatDocNum, HistoricFiltreDto filtre);

}
