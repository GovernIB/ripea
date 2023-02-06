/**
 * 
 */
package es.caib.ripea.war.controller;

import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto.ExecucioMassivaTipusDto;
import es.caib.ripea.core.api.service.*;
import es.caib.ripea.war.command.ContingutMassiuFiltreCommand;
import es.caib.ripea.war.command.PortafirmesEnviarCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controlador per al manteniment de bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/massiu/portafirmes")
public class DocumentMassiuPortafirmesController extends BaseUserOAdminOOrganController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "DocumentMassiuPortafirmesController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "DocumentMassiuPortafirmesController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_TRANSACCIOID = "DocumentController.session.transaccioID";

	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;

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
				getMessage(request, "accio.massiva.titol.portafirmes"));
		model.addAttribute(
				"botoMassiu",
				getMessage(request, "accio.massiva.boto.crear.portafirmes"));
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

		MissatgesHelper.info(
				request,
				getMessage(request, "accio.massiva.list.filtre.tipusdocument.comment"));

		return "documentMassiuPortafirmesList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String portafirmesPost(
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
		filtreCommand.setBloquejarTipusElement(true);
		filtreCommand.setBloquejarMetaDada(true);
		filtreCommand.setBloquejarMetaExpedient(false);
		
		return "redirect:/massiu/portafirmes";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/crear", method = RequestMethod.GET)
	public String getCrearPortafirmes(
			HttpServletRequest request,
			Model model) {
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/portafirmes",
					"accio.massiva.seleccio.buida",
					null);
		}

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);

		PortafirmesEnviarCommand command = new PortafirmesEnviarCommand();
		model.addAttribute(command);

		//Flux de firma
		if (filtreCommand.getMetaExpedientId() != null && filtreCommand.getMetaDocumentId() != null) {
			MetaDocumentDto metaDocument = metaDocumentService.findById(
					entitatActual.getId(),
					filtreCommand.getMetaExpedientId(),
					filtreCommand.getMetaDocumentId());

			setFluxPredefinit(
					metaDocument,
					model,
					command);

			model.addAttribute("metadocumentId", metaDocument.getId());
		} else {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/portafirmes",
					"accio.massiva.seleccio.document.biud",
					null);
		}
		return "enviarPortafirmes";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/crear", method = RequestMethod.POST)
	public String postCrearPortafirmes(
			HttpServletRequest request,
			@Valid PortafirmesEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		if (command.getPortafirmesFluxTipus() == MetaDocumentFirmaFluxTipusEnumDto.SIMPLE && (command.getPortafirmesResponsables() == null || command.getPortafirmesResponsables().length == 0)) {
			bindingResult.rejectValue("portafirmesResponsables", "NotNull");
		}
		
		if (bindingResult.hasErrors()) {
			
			ContingutMassiuFiltreCommand filtreCommand = getFiltreCommand(request);
			
			MetaDocumentDto metaDocument = metaDocumentService.findById(
					entitatActual.getId(),
					filtreCommand.getMetaExpedientId(),
					filtreCommand.getMetaDocumentId());

			setFluxPredefinit(
					metaDocument,
					model,
					command);

			model.addAttribute("metadocumentId", metaDocument.getId());
			
			return "enviarPortafirmes";
		}
		
		
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);

		String transaccioId = null;
		if (MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB.equals(command.getPortafirmesFluxTipus())) {
			transaccioId = (String)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_TRANSACCIOID);
		}
		ExecucioMassivaDto dto = new ExecucioMassivaDto();
		dto.setTipus(ExecucioMassivaTipusDto.PORTASIGNATURES);
		dto.setDataInici(command.getDataInici());
		dto.setEnviarCorreu(command.isEnviarCorreu());
		dto.setMotiu(command.getMotiu());
		dto.setPrioritat(command.getPrioritat());
//		dto.setDataCaducitat(filtreCommand.getDataCaducitat());
		dto.setPortafirmesResponsables(command.getPortafirmesResponsables());
		dto.setPortafirmesSequenciaTipus(command.getPortafirmesSequenciaTipus());
		dto.setPortafirmesFluxId(command.getPortafirmesEnviarFluxId());
		dto.setPortafirmesTransaccioId(transaccioId);
		dto.setContingutIds(new ArrayList<Long>(seleccio));
		dto.setRolActual((String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL));

		execucioMassivaService.crearExecucioMassiva(entitatActual.getId(), dto);
		
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO);
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../massiu/portafirmes",
				"accio.massiva.creat.ok");
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
					 contingutService.findDocumentsPerFirmaMassiu(
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

	@RequestMapping(value = "/consulta/{pagina}", method = RequestMethod.GET)
	public String getConsultaExecucions(
			HttpServletRequest request,
			@PathVariable int pagina,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		pagina = (pagina < 0 ? 0 : pagina);
		List<ExecucioMassivaDto> execucionsMassives = new ArrayList<ExecucioMassivaDto>();
		UsuariDto usuariActual = null;
		if (RolHelper.isRolActualAdministrador(request)) {
			model.addAttribute(
					"titolConsulta",
					getMessage(request, "accio.massiva.consulta.titol.gobal"));
		} else if (RolHelper.isRolActualUsuari(request) || RolHelper.isRolActualAdministradorOrgan(request)) {
			usuariActual = aplicacioService.getUsuariActual();
			model.addAttribute(
					"titolConsulta",
					getMessage(request, "accio.massiva.consulta.titol.usuari", new String[]{usuariActual.getNom()}));
		}
		execucionsMassives = execucioMassivaService.findExecucionsMassivesPerUsuari(entitatActual.getId(), usuariActual,pagina);
		if (execucionsMassives.size() < 8) {
			model.addAttribute("sumador", 0);
		} else {
			model.addAttribute("sumador", 1);
		}
		model.addAttribute("pagina",pagina);
		model.addAttribute("execucionsMassives", execucionsMassives);
		return "consultaExecucionsMassives";
	}

	@RequestMapping(value = "/consultaContingut/{execucioMassivaId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExecucioMassivaContingutDto> getConsultaContinguts(
			HttpServletRequest request,
			@PathVariable Long execucioMassivaId) {
		if (RolHelper.isRolActualUsuari(request)) 
			getEntitatActualComprovantPermisos(request);
		return execucioMassivaService.findContingutPerExecucioMassiva(execucioMassivaId);
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	private void setFluxPredefinit(
			MetaDocumentDto metaDocument,
			Model model,
			PortafirmesEnviarCommand command) {
		
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromMetaDocumentId(metaDocument.getId()));
		model.addAttribute("fluxTipus", metaDocument.getPortafirmesFluxTipus());
		if (metaDocument.getPortafirmesFluxTipus() != null) {
			command.setPortafirmesFluxTipus(metaDocument.getPortafirmesFluxTipus());
			model.addAttribute("portafirmesFluxId", metaDocument.getPortafirmesFluxId());
			if (metaDocument.getPortafirmesFluxTipus().equals(MetaDocumentFirmaFluxTipusEnumDto.PORTAFIB) && metaDocument.getPortafirmesFluxId() == null) {
				model.addAttribute("nouFluxDeFirma", true);
			} else {
				String urlPlantilla = portafirmesFluxService.recuperarUrlMostrarPlantilla(metaDocument.getPortafirmesFluxId());
				model.addAttribute("nouFluxDeFirma", false);
				model.addAttribute("urlPlantilla", urlPlantilla);
			}
		} else {
			model.addAttribute("nouFluxDeFirma", false);
			command.setPortafirmesFluxTipus(MetaDocumentFirmaFluxTipusEnumDto.SIMPLE);
		}
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
