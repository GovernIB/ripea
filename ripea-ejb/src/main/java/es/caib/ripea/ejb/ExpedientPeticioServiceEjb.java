/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientPeticioServiceEjb implements ExpedientPeticioService {

	@Delegate
	private ExpedientPeticioService delegateService;

	protected void setDelegateService(ExpedientPeticioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientPeticioListDto> findAmbFiltre(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual, Long organActualId) {
		return delegateService.findAmbFiltre(
				entitatId,
				filtre,
				paginacioParams, rolActual, organActualId);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientPeticioDto findOne(
			Long expedientPeticioId) {
		return delegateService.findOne(expedientPeticioId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getAnnexContent(
			Long annexId, boolean versioImprimible) {
		return delegateService.getAnnexContent(annexId, versioImprimible);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ArxiuFirmaDto> annexFirmaInfo(
			String fitxerArxiuUuid) {
		return delegateService.annexFirmaInfo(fitxerArxiuUuid);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreAnnexDto findAnnexById(
			Long annexId) {
		return delegateService.findAnnexById(annexId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getAnnexFirmaContingut(
			Long annexId) {
		return delegateService.getAnnexFirmaContingut(annexId);
	}

	@Override
	@RolesAllowed("tothom")
	public void rebutjar(
			Long expedientPeticioId,
			String observacions) {
		delegateService.rebutjar(expedientPeticioId, observacions);
		
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientSelectDto> findMetaExpedientSelect(String entitatCodi) {
		return delegateService.findMetaExpedientSelect(entitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreDto findRegistreById(Long registreId) {
		return delegateService.findRegistreById(registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientPeticioListDto> findByExpedientAmbFiltre(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findByExpedientAmbFiltre(entitatId, expedientId, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto findByEntitatAndMetaExpedientAndExpedientNumero(Long entitatId,
	                                                                    Long metaExpedientId,
	                                                                    String expedientNumero) {
		return delegateService.findByEntitatAndMetaExpedientAndExpedientNumero(entitatId, metaExpedientId, expedientNumero);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getJustificantContent(String arxiuUuid) {
		return delegateService.getJustificantContent(arxiuUuid);
	}

	@Override
	@RolesAllowed("tothom")
	public long countAnotacionsPendents(Long entitatId, String rolActual, Long organActualId) {
		return delegateService.countAnotacionsPendents(entitatId, rolActual, organActualId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean comprovarExistenciaInteressatsPeticio(Long entitatId, Long expedientId, Long expedientPeticioId) {
		return delegateService.comprovarExistenciaInteressatsPeticio(entitatId, expedientId, expedientPeticioId);
	}

	@Override
	@RolesAllowed("tothom")
	public ResultDto<RegistreAnnexDto> findAnnexosPendentsProcesarMassiu(Long entitatId,
	                                                                     MassiuAnnexProcesarFiltreDto filtre,
	                                                                     PaginacioParamsDto paginacioParams,
	                                                                     ResultEnumDto resultEnum,
	                                                                     String rolActual,
	                                                                     Long organActualId) throws NotFoundException {
		return delegateService.findAnnexosPendentsProcesarMassiu(
				entitatId,
				filtre,
				paginacioParams,
				resultEnum, 
				rolActual, 
				organActualId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ResultDto<ExpedientPeticioListDto> findPendentsCanviEstatDistribucio(
			Long entitatId,
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			ResultEnumDto resultEnum) {
		return delegateService.findPendentsCanviEstatDistribucio(
				entitatId,
				filtre,
				paginacioParams,
				resultEnum);
	}


	@Override
	@RolesAllowed("IPA_ADMIN")
	public Exception canviarEstatAnotacioDistribucio(Long entitatId, Long id) {
		return delegateService.canviarEstatAnotacioDistribucio(entitatId, id);
	}

	
	@Override
	@RolesAllowed("tothom")
	public List<MetaExpedientDto> findMetaExpedientsPermesosPerAnotacions(
			Long entitatId,
			Long organActualId,
			String rolActual) {
		return delegateService.findMetaExpedientsPermesosPerAnotacions(
				entitatId,
				organActualId,
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public void canviarProcediment(
			Long expedientPeticioId,
			Long procedimentId, 
			Long grupId) {
		delegateService.canviarProcediment(
				expedientPeticioId,
				procedimentId, 
				grupId);
		
	}

	@Override
	@RolesAllowed("tothom")
	public void retornarPendent(Long expedientPeticioId) {
		delegateService.retornarPendent(expedientPeticioId);
	}

	@Override
	@RolesAllowed("tothom")
	public void evictCountAnotacionsPendents(
			Long entitatId) {
		delegateService.evictCountAnotacionsPendents(entitatId);
		
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ResultDto<ExpedientPeticioListDto> findComunicadesAmbFiltre(
			ExpedientPeticioFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum) {
		return delegateService.findComunicadesAmbFiltre(
				filtre,
				paginacioParams, 
				resultEnum);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void comunicadaReprocessar(
			Long expedientPeticioId) throws Throwable {
		delegateService.comunicadaReprocessar(expedientPeticioId);
	}

    @Override
	@RolesAllowed("tothom")
    public Long getPeriodeActualitzacioContadorAnotacionsPendents() {
        return delegateService.getPeriodeActualitzacioContadorAnotacionsPendents();
    }


}