package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.dto.historic.*;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricInteressatSerializer.RegistresInteressatDiari;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer.RegistreOrganGestor;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer.RegistresOrganGestor;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricUsuariSerializer.RegistresUsuariDiari;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
	 * @param rolActual 	 Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 * @param paginacioParams Parametre amb la configuració de la paginació de la consulta.
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	PaginaDto<HistoricExpedientDto> getPageDadesEntitat(
			Long entitatId,
			HistoricFiltreDto filtre,
			String rolActual, 
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta l'històric dels expedients d'una entitat concreta dins un rang de
	 * dates definit.
	 * 
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param rolActual 	 Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 * @param filtre         Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	List<HistoricExpedientDto> getDadesEntitat(Long entitatId, String rolActual, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients agrupats per data i per organ gestor.
	 * 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> getDadesOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre);

	@PreAuthorize("isAuthenticated()")
	List<RegistresOrganGestor> getRegistresDadesOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament);

	/**
	 * Consulta l'històric dels expedients d'un conjunt d'òrgans gestors agrupats per organs gestors.
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param rolActual 	 Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Map<OrganGestorDto, List<HistoricExpedientDto>> getHistoricsByOrganGestor(Long entitatId, String rolActual, HistoricFiltreDto filtre);
	
	/**
	 * Consulta l'històric dels expedients d'un usuari concret.
	 * 
	 * @param usuariCodi     Codi de l'usuari que es vol consultar
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param rolActual 	 Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	List<HistoricUsuariDto> getDadesUsuari(String usuariCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre);

	@PreAuthorize("isAuthenticated()")
	List<RegistresUsuariDiari> getRegistresDadesUsuaris(List<String> usuarisCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament);

	/**
	 * Consulta l'històric dels expedients d'un interessat concret.
	 * 
	 * @param interessatDocNum Número del document de l'interessat que es vol
	 *                         consultar.
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param rolActual 	 Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	List<HistoricInteressatDto> getDadesInteressat(String interessatDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre);

	@PreAuthorize("isAuthenticated()")
	List<RegistresInteressatDiari> getRegistresDadesInteressat(List<String> interessatsDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre, HistoricTipusEnumDto tipusAgrupament);

	/**
	 * Consulta l'històric dels expedients d'una entitat concreta per el dia d'avui.
	 * 
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param rolActual Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	List<HistoricExpedientDto> getDadesActualsEntitat(Long entitatId, String rolActual, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients del dia d'avui agrupats per òrgans gestors.
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param rolActual 	 Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Map<OrganGestorDto, HistoricExpedientDto> getDadesActualsOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre);

	@PreAuthorize("isAuthenticated()")
	List<RegistreOrganGestor> getRegistresDadesActualsOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'una entitat concreta per el dia d'avui.
	 * 
	 * @param codiUsuari     Codi de l'usuari que es vol consultar
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param rolActual 	 Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 * @param filtre    Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	List<HistoricUsuariDto> getDadesActualsUsuari(String codiUsuari, Long entitatId, String rolActual, HistoricFiltreDto filtre);

	/**
	 * Consulta l'històric dels expedients d'un usuari concret per el dia d'avui.
	 * 
	 * @param interessatDocNum	Nombre del document de l'interessat a consultar.
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param rolActual 	 Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 * @param filtre    		Configuració de la selecció de històrics a consultar
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	List<HistoricInteressatDto> getDadesActualsInteressat(String interessatDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre);

	/**
	 * Comprova si l'usuari actual disposa de permisos per consultar les estadístiques d'algún expedient.
	 * 
	 * @param entitatId      Identificador de l'entitat a consultar
	 * @param rolActual 	 Rol usuari actual (expedients permís consulta en cas d'usuaris)
	 * 
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	List<Long> comprovarAccesEstadistiques(Long entitatId, String rolActual);

	@PreAuthorize("isAuthenticated()")
	FitxerDto exportarHistoricEntitat(EntitatDto entitat, String rolActual, HistoricFiltreDto filtre, String format) throws Exception;

	@PreAuthorize("isAuthenticated()")
	public FitxerDto exportarHistoricOrgansGestors(Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception;

	@PreAuthorize("isAuthenticated()")
	FitxerDto exportarHistoricUsuaris(String[] usuarisCodi, Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception;

	@PreAuthorize("isAuthenticated()")
	FitxerDto exportarHistoricInteressats(String[] interessatsDocNum, Long entitatId, String rolActual, HistoricFiltreDto filtre, String format) throws Exception;


}
