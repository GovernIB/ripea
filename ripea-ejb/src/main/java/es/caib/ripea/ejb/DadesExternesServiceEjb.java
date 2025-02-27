/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.DadesExternesService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de DadesExternesService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DadesExternesServiceEjb implements DadesExternesService {

	@Delegate
	private DadesExternesService delegateService;

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
