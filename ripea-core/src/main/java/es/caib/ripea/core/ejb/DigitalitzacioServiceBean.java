package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.core.api.dto.DigitalitzacioResultatDto;
import es.caib.ripea.core.api.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.core.api.service.DigitalitzacioService;

/**
 * Implementació de DigitalitzacioService que empra una clase delegada per accedir a la
 * funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DigitalitzacioServiceBean implements DigitalitzacioService {

	@Autowired
	DigitalitzacioService delegate;
	
	@Override
	@RolesAllowed("tothom")
	public List<DigitalitzacioPerfilDto> getPerfilsDisponibles() {
		return delegate.getPerfilsDisponibles();
	}

	@Override
	@RolesAllowed("tothom")
	public DigitalitzacioTransaccioRespostaDto iniciarDigitalitzacio(String codiPerfil, String urlReturn) {
		return delegate.iniciarDigitalitzacio(
				codiPerfil, 
				urlReturn);
	}

	@Override
	@RolesAllowed("tothom")
	public DigitalitzacioResultatDto recuperarResultat(String idTransaccio, boolean returnScannedFile,
			boolean returnSignedFile) {
		return delegate.recuperarResultat(
				idTransaccio, 
				returnScannedFile, 
				returnSignedFile);
	}

	@Override
	@RolesAllowed("tothom")
	public void tancarTransaccio(String idTransaccio) {
		delegate.tancarTransaccio(idTransaccio);
	}

}
