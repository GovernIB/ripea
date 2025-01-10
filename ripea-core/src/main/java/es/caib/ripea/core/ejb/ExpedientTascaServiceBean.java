/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.io.IOException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ExpedientTascaComentariDto;
import es.caib.ripea.core.api.dto.ExpedientTascaDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaDto;
import es.caib.ripea.core.api.dto.MetaExpedientTascaValidacioDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.TascaEstatEnumDto;
import es.caib.ripea.core.api.dto.UsuariTascaFiltreDto;
import es.caib.ripea.core.api.service.ExpedientTascaService;

/**
 * Implementació de ExpedientTascaService com a EJB que empra una clase delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientTascaServiceBean implements ExpedientTascaService {

	@Autowired
	ExpedientTascaService delegate;

	@Override
	@RolesAllowed("tothom")
	public ExpedientTascaDto findOne(Long expedientPeticioId) {
		return delegate.findOne(expedientPeticioId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientTascaDto> findAmbExpedient(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParam) {
		return delegate.findAmbExpedient(entitatId, expedientId, paginacioParam);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientTascaDto> findAmbMetaExpedient(Long entitatId,
			Long metaExpedientId) {
		return delegate.findAmbMetaExpedient(entitatId,
				metaExpedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientTascaDto createTasca(
			Long entitatId,
			Long expedientId,
			ExpedientTascaDto expedientTasca) {
		return delegate.createTasca(
				entitatId,
				expedientId,
				expedientTasca);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaExpedientTascaDto findMetaExpedientTascaById(Long metaExpedientTascaId) {
		return delegate.findMetaExpedientTascaById(metaExpedientTascaId);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientTascaDto> findAmbAuthentication(
			Long entitatId,
			UsuariTascaFiltreDto filtre,
			PaginacioParamsDto paginacioParam) {
		return delegate.findAmbAuthentication(
				entitatId,
				filtre,
				paginacioParam);
	}

	@Override
	@RolesAllowed("tothom")
	public long countTasquesPendents() {
		return delegate.countTasquesPendents();
	}


	@Override
	@RolesAllowed("tothom")
	public ContingutDto findTascaExpedient(Long entitatId,
			Long contingutId,
			Long tascaId,
			boolean ambFills,
			boolean ambVersions) {
		return delegate.findTascaExpedient(entitatId,
				contingutId,
				tascaId,
				ambFills,
				ambVersions);
	}


	@Override
	@RolesAllowed("tothom")
	public void deleteTascaReversible(
			Long entitatId,
			Long tascaId,
			Long contingutId) throws IOException {
		delegate.deleteTascaReversible(
				entitatId,
				tascaId,
				contingutId);
	}


	@Override
	public ExpedientTascaDto canviarTascaEstat(
			Long expedientTascaId,
			TascaEstatEnumDto tascaEstatEnumDto,
			String motiu, 
			String rolActual) {
		return delegate.canviarTascaEstat(
				expedientTascaId,
				tascaEstatEnumDto,
				motiu, 
				rolActual);
	}
	public ExpedientTascaDto updateResponsables(Long expedientTascaId, 
			List<String> responsablesCodi) {
		return delegate.updateResponsables(
				expedientTascaId,
				responsablesCodi);
	}
		
	@Override
	public List<MetaExpedientTascaDto> findAmbEntitat(Long entitatId) {
		return delegate.findAmbEntitat(entitatId);
	}

    @Override
    public boolean publicarComentariPerExpedientTasca(Long entitatId, Long expedientTascaId, String text, String rolActual) {
        return delegate.publicarComentariPerExpedientTasca(entitatId, expedientTascaId, text, rolActual);
    }

	@Override
	public List<ExpedientTascaComentariDto> findComentarisPerTasca(Long entitatId, Long expedientTascaId) {
		return delegate.findComentarisPerTasca(entitatId, expedientTascaId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto findByTascaBasicInfo(Long contingutId, Long tascaId) {
		return delegate.findByTascaBasicInfo(contingutId, tascaId);
	}

	@Override
	public ExpedientTascaDto updateDataLimit(ExpedientTascaDto expedientTascaDto) {
		return delegate.updateDataLimit(expedientTascaDto);
	}

	@Override
	@RolesAllowed("tothom")
	public void changeTascaPrioritat(ExpedientTascaDto expedientTascaDto) {
		delegate.changeTascaPrioritat(expedientTascaDto);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientTascaDto updateDelegat(Long expedientTascaId, String delegatCodi, String comentari) {
		return delegate.updateDelegat(expedientTascaId, delegatCodi, comentari);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientTascaDto cancelarDelegacio(Long expedientTascaId, String comentari) {
		return delegate.cancelarDelegacio(expedientTascaId, comentari);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientTascaDto reobrirTasca(Long expedientTascaId, List<String> responsablesCodi, String motiu,
			String rolActual) {
		return delegate.reobrirTasca(expedientTascaId, responsablesCodi, motiu, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientTascaValidacioDto> getValidacionsPendentsTasca(Long expedientTascaId) {
		return delegate.getValidacionsPendentsTasca(expedientTascaId);
	}
}