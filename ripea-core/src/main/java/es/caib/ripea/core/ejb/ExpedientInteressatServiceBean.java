/**
 * 
 */
package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.dto.InteressatAdministracioDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatPersonaFisicaDto;
import es.caib.ripea.core.api.dto.InteressatPersonaJuridicaDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

/**
 * Implementació de InteressatService com a EJB que empra una clase delegada per
 * accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientInteressatServiceBean implements ExpedientInteressatService {

	@Autowired
	ExpedientInteressatService delegate;

	@Override
	@RolesAllowed("tothom")
	public InteressatDto create(Long entitatId, Long expedientId, InteressatDto interessat, String rolActual) {
		return delegate.create(entitatId, expedientId, interessat, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto create(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto representant,
			boolean propagarArxiu, String rolActual) {
		return delegate.create(entitatId, expedientId, interessatId, representant, propagarArxiu, null);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto update(Long entitatId, Long expedientId, InteressatDto interessat, String rolActual) {
		return delegate.update(entitatId, expedientId, interessat, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto update(Long entitatId, Long expedientId, Long interessatId, InteressatDto representant, String rolActual) {
		return delegate.update(entitatId, expedientId, interessatId, representant, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public void delete(Long entitatId, Long expedientId, Long interessatId, String rolActual) {
		delegate.delete(entitatId, expedientId, interessatId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public void deleteRepresentant(Long entitatId, Long expedientId, Long interessatId, Long representantId, String rolActual) {
		delegate.deleteRepresentant(entitatId, expedientId, interessatId, representantId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto findById(Long id, boolean consultarDadesExternes) {
		return delegate.findById(id, consultarDadesExternes);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto findRepresentantById(Long interessatId, Long id) {
		return delegate.findRepresentantById(interessatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public long countByExpedient(Long entitatId, Long expedientId) {
		return delegate.countByExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatDto> findAmbDocumentPerNotificacio(Long entitatId, Long documentId) {
		return delegate.findAmbDocumentPerNotificacio(entitatId, documentId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatPersonaFisicaDto> findByFiltrePersonaFisica(
			String documentNum,
			String nom,
			String llinatge1,
			String llinatge2,
			Long expedientId) {
		return delegate.findByFiltrePersonaFisica(documentNum, nom, llinatge1, llinatge2, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatPersonaJuridicaDto> findByFiltrePersonaJuridica(
			String documentNum,
			String raoSocial,
			Long expedientId) {
		return delegate.findByFiltrePersonaJuridica(documentNum, raoSocial, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatAdministracioDto> findByFiltreAdministracio(String organCodi, Long expedientId) {
		return delegate.findByFiltreAdministracio(organCodi, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatDto> findByExpedient(
			Long entitatId,
			Long expedientId,
			boolean nomesAmbNotificacioActiva) throws NotFoundException {
		return delegate.findByExpedient(entitatId, expedientId, nomesAmbNotificacioActiva);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto findByExpedientAndDocumentNum(
			String documentNum,
			Long expedientId) throws NotFoundException {
		return delegate.findByExpedientAndDocumentNum(documentNum, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<InteressatDto> findByText(String text) {
		return delegate.findByText(text);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto findByDocumentNum(String documentNum) throws NotFoundException {
		return delegate.findByDocumentNum(documentNum);
	}

	@Override
	@RolesAllowed("tothom")
	public Exception guardarInteressatsArxiu(Long expId) {
		return delegate.guardarInteressatsArxiu(expId);
	}

	@Override
	@RolesAllowed("tothom")
	public Long findExpedientIdByInteressat(Long interessatId) {
		return delegate.findExpedientIdByInteressat(interessatId);
	}

	@Override
	@RolesAllowed("tothom")
	public InteressatDto createRepresentant(
			Long entitatId,
			Long expedientId,
			Long interessatId,
			InteressatDto interessat,
			boolean propagarArxiu,
			String rolActual) {
		return delegate.createRepresentant(
				entitatId,
				expedientId,
				interessatId,
				interessat,
				propagarArxiu,
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public String importarInteressats(Long entitatId, Long expedientId, String rolActual, List<InteressatDto> interessats) throws NotFoundException {
		return delegate.importarInteressats(entitatId, expedientId, rolActual, interessats);
	}
}