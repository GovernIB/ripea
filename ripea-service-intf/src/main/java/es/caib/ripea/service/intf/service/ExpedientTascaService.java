package es.caib.ripea.service.intf.service;

import es.caib.ripea.service.intf.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.List;

@PreAuthorize("isAuthenticated()")
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
	
	public List<MetaExpedientTascaValidacioDto> getValidacionsPendentsTasca(Long expedientTascaId);
	
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
	
	public ExpedientTascaDto reobrirTasca(
			Long expedientTascaId, 
			List<String> responsablesCodi,
			String motiu, 
			String rolActual);
	
	public ExpedientTascaDto updateDataLimit(ExpedientTascaDto expedientTascaDto);
	
	public List<MetaExpedientTascaDto> findAmbEntitat(Long entitatId);

	public boolean publicarComentariPerExpedientTasca(
			Long entitatId,
			Long expedientTascaId,
			String text,
			String rolActual);

	public List<ExpedientTascaComentariDto> findComentarisPerTasca(
			Long entitatId, 
			Long expedientTascaId);
	
	@PreAuthorize("isAuthenticated()")
	public ContingutDto findByTascaBasicInfo(Long contingutId, Long tascaId);

	@PreAuthorize("isAuthenticated()")
	public void changeTascaPrioritat(ExpedientTascaDto expedientTascaDto);
}