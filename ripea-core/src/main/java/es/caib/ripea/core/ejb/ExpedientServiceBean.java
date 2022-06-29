/**
 * 
 */
package es.caib.ripea.core.ejb;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.ripea.core.api.dto.CodiValorDto;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.ExpedientComentariDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.ExpedientFiltreDto;
import es.caib.ripea.core.api.dto.ExpedientSelectorDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExpedientService;

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
			Long organGestorId,
			Integer any,
			Long sequencia,
			String nom,
			Long expedientPeticioId,
			boolean associarInteressats,
			Long grupId, 
			String rolActual, 
			Map<Long, Long> anexosIdsMetaDocsIdsMap) {
		return delegate.create(
				entitatId,
				contenidorId,
				metaExpedientId,
				metaExpedientDominiId,
				organGestorId,
				any,
				sequencia,
				nom,
				expedientPeticioId,
				associarInteressats,
				grupId, 
				rolActual, 
				anexosIdsMetaDocsIdsMap);
	}
	public ExpedientDto findByMetaExpedientAndPareAndNomAndEsborrat(
			Long entitatId,
			Long metaExpedientId,
			Long pareId,
			String nom,
			int esborrat, 
			String rolActual, 
			Long organId) {
		return delegate.findByMetaExpedientAndPareAndNomAndEsborrat(
				entitatId,
				metaExpedientId,
				pareId,
				nom,
				esborrat,
				rolActual,
				organId);
	}
	@Override
	@RolesAllowed("tothom")
	public ExpedientDto update(
			Long entitatId,
			Long id,
			String nom) {
		return delegate.update(
				entitatId,
				id,
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
			PaginacioParamsDto paginacioParams, String rolActual) {
		return delegate.findAmbFiltreUser(entitatId, filtre, paginacioParams, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			ExpedientFiltreDto filtre, String rolActual) throws NotFoundException {
		return delegate.findIdsAmbFiltre(entitatId, filtre, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public void agafarUser(
			Long entitatId,
			Long id) {
		delegate.agafarUser(entitatId, id);
	}


	@Override
	@RolesAllowed({"IPA_ADMIN", "IPA_ORGAN_ADMIN"})
	public void agafarAdmin(
			Long entitatId,
			Long arxiuId,
			Long id,
			String usuariCodi) {
		delegate.agafarAdmin(entitatId, arxiuId, id, usuariCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public void alliberarUser(
			Long entitatId,
			Long id) {
		delegate.alliberarUser(entitatId, id);
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
	public void tancar(
			Long entitatId,
			Long id,
			String motiu,
			Long[] documentsPerFirmar, boolean checkPerMassiuAdmin) {
		delegate.tancar(entitatId, id, motiu, documentsPerFirmar, checkPerMassiuAdmin);
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
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltreNoRelacionat(
				entitatId,
				filtre,
				expedientId,
				paginacioParams);
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
	public boolean publicarComentariPerExpedient(Long entitatId, Long expedientId, String text, String rolActual) {
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
	public ExpedientDto update(Long entitatId, Long id, String nom, int any, Long metaExpedientDominiId, Long organGestorId, String rolActual, Long grupId) {
		return delegate.update(entitatId, id, nom, any, metaExpedientDominiId, organGestorId, rolActual, grupId);
	}


	@Override
	public Exception retryCreateDocFromAnnex(
			Long registreAnnexId,
			Long expedientPeticioId, 
			Long metaDocumentId, String rolActual) {
		return delegate.retryCreateDocFromAnnex(
				registreAnnexId, 
				expedientPeticioId, 
				metaDocumentId, rolActual);		
	}

	@Override
	public boolean retryNotificarDistribucio(Long expedientPeticioId) {
		return delegate.retryNotificarDistribucio(expedientPeticioId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean incorporar(
			Long entitatId,
			Long expedientId,
			Long expedientPeticioId,
			boolean associarInteressats, 
			String rolActual, 
			Map<Long, Long> anexosIdsMetaDocsIdsMap, boolean agafarExpedient) {
		return delegate.incorporar(
				entitatId, 
				expedientId, 
				expedientPeticioId,  
				associarInteressats, 
				rolActual, 
				anexosIdsMetaDocsIdsMap, agafarExpedient);
		
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
	public FitxerDto exportIndexExpedient(Long entitatId, Set<Long> expedientId, boolean exportar) throws IOException {
		return delegate.exportIndexExpedient(
				entitatId,
				expedientId,
				exportar);
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
	@RolesAllowed("IPA_SUPER")
	public List<ExpedientDto> findByText(
			Long entitatId,
			String text){
		return delegate.findByText(entitatId, text);
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
	public List<ExpedientDto> findByIds(
			Long entitatId,
			Set<Long> ids) {
		return delegate.findByIds(
				entitatId,
				ids);
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
}