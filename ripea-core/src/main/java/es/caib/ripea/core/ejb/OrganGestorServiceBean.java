package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.OrganGestorFiltreDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.OrganGestorService;


/**
 * Implementació de OrganGestorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class OrganGestorServiceBean implements OrganGestorService{
	@Autowired
	OrganGestorService delegate;
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<OrganGestorDto> findAll()
	{
		return delegate.findAll();
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public OrganGestorDto findItem(Long id) 
	{
		return delegate.findItem(id);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<OrganGestorDto> findByEntitat(Long entitatId)
	{
		return delegate.findByEntitat(entitatId);
	}

	@Override
	public PaginaDto<OrganGestorDto> findOrgansGestorsAmbFiltrePaginat(Long entitatId, OrganGestorFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findOrgansGestorsAmbFiltrePaginat(entitatId, filtre, paginacioParams);
	}

	@Override
	public boolean syncDir3OrgansGestors(Long entitatId) {
		return delegate.syncDir3OrgansGestors(entitatId);
	}

}
