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

import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.service.UnitatOrganitzativaService;

/**
 * Implementació de UnitatsOrganitzativesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class UnitatOrganitzativaServiceBean implements UnitatOrganitzativaService {

	@Autowired
	private UnitatOrganitzativaService delegate;

	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) {
		return delegate.findByEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public UnitatOrganitzativaDto findByCodi(
			String codi) {
		return delegate.findByCodi(codi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> findByFiltre(
			String codiDir3, 
			String denominacio, 
			String nivellAdm,
			String comunitat, 
			String provincia, 
			String municipi, 
			Boolean arrel) {
		return delegate.findByFiltre(
				codiDir3,
				denominacio,
				nivellAdm,
				comunitat,
				provincia,
				municipi,
				arrel);
	}

	/*@Autowired
	UnitatsOrganitzativesService delegate;

	@Override
	@RolesAllowed("tothom")
	public UnitatOrganitzativaDto findUnitatOrganitzativaByCodi(String codi) throws NotFoundException {
		return delegate.findUnitatOrganitzativaByCodi(codi);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<UnitatOrganitzativaD3Dto> findUnitatsOrganitzativesPerDatatable(UnitatsFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.findUnitatsOrganitzativesPerDatatable(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<LocalitatDto> findLocalitatsPerProvincia(String codiProvincia) throws NotFoundException {
		return delegate.findLocalitatsPerProvincia(codiProvincia);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ProvinciaRw3Dto> findProvinciesPerComunitat(String codiComunitat) throws NotFoundException {
		return delegate.findProvinciesPerComunitat(codiComunitat);
	}*/
	
}
