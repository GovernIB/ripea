package es.caib.ripea.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.ripea.ejb.base.AbstractServiceEjb;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.MetaDocumentTipusGenericEnumDto;
import es.caib.ripea.service.intf.dto.PaginaDto;
import es.caib.ripea.service.intf.dto.PaginacioParamsDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import es.caib.ripea.service.intf.dto.PortafirmesDocumentTipusDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.service.MetaDocumentService;
import lombok.experimental.Delegate;

@Stateless
@RolesAllowed("**")
public class MetaDocumentServiceEjb extends AbstractServiceEjb<MetaDocumentService> implements MetaDocumentService {

	@Delegate private MetaDocumentService delegateService;

	protected void setDelegateService(MetaDocumentService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentDto create(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut, String rolActual, Long organId) {
		return delegateService.create(
				entitatId,
				metaExpedientId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut, rolActual, organId);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto create(Long entitatId, 
			MetaDocumentDto metaDocument, String plantillaNom,
			String plantillaContentType, byte[] plantillaContingut) throws NotFoundException {
		return delegateService.create(
				entitatId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut);
	}


	@Override
	@RolesAllowed("**")
	public MetaDocumentDto update(
			Long entitatId,
			Long metaExpedientId,
			MetaDocumentDto metaDocument,
			String plantillaNom,
			String plantillaContentType,
			byte[] plantillaContingut, String rolActual, Long organId) {
		return delegateService.update(
				entitatId,
				metaExpedientId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut, rolActual, organId);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public MetaDocumentDto update(Long entitatId, MetaDocumentDto metaDocument, String plantillaNom,
			String plantillaContentType, byte[] plantillaContingut) throws NotFoundException {
		return delegateService.update(
				entitatId,
				metaDocument,
				plantillaNom,
				plantillaContentType,
				plantillaContingut);
	}
	
	@Override
	@RolesAllowed("**")
	public MetaDocumentDto updateActiu(
			Long entitatId,
			Long metaExpedientId,
			Long id,
			boolean actiu, String rolActual) {
		return delegateService.updateActiu(
				entitatId,
				metaExpedientId,
				id,
				actiu, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentDto delete(
			Long entitatId,
			Long metaExpedientId,
			Long metaDocumentId, String rolActual, Long organId) {
		return delegateService.delete(
				entitatId,
				metaExpedientId,
				metaDocumentId, rolActual, organId);
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentDto findById(
			Long entitatId,
			Long metaExpedientId,
			Long id) {
		return delegateService.findById(
				entitatId,
				metaExpedientId,
				id);
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentDto findByCodi(
			Long entitatId,
			Long metaExpedientId,
			String codi) {
		return delegateService.findByCodi(
				entitatId,
				metaExpedientId,
				codi);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<MetaDocumentDto> findByMetaExpedient(
			Long entitatId,
			Long metaExpedientId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findByMetaExpedient(
				entitatId,
				metaExpedientId,
				paginacioParams);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public PaginaDto<MetaDocumentDto> findWithoutMetaExpedient(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findWithoutMetaExpedient(
				entitatId,
				paginacioParams);
	}
	
	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<MetaDocumentDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return delegateService.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getPlantilla(
			Long entitatId,
			Long contingutId,
			Long id) {
		return delegateService.getPlantilla(
				entitatId,
				contingutId,
				id);
	}

	@Override
	@RolesAllowed("**")
	public List<MetaDocumentDto> findActiusPerCreacio(
			Long entitatId,
			Long contenidorId, 
			Long metaExpedientId, boolean findAllMarkDisponiblesPerCreacio) {
		return delegateService.findActiusPerCreacio(
				entitatId,
				contenidorId, 
				metaExpedientId, findAllMarkDisponiblesPerCreacio);
	}

	@Override
	@RolesAllowed("**")
	public List<MetaDocumentDto> findActiusPerModificacio(
			Long entitatId,
			Long documentId) throws NotFoundException {
		return delegateService.findActiusPerModificacio(
				entitatId,
				documentId);
	}

	@Override
	@RolesAllowed("**")
	public List<PortafirmesDocumentTipusDto> portafirmesFindDocumentTipus() {
		return delegateService.portafirmesFindDocumentTipus();
	}

	@Override
	@RolesAllowed("**")
	public List<MetaDocumentDto> findByMetaExpedient(Long entitatId, Long metaExpedientId) {
		return delegateService.findByMetaExpedient(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentDto getDadesNti(Long entitatId, Long contingutId, Long id) throws NotFoundException {
		return delegateService.getDadesNti(
				entitatId, 
				contingutId, 
				id);
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentDto findById(
			Long metaDocumentId) {
		return delegateService.findById(metaDocumentId);
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentDto findByTipusGeneric(
			Long entitatId, 
			MetaDocumentTipusGenericEnumDto tipusGeneric) {
		return delegateService.findByTipusGeneric(
				entitatId, 
				tipusGeneric);
	}

	@Override
	@RolesAllowed("**")
	public List<MetaDocumentDto> findByMetaExpedientAndFirmaPortafirmesActiva(Long entitatId, Long metaExpedientId) {
		return delegateService.findByMetaExpedientAndFirmaPortafirmesActiva(entitatId, metaExpedientId);
	}

	@Override
	@RolesAllowed("**")
	public void marcarPerDefecte(Long entitatId, Long metaExpedientId, Long metaDocumentId, boolean remove) throws NotFoundException {
		delegateService.marcarPerDefecte(entitatId, metaExpedientId, metaDocumentId, remove);
	}

	@Override
	@RolesAllowed("**")
	public MetaDocumentDto findByMetaExpedientAndPerDefecteTrue(
			Long metaExpedientId) throws NotFoundException {
		return delegateService.findByMetaExpedientAndPerDefecteTrue(
				metaExpedientId);
	}

	@Override
	@RolesAllowed("**")
	public void moveTo(
			Long entitatId,
			Long metaDocumentId,
			int posicio) throws NotFoundException {
		delegateService.moveTo(
				entitatId,
				metaDocumentId,
				posicio);
		
	}

	@Override
	@RolesAllowed("**")
	public PinbalServeiDto findPinbalServei(Long metaDocumentId) {
		return delegateService.findPinbalServei(metaDocumentId);
	}

	@Override
	@RolesAllowed("**")
	public List<MetaDocumentDto> findByMetaExpedientAndFirmaSimpleWebActiva(
			Long entitatId,
			Long metaExpedientId) {
		return delegateService.findByMetaExpedientAndFirmaSimpleWebActiva(
				entitatId,
				metaExpedientId);
	}

}