package es.caib.ripea.core.api.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ExpedientTascaComentariDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariTascaFiltreDto;

public interface ExpedientTascaService {

	public ExpedientTascaDto findOne(
			Long expedientPeticioId);

	public List<ExpedientTascaDto> findAmbExpedient(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParam);

	public List<MetaExpedientTascaDto> findAmbMetaExpedient(
			Long entitatId,
			Long metaExpedientId);

	public ExpedientTascaDto createTasca(
			Long entitatId,
			Long expedientId,
			ExpedientTascaDto expedientTasca);

	public MetaExpedientTascaDto findMetaExpedientTascaById(
			Long metaExpedientTascaId);

	public PaginaDto<ExpedientTascaDto> findAmbAuthentication(
			Long entitatId, 
			UsuariTascaFiltreDto filtre, 
			PaginacioParamsDto paginacioParam);

	public long countTasquesPendents();


	public ContingutDto findTascaExpedient(
			Long entitatId,
			Long contingutId,
			Long tascaId,
			boolean ambFills,
			boolean ambVersions);

	public void deleteTascaReversible(
			Long entitatId,
			Long tascaId,
			Long contingutId) throws IOException;


	public ExpedientTascaDto canviarTascaEstat(
			Long expedientTascaId,
			TascaEstatEnumDto tascaEstatEnumDto,
			String motiu, 
			String rolActual);
	
	public ExpedientTascaDto updateResponsables(
			Long expedientTascaId, 
			List<String> responsablesCodi);

	public ExpedientTascaDto updateDelegat(
			Long expedientTascaId, 
			String delegatCodi,
			String comentari);
	
	public ExpedientTascaDto cancelarDelegacio(
			Long expedientTascaId,
			String comentari);
	
	public ExpedientTascaDto updateDataLimit(
			Long expedientTascaId, 
			Date dataLimit);
	
	public List<MetaExpedientTascaDto> findAmbEntitat(Long entitatId);

	public boolean publicarComentariPerExpedientTasca(
			Long entitatId,
			Long expedientTascaId,
			String text,
			String rolActual);

	public List<ExpedientTascaComentariDto> findComentarisPerTasca(
			Long entitatId, 
			Long expedientTascaId);
	
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto findByTascaBasicInfo(Long contingutId, Long tascaId);

	@PreAuthorize("hasRole('tothom')")
	public void changeTascaPrioritat(ExpedientTascaDto expedientTascaDto);
}