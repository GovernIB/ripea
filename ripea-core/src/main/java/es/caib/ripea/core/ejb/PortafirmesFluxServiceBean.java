/**
 *
 */
package es.caib.ripea.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.stereotype.Service;

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
	@RolesAllowed("IPA_ADMIN")
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(String urlReturn, String tipusDocumentNom) {
		return delegate.iniciarFluxFirma(
				urlReturn, 
				tipusDocumentNom);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transactionId) {
		return delegate.recuperarFluxFirma(transactionId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void tancarTransaccio(String idTransaccio) {
		delegate.tancarTransaccio(idTransaccio);
	}

	@Override
	@RolesAllowed("tothom")
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String idTransaccio) {
		return delegate.recuperarDetallFluxFirma(idTransaccio);
	}

}