package es.caib.ripea.core.api.service;

import java.io.IOException;
import java.util.List;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.ExpedientTascaComentariDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;

public interface ExpedientTascaService {

	public ExpedientTascaDto findOne(
			Long expedientPeticioId);

	public List<ExpedientTascaDto> findAmbExpedient(
			Long entitatId,
			Long expedientId);

	public List<MetaExpedientTascaDto> findAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId);

	public ExpedientTascaDto createTasca(
			Long entitatId,
			Long expedientId,
			ExpedientTascaDto expedientTasca);

	public MetaExpedientTascaDto findMetaExpedientTascaById(
			Long metaExpedientTascaId);

	public List<ExpedientTascaDto> findAmbAuthentication(
			Long entitatId, 
			PaginacioParamsDto paginacioParam);

	public long countTasquesPendents();


	public ContingutDto findTascaExpedient(
			Long entitatId,
			Long contingutId,
			Long tascaId,
			boolean ambFills,
			boolean ambVersions);



	public DocumentDto findDocumentById(
			Long entitatId,
			Long tascaId,
			Long documentId);


	public ContingutDto deleteTascaReversible(
			Long entitatId,
			Long tascaId,
			Long contingutId) throws IOException;


	public ExpedientTascaDto canviarTascaEstat(
			Long expedientTascaId,
			TascaEstatEnumDto tascaEstatEnumDto,
			String motiu);
	
	public ExpedientTascaDto updateResponsables(
			Long expedientTascaId, 
			List<String> responsablesCodi);

	public List<MetaExpedientTascaDto> findAmbEntitat(Long entitatId);

	public boolean publicarComentariPerExpedientTasca(
			Long entitatId,
			Long expedientTascaId,
			String text,
			String rolActual);

	public List<ExpedientTascaComentariDto> findComentarisPerTasca(
			Long entitatId, 
			Long expedientTascaId);
}
