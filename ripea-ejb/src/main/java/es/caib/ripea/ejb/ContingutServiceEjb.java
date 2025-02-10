/**
 * 
 */
package es.caib.ripea.ejb;

import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.service.ContingutService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ContingutServiceEjb implements ContingutService {

	@Delegate
	private ContingutService delegateService;

	protected void setDelegateService(ContingutService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed("tothom")
	public void dadaSave(
			Long entitatId,
			Long contingutId,
			Map<String, Object> valors, 
			Long tascaId) {
		delegateService.dadaSave(
				entitatId,
				contingutId,
				valors, 
				tascaId);
	}

	@Override
	@RolesAllowed("tothom")
	public void deleteReversible(
			Long entitatId,
			Long contingutId,
			String rolActual,
			Long tascaId) throws IOException {
		delegateService.deleteReversible(
				entitatId,
				contingutId,
				rolActual,
				tascaId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void deleteDefinitiu(
			Long entitatId,
			Long contingutId) {
		delegateService.deleteDefinitiu(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void undelete(
			Long entitatId,
			Long contingutId) throws IOException {
		delegateService.undelete(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public void move(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId, String rolActual) {
		delegateService.move(entitatId, contingutOrigenId, contingutDestiId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto copy(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId,
			boolean recursiu) {
		return delegateService.copy(entitatId, contingutOrigenId, contingutDestiId, recursiu);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, 
			boolean ambPermisos, 
			String rolActual, 
			Long organActualId) {
		return delegateService.findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions, 
				ambPermisos, 
				rolActual, 
				organActualId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId) {
		return delegateService.findAmbIdAdmin(
				entitatId,
				contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ValidacioErrorDto> findErrorsValidacio(
			Long entitatId,
			Long contingutId) {
		return delegateService.findErrorsValidacio(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<AlertaDto> findAlertes(
			Long entitatId,
			Long contingutId) throws NotFoundException {
		return delegateService.findAlertes(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegateService.findLogsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegateService.findLogsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ContingutLogDetallsDto findLogDetallsPerContingutAdmin(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException {
		return delegateService.findLogDetallsPerContingutAdmin(
				entitatId,
				contingutId,
				contingutLogId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException {
		return delegateService.findLogDetallsPerContingutUser(
				entitatId,
				contingutId,
				contingutLogId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegateService.findMovimentsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegateService.findMovimentsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ResultDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum) {
		return delegateService.findAdmin(
				entitatId,
				filtre,
				paginacioParams, 
				resultEnum);
	}





	@Override
	@RolesAllowed("tothom")
	public ArxiuDetallDto getArxiuDetall(
			Long entitatId,
			Long contingutId) {
		return delegateService.getArxiuDetall(
				entitatId,
				contingutId);
	}

    @Override
	@RolesAllowed("tothom")
    public List<CodiValorDto> sincronitzarEstatArxiu(Long entitatId, Long contingutId) {
        return delegateService.sincronitzarEstatArxiu(entitatId, contingutId);
    }

    @Override
	@RolesAllowed("tothom")
	public FitxerDto exportacioEni(
			Long entitatId,
			Long contingutId) {
		return delegateService.exportacioEni(
				entitatId,
				contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual)
			throws NotFoundException {
		return delegateService.findIdsDocumentsPerFirmaMassiu(
				entitatId,
				filtre, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<DocumentDto> findDocumentsMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			String rolActual) throws NotFoundException {
		return delegateService.findDocumentsMassiu(
				entitatId,
				filtre,
				paginacioParams, 
				rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public Long link(
			Long entitatId, 
			Long contingutOrigenId, 
			Long contingutDestiId, 
			boolean recursiu)
			throws NotFoundException, ValidationException {
		return delegateService.link(
				entitatId, 
				contingutOrigenId, 
				contingutDestiId, 
				recursiu);
	}


	@Override
	@RolesAllowed("tothom")
	public void order(
			Long entitatId,
			Long contingutId, 
			Map<Integer, Long> orderedElements)
			throws NotFoundException, ValidationException {
		delegateService.order(entitatId, contingutId, orderedElements);
	}


	@Override
	public PaginaDto<DocumentDto> findDocumentsPerCopiarCsv(Long entitatId, ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException {
		return delegateService.findDocumentsPerCopiarCsv(entitatId, filtre, paginacioParams, rolActual);
	}


	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

    @Override
    public Boolean netejaContingutsOrfes() {
        return delegateService.netejaContingutsOrfes();
    }

    @Override
	@RolesAllowed("IPA_SUPER")
    public ResultDocumentsSenseContingut arreglaDocumentsSenseContingut() {
        return delegateService.arreglaDocumentsSenseContingut();
    }

	@Override
	public boolean isExpedient(
			Long contingutId) {
		return delegateService.isExpedient(contingutId);
	}

	@Override
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions,
			boolean ambPermisos,
			String rolActual,
			boolean ambEntitat,
			boolean ambMapPerTipusDocument, 
			boolean ambMapPerEstat) {
		return delegateService.findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions,
				ambPermisos,
				rolActual,
				ambEntitat,
				ambMapPerTipusDocument, 
				ambMapPerEstat);
	}

	@Override
	public ContingutDto findAmbIdUserPerMoureCopiarVincular(Long entitatId, Long contingutId) throws NotFoundException {
		return delegateService.findAmbIdUserPerMoureCopiarVincular(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public void checkIfPermitted(
			Long contingutId,
			String rolActual, 
			PermissionEnumDto permission) {
		delegateService.checkIfPermitted(
				contingutId,
				rolActual, 
				permission);
	}

	@Override
	@RolesAllowed("tothom")
	public Long getPareId(Long contingutId) {
		return delegateService.getPareId(contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public Long getExpedientId(Long contingutId) {
		return delegateService.getExpedientId(contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean isDeleted(Long contingutId) {
		return delegateService.isDeleted(contingutId);
	}

	
	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ContingutMassiuDto> findDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual) throws NotFoundException {
		return delegateService.findDocumentsPerFirmaMassiu(
				entitatId,
				filtre,
				paginacioParams,
				rolActual);
	}

	
	@Override
	@RolesAllowed("tothom")
	public List<ContingutDto> getFillsBasicInfo(Long contingutId) {
		return delegateService.getFillsBasicInfo(contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto getBasicInfo(Long contingutId, boolean checkPermissions) {
		return delegateService.getBasicInfo(contingutId, checkPermissions);
	}


	@Override
	@RolesAllowed("tothom")
	public ResultDto<ContingutMassiuDto> findDocumentsPerFirmaSimpleWebMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum) {
		return delegateService.findDocumentsPerFirmaSimpleWebMassiu(
				entitatId,
				filtre,
				paginacioParams,
				rolActual,
				resultEnum);
	}

}