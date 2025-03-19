/**
 * 
 */
package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.ComunitatDto;
import es.caib.ripea.service.intf.dto.MunicipiDto;
import es.caib.ripea.service.intf.dto.NivellAdministracioDto;
import es.caib.ripea.service.intf.dto.PaisDto;
import es.caib.ripea.service.intf.dto.ProvinciaDto;
import es.caib.ripea.service.intf.service.DadesExternesService;
import lombok.experimental.Delegate;

@Stateless
public class DadesExternesServiceEjb extends AbstractServiceEjb<DadesExternesService> implements DadesExternesService {

	@Delegate private DadesExternesService delegateService;

	protected void setDelegateService(DadesExternesService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public List<PaisDto> findPaisos() {
		return delegateService.findPaisos();
	}
	
	@Override
	@RolesAllowed("**")
	public List<ComunitatDto> findComunitats() {
		return delegateService.findComunitats();
	}
	
	@Override
	@RolesAllowed("**")
	public List<ProvinciaDto> findProvincies() {
		return delegateService.findProvincies();
	}
	
	@Override
	@RolesAllowed("**")
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) {
		return delegateService.findProvinciesPerComunitat(comunitatCodi);
	}

	@Override
	@RolesAllowed("**")
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) {
		return delegateService.findMunicipisPerProvincia(provinciaCodi);
	}

	@Override
	@RolesAllowed("**")
	public List<NivellAdministracioDto> findNivellAdministracions() {
		return delegateService.findNivellAdministracions();
	}

	@Override
	@RolesAllowed("**")
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(String provinciaCodi) {
		return delegateService.findMunicipisPerProvinciaPinbal(provinciaCodi);
	}

}
