/**
 * 
 */
package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ws.backoffice.AnotacioRegistreId;
import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.ArxiuFirmaDto;
import es.caib.ripea.service.intf.dto.ExpedientDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioFiltreDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioListDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.MassiuAnnexProcesarFiltreDto;
import es.caib.ripea.service.intf.dto.MetaExpedientDto;
import es.caib.ripea.service.intf.dto.MetaExpedientSelectDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.RegistreAnnexDto;
import es.caib.ripea.service.intf.dto.RegistreDto;
import es.caib.ripea.service.intf.dto.ResultDto;
import es.caib.ripea.service.intf.dto.ResultEnumDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import lombok.experimental.Delegate;

@Stateless
public class ExpedientPeticioServiceEjb extends AbstractServiceEjb<ExpedientPeticioService> implements ExpedientPeticioService {

	@Delegate private ExpedientPeticioService delegateService;

	protected void setDelegateService(ExpedientPeticioService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("IPA_BSTWS")
	public void crearExpedientPeticion(List<AnotacioRegistreId> anotacioRegistreIds) {
		delegateService.crearExpedientPeticion(anotacioRegistreIds);
	}
	
	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public ExpedientPeticioDto findOne(
			Long expedientPeticioId) {
		return delegateService.findOne(expedientPeticioId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getAnnexContent(
			Long annexId, boolean versioImprimible) {
		return delegateService.getAnnexContent(annexId, versioImprimible);
	}

	@Override
	@RolesAllowed("**")
	public List<ArxiuFirmaDto> annexFirmaInfo(
			String fitxerArxiuUuid) {
		return delegateService.annexFirmaInfo(fitxerArxiuUuid);
	}

	@Override
	@RolesAllowed("**")
	public RegistreAnnexDto findAnnexById(
			Long annexId) {
		return delegateService.findAnnexById(annexId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getAnnexFirmaContingut(
			Long annexId) {
		return delegateService.getAnnexFirmaContingut(annexId);
	}

	@Override
	@RolesAllowed("**")
	public void rebutjar(
			Long expedientPeticioId,
			String observacions) {
		delegateService.rebutjar(expedientPeticioId, observacions);
		
	}

	@Override
	@RolesAllowed("**")
	public List<MetaExpedientSelectDto> findMetaExpedientSelect(String entitatCodi) {
		return delegateService.findMetaExpedientSelect(entitatCodi);
	}

	@Override
	@RolesAllowed("**")
	public RegistreDto findRegistreById(Long registreId) {
		return delegateService.findRegistreById(registreId);
	}

	@Override
	@RolesAllowed("**")
	public List<ExpedientPeticioListDto> findByExpedientAmbFiltre(
			Long entitatId,
			Long expedientId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findByExpedientAmbFiltre(entitatId, expedientId, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientDto findByEntitatAndMetaExpedientAndExpedientNumero(Long entitatId,
	                                                                    Long metaExpedientId,
	                                                                    String expedientNumero) {
		return delegateService.findByEntitatAndMetaExpedientAndExpedientNumero(entitatId, metaExpedientId, expedientNumero);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getJustificantContent(String arxiuUuid) {
		return delegateService.getJustificantContent(arxiuUuid);
	}

	@Override
	@RolesAllowed("**")
	public long countAnotacionsPendents(Long entitatId, String rolActual, Long organActualId) {
		return delegateService.countAnotacionsPendents(entitatId, rolActual, organActualId);
	}

	@Override
	@RolesAllowed("**")
	public boolean comprovarExistenciaInteressatsPeticio(Long entitatId, Long expedientId, Long expedientPeticioId) {
		return delegateService.comprovarExistenciaInteressatsPeticio(entitatId, expedientId, expedientPeticioId);
	}

	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
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
	@RolesAllowed("**")
	public void retornarPendent(Long expedientPeticioId) {
		delegateService.retornarPendent(expedientPeticioId);
	}

	@Override
	@RolesAllowed("**")
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
	@RolesAllowed("**")
    public Long getPeriodeActualitzacioContadorAnotacionsPendents() {
        return delegateService.getPeriodeActualitzacioContadorAnotacionsPendents();
    }


}