package es.caib.ripea.core.api.service;

import java.util.List;

import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioListDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientSelectDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.RegistreDto;

public interface ExpedientPeticioService {

	PaginaDto<ExpedientPeticioListDto> findAmbFiltre(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual, Long organActualId);

	ExpedientPeticioDto findOne(
			Long expedientPeticioId);

	FitxerDto getAnnexContent(
			Long annexId, boolean versioImprimible);

	List<ArxiuFirmaDto> annexFirmaInfo(
			String fitxerArxiuUuid);

	RegistreAnnexDto findAnnexById(
			Long annexId);

	FitxerDto getAnnexFirmaContingut(
			Long annexId);

	void rebutjar(
			Long expedientPeticioId,
			String observacions);


	MetaExpedientDto findMetaExpedientByEntitatAndProcedimentCodi(String entitatCodi,
			String procedimentCodi);

	List<MetaExpedientSelectDto> findMetaExpedientSelect(String entitatCodi);

	RegistreDto findRegistreById(Long registreId);

	List<ExpedientPeticioListDto> findByExpedientAmbFiltre(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParams);

	ExpedientDto findByEntitatAndMetaExpedientAndExpedientNumero(Long entitatId,
			Long metaExpedientId,
			String expedientNumero);

	FitxerDto getJustificantContent(String arxiuUuid);

	public long countAnotacionsPendents(Long entitatId, String rolActual, Long organActualId);

	boolean comprovarExistenciaInteressatsPeticio(
			Long entitatId, 
			Long expedientId, 
			Long expedientPeticioId);
}
