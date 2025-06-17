package es.caib.ripea.service.intf.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.ws.backoffice.AnotacioRegistreId;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDto;
import es.caib.ripea.service.intf.dto.ExpedientDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioListDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.MassiuAnnexProcesarFiltreDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.dto.MetaExpedientSelectDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.RegistreAnnexDto;
import es.caib.ripea.service.intf.dto.RegistreDto;
import es.caib.ripea.service.intf.dto.ResultDto;
import es.caib.ripea.service.intf.dto.ResultEnumDto;
import es.caib.ripea.service.intf.exception.NotFoundException;

@PreAuthorize("isAuthenticated()")
public interface ExpedientPeticioService {

	@PreAuthorize("hasRole('IPA_BSTWS')")
	public void crearExpedientPeticion(List<AnotacioRegistreId> anotacioRegistreIds);

	@PreAuthorize("isAuthenticated()")
	public void crearExpedientPeticion(String clauAcces, String itentificador);
	
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<ExpedientPeticioListDto> findAmbFiltre(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual, Long organActualId);

	@PreAuthorize("isAuthenticated()")
	public ExpedientPeticioDto findOne(
			Long expedientPeticioId);

	@PreAuthorize("isAuthenticated()")
	public FitxerDto getAnnexContent(
			Long annexId, boolean versioImprimible);

	@PreAuthorize("isAuthenticated()")
	public List<ArxiuFirmaDto> annexFirmaInfo(
			String fitxerArxiuUuid);

	@PreAuthorize("isAuthenticated()")
	public RegistreAnnexDto findAnnexById(
			Long annexId);

	@PreAuthorize("isAuthenticated()")
	public FitxerDto getAnnexFirmaContingut(
			Long annexId);

	@PreAuthorize("isAuthenticated()")
	public void rebutjar(
			Long expedientPeticioId,
			String observacions);

	@PreAuthorize("isAuthenticated()")
	public List<MetaExpedientSelectDto> findMetaExpedientSelect(String entitatCodi);

	@PreAuthorize("isAuthenticated()")
	public RegistreDto findRegistreById(Long registreId);

	@PreAuthorize("isAuthenticated()")
	public List<ExpedientPeticioListDto> findByExpedientAmbFiltre(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("isAuthenticated()")
	public ExpedientDto findByEntitatAndMetaExpedientAndExpedientNumero(Long entitatId,
	                                                                    Long metaExpedientId,
	                                                                    String expedientNumero);

	@PreAuthorize("isAuthenticated()")
	public FitxerDto getJustificantContent(String arxiuUuid);

	@PreAuthorize("isAuthenticated()")
	public long countAnotacionsPendents(Long entitatId, String rolActual, Long organActualId);

	@PreAuthorize("isAuthenticated()")
	public boolean comprovarExistenciaInteressatsPeticio(
			Long entitatId, 
			Long expedientId, 
			Long expedientPeticioId);
	
	@PreAuthorize("isAuthenticated()")
	public ResultDto<RegistreAnnexDto> findAnnexosPendentsProcesarMassiu(
			Long entitatId,
			MassiuAnnexProcesarFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			ResultEnumDto resultEnum,
			String rolActual, 
			Long organActualId) throws NotFoundException;


	@PreAuthorize("hasRole('IPA_ADMIN')")
	ResultDto<ExpedientPeticioListDto> findPendentsCanviEstatDistribucio(Long entitatId, ExpedientPeticioFiltreDto filtre, PaginacioParamsDto paginacioParams, ResultEnumDto resultEnum);


	@PreAuthorize("hasRole('IPA_ADMIN')")
	Exception canviarEstatAnotacioDistribucio(Long entitatId, Long id);

	@PreAuthorize("isAuthenticated()")
	public List<MetaExpedientDto> findMetaExpedientsPermesosPerAnotacions(
			Long entitatId,
			Long organActualId,
			String rolActual);

	@PreAuthorize("isAuthenticated()")
	public void canviarProcediment(
			Long expedientPeticioId,
			Long procedimentId, 
			Long grupId);

	@PreAuthorize("isAuthenticated()")
	public void retornarPendent(Long expedientPeticioId);

	@PreAuthorize("isAuthenticated()")
	public void evictCountAnotacionsPendents(
			Long entitatId);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public ResultDto<ExpedientPeticioListDto> findComunicadesAmbFiltre(
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum);

	@PreAuthorize("hasRole('IPA_ADMIN')")
	public void comunicadaReprocessar(
			Long expedientPeticioId) throws Throwable;

	@PreAuthorize("isAuthenticated()")
    public Long getPeriodeActualitzacioContadorAnotacionsPendents();
}
