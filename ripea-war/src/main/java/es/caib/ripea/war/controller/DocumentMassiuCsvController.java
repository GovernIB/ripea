/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.service.*;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * Controlador per a l'acció massiva: copiar enllaços
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/massiu/csv")
public class DocumentMassiuCsvController extends BaseUserOAdminOOrganController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "DocumentMassiuCsvController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "DocumentMassiuCsvController.session.seleccio";

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	@Autowired
	private OrganGestorService organGestorService;

	@RequestMapping(method = RequestMethod.GET)
	public String portafirmesGet(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
		filtreCommand.setTipusElement(ContingutTipusEnumDto.DOCUMENT);
		filtreCommand.setBloquejarTipusElement(true);
		filtreCommand.setBloquejarMetaDada(true);
		filtreCommand.setBloquejarMetaExpedient(false);
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO));
		model.addAttribute(
				"titolMassiu",
				getMessage(request, "accio.massiva.titol.csv"));
		model.addAttribute(
				"botoMassiu",
				getMessage(request, "accio.massiva.boto.crear.csv"));
		model.addAttribute(
				filtreCommand);
		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerModificacio(entitatActual.getId(), rolActual));

		List<ExpedientSelectorDto> expedients = new ArrayList<ExpedientSelectorDto>();
		if (filtreCommand.getMetaExpedientId() != null)
			expedients = expedientService.findPerUserAndProcediment(entitatActual.getId(), filtreCommand.getMetaExpedientId(), rolActual);
		model.addAttribute(
				"expedients",
				expedients);

		return "documentMassiuCsvList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String filtrePost(
			HttpServletRequest request,
			@Valid ContingutMassiuFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		
		filtreCommand.setTipusElement(ContingutTipusEnumDto.DOCUMENT);
		filtreCommand.setBloquejarTipusElement(false);
		filtreCommand.setBloquejarMetaDada(false);
		filtreCommand.setBloquejarMetaExpedient(false);
		
		return "redirect:/massiu/csv";
	}
	
	@RequestMapping(value = "/urlValidacio", method = RequestMethod.GET)
	@ResponseBody
	public String portafirmesPost(
			HttpServletRequest request,
			@Valid ContingutMassiuFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {

		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/csv",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String urlValidacio = aplicacioService.propertyFindByNom("es.caib.ripea.documents.validacio.url");
		String enllacCsv = "";
		
		if (urlValidacio != null) {
			for (Long documentId : seleccio) {
				ArxiuDetallDto arxiuDetall = contingutService.getArxiuDetall(
						entitatActual.getId(),
						documentId);
				enllacCsv += urlValidacio + arxiuDetall.getMetadadesAddicionals().get("csv") + "\n";
			}
			MissatgesHelper.success(
					request,
					getMessage(request, "accio.massiva.csv.copiat.ok"));
		} else {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/csv",
					"accio.massiva.url.ko",
					null);
		}
		return enllacCsv;
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand contingutMassiuFiltreCommand = getFiltreCommand(request);

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		try {
			return DatatablesHelper.getDatatableResponse(
					request,
					 contingutService.findDocumentsPerCopiarCsv(
								entitatActual.getId(), 
								ContingutMassiuFiltreCommand.asDto(contingutMassiuFiltreCommand),
								DatatablesHelper.getPaginacioDtoFromRequest(request), 
								rolActual),
					 "id",
					 SESSION_ATTRIBUTE_SELECCIO);
		} catch (Exception e) {
			throw e;
		}
		
	}

	@RequestMapping(value = "/expedients/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientSelectorDto> findAll(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		List<ExpedientSelectorDto> expedients = new ArrayList<ExpedientSelectorDto>();
		if (metaExpedientId != null)
			expedients = expedientService.findPerUserAndProcediment(entitatActual.getId(), metaExpedientId, rolActual);
		return expedients;
	}
	
	
	
	@RequestMapping(value = "/metaDocuments/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaDocumentDto> findMetaDocuments(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		List<MetaDocumentDto> metaDocuments = new ArrayList<MetaDocumentDto>();
		metaDocuments = metaDocumentService.findByMetaExpedientAndFirmaPortafirmesActiva(entitatActual.getId(), metaExpedientId);
		
		return metaDocuments;
	}

	@RequestMapping(value = "/{metadocumentId}/portafirmes/flux/plantilles", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesFluxRespostaDto> getPlantillesDisponibles(HttpServletRequest request, @PathVariable Long metadocumentId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromMetaDocumentId(metadocumentId));
		List<PortafirmesFluxRespostaDto> resposta;

		Boolean filtrarPerUsuariActual = aplicacioService.propertyBooleanFindByKey("es.caib.ripea.plugin.portafirmes.flux.filtrar.usuari.descripcio");
		if (filtrarPerUsuariActual == null || filtrarPerUsuariActual.equals(true)) {

			resposta = portafirmesFluxService.recuperarPlantillesDisponibles(true);

			MetaDocumentDto metaDocument = metaDocumentService.findById(entitatActual.getId(), metadocumentId);
			String fluxPerDefecteId = metaDocument.getPortafirmesFluxId();
			if (fluxPerDefecteId != null && !fluxPerDefecteId.isEmpty()) {
				PortafirmesFluxInfoDto portafirmesFluxInfoDto = portafirmesFluxService.recuperarDetallFluxFirma(fluxPerDefecteId);

				boolean isAlreadyOnList = false;
				for (PortafirmesFluxRespostaDto respostaDto : resposta) {
					if (respostaDto.getFluxId().equals(fluxPerDefecteId)) {
						isAlreadyOnList = true;
					}
				}
				if (!isAlreadyOnList) {
					PortafirmesFluxRespostaDto portafirmesFluxRespostaDto = new PortafirmesFluxRespostaDto();
					portafirmesFluxRespostaDto.setFluxId(fluxPerDefecteId);
					portafirmesFluxRespostaDto.setNom(portafirmesFluxInfoDto.getNom());
					resposta.add(0, portafirmesFluxRespostaDto);
				}
			}
		} else {
			resposta = portafirmesFluxService.recuperarPlantillesDisponibles(false);
		}


		return resposta;
	}

	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
			
			String rolActual = (String)request.getSession().getAttribute(
					SESSION_ATTRIBUTE_ROL_ACTUAL);
			
			seleccio.addAll(
					contingutService.findIdsDocumentsPerFirmaMassiu(
							entitatActual.getId(),
							ContingutMassiuFiltreCommand.asDto(filtreCommand), rolActual));
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		return seleccio.size();
	}

	private ContingutMassiuFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ContingutMassiuFiltreCommand filtreCommand = (ContingutMassiuFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ContingutMassiuFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
