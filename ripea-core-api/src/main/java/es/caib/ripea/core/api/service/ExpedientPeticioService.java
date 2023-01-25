package es.caib.ripea.core.api.service;

import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.exception.NotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ExpedientPeticioService {

	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<ExpedientPeticioListDto> findAmbFiltre(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual, Long organActualId);

	@PreAuthorize("hasRole('tothom')")
	public ExpedientPeticioDto findOne(
			Long expedientPeticioId);

	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getAnnexContent(
			Long annexId, boolean versioImprimible);

	@PreAuthorize("hasRole('tothom')")
	public List<ArxiuFirmaDto> annexFirmaInfo(
			String fitxerArxiuUuid);

	@PreAuthorize("hasRole('tothom')")
	public RegistreAnnexDto findAnnexById(
			Long annexId);

	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getAnnexFirmaContingut(
			Long annexId);

	@PreAuthorize("hasRole('tothom')")
	public void rebutjar(
			Long expedientPeticioId,
			String observacions);

	@PreAuthorize("hasRole('tothom')")
	public MetaExpedientDto findMetaExpedientByEntitatAndProcedimentCodi(String entitatCodi,
			String procedimentCodi);

	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientSelectDto> findMetaExpedientSelect(String entitatCodi);

	@PreAuthorize("hasRole('tothom')")
	public RegistreDto findRegistreById(Long registreId);

	@PreAuthorize("hasRole('tothom')")
	public List<ExpedientPeticioListDto> findByExpedientAmbFiltre(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParams);

	@PreAuthorize("hasRole('tothom')")
	public ExpedientDto findByEntitatAndMetaExpedientAndExpedientNumero(Long entitatId,
			Long metaExpedientId,
			String expedientNumero);

	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getJustificantContent(String arxiuUuid);

	@PreAuthorize("hasRole('tothom')")
	public long countAnotacionsPendents(Long entitatId, String rolActual, Long organActualId);

	@PreAuthorize("hasRole('tothom')")
	public boolean comprovarExistenciaInteressatsPeticio(
			Long entitatId, 
			Long expedientId, 
			Long expedientPeticioId);
	
	@PreAuthorize("hasRole('tothom')")
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

	@PreAuthorize("hasRole('tothom')")
	public List<MetaExpedientDto> findMetaExpedientsPermesosPerAnotacions(
			Long entitatId,
			Long organActualId,
			String rolActual);

	@PreAuthorize("hasRole('tothom')")
	public void canviarProcediment(
			Long expedientPeticioId,
			Long procedimentId);

	@PreAuthorize("hasRole('tothom')")
	public void retornarPendent(Long expedientPeticioId);
 }
