/**
 *
 */
package es.caib.ripea.core.ejb;

import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.stereotype.Service;

import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.service.MetaDocumentFluxService;

/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MetaDocumentFluxServiceBean implements MetaDocumentFluxService {

	@Autowired
	MetaDocumentFluxService delegate;

	@Override
	@RolesAllowed("IPA_ADMIN")
	public Map<String, String> iniciarFluxFirma(
			String urlReturn,
			String tipusDocumentNom) {
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

}