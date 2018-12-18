/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;
import java.util.Properties;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ExcepcioLogDto;
import es.caib.ripea.core.api.dto.IntegracioAccioDto;
import es.caib.ripea.core.api.dto.IntegracioDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AplicacioServiceBean implements AplicacioService {

	@Autowired
	AplicacioService delegate;



	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public String getVersioActual() {
		return delegate.getVersioActual();
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public void processarAutenticacioUsuari() {
		delegate.processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public UsuariDto getUsuariActual() {
		return delegate.getUsuariActual();
	}
	
	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public UsuariDto updateUsuariActual(UsuariDto usuari) {
		return delegate.updateUsuariActual(usuari);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public UsuariDto findUsuariAmbCodi(String codi) {
		return delegate.findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public List<UsuariDto> findUsuariAmbText(String text) {
		return delegate.findUsuariAmbText(text);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<IntegracioDto> integracioFindAll() {
		return delegate.integracioFindAll();
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi) {
		return delegate.integracioFindDarreresAccionsByCodi(codi);
	}

	@Override
	public void excepcioSave(Throwable exception) {
		delegate.excepcioSave(exception);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return delegate.excepcioFindOne(index);
	}

	@Override
	@RolesAllowed({"IPA_SUPER"})
	public List<ExcepcioLogDto> excepcioFindAll() {
		return delegate.excepcioFindAll();
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		return delegate.permisosFindRolsDistinctAll();
	}

	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public boolean isPluginArxiuActiu() {
		return delegate.isPluginArxiuActiu();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public String propertyBaseUrl() {
		return delegate.propertyBaseUrl();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public String propertyPluginPassarelaFirmaIds() {
		return delegate.propertyPluginPassarelaFirmaIds();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public String propertyPluginPassarelaFirmaIgnorarModalIds() {
		return delegate.propertyPluginPassarelaFirmaIgnorarModalIds();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public String propertyPluginEscaneigIds() {
		return delegate.propertyPluginEscaneigIds();
	}

	@Override
	@RolesAllowed({"IPA_SUPER", "IPA_ADMIN", "tothom"})
	public Properties propertyFindByPrefix(String prefix) {
		return delegate.propertyFindByPrefix(prefix);
	}

	@Override
	public String propertyFindByNom(String nom) {
		return delegate.propertyFindByNom(nom);
	}

}
