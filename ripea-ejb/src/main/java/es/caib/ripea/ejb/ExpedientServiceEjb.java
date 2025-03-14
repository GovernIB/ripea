package es.caib.ripea.ejb;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.CodiValorDto;
import es.caib.ripea.service.intf.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.service.intf.dto.ContingutVistaEnumDto;
import es.caib.ripea.service.intf.dto.DocumentDto;
import es.caib.ripea.service.intf.dto.ExpedientComentariDto;
import es.caib.ripea.service.intf.dto.ExpedientDto;
import es.caib.ripea.service.intf.dto.ExpedientFiltreDto;
import es.caib.ripea.service.intf.dto.ExpedientSelectorDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.InteressatAssociacioAccioEnum;
import es.caib.ripea.service.intf.dto.MoureDestiVistaEnumDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.RespostaPublicacioComentariDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.ExpedientService;
import lombok.experimental.Delegate;

@Stateless
public class ExpedientServiceEjb extends AbstractServiceEjb<ExpedientService> implements ExpedientService {

	@Delegate private ExpedientService delegateService;

	protected void setDelegateService(ExpedientService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public ExpedientDto create(
			Long entitatId,
			Long contenidorId,
			Long metaExpedientId,
			Long metaExpedientDominiId,
			Integer any,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Long grupId,
			String rolActual,
			Map<Long, Long> anexosIdsMetaDocsIdsMap, 
			Long justificantIdMetaDoc,
			Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap,
			PrioritatEnumDto prioritat,
			String prioritatMotiu) {
		return delegateService.create(
				entitatId,
				contenidorId,
				metaExpedientId,
				metaExpedientDominiId,
				any,
				nom,
				expedientPeticioId,
				associarInteressats,
				grupId,
				rolActual,
				anexosIdsMetaDocsIdsMap, 
				justificantIdMetaDoc,
				interessatsAccionsMap,
				prioritat,
				prioritatMotiu);
	}
	public Long checkIfExistsByMetaExpedientAndNom(
			Long metaExpedientId,
			String nom) {
		return delegateService.checkIfExistsByMetaExpedientAndNom(
				metaExpedientId,
				nom);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientDto findById(
			Long entitatId,
			Long id, 
			String rolActual) {
		return delegateService.findById(entitatId, id, rolActual);
	}


	@Override
	@RolesAllowed("**")
	public PaginaDto<ExpedientDto> findAmbFiltreUser(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			Long organActual) {
		return delegateService.findAmbFiltreUser(entitatId, filtre, paginacioParams, rolActual, organActual);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			ExpedientFiltreDto filtre,
			String rolActual,
			Long organActual) throws NotFoundException {
		return delegateService.findIdsAmbFiltre(entitatId, filtre, rolActual, organActual);
	}

	@Override
	@RolesAllowed("**")
	public String agafarUser(
			Long entitatId,
			Long id) {
		return delegateService.agafarUser(entitatId, id);
	}


	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public String agafarAdmin(
			Long entitatId,
			Long arxiuId,
			Long id,
			String usuariCodi) {
		return delegateService.agafarAdmin(entitatId, arxiuId, id, usuariCodi);
	}

	@Override
	@RolesAllowed("**")
	public String alliberarUser(
			Long entitatId,
			Long id) {
		return delegateService.alliberarUser(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void alliberarAdmin(
			Long entitatId,
			Long id) {
		delegateService.alliberarAdmin(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public String tancar(
			Long entitatId,
			Long id,
			String motiu,
			Long[] documentsPerFirmar, boolean checkPerMassiuAdmin) {
		return delegateService.tancar(entitatId, id, motiu, documentsPerFirmar, checkPerMassiuAdmin);
	}

	@Override
	@RolesAllowed("**")
	public void reobrir(
			Long entitatId,
			Long id) throws NotFoundException {
		delegateService.reobrir(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public void relacioCreate(
			Long entitatId,
			Long id,
			Long relacionatId, String rolActual) {
		delegateService.relacioCreate(
				entitatId,
				id,
				relacionatId, 
				rolActual);
	}

	@Override
	@RolesAllowed("**")
	public boolean relacioDelete(
			Long entitatId, 
			Long expedientId, 
			Long relacionatId, 
			String rolActual) {
		return delegateService.relacioDelete(
				entitatId,
				expedientId,
				relacionatId, 
				rolActual);
	}

	@Override
	@RolesAllowed("**")
	public List<ExpedientDto> relacioFindAmbExpedient(
			Long entitatId, 
			Long expedientId) {
		return delegateService.relacioFindAmbExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto exportacio(
			Long entitatId,
			Collection<Long> expedientIds,
			String format) throws IOException {
		return delegateService.exportacio(
				entitatId,
				expedientIds,
				format);
	}

	@Override
	@RolesAllowed("**")
	public List<ExpedientSelectorDto> findPerUserAndProcediment(Long entitatId, Long metaExpedientId, String rolActual) throws NotFoundException {
		return delegateService.findPerUserAndProcediment(entitatId, metaExpedientId, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<ExpedientDto> findAmbFiltreNoRelacionat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			Long expedientId,
			PaginacioParamsDto paginacioParams, 
			String rolActual,
			Long organActual) {
		return delegateService.findAmbFiltreNoRelacionat(
				entitatId,
				filtre,
				expedientId,
				paginacioParams, 
				rolActual,
				organActual);
	}

	@Override
	public List<ExpedientDto> findByEntitatAndMetaExpedient(
			Long entitatId,
			Long metaExpedientId, String rolActual, Long organActualId) {
		return delegateService.findByEntitatAndMetaExpedient(
				entitatId,
				metaExpedientId, 
				rolActual, organActualId);
	}

	@Override
	public RespostaPublicacioComentariDto<ExpedientComentariDto> publicarComentariPerExpedient(Long entitatId, Long expedientId, String text, String rolActual) {
		return delegateService.publicarComentariPerExpedient(
				entitatId,
				expedientId,
				text, 
				rolActual);
	}

	@Override
	public List<ExpedientComentariDto> findComentarisPerContingut(Long entitatId, Long expedientId) {
		return delegateService.findComentarisPerContingut(
				entitatId,
				expedientId);
	}

	@Override
	public boolean hasWritePermission(Long expedientId) {
		return delegateService.hasWritePermission(expedientId);
	}

	@Override
	public ExpedientDto update(
			Long entitatId,
			Long id,
			String nom,
			int any,
			Long metaExpedientDominiId,
			Long organGestorId,
			String rolActual,
			Long grupId,
			PrioritatEnumDto prioritat,
			String prioritatMotiu) {
		return delegateService.update(entitatId, id, nom, any, metaExpedientDominiId, organGestorId, rolActual, grupId, prioritat, prioritatMotiu);
	}


	@Override
	public Exception retryCreateDocFromAnnex(
			Long registreAnnexId,
			Long metaDocumentId, 
			String rolActual) {
		return delegateService.retryCreateDocFromAnnex(
				registreAnnexId, 
				metaDocumentId, 
				rolActual);		
	}


	@Override
	@RolesAllowed("**")
	public boolean incorporar(
			Long entitatId,
			Long expedientId,
			Long expedientPeticioId,
			boolean associarInteressats, 
			String rolActual, 
			Map<Long, Long> anexosIdsMetaDocsIdsMap,
			Long justificantIdMetaDoc,
			boolean agafarExpedient,
			Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap) {
		return delegateService.incorporar(
				entitatId, 
				expedientId, 
				expedientPeticioId,  
				associarInteressats, 
				rolActual, 
				anexosIdsMetaDocsIdsMap,
				justificantIdMetaDoc,
				agafarExpedient,
				interessatsAccionsMap);
		
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto exportIndexExpedients(Long entitatId, Set<Long> expedientIds, String format) throws IOException {
		return delegateService.exportIndexExpedients(
				entitatId, 
				expedientIds,
				format);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto exportIndexExpedient(Long entitatId, Set<Long> expedientId, boolean exportar, String format) throws IOException {
		return delegateService.exportIndexExpedient(
				entitatId,
				expedientId,
				exportar,
				format);
	}
	
	@Override
	@RolesAllowed("**")
	public PaginaDto<ExpedientDto> findExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException {
		return delegateService.findExpedientsPerTancamentMassiu(
				entitatId,
				filtre,
				paginacioParams, rolActual);
	}
	@Override
	@RolesAllowed("**")
	public List<Long> findIdsExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual) throws NotFoundException {
		return delegateService.findIdsExpedientsPerTancamentMassiu(
				entitatId,
				filtre, rolActual);
	}
	@Override
	@RolesAllowed("**")
	public void assignar(
			Long entitatId,
			Long expedientId,
			String usuariCodi) {
		delegateService.assignar(
				entitatId,
				expedientId,
				usuariCodi);
	}
	@Override
	@RolesAllowed("**")
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return delegateService.consultaExpedientsAmbImportacio();
	}
	@Override
	@RolesAllowed("IPA_ORGAN_ADMIN")
	public boolean isOrganGestorPermes (Long expedientId, String rolActual) {
		return delegateService.isOrganGestorPermes(expedientId, rolActual);
	}
	
	@Override
	@RolesAllowed("**")
	public Exception guardarExpedientArxiu(Long expId) {
		return delegateService.guardarExpedientArxiu(expId);
	}
	@Override
	@RolesAllowed("**")
	public List<ExpedientDto> findByText(
			Long entitatId,
			String text, 
			String rolActual, 
			Long procedimentId,
			Long organActual){
		return delegateService.findByText(
				entitatId,
				text,
				rolActual, 
				procedimentId,
				organActual);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN", "IPA_DISSENY"})
	public PaginaDto<ExpedientDto> findExpedientMetaExpedientPaginat(Long entitatId, Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findExpedientMetaExpedientPaginat(entitatId, metaExpedientId, paginacioParams);
	}
	@Override
	@RolesAllowed({"IPA_ADMIN"})
	public List<CodiValorDto> findByEntitat(
			Long entitatId) {
		return delegateService.findByEntitat(entitatId);

	}
	
	@Override
	@RolesAllowed("**")
	public boolean hasReadPermissionsAny(
			String rolActual,
			Long entitatId) {
		return delegateService.hasReadPermissionsAny(rolActual, entitatId);
	}
	

	
	@Override
	@RolesAllowed("**")
	public PaginaDto<ExpedientDto> relacioFindAmbExpedientPaginat(
			Long id,
			ExpedientFiltreDto filtre,
			Long expedientId,
			PaginacioParamsDto paginacioDtoFromRequest) {
		return delegateService.relacioFindAmbExpedientPaginat(id, filtre, expedientId, paginacioDtoFromRequest);
	}
	
	@Override
	@RolesAllowed("**")
	public void importarExpedient(Long entitatId, Long expedientPareId, Long expedientId, String rolActual)
			throws NotFoundException {
		delegateService.importarExpedient(entitatId, expedientPareId, expedientId, rolActual);
	}
	
	@Override
	@RolesAllowed("**")
	public boolean esborrarExpedientFill(Long entitatId, Long expedientPareId, Long expedientId, String rolActual)
			throws NotFoundException {
		return delegateService.esborrarExpedientFill(entitatId, expedientPareId, expedientId, rolActual);
	}
	
	@Override
	@RolesAllowed("**")
	public Exception retryMoverAnnexArxiu(Long registreAnnexId) {
		return delegateService.retryMoverAnnexArxiu(registreAnnexId);
	}
	
	@Override
	@RolesAllowed("**")
	public long countByMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		return delegateService.countByMetaExpedient(
				entitatId,
				metaExpedientId);
	}
	
	@Override
	@RolesAllowed("**")
	public ContingutVistaEnumDto getVistaUsuariActual() {
		return delegateService.getVistaUsuariActual();
	}
	
	@Override
	@RolesAllowed("**")
	public void setVistaUsuariActual(
			ContingutVistaEnumDto vistaActual) {
		delegateService.setVistaUsuariActual(vistaActual);
	}
	@Override
	@RolesAllowed("**")
	public FitxerDto exportarEniExpedient(Long entitatId, Set<Long> expedientIds, boolean ambDocuments) throws IOException {
		return delegateService.exportarEniExpedient(entitatId, expedientIds, ambDocuments);
	}
	
	@Override
	@RolesAllowed("**")
	public String getNom(Long id) {
		return delegateService.getNom(id);
	}

	@Override
	@RolesAllowed("**")
	public ExpedientDto changeExpedientPrioritat(
			Long entitatId,
			Long expedientId,
			PrioritatEnumDto prioritat,
			String prioritatMotiu) {
		return delegateService.changeExpedientPrioritat(entitatId, expedientId, prioritat, prioritatMotiu);
	}

    @Override
	@RolesAllowed("**")
    public void changeExpedientsPrioritat(Long entitatId, Set<Long> expedientsId, PrioritatEnumDto prioritat) {
        delegateService.changeExpedientsPrioritat(entitatId, expedientsId, prioritat);
    }
    
	@Override
	@RolesAllowed("**")
	public MoureDestiVistaEnumDto getVistaMoureUsuariActual() {
		return delegateService.getVistaMoureUsuariActual();
	}
	
	@Override
	@RolesAllowed("**")
	public String retornaUser(Long entitatId, Long id) throws NotFoundException {
		return delegateService.retornaUser(entitatId, id);
	}
}