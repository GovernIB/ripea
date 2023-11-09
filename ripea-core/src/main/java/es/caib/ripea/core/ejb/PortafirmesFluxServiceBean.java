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

import es.caib.ripea.core.api.dto.PortafirmesCarrecDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.core.api.service.PortafirmesFluxService;

/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class PortafirmesFluxServiceBean implements PortafirmesFluxService {

	@Autowired
	PortafirmesFluxService delegate;


	@Override
	@RolesAllowed("tothom")
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn, 
			boolean isPlantilla) {
		return delegate.iniciarFluxFirma(
				urlReturn, 
				isPlantilla);
	}
	
	@Override
	@RolesAllowed("tothom")
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId) {
		return delegate.recuperarFluxFirma(transactionId);
	}

	@Override
	@RolesAllowed("tothom")
	public void tancarTransaccio(String idTransaccio) {
		delegate.tancarTransaccio(idTransaccio);
	}

	@Override
	@RolesAllowed("tothom")
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String idTransaccio, boolean signerInfo) {
		return delegate.recuperarDetallFluxFirma(idTransaccio, signerInfo);
	}

	@Override
	@RolesAllowed("tothom")
	public String recuperarUrlMostrarPlantilla(String plantillaFluxId) {
		return delegate.recuperarUrlMostrarPlantilla(plantillaFluxId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long entitatId, String rolActual, boolean filtrar) {
		return delegate.recuperarPlantillesDisponibles(entitatId, rolActual, filtrar);
	}
	@Override
	@RolesAllowed("tothom")
	public String recuperarUrlEdicioPlantilla(String plantillaFluxId, String returnUrl) {
		return delegate.recuperarUrlEdicioPlantilla(plantillaFluxId, returnUrl);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean esborrarPlantilla(String plantillaFluxId) {
		return delegate.esborrarPlantilla(plantillaFluxId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<PortafirmesCarrecDto> recuperarCarrecs() {
		return delegate.recuperarCarrecs();
	}
}