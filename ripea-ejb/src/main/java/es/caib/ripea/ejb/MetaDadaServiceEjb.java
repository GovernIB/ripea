/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.MetaDadaDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.MetaDadaService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de MetaDadaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MetaDadaServiceEjb implements MetaDadaService {

	@Delegate
	private MetaDadaService delegateService;

	protected void setDelegateService(MetaDadaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto create(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada, String rolActual, Long organId) {
		return delegateService.create(
				entitatId,
				metaNodeId,
				metaDada, rolActual, organId);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto update(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada, String rolActual, Long organId) {
		return delegateService.update(
				entitatId,
				metaNodeId,
				metaDada, rolActual, organId);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto updateActiva(
			Long entitatId,
			Long metaNodeId,
			Long id,
			boolean activa, String rolActual, Long organId) {
		return delegateService.updateActiva(
				entitatId,
				metaNodeId,
				id,
				activa, rolActual, organId);
	}

	@Override
	public void moveUp(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId) throws NotFoundException {
		delegateService.moveUp(
				entitatId,
				metaNodeId,
				metaDadaId);
	}

	@Override
	public void moveDown(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId) throws NotFoundException {
		delegateService.moveDown(
				entitatId,
				metaNodeId,
				metaDadaId);
	}

	@Override
	public void moveTo(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId,
			int posicio) throws NotFoundException {
		delegateService.moveTo(
				entitatId,
				metaNodeId,
				metaDadaId,
				posicio);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto delete(
			Long entitatId,
			Long metaNodeId,
			Long id, String rolActual, Long organId) {
		return delegateService.delete(
				entitatId,
				metaNodeId,
				id, rolActual, organId);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto findById(
			Long entitatId,
			Long metaNodeId,
			Long id) {
		return delegateService.findById(
				entitatId,
				metaNodeId,
				id);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto findByCodi(
			Long entitatId,
			Long metaNodeId,
			String codi) {
		return delegateService.findByCodi(
				entitatId,
				metaNodeId,
				codi);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<MetaDadaDto> findByMetaNodePaginat(
			Long entitatId,
			Long metaNodeId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findByMetaNodePaginat(
				entitatId,
				metaNodeId,
				paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDadaDto> findActiveByMetaNode(
			Long entitatId,
			Long metaNodeId) {
		return delegateService.findActiveByMetaNode(
				entitatId,
				metaNodeId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDadaDto> findByNode(
			Long entitatId,
			Long nodeId) {
		return delegateService.findByNode(
				entitatId,
				nodeId);
	}

	@Override
	@RolesAllowed("tothom")
	public Long findMetaNodeIdByNodeId(
			Long entitatId,
			Long nodeId) {
		return delegateService.findMetaNodeIdByNodeId(
				entitatId,
				nodeId);
	}

}