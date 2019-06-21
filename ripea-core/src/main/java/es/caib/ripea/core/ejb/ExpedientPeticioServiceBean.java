/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.ws.backoffice.AnotacioRegistreId;
import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientEstatDto;
import es.caib.ripea.core.api.dto.ExpedientFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientSelectorDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientSelectDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.RegistreDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.core.api.service.ExpedientService;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientPeticioServiceBean implements ExpedientPeticioService {

	@Autowired
	ExpedientPeticioService delegate;

	
	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientPeticioDto> findAmbFiltre(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltre(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientPeticioDto findOne(
			Long expedientPeticioId) {
		return delegate.findOne(expedientPeticioId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getAnnexContent(
			Long annexId) {
		return delegate.getAnnexContent(annexId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ArxiuFirmaDto> annexFirmaInfo(
			String fitxerArxiuUuid) {
		return delegate.annexFirmaInfo(fitxerArxiuUuid);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreAnnexDto findAnnexById(
			Long annexId) {
		return delegate.findAnnexById(annexId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getAnnexFirmaContingut(
			Long annexId) {
		return delegate.getAnnexFirmaContingut(annexId);
	}

	@Override
	@RolesAllowed("tothom")
	public void rebutjar(
			Long expedientPeticioId,
			String observacions) {
		delegate.rebutjar(expedientPeticioId, observacions);
		
	}


	
	@Override
	@RolesAllowed("tothom")
	public MetaExpedientDto findMetaExpedientByEntitatAndProcedimentCodi(String entitatCodi,
			String procedimentCodi) {
		return delegate.findMetaExpedientByEntitatAndProcedimentCodi(entitatCodi, procedimentCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientSelectDto> findMetaExpedientSelect(String entitatCodi) {
		return delegate.findMetaExpedientSelect(entitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreDto findRegistreById(Long registreId) {
		return delegate.findRegistreById(registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientPeticioDto> findByExpedient(Long entitatId,
			Long expedientId) {
		return delegate.findByExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto findByEntitatAndMetaExpedientAndExpedientNumero(Long entitatId,
			Long metaExpedientId,
			String expedientNumero) {
		return delegate.findByEntitatAndMetaExpedientAndExpedientNumero(entitatId, metaExpedientId, expedientNumero);
	}
	
	
}