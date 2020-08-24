/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.MetaDadaDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.MetaDadaService;

/**
 * Implementació de MetaDadaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MetaDadaServiceBean implements MetaDadaService {

	@Autowired
	MetaDadaService delegate;



	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto create(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada) {
		return delegate.create(
				entitatId,
				metaNodeId,
				metaDada);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto update(
			Long entitatId,
			Long metaNodeId,
			MetaDadaDto metaDada) {
		return delegate.update(
				entitatId,
				metaNodeId,
				metaDada);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto updateActiva(
			Long entitatId,
			Long metaNodeId,
			Long id,
			boolean activa) {
		return delegate.updateActiva(
				entitatId,
				metaNodeId,
				id,
				activa);
	}

	@Override
	public void moveUp(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId) throws NotFoundException {
		delegate.moveUp(
				entitatId,
				metaNodeId,
				metaDadaId);
	}

	@Override
	public void moveDown(
			Long entitatId,
			Long metaNodeId,
			Long metaDadaId) throws NotFoundException {
		delegate.moveDown(
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
		delegate.moveTo(
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
			Long id) {
		return delegate.delete(
				entitatId,
				metaNodeId,
				id);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto findById(
			Long entitatId,
			Long metaNodeId,
			Long id) {
		return delegate.findById(
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
		return delegate.findByCodi(
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
		return delegate.findByMetaNodePaginat(
				entitatId,
				metaNodeId,
				paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDadaDto> findActiveByMetaNode(
			Long entitatId,
			Long metaNodeId) {
		return delegate.findActiveByMetaNode(
				entitatId,
				metaNodeId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDadaDto> findByNode(
			Long entitatId,
			Long nodeId) {
		return delegate.findByNode(
				entitatId,
				nodeId);
	}

	@Override
	@RolesAllowed("tothom")
	public Long findMetaNodeIdByNodeId(
			Long entitatId,
			Long nodeId) {
		return delegate.findMetaNodeIdByNodeId(
				entitatId,
				nodeId);
	}

}