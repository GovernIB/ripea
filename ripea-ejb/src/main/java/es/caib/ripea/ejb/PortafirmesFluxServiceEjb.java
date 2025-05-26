/**
 *
 */
package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.PortafirmesCarrecDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.service.intf.service.PortafirmesFluxService;
import lombok.experimental.Delegate;

@Stateless
public class PortafirmesFluxServiceEjb extends AbstractServiceEjb<PortafirmesFluxService> implements PortafirmesFluxService {

	@Delegate private PortafirmesFluxService delegateService;

	protected void setDelegateService(PortafirmesFluxService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn, 
			boolean isPlantilla) {
		return delegateService.iniciarFluxFirma(
				urlReturn, 
				isPlantilla);
	}

	@Override
	@RolesAllowed("**")
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId) {
		return delegateService.recuperarFluxFirma(transactionId);
	}

	@Override
	@RolesAllowed("**")
	public void tancarTransaccio(String idTransaccio) {
		delegateService.tancarTransaccio(idTransaccio);
	}

	@Override
	@RolesAllowed("**")
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String idTransaccio, boolean signerInfo) {
		return delegateService.recuperarDetallFluxFirma(idTransaccio, signerInfo);
	}

	@Override
	@RolesAllowed("**")
	public String recuperarUrlMostrarPlantilla(String plantillaFluxId) {
		return delegateService.recuperarUrlMostrarPlantilla(plantillaFluxId);
	}

	@Override
	@RolesAllowed("**")
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long entitatId, String rolActual, boolean filtrar) {
		return delegateService.recuperarPlantillesDisponibles(entitatId, rolActual, filtrar);
	}
	@Override
	@RolesAllowed("**")
	public String recuperarUrlEdicioPlantilla(String plantillaFluxId, String returnUrl) {
		return delegateService.recuperarUrlEdicioPlantilla(plantillaFluxId, returnUrl);
	}

	@Override
	@RolesAllowed("**")
	public boolean esborrarPlantilla(String plantillaFluxId) {
		return delegateService.esborrarPlantilla(plantillaFluxId);
	}

	@Override
	@RolesAllowed("**")
	public List<PortafirmesCarrecDto> recuperarCarrecs() {
		return delegateService.recuperarCarrecs();
	}
}