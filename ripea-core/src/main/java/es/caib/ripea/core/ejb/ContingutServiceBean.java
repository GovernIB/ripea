/**
 * 
 */
package es.caib.ripea.core.ejb;

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.ArxiuDetallDto;
import es.caib.ripea.core.api.dto.CodiValorDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutFiltreDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.ContingutLogDto;
import es.caib.ripea.core.api.dto.ContingutMassiuDto;
import es.caib.ripea.core.api.dto.ContingutMassiuFiltreDto;
import es.caib.ripea.core.api.dto.ContingutMovimentDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.PermissionEnumDto;
import es.caib.ripea.core.api.dto.ResultDocumentsSenseContingut;
import es.caib.ripea.core.api.dto.ResultDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.dto.ValidacioErrorDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.exception.ValidationException;
import es.caib.ripea.core.api.service.ContingutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
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
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ContingutServiceBean implements ContingutService {

	@Autowired
	ContingutService delegate;




	@Override
	@RolesAllowed("tothom")
	public void dadaSave(
			Long entitatId,
			Long contingutId,
			Map<String, Object> valors, 
			Long tascaId) {
		delegate.dadaSave(
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
		delegate.deleteReversible(
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
		delegate.deleteDefinitiu(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public void undelete(
			Long entitatId,
			Long contingutId) throws IOException {
		delegate.undelete(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public void move(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId, String rolActual) {
		delegate.move(entitatId, contingutOrigenId, contingutDestiId, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto copy(
			Long entitatId,
			Long contingutOrigenId,
			Long contingutDestiId,
			boolean recursiu) {
		return delegate.copy(entitatId, contingutOrigenId, contingutDestiId, recursiu);
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
		return delegate.findAmbIdUser(
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
		return delegate.findAmbIdAdmin(
				entitatId,
				contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ValidacioErrorDto> findErrorsValidacio(
			Long entitatId,
			Long contingutId) {
		return delegate.findErrorsValidacio(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<AlertaDto> findAlertes(
			Long entitatId,
			Long contingutId) throws NotFoundException {
		return delegate.findAlertes(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegate.findLogsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegate.findLogsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ContingutLogDetallsDto findLogDetallsPerContingutAdmin(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException {
		return delegate.findLogDetallsPerContingutAdmin(
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
		return delegate.findLogDetallsPerContingutUser(
				entitatId,
				contingutId,
				contingutLogId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegate.findMovimentsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegate.findMovimentsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("IPA_ADMIN")
	public ResultDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			ResultEnumDto resultEnum) {
		return delegate.findAdmin(
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
		return delegate.getArxiuDetall(
				entitatId,
				contingutId);
	}

    @Override
	@RolesAllowed("tothom")
    public List<CodiValorDto> sincronitzarEstatArxiu(Long entitatId, Long contingutId) {
        return delegate.sincronitzarEstatArxiu(entitatId, contingutId);
    }

    @Override
	@RolesAllowed("tothom")
	public FitxerDto exportacioEni(
			Long entitatId,
			Long contingutId) {
		return delegate.exportacioEni(
				entitatId,
				contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre, String rolActual)
			throws NotFoundException {
		return delegate.findIdsDocumentsPerFirmaMassiu(
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
		return delegate.findDocumentsMassiu(
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
		return delegate.link(
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
		delegate.order(entitatId, contingutId, orderedElements);
	}


	@Override
	public PaginaDto<DocumentDto> findDocumentsPerCopiarCsv(Long entitatId, ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams, String rolActual) throws NotFoundException {
		return delegate.findDocumentsPerCopiarCsv(entitatId, filtre, paginacioParams, rolActual);
	}


	// Mètodes per evitar errors al tenir continguts orfes en base de dades
	// ////////////////////////////////////////////////////////////////////

    @Override
    public Boolean netejaContingutsOrfes() {
        return delegate.netejaContingutsOrfes();
    }

    @Override
	@RolesAllowed("IPA_SUPER")
    public ResultDocumentsSenseContingut arreglaDocumentsSenseContingut() {
        return delegate.arreglaDocumentsSenseContingut();
    }

	@Override
	public boolean isExpedient(
			Long contingutId) {
		return delegate.isExpedient(contingutId);
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
		return delegate.findAmbIdUser(
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
		return delegate.findAmbIdUserPerMoureCopiarVincular(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public void checkIfPermitted(
			Long contingutId,
			String rolActual, 
			PermissionEnumDto permission) {
		delegate.checkIfPermitted(
				contingutId,
				rolActual, 
				permission);
	}

	@Override
	@RolesAllowed("tothom")
	public Long getPareId(Long contingutId) {
		return delegate.getPareId(contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public Long getExpedientId(Long contingutId) {
		return delegate.getExpedientId(contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean isDeleted(Long contingutId) {
		return delegate.isDeleted(contingutId);
	}

	
	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ContingutMassiuDto> findDocumentsPerFirmaMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual) throws NotFoundException {
		return delegate.findDocumentsPerFirmaMassiu(
				entitatId,
				filtre,
				paginacioParams,
				rolActual);
	}

	
	@Override
	@RolesAllowed("tothom")
	public List<ContingutDto> getFillsBasicInfo(Long contingutId) {
		return delegate.getFillsBasicInfo(contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto getBasicInfo(Long contingutId, boolean checkPermissions) {
		return delegate.getBasicInfo(contingutId, checkPermissions);
	}


	@Override
	@RolesAllowed("tothom")
	public ResultDto<ContingutMassiuDto> findDocumentsPerFirmaSimpleWebMassiu(
			Long entitatId,
			ContingutMassiuFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			String rolActual,
			ResultEnumDto resultEnum) {
		return delegate.findDocumentsPerFirmaSimpleWebMassiu(
				entitatId,
				filtre,
				paginacioParams,
				rolActual,
				resultEnum);
	}

}