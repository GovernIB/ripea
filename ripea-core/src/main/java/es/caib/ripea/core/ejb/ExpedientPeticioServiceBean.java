/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioPendentDist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.ArxiuFirmaDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioListDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.MassiuAnnexProcesarFiltreDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.MetaExpedientSelectDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDto;
import es.caib.ripea.core.api.dto.RegistreDto;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientPeticioService;

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
	private ExpedientPeticioService delegate;

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientPeticioListDto> findAmbFiltre(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual, Long organActualId) {
		return delegate.findAmbFiltre(
				entitatId,
				filtre,
				paginacioParams, rolActual, organActualId);
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
			Long annexId, boolean versioImprimible) {
		return delegate.getAnnexContent(annexId, versioImprimible);
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
	public List<ExpedientPeticioListDto> findByExpedientAmbFiltre(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findByExpedientAmbFiltre(entitatId, expedientId, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto findByEntitatAndMetaExpedientAndExpedientNumero(Long entitatId,
			Long metaExpedientId,
			String expedientNumero) {
		return delegate.findByEntitatAndMetaExpedientAndExpedientNumero(entitatId, metaExpedientId, expedientNumero);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getJustificantContent(String arxiuUuid) {
		return delegate.getJustificantContent(arxiuUuid);
	}

	@Override
	@RolesAllowed("tothom")
	public long countAnotacionsPendents(Long entitatId, String rolActual, Long organActualId) {
		return delegate.countAnotacionsPendents(entitatId, rolActual, organActualId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean comprovarExistenciaInteressatsPeticio(Long entitatId, Long expedientId, Long expedientPeticioId) {
		return delegate.comprovarExistenciaInteressatsPeticio(entitatId, expedientId, expedientPeticioId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ResultDto<RegistreAnnexDto> findAnnexosPendentsProcesarMassiu(Long entitatId,
			MassiuAnnexProcesarFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			ResultEnumDto resultEnum) throws NotFoundException {
		return delegate.findAnnexosPendentsProcesarMassiu(
				entitatId,
				filtre,
				paginacioParams,
				resultEnum);
	}

	@Override
	public PaginaDto<ExpedientPeticioPendentDist> findPendentsCanviEstatAnotacioDistribucio(Long entitatId, ContingutMassiuFiltreDto filtre, PaginacioParamsDto paginacioParams) {
		return delegate.findPendentsCanviEstatAnotacioDistribucio(entitatId, filtre, paginacioParams);
	}

	@Override
	public List<Long> findIdsPendentsCanviEstatAnotacioDistribucio(Long entitatId, ContingutMassiuFiltreDto filtre) {
		return delegate.findIdsPendentsCanviEstatAnotacioDistribucio(entitatId, filtre);
	}

	@Override
	public boolean canviarEstatAnotacioDistribucio(List<Long> ids) {
		return delegate.canviarEstatAnotacioDistribucio(ids);
	}


}