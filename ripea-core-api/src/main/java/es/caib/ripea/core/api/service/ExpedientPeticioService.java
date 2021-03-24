package es.caib.ripea.core.api.service;

import java.util.List;

import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientSelectDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.RegistreDto;

public interface ExpedientPeticioService {

	PaginaDto<ExpedientPeticioDto> findAmbFiltre(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	ExpedientPeticioDto findOne(
			Long expedientPeticioId);

	FitxerDto getAnnexContent(
			Long annexId);

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

	List<ExpedientPeticioDto> findByExpedientAmbFiltre(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParams);

	ExpedientDto findByEntitatAndMetaExpedientAndExpedientNumero(Long entitatId,
			Long metaExpedientId,
			String expedientNumero);

	FitxerDto getJustificantContent(String arxiuUuid);




}
