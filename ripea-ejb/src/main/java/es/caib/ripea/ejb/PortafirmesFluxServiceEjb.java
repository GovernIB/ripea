/**
 *
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.PortafirmesCarrecDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.service.intf.service.PortafirmesFluxService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class PortafirmesFluxServiceEjb implements PortafirmesFluxService {

	@Delegate
	private PortafirmesFluxService delegateService;

	protected void delegate(PortafirmesFluxService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn, 
			boolean isPlantilla) {
		return delegateService.iniciarFluxFirma(
				urlReturn, 
				isPlantilla);
	}
	
	@Override
	@RolesAllowed("tothom")
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId) {
		return delegateService.recuperarFluxFirma(transactionId);
	}

	@Override
	@RolesAllowed("tothom")
	public void tancarTransaccio(String idTransaccio) {
		delegateService.tancarTransaccio(idTransaccio);
	}

	@Override
	@RolesAllowed("tothom")
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String idTransaccio, boolean signerInfo) {
		return delegateService.recuperarDetallFluxFirma(idTransaccio, signerInfo);
	}

	@Override
	@RolesAllowed("tothom")
	public String recuperarUrlMostrarPlantilla(String plantillaFluxId) {
		return delegateService.recuperarUrlMostrarPlantilla(plantillaFluxId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long entitatId, String rolActual, boolean filtrar) {
		return delegateService.recuperarPlantillesDisponibles(entitatId, rolActual, filtrar);
	}
	@Override
	@RolesAllowed("tothom")
	public String recuperarUrlEdicioPlantilla(String plantillaFluxId, String returnUrl) {
		return delegateService.recuperarUrlEdicioPlantilla(plantillaFluxId, returnUrl);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean esborrarPlantilla(String plantillaFluxId) {
		return delegateService.esborrarPlantilla(plantillaFluxId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<PortafirmesCarrecDto> recuperarCarrecs() {
		return delegateService.recuperarCarrecs();
	}
}