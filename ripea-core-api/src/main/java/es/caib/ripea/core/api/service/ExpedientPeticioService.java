package es.caib.ripea.core.api.service;

import java.util.List;

import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioPendentDist;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioListDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MassiuAnnexProcesarFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientSelectDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.RegistreDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;

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
	
	@PreAuthorize("hasRole('IPA_ADMIN')")
	public ResultDto<RegistreAnnexDto> findAnnexosPendentsProcesarMassiu(
			Long entitatId,
			MassiuAnnexProcesarFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			ResultEnumDto resultEnum) throws NotFoundException;


	@PreAuthorize("hasRole('IPA_ADMIN')")
	PaginaDto<ExpedientPeticioPendentDist> findPendentsCanviEstatAnotacioDistribucio(Long entitatId, ContingutMassiuFiltreDto filtre, PaginacioParamsDto paginacioParams);
 }
