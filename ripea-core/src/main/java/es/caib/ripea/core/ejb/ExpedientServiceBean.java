/**
 * 
 */
package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientServiceBean implements ExpedientService {

	@Autowired
	private ExpedientService delegate;

	@Override
	@RolesAllowed("tothom")
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
			PrioritatEnumDto prioritat) {
		return delegate.create(
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
				prioritat);
	}
	public Long checkIfExistsByMetaExpedientAndNom(
			Long metaExpedientId,
			String nom) {
		return delegate.checkIfExistsByMetaExpedientAndNom(
				metaExpedientId,
				nom);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto findById(
			Long entitatId,
			Long id, 
			String rolActual) {
		return delegate.findById(entitatId, id, rolActual);
	}


	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientDto> findAmbFiltreUser(
			Long entitatId,
			ExpedientFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			Long organActual) {
		return delegate.findAmbFiltreUser(entitatId, filtre, paginacioParams, rolActual, organActual);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			ExpedientFiltreDto filtre,
			String rolActual,
			Long organActual) throws NotFoundException {
		return delegate.findIdsAmbFiltre(entitatId, filtre, rolActual, organActual);
	}

	@Override
	@RolesAllowed("tothom")
	public String agafarUser(
			Long entitatId,
			Long id) {
		return delegate.agafarUser(entitatId, id);
	}


	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public String agafarAdmin(
			Long entitatId,
			Long arxiuId,
			Long id,
			String usuariCodi) {
		return delegate.agafarAdmin(entitatId, arxiuId, id, usuariCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public String alliberarUser(
			Long entitatId,
			Long id) {
		return delegate.alliberarUser(entitatId, id);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void alliberarAdmin(
			Long entitatId,
			Long id) {
		delegate.alliberarAdmin(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public String tancar(
			Long entitatId,
			Long id,
			String motiu,
			Long[] documentsPerFirmar, boolean checkPerMassiuAdmin) {
		return delegate.tancar(entitatId, id, motiu, documentsPerFirmar, checkPerMassiuAdmin);
	}

	@Override
	@RolesAllowed("tothom")
	public void reobrir(
			Long entitatId,
			Long id) throws NotFoundException {
		delegate.reobrir(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public void relacioCreate(
			Long entitatId,
			Long id,
			Long relacionatId, String rolActual) {
		delegate.relacioCreate(
				entitatId,
				id,
				relacionatId, 
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean relacioDelete(
			Long entitatId, 
			Long expedientId, 
			Long relacionatId, 
			String rolActual) {
		return delegate.relacioDelete(
				entitatId,
				expedientId,
				relacionatId, 
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientDto> relacioFindAmbExpedient(
			Long entitatId, 
			Long expedientId) {
		return delegate.relacioFindAmbExpedient(entitatId, expedientId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto exportacio(
			Long entitatId,
			Collection<Long> expedientIds,
			String format) throws IOException {
		return delegate.exportacio(
				entitatId,
				expedientIds,
				format);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ExpedientSelectorDto> findPerUserAndProcediment(Long entitatId, Long metaExpedientId, String rolActual) throws NotFoundException {
		return delegate.findPerUserAndProcediment(entitatId, metaExpedientId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientDto> findAmbFiltreNoRelacionat(
			Long entitatId,
			ExpedientFiltreDto filtre,
			Long expedientId,
			PaginacioParamsDto paginacioParams, 
			String rolActual,
			Long organActual) {
		return delegate.findAmbFiltreNoRelacionat(
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
		return delegate.findByEntitatAndMetaExpedient(
				entitatId,
				metaExpedientId, 
				rolActual, organActualId);
	}

	@Override
	public RespostaPublicacioComentariDto<ExpedientComentariDto> publicarComentariPerExpedient(Long entitatId, Long expedientId, String text, String rolActual) {
		return delegate.publicarComentariPerExpedient(
				entitatId,
				expedientId,
				text, 
				rolActual);
	}

	@Override
	public List<ExpedientComentariDto> findComentarisPerContingut(Long entitatId, Long expedientId) {
		return delegate.findComentarisPerContingut(
				entitatId,
				expedientId);
	}

	@Override
	public boolean hasWritePermission(Long expedientId) {
		return delegate.hasWritePermission(expedientId);
	}

	@Override
	public ExpedientDto update(Long entitatId, Long id, String nom, int any, Long metaExpedientDominiId, Long organGestorId, String rolActual, Long grupId, PrioritatEnumDto prioritat) {
		return delegate.update(entitatId, id, nom, any, metaExpedientDominiId, organGestorId, rolActual, grupId, prioritat);
	}


	@Override
	public Exception retryCreateDocFromAnnex(
			Long registreAnnexId,
			Long metaDocumentId, 
			String rolActual) {
		return delegate.retryCreateDocFromAnnex(
				registreAnnexId, 
				metaDocumentId, 
				rolActual);		
	}


	@Override
	@RolesAllowed("tothom")
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
		return delegate.incorporar(
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
	@RolesAllowed("tothom")
	public FitxerDto exportIndexExpedients(Long entitatId, Set<Long> expedientIds, String format) throws IOException {
		return delegate.exportIndexExpedients(
				entitatId, 
				expedientIds,
				format);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto exportIndexExpedient(Long entitatId, Set<Long> expedientId, boolean exportar, String format) throws IOException {
		return delegate.exportIndexExpedient(
				entitatId,
				expedientId,
				exportar,
				format);
	}
	
	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientDto> findExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException {
		return delegate.findExpedientsPerTancamentMassiu(
				entitatId,
				filtre,
				paginacioParams, rolActual);
	}
	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsExpedientsPerTancamentMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual) throws NotFoundException {
		return delegate.findIdsExpedientsPerTancamentMassiu(
				entitatId,
				filtre, rolActual);
	}
	@Override
	@RolesAllowed("tothom")
	public void assignar(
			Long entitatId,
			Long expedientId,
			String usuariCodi) {
		delegate.assignar(
				entitatId,
				expedientId,
				usuariCodi);
	}
	@Override
	@RolesAllowed("tothom")
	public List<DocumentDto> consultaExpedientsAmbImportacio() {
		return delegate.consultaExpedientsAmbImportacio();
	}
	@Override
	@RolesAllowed("IPA_ORGAN_ADMIN")
	public boolean isOrganGestorPermes (Long expedientId, String rolActual) {
		return delegate.isOrganGestorPermes(expedientId, rolActual);
	}
	
	@Override
	@RolesAllowed("tothom")
	public Exception guardarExpedientArxiu(Long expId) {
		return delegate.guardarExpedientArxiu(expId);
	}
	@Override
	@RolesAllowed("tothom")
	public List<ExpedientDto> findByText(
			Long entitatId,
			String text, 
			String rolActual, 
			Long procedimentId,
			Long organActual){
		return delegate.findByText(
				entitatId,
				text,
				rolActual, 
				procedimentId,
				organActual);
	}

	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public PaginaDto<ExpedientDto> findExpedientMetaExpedientPaginat(Long entitatId, Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findExpedientMetaExpedientPaginat(entitatId, metaExpedientId, paginacioParams);
	}
	@Override
	@RolesAllowed({"IPA_ADMIN"})
	public List<CodiValorDto> findByEntitat(
			Long entitatId) {
		return delegate.findByEntitat(entitatId);

	}
	
	@Override
	@RolesAllowed("tothom")
	public boolean hasReadPermissionsAny(
			String rolActual,
			Long entitatId) {
		return delegate.hasReadPermissionsAny(rolActual, entitatId);
	}
	

	
	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ExpedientDto> relacioFindAmbExpedientPaginat(
			Long id,
			ExpedientFiltreDto filtre,
			Long expedientId,
			PaginacioParamsDto paginacioDtoFromRequest) {
		return delegate.relacioFindAmbExpedientPaginat(id, filtre, expedientId, paginacioDtoFromRequest);
	}
	
	@Override
	@RolesAllowed("tothom")
	public void importarExpedient(Long entitatId, Long expedientPareId, Long expedientId, String rolActual)
			throws NotFoundException {
		delegate.importarExpedient(entitatId, expedientPareId, expedientId, rolActual);
	}
	
	@Override
	@RolesAllowed("tothom")
	public boolean esborrarExpedientFill(Long entitatId, Long expedientPareId, Long expedientId, String rolActual)
			throws NotFoundException {
		return delegate.esborrarExpedientFill(entitatId, expedientPareId, expedientId, rolActual);
	}
	
	@Override
	@RolesAllowed("tothom")
	public Exception retryMoverAnnexArxiu(Long registreAnnexId) {
		return delegate.retryMoverAnnexArxiu(registreAnnexId);
	}
	
	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN", "IPA_REVISIO"})
	public long countByMetaExpedient(
			Long entitatId,
			Long metaExpedientId) {
		return delegate.countByMetaExpedient(
				entitatId,
				metaExpedientId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public ContingutVistaEnumDto getVistaUsuariActual() {
		return delegate.getVistaUsuariActual();
	}
	
	@Override
	@RolesAllowed("tothom")
	public void setVistaUsuariActual(
			ContingutVistaEnumDto vistaActual) {
		delegate.setVistaUsuariActual(vistaActual);
	}
	@Override
	@RolesAllowed("tothom")
	public FitxerDto exportarEniExpedient(Long entitatId, Set<Long> expedientIds, boolean ambDocuments) throws IOException {
		return delegate.exportarEniExpedient(entitatId, expedientIds, ambDocuments);
	}
	
	@Override
	@RolesAllowed("tothom")
	public String getNom(Long id) {
		return delegate.getNom(id);
	}

	@Override
	@RolesAllowed("tothom")
	public ExpedientDto changeExpedientPrioritat(
			Long entitatId,
			Long expedientId,
			PrioritatEnumDto prioritat) {
		return delegate.changeExpedientPrioritat(entitatId, expedientId, prioritat);
	}

    @Override
	@RolesAllowed("tothom")
    public void changeExpedientsPrioritat(Long entitatId, Set<Long> expedientsId, PrioritatEnumDto prioritat) {
        delegate.changeExpedientsPrioritat(entitatId, expedientsId, prioritat);
    }
}