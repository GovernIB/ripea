/**
 * 
 */
package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.service.UnitatOrganitzativaService;
import lombok.experimental.Delegate;

@Stateless
public class UnitatOrganitzativaServiceEjb extends AbstractServiceEjb<UnitatOrganitzativaService> implements UnitatOrganitzativaService {

	@Delegate private UnitatOrganitzativaService delegateService;

	protected void setDelegateService(UnitatOrganitzativaService delegateService) {
		this.delegateService = delegateService;
	}

	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) {
		return delegateService.findByEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed("**")
	public UnitatOrganitzativaDto findByCodi(
			String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByFiltre(
			String codiDir3, 
			String denominacio, 
			String nivellAdm,
			String comunitat, 
			String provincia, 
			String municipi, 
			Boolean arrel) {
		return delegateService.findByFiltre(
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
	@RolesAllowed("**")
	public UnitatOrganitzativaDto findUnitatOrganitzativaByCodi(String codi) throws NotFoundException {
		return delegate.findUnitatOrganitzativaByCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<UnitatOrganitzativaD3Dto> findUnitatsOrganitzativesPerDatatable(UnitatsFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.findUnitatsOrganitzativesPerDatatable(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<LocalitatDto> findLocalitatsPerProvincia(String codiProvincia) throws NotFoundException {
		return delegate.findLocalitatsPerProvincia(codiProvincia);
	}

	@Override
	@RolesAllowed("**")
	public List<ProvinciaRw3Dto> findProvinciesPerComunitat(String codiComunitat) throws NotFoundException {
		return delegate.findProvinciesPerComunitat(codiComunitat);
	}*/
	
}
