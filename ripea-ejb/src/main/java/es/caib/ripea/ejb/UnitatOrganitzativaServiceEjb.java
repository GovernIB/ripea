/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.UnitatOrganitzativaDto;
import es.caib.ripea.service.intf.service.UnitatOrganitzativaService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de UnitatsOrganitzativesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class UnitatOrganitzativaServiceEjb implements UnitatOrganitzativaService {

	@Delegate
	private UnitatOrganitzativaService delegateService;

	protected void delegate(UnitatOrganitzativaService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) {
		return delegateService.findByEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public UnitatOrganitzativaDto findByCodi(
			String codi) {
		return delegateService.findByCodi(codi);
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
