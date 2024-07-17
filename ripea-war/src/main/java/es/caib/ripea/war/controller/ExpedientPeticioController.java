/**
 * 
 */
package es.caib.ripea.war.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.ripea.core.api.dto.*;
import es.caib.ripea.core.api.exception.DocumentAlreadyImportedException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.ExpedientPeticioService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.GrupService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.ExpedientPeticioAcceptarCommand;
import es.caib.ripea.war.command.ExpedientPeticioFiltreCommand;
import es.caib.ripea.war.command.ExpedientPeticioModificarCommand;
import es.caib.ripea.war.command.ExpedientPeticioRebutjarCommand;
import es.caib.ripea.war.command.RegistreAnnexCommand;
import es.caib.ripea.war.helper.AnotacionsPendentsHelper;
import es.caib.ripea.war.command.RegistreInteressatsCommand;
import es.caib.ripea.war.helper.ConversioTipusHelper;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.JsonResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador per al llistat d'expedients peticions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/expedientPeticio")
public class ExpedientPeticioController extends BaseUserOAdminOOrganController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "ExpedientPeticioController.session.filtre";
	
	private static final String SESSION_ATTRIBUTE_COMMAND = "ExpedientPeticioController.session.command";
	private static final String SESSION_ATTRIBUTE_TIPUS_DOCS_DISPONIBLES = "ExpedientPeticioController.session.tipusDocsDisponibles";
	private static final String SESSION_ATTRIBUTE_INDEX = "ExpedientPeticioController.session.index";

	@Autowired
	private ExpedientPeticioService expedientPeticioService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private MetaExpedientService metaExpedientService;	
	@Autowired
	private ExpedientService expedientService;		
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private GrupService grupService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		
		long t1 = System.currentTimeMillis();
    	if (aplicacioService.mostrarLogsCercadorAnotacio())
    		logger.info("expedientPeticio start ");
    	
		long t2 = System.currentTimeMillis();

		model.addAttribute(getFiltreCommand(request));
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long organActualId = EntitatHelper.getOrganGestorActualId(request);
		
		if (aplicacioService.mostrarLogsCercadorAnotacio())
    		logger.info("getFiltreCommand time:  " + (System.currentTimeMillis() - t2) + " ms");
		
		long t3 = System.currentTimeMillis();
		List<MetaExpedientDto> metaExpedientsPermesos = expedientPeticioService.findMetaExpedientsPermesosPerAnotacions(
				entitatActual.getId(),
				organActualId,
				rolActual);
		model.addAttribute("metaExpedients", metaExpedientsPermesos);
		model.addAttribute("isRolActualAdmin", rolActual.equals("IPA_ADMIN") || rolActual.equals("IPA_ORGAN_ADMIN"));

		// Actualitzar nombre d'anotacions pendents
		AnotacionsPendentsHelper.resetCounterAnotacionsPendents(request);

		if (aplicacioService.mostrarLogsCercadorAnotacio())
    		logger.info("findMetaExpedientsPermesosPerAnotacions time:  " + (System.currentTimeMillis() - t3) + " ms");
		
    	if (aplicacioService.mostrarLogsCercadorAnotacio())
    		logger.info("expedientPeticio end:  " + (System.currentTimeMillis() - t1) + " ms");
		return "expedientPeticioList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			ExpedientPeticioFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {

		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
			}
		}
		return "redirect:expedientPeticio";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		
    	if (aplicacioService.mostrarLogsCercadorAnotacio())
    		logger.info("expedientPeticioDatatable start ");
		long t1 = System.currentTimeMillis();

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientPeticioFiltreCommand expedientPeticioFiltreCommand = getFiltreCommand(request);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		DatatablesResponse dr = DatatablesHelper.getDatatableResponse(
				request,
				expedientPeticioService.findAmbFiltre(
						entitatActual.getId(),
						ExpedientPeticioFiltreCommand.asDto(expedientPeticioFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request),
						rolActual,
						EntitatHelper.getOrganGestorActualId(request)),
				"id");
		
    	if (aplicacioService.mostrarLogsCercadorAnotacio())
    		logger.info("expedientPeticioDatatable end:  " + (System.currentTimeMillis() - t1) + " ms");
    	
    	return dr;
	}
	

	@RequestMapping(value = "/{registreAnnexId}/{expedientPeticioId}/reintentar", method = RequestMethod.GET)
	public String retryCreateDocFromAnnex(HttpServletRequest request, @PathVariable Long registreAnnexId, @PathVariable Long expedientPeticioId, Model model) {

		RegistreAnnexDto registreAnnex = expedientPeticioService.findAnnexById(registreAnnexId);
		if (registreAnnex.getDocumentId() != null) {
			
			Exception exception = expedientService.retryMoverAnnexArxiu(registreAnnexId);
			if (exception == null) {
				return getModalControllerReturnValueSuccess(request, "", "expedient.peticio.detalls.controller.reintentat.ok");
			}
			Throwable root = ExceptionHelper.getRootCauseOrItself(exception);
			Object[] o = new Object[]{root.getMessage()};
 			return getModalControllerReturnValueError(request, "", "expedient.peticio.detalls.controller.reintentat.error", o, root);
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		List<MetaDocumentDto> metaDocumentsQueQuedenPerCreacio = metaDocumentService.findActiusPerCreacio(entitatActual.getId(), expedientPeticioDto.getExpedientId(), null, false);
		model.addAttribute("metaDocuments", metaDocumentsQueQuedenPerCreacio);
		model.addAttribute("expedientPeticioId", expedientPeticioId);
		RegistreAnnexCommand registreAnnexCommand = ConversioTipusHelper.convertir(expedientPeticioService.findAnnexById(registreAnnexId), RegistreAnnexCommand.class);
		ExpedientDto expedientDto = expedientService.findById(entitatActual.getId(), expedientPeticioDto.getExpedientId(), null);
		MetaDocumentDto metaDocPerDefecte = metaDocumentService.findByMetaExpedientAndPerDefecteTrue(expedientDto.getMetaExpedient().getId());
		if (metaDocPerDefecte != null) {
			boolean potCrearMetaDocPerDefecte = false;
			for (MetaDocumentDto metaDocumentDto : metaDocumentsQueQuedenPerCreacio) {
				if (metaDocumentDto.getId().equals(metaDocPerDefecte.getId())) {
					potCrearMetaDocPerDefecte = true;
				}
			}
			if (potCrearMetaDocPerDefecte) {
				registreAnnexCommand.setMetaDocumentId(metaDocPerDefecte.getId());
			} else {
				Object [] o = new Object[] { metaDocPerDefecte.getNom() };
				MissatgesHelper.warning(request, getMessage(request, "expedient.peticio.controller.acceptar.warning.no.pot.crear.metadoc.per.defecte", o));
			}
		}
		model.addAttribute("registreAnnexCommand", registreAnnexCommand);
		return "expedientPeticioReintentarMetaDoc";
	}
	
	@RequestMapping(value = "/{expedientPeticioId}/reintentar", method = RequestMethod.POST)
	public String retryCreateDocFromAnnexPost(HttpServletRequest request, @Valid RegistreAnnexCommand command, BindingResult bindingResult,
											  @PathVariable Long expedientPeticioId, Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (command.getMetaDocumentId() == null) {
			bindingResult.rejectValue("metaDocumentId", "NotNull");
		}
		if (bindingResult.hasErrors()) {
			ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
			List<MetaDocumentDto> metaDocumentsQueQuedenPerCreacio = metaDocumentService.findActiusPerCreacio(entitatActual.getId(), expedientPeticioDto.getExpedientId(), null, false);
			model.addAttribute("metaDocuments", metaDocumentsQueQuedenPerCreacio);
			model.addAttribute("expedientPeticioId", expedientPeticioId);
			command.setNtiFechaCaptura(expedientPeticioService.findAnnexById(command.getId()).getNtiFechaCaptura()); //in POST it is passed as date without time, we want timpestamp 
			return "expedientPeticioReintentarMetaDoc";
		}
		Exception exception = expedientService.retryCreateDocFromAnnex(command.getId(), command.getMetaDocumentId(), RolHelper.getRolActual(request));
		if (exception == null) {
			return getModalControllerReturnValueSuccess(
					request,
					"",
					"expedient.peticio.detalls.controller.reintentat.ok");			
		} else {
			return getModalControllerReturnValueError(
					request,
					"",
					"expedient.peticio.detalls.controller.reintentat.error",
					new Object[]{ExceptionHelper.getRootCauseOrItself(exception).getMessage()},
					exception);
		}
	}


	@RequestMapping(value = "/{expedientPeticioId}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long expedientPeticioId, Model model) {
		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		
		boolean isErrorDocuments = false;
		for (RegistreAnnexDto registreAnnexDto : expedientPeticioDto.getRegistre().getAnnexos()) {
			if (registreAnnexDto.getExpedientId() != null && (registreAnnexDto.getDocumentId() == null || !StringUtils.isEmpty(registreAnnexDto.getError()))) {
				isErrorDocuments = true;
			}
		}
		if (isErrorDocuments) {
			MissatgesHelper.warning(request, getMessage(request, "expedient.peticio.controller.acceptat.warning"));
		}
		model.addAttribute("isErrorDocuments", isErrorDocuments);
		model.addAttribute("peticio", expedientPeticioDto);
		model.addAttribute("isIncorporacioJustificantActiva", isIncorporacioJustificantActiva());
		return "expedientPetcioDetall";
	}

	@RequestMapping(value = "/{expedientId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse peticionsDatatable(
			HttpServletRequest request,
			@PathVariable Long expedientId,
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, expedientPeticioService.findByExpedientAmbFiltre(entitatActual.getId(), expedientId, params));
	}

	@RequestMapping(value = "/rebutjar/{expedientPeticioId}", method = RequestMethod.GET)
	public String rebutjarGet(HttpServletRequest request, @PathVariable Long expedientPeticioId, Model model) {

		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		ExpedientPeticioRebutjarCommand command = new ExpedientPeticioRebutjarCommand();
		command.setId(expedientPeticioId);
		model.addAttribute("expedientPeticioRebutjarCommand", command);
		model.addAttribute("expedientPeticioId", expedientPeticioDto.getId());
		return "expedientPeticioRebutjar";
	}

	@RequestMapping(value = "/rebutjar", method = RequestMethod.POST)
	public String rebutjarPost(HttpServletRequest request, @Valid ExpedientPeticioRebutjarCommand command, BindingResult bindingResult, Model model) {
		
		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(command.getId());
		if (bindingResult.hasErrors()) {
			model.addAttribute("expedientPeticioRebutjarCommand", command);
			model.addAttribute("expedientPeticioId", command.getId());
			return "expedientPeticioRebutjar";
		}
		try {
			expedientPeticioService.rebutjar(command.getId(), command.getObservacions());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientPeticio",
					"expedient.peticio.controller.rebutjat.ok",
					new Object[] { expedientPeticioDto.getRegistre().getIdentificador() });
		} catch (Exception ex) {
			logger.error("Error al rebujtar anotacio", ex);
			return getModalControllerReturnValueErrorMessageText(request, "redirect:expedientPeticio", ex.getMessage(), ex);
		}
	}

	@RequestMapping(value = "/acceptar/{expedientPeticioId}", method = RequestMethod.GET)
	public String acceptar(HttpServletRequest request, @PathVariable Long expedientPeticioId, Model model) {
		
		ExpedientPeticioAcceptarCommand command = new ExpedientPeticioAcceptarCommand();
		command.setAgafarExpedient(true);
		command.setPrioritat(PrioritatEnumDto.NORMAL);
		omplirModel(expedientPeticioId, request, model, command);
		return "expedientPeticioAccept";

	}

	private void validateExpedient(
			ExpedientPeticioAcceptarCommand command,
			BindingResult bindingResult) {
		
		if (command.getMetaExpedientId() == null) {
			bindingResult.rejectValue("metaExpedientId", "NotNull");
		}
		if (command.getAccio() == ExpedientPeticioAccioEnumDto.INCORPORAR && command.getExpedientId() == null) {
			bindingResult.rejectValue("expedientId", "NotNull");
		}
		if (command.getAccio() == ExpedientPeticioAccioEnumDto.CREAR ) {
			if (command.getNewExpedientTitol() == null || command.getNewExpedientTitol().isEmpty()) {
				bindingResult.rejectValue("newExpedientTitol", "NotNull");
			}
			if (command.getAny() == 0) {
				bindingResult.rejectValue("any", "NotNull");
			}
		}
		if (command.getNewExpedientTitol() != null && command.getNewExpedientTitol().contains(".")) {
			bindingResult.rejectValue("newExpedientTitol", "ExpedientODocumentNom");
		}
		
		boolean exists = expedientService.checkIfExistsByMetaExpedientAndNom(
				command.getMetaExpedientId(),
				command.getNewExpedientTitol()) != null;
		if (exists) {
			bindingResult.rejectValue("newExpedientTitol", "NomExpedienteRepetit");
		}
		
		if (Utils.isBiggerThan(command.getNewExpedientTitol(), 239)) {
			bindingResult.rejectValue("newExpedientTitol", "Size", new Object[] { null, 239, 0 }, null);
		}

		if (command.isGestioAmbGrupsActiva() && command.getGrupId() == null) {
			bindingResult.rejectValue("grupId", "NotNull");
		}

	}
	

	@RequestMapping(value = "/acceptar/{expedientPeticioId}/getFirstAnnex", method = RequestMethod.POST)
	public String acceptarPostGetFirstAnnex(
			HttpServletRequest request,
			@Valid ExpedientPeticioAcceptarCommand expedientPeticioAcceptarCommand,
			@PathVariable Long expedientPeticioId,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		validateExpedient(expedientPeticioAcceptarCommand, bindingResult);
		if (bindingResult.hasErrors()) {
			omplirModel(expedientPeticioId, request, model, expedientPeticioAcceptarCommand);
			return "expedientPeticioAccept";
		}
		
		// find tipus docs disponibles
		List<MetaDocumentDto> tipusDocsDisponibles = new ArrayList<>();
		if (expedientPeticioAcceptarCommand.getAccio() == ExpedientPeticioAccioEnumDto.CREAR) {
			tipusDocsDisponibles = metaDocumentService.findActiusPerCreacio(entitatActual.getId(), null, expedientPeticioAcceptarCommand.getMetaExpedientId(), false);
		} else {
			tipusDocsDisponibles = metaDocumentService.findActiusPerCreacio(entitatActual.getId(), expedientPeticioAcceptarCommand.getExpedientId(), null, false);
		}
		model.addAttribute("metaDocuments", tipusDocsDisponibles);
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_TIPUS_DOCS_DISPONIBLES, tipusDocsDisponibles);
		
		
		RegistreDto registre = expedientPeticioService.findOne(expedientPeticioId).getRegistre();
		// set annexos
		expedientPeticioAcceptarCommand.setAnnexos(ConversioTipusHelper.convertirList(registre.getAnnexos(), RegistreAnnexCommand.class));
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_COMMAND, expedientPeticioAcceptarCommand);
		
		// set justificant
		if (isIncorporacioJustificantActiva() && registre.getJustificant() != null) {
			RegistreAnnexCommand justificant = ConversioTipusHelper.convertir(registre.getJustificant(), RegistreAnnexCommand.class);
			justificant.setId(-1L); // to differenciate justificant from annexes
			expedientPeticioAcceptarCommand.getAnnexos().add(justificant);
		}

		// set first annex
		RegistreAnnexCommand registreAnnexCommand = null;
		if (Utils.isNotEmpty(expedientPeticioAcceptarCommand.getAnnexos())) {
			registreAnnexCommand =  Utils.getFirst(expedientPeticioAcceptarCommand.getAnnexos());
			tipusPerDefecte(
					request,
					expedientPeticioAcceptarCommand.getMetaExpedientId(),
					tipusDocsDisponibles,
					registreAnnexCommand);
		}
		model.addAttribute("registreAnnexCommand", registreAnnexCommand);
		
		
		Integer index = 0;
		setIndexAndSize(
				request,
				model,
				index,
				expedientPeticioAcceptarCommand.getAnnexos().size());
		
		model.addAttribute("isCrearNewExpedient", expedientPeticioAcceptarCommand.getAccio() == ExpedientPeticioAccioEnumDto.CREAR);
		model.addAttribute("isAssociarInteressats", expedientPeticioAcceptarCommand.isAssociarInteressats());


		return "expedientPeticioAcceptMetaDocs";

		
	}
	
	@SuppressWarnings("unchecked")
	private ExpedientPeticioAcceptarCommand processAnnex(
			HttpServletRequest request,
			RegistreAnnexCommand registreAnnexCommand,
			Model model,
			BindingResult bindingResult,
			boolean isThereNext) {
		
		
		List<MetaDocumentDto> tipusDocsDisponibles = (List<MetaDocumentDto>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_TIPUS_DOCS_DISPONIBLES);	
		ExpedientPeticioAcceptarCommand expedientPeticioAcceptarCommand = (ExpedientPeticioAcceptarCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_COMMAND);	
		
		
		boolean noAnnexos = Utils.isEmpty(expedientPeticioAcceptarCommand.getAnnexos());
		if (noAnnexos) {
			return expedientPeticioAcceptarCommand;
		}
		
		if (registreAnnexCommand.getMetaDocumentId() == null) {
			bindingResult.rejectValue("metaDocumentId", "NotNull");
		}	

		Integer index = (Integer)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_INDEX);	
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("metaDocuments", tipusDocsDisponibles);
			setIndexAndSize(
					request,
					model,
					index,
					expedientPeticioAcceptarCommand.getAnnexos().size());
			registreAnnexCommand.setNtiFechaCaptura(expedientPeticioService.findAnnexById(registreAnnexCommand.getId()).getNtiFechaCaptura()); //in POST it is passed as date without time, we want timpestamp 
			
		} else {
			
			expedientPeticioAcceptarCommand.getAnnexos().get(index).setMetaDocumentId(registreAnnexCommand.getMetaDocumentId());
			
			if (isThereNext) {
				
				index++;
				setIndexAndSize(
						request,
						model,
						index,
						expedientPeticioAcceptarCommand.getAnnexos().size());


				
				MetaDocumentDto metaDocument = metaDocumentService.findById(registreAnnexCommand.getMetaDocumentId());
				if (!metaDocument.isPermetMultiple()) {
					tipusDocsDisponibles.remove(metaDocument);
				}
				model.addAttribute("metaDocuments", tipusDocsDisponibles);
				
				RegistreAnnexCommand nextAnnexCommand = expedientPeticioAcceptarCommand.getAnnexos().get(index);
				tipusPerDefecte(
						request,
						expedientPeticioAcceptarCommand.getMetaExpedientId(),
						tipusDocsDisponibles,
						nextAnnexCommand);
				model.addAttribute("registreAnnexCommand", nextAnnexCommand);
				
				
			}
			
		}
		
		model.addAttribute("isCrearNewExpedient", expedientPeticioAcceptarCommand.getAccio() == ExpedientPeticioAccioEnumDto.CREAR);
		model.addAttribute("isAssociarInteressats", expedientPeticioAcceptarCommand.isAssociarInteressats());

		return expedientPeticioAcceptarCommand;
		
	}
	
	private void setIndexAndSize(
			HttpServletRequest request,
			Model model,
			Integer index,
			int size) {
		model.addAttribute("index", index);
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_INDEX, index);
		model.addAttribute("size", size);
		boolean isLast = (index + 1 == size) || size == 0;
		model.addAttribute("isLast", isLast);
		
		boolean isFirst = (index == 0);
		model.addAttribute("isFirst", isFirst);
	}
	
	
	private void tipusPerDefecte(
			HttpServletRequest request,
			Long metaExpedientId,
			List<MetaDocumentDto> tipusDocsDisponibles,
			RegistreAnnexCommand registreAnnexCommand) {
		
		MetaDocumentDto tipusDocPerDefecte = metaDocumentService.findByMetaExpedientAndPerDefecteTrue(metaExpedientId);
		if (tipusDocPerDefecte != null) {
			boolean isTipusDocPerDefecteDisponible = tipusDocsDisponibles.contains(tipusDocPerDefecte);
			if (isTipusDocPerDefecteDisponible) {
				registreAnnexCommand.setMetaDocumentId(tipusDocPerDefecte.getId());
			} else {
				MissatgesHelper.warning(request, getMessage(request, "expedient.peticio.controller.acceptar.warning.no.pot.crear.metadoc.per.defecte", new Object[] { tipusDocPerDefecte.getNom() }));
			}
		}
		
	}
	
	@RequestMapping(value = "/acceptar/{expedientPeticioId}/getNextAnnex", method = RequestMethod.POST)
	public String acceptarPostNextAnnex(
			HttpServletRequest request,
			@Valid RegistreAnnexCommand registreAnnexCommand,
			@PathVariable Long expedientPeticioId,
			BindingResult bindingResult,
			Model model) {
		

		processAnnex(
				request,
				registreAnnexCommand,
				model,
				bindingResult,
				true);
		
		
		return "expedientPeticioAcceptMetaDocs";
		
	}

	@RequestMapping(value = "/acceptar/{expedientPeticioId}/getInteressats", method = RequestMethod.POST)
	public String acceptarPostInteressats(
			HttpServletRequest request,
			@Valid RegistreAnnexCommand registreAnnexCommand,
			@PathVariable Long expedientPeticioId,
			BindingResult bindingResult,
			Model model) throws JsonProcessingException {

		processAnnex(
				request,
				registreAnnexCommand,
				model,
				bindingResult,
				false);

		ExpedientPeticioAcceptarCommand expedientPeticioAcceptarCommand = (ExpedientPeticioAcceptarCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_COMMAND);
		Integer index = (Integer)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_INDEX);
		index++;
		setIndexAndSize(
				request,
				model,
				index,
				expedientPeticioAcceptarCommand.getAnnexos().size());

		boolean isCrearNew = expedientPeticioAcceptarCommand.getAccio() == ExpedientPeticioAccioEnumDto.CREAR;

		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		RegistreDto registre = expedientPeticioDto.getRegistre();
		EntitatDto entitat = entitatService.findByUnitatArrel(registre.getEntitatCodi());
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);

		List<RegistreInteressatDto> interessatsDistribucio = getSortedInteressatsDistribucio(registre.getInteressats());
		List<InteressatDto> interessatsRipea = new ArrayList<>();
		List<String> documentInteressatsOverwritten = new ArrayList<>();

		ExpedientDto expedient = null;
		if (!isCrearNew) {
			expedient = expedientService.findById(entitat.getId(), expedientPeticioAcceptarCommand.getExpedientId(), rolActual);
			interessatsRipea = getSortedInteressatRipea(expedient);
			documentInteressatsOverwritten = getDocumentInteressatsOverwritten(interessatsRipea, interessatsDistribucio);
		}

		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("interessatsRipea", interessatsRipea);
		model.addAttribute("jsonInteressatsRipea", mapper.writeValueAsString(interessatsRipea));
		model.addAttribute("interessatsDistribucio", interessatsDistribucio);
		model.addAttribute("jsonInteressatsDistribucio", mapper.writeValueAsString(interessatsDistribucio));
		model.addAttribute("documentInteressatsOverwritten", documentInteressatsOverwritten);
		model.addAttribute("jsonDocumentInteressatsOverwritten", mapper.writeValueAsString(documentInteressatsOverwritten));

		carregaInteressats(expedientPeticioAcceptarCommand, registre, expedient);
		model.addAttribute(expedientPeticioAcceptarCommand);

		model.addAttribute("isCrearNewExpedient", isCrearNew);

		return "expedientPeticioAcceptInteressats";
	}

	private List<InteressatDto> getSortedInteressatRipea(ExpedientDto expedient) {
		List<InteressatDto> sortedInteressatsRipea = new ArrayList<>();
		Set<InteressatDto> interessatsRipea = expedient.getInteressats();
		if (interessatsRipea != null) {
			sortedInteressatsRipea.addAll(interessatsRipea);
			Collections.sort(sortedInteressatsRipea, new Comparator<InteressatDto>() {
				@Override
				public int compare(InteressatDto a, InteressatDto b) {
					return compareByDocumentNum(a, b);
				}
			});
		}
		return sortedInteressatsRipea;
	}

	private int compareByDocumentNum(InteressatDto a, InteressatDto b) {
		if (a.getDocumentNum() == null && b.getDocumentNum() == null) return 0;
		if (a.getDocumentNum() == null) return 1;
		if (b.getDocumentNum() == null) return -1;
		return a.getDocumentNum().compareTo(b.getDocumentNum());
	}

	private List<RegistreInteressatDto> getSortedInteressatsDistribucio(List<RegistreInteressatDto> interessatsDistribucio) {
		if (interessatsDistribucio == null) return null;

		Collections.sort(interessatsDistribucio, new Comparator<RegistreInteressatDto>() {
			@Override
			public int compare(RegistreInteressatDto a, RegistreInteressatDto b) {
				return compareByDocumentNum(a, b);
			}
		});
		return interessatsDistribucio;
	}

	private int compareByDocumentNum(RegistreInteressatDto a, RegistreInteressatDto b) {
		if (a.getDocumentNumero() == null && b.getDocumentNumero() == null) return 0;
		if (a.getDocumentNumero() == null) return 1;
		if (b.getDocumentNumero() == null) return -1;
		return a.getDocumentNumero().compareTo(b.getDocumentNumero());
	}

	private static List<String> getDocumentInteressatsOverwritten(List<InteressatDto> interessatsRipea, List<RegistreInteressatDto> interessatsDistribucio) {

		Set<String> interessatsOverwritten = new HashSet<>();
		Set<RegistreInteressatDto> interessatsOrRepresentantsDistribucio = getInteressatOrRepresentantsDistribucio(interessatsDistribucio);
		for (InteressatDto interessatRipea : interessatsRipea) {
			for (RegistreInteressatDto interessatDistribucio : interessatsOrRepresentantsDistribucio) {
				if (interessatRipea.getDocumentNum().equals(interessatDistribucio.getDocumentNumero())) {
					interessatsOverwritten.add(interessatRipea.getDocumentNum());
				}
			}
		}

		List<String> sortedList = new ArrayList<>(interessatsOverwritten);
		Collections.sort(sortedList);
		return sortedList;
	}

	private static Set<RegistreInteressatDto> getInteressatOrRepresentantsDistribucio(List<RegistreInteressatDto> interessatsDistribucio) {
		Set<RegistreInteressatDto> interessatsOrRepresentantsDistribucio = new HashSet<>();
		for (RegistreInteressatDto inter : interessatsDistribucio) {
			interessatsOrRepresentantsDistribucio.add(inter);
			if (inter.getRepresentant() != null) {
				interessatsOrRepresentantsDistribucio.add(inter.getRepresentant());
			}
		}
		return interessatsOrRepresentantsDistribucio;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/previousPage", method = RequestMethod.GET)
	public String previousPage(
			HttpServletRequest request,
			Model model) {

		
		ExpedientPeticioAcceptarCommand expedientPeticioAcceptarCommand = (ExpedientPeticioAcceptarCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_COMMAND);	
		Integer index = (Integer)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_INDEX);
		index--;
		setIndexAndSize(
				request,
				model,
				index,
				expedientPeticioAcceptarCommand.getAnnexos().size());

		RegistreAnnexCommand previousAnnexCommand = expedientPeticioAcceptarCommand.getAnnexos().get(index);

		List<MetaDocumentDto> tipusDocsDisponibles = (List<MetaDocumentDto>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_TIPUS_DOCS_DISPONIBLES);	
		MetaDocumentDto metaDocument = metaDocumentService.findById(previousAnnexCommand.getMetaDocumentId());
		if (!metaDocument.isPermetMultiple()) {
			tipusDocsDisponibles.add(metaDocument);
		}
		
		model.addAttribute("metaDocuments", tipusDocsDisponibles);
		model.addAttribute("registreAnnexCommand", previousAnnexCommand);
		model.addAttribute("expedientPeticioId", expedientPeticioAcceptarCommand.getId());

		model.addAttribute("isCrearNewExpedient", expedientPeticioAcceptarCommand.getAccio() == ExpedientPeticioAccioEnumDto.CREAR);
		model.addAttribute("isAssociarInteressats", expedientPeticioAcceptarCommand.isAssociarInteressats());

		return "expedientPeticioAcceptMetaDocs";
	}


	@RequestMapping(value = "/acceptar/{expedientPeticioId}", method = RequestMethod.POST)
	public String acceptarPost(
			HttpServletRequest request,
			@Valid RegistreAnnexCommand registreAnnexCommand,
			@PathVariable Long expedientPeticioId,
			BindingResult bindingResult,
			Model model) {
		
		
		ExpedientPeticioAcceptarCommand expedientPeticioAcceptarCommand = processAnnex(
				request,
				registreAnnexCommand,
				model,
				bindingResult,
				false);

		if (bindingResult.hasErrors()) {
			return "expedientPeticioAcceptMetaDocs";
		}
		
		return processarAnotacio(request, expedientPeticioId, expedientPeticioAcceptarCommand);
	}

	@RequestMapping(value = "/acceptarAmbInteressats/{expedientPeticioId}", method = RequestMethod.POST)
	public String acceptarInteressatsPost(
			HttpServletRequest request,
			ExpedientPeticioAcceptarCommand expedientPeticioAcceptarCommand,
			@PathVariable Long expedientPeticioId,
			Model model) {

		List<RegistreInteressatsCommand> interessats = expedientPeticioAcceptarCommand.getInteressats();
		expedientPeticioAcceptarCommand = (ExpedientPeticioAcceptarCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_COMMAND);
		expedientPeticioAcceptarCommand.setInteressats(interessats);

		return processarAnotacio(request, expedientPeticioId, expedientPeticioAcceptarCommand);
	}

	private String  processarAnotacio(HttpServletRequest request, Long expedientPeticioId, ExpedientPeticioAcceptarCommand expedientPeticioAcceptarCommand) {
		RegistreAnnexCommand last = Utils.getLast(expedientPeticioAcceptarCommand.getAnnexos());
		Long justificantIdMetaDoc = null;
		if (last != null && last.getId() == -1) { // if is justificant
			justificantIdMetaDoc = last.getMetaDocumentId();
			Utils.removeLast(expedientPeticioAcceptarCommand.getAnnexos());
		}

		Map<Long, Long> anexosIdsMetaDocsIdsMap = new HashMap<Long, Long>();
		for (RegistreAnnexCommand registreAnnex : expedientPeticioAcceptarCommand.getAnnexos()) {
			anexosIdsMetaDocsIdsMap.put(registreAnnex.getId(), registreAnnex.getMetaDocumentId());
		}

		Map<String, InteressatAssociacioAccioEnum> interessatsAccionsMap = new HashMap<>();
		for (RegistreInteressatsCommand interessat: expedientPeticioAcceptarCommand.getInteressats()) {
			interessatsAccionsMap.put(interessat.getInteressatDocNumero(), interessat.getAccio());
		}

		boolean processatOk = true;
		boolean expCreatArxiuOk = true;
		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		EntitatDto entitat = entitatService.findByUnitatArrel(expedientPeticioDto.getRegistre().getEntitatCodi());
		Long expedientId = null;
		try {
			if (expedientPeticioAcceptarCommand.getAccio() == ExpedientPeticioAccioEnumDto.CREAR) {
				ExpedientDto expedientDto = expedientService.create(
						entitat.getId(),
						expedientPeticioAcceptarCommand.getMetaExpedientId(),
						null,
						expedientPeticioAcceptarCommand.getOrganGestorId(),
						expedientPeticioAcceptarCommand.getAny(),
						expedientPeticioAcceptarCommand.getNewExpedientTitol(),
						expedientPeticioDto.getId(),
						expedientPeticioAcceptarCommand.isAssociarInteressats(),
						expedientPeticioAcceptarCommand.getGrupId(),
						RolHelper.getRolActual(request),
						anexosIdsMetaDocsIdsMap,
						justificantIdMetaDoc,
						interessatsAccionsMap,
						expedientPeticioAcceptarCommand.getPrioritat());
				processatOk = expedientDto.isProcessatOk();
				expCreatArxiuOk = expedientDto.isExpCreatArxiuOk();
				expedientId = expedientDto.getId();

				logger.info("Expedient creat per anotacio: id=" + expedientDto.getId() + ", numero=" + expedientDto.getMetaExpedient().getCodi() + "/" +  expedientDto.getSequencia() + "/" + expedientDto.getAny());

			} else if (expedientPeticioAcceptarCommand.getAccio() == ExpedientPeticioAccioEnumDto.INCORPORAR) {
					processatOk = expedientService.incorporar(
							entitat.getId(),
							expedientPeticioAcceptarCommand.getExpedientId(),
							expedientPeticioDto.getId(),
							expedientPeticioAcceptarCommand.isAssociarInteressats(),
							RolHelper.getRolActual(request),
							anexosIdsMetaDocsIdsMap,
							justificantIdMetaDoc,
							expedientPeticioAcceptarCommand.isAgafarExpedient(),
							interessatsAccionsMap);

					expedientId = expedientPeticioAcceptarCommand.getExpedientId();
				logger.info("Expedient incorporat per anotacio: " + processatOk);
			}
		} catch (Exception ex) {
			if (expedientPeticioAcceptarCommand.getAccio() == ExpedientPeticioAccioEnumDto.CREAR) {
				logger.error("Error al crear expedient per anotacio", ex);
			} else {
				logger.error("Error al incorporar anotacio al expedient", ex);
			}

			if (ex.getCause() instanceof DocumentAlreadyImportedException) {
				addWarningDocumentExists(request);
				return getModalControllerReturnValueError(request, "redirect:expedientPeticio", "expedient.peticio.controller.acceptat.ko", ex);
			}
			return getModalControllerReturnValueErrorMessageText(request, "redirect:expedientPeticio",
					getMessage(request, "expedient.peticio.controller.acceptat.ko") + ": " + ExceptionHelper.getRootCauseOrItself(ex).getMessage(), ex);

		}

		String expNom = expedientService.getNom(expedientId);

		if (!expCreatArxiuOk) {
			return getModalControllerReturnValueWarning(
					request,
					"redirect:expedientPeticio",
					"expedient.peticio.controller.acceptat.warning.arxiu",
					new Object[]{
							expedientId.toString(),
							expNom,
							expedientPeticioDto.getRegistre().getIdentificador()});
		}
		if (!processatOk) {
			MissatgesHelper.warning(
					request,
					getMessage(request, "expedient.peticio.controller.acceptat.warning"));
		}

		if (expedientPeticioAcceptarCommand.getAccio() == ExpedientPeticioAccioEnumDto.CREAR) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientPeticio",
					"expedient.peticio.controller.acceptat.ok",
					new Object[]{
							expedientId.toString(),
							expNom,
							expedientPeticioDto.getRegistre().getIdentificador()});
		} else {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientPeticio",
					"expedient.peticio.controller.acceptat.incorporat.ok",
					new Object[]{
							expedientPeticioDto.getRegistre().getIdentificador(),
							expedientId.toString(),
							expNom});
		}
	}


	@RequestMapping(value = "/retornarPendent/{expedientPeticioId}", method = RequestMethod.GET)
	public String retornarPendent(HttpServletRequest request, @PathVariable Long expedientPeticioId) {

		try {
			expedientPeticioService.retornarPendent(expedientPeticioId);
			return getModalControllerReturnValueSuccess(request, "redirect:/expedientPeticio", "expedient.peticio.controller.retornar.pendent.ok");
		} catch (Exception ex) {
			logger.error("Error al retornar a pendent", ex);
			return getModalControllerReturnValueErrorMessageText(request, "redirect:/expedientPeticio", ex.getMessage(), ex);
		}
	}

	
	
	@RequestMapping(value = "/canviarProcediment/{expedientPeticioId}", method = RequestMethod.GET)
	public String canviarProcedimentGet(HttpServletRequest request, @PathVariable Long expedientPeticioId, Model model) {
		
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<MetaExpedientDto> metaExpedients =  metaExpedientService.findCreateWritePerm(entitatActual.getId(), rolActual);
		model.addAttribute("metaExpedients", metaExpedients);
		
		ExpedientPeticioModificarCommand command = new ExpedientPeticioModificarCommand();
		command.setId(expedientPeticioId);
		command.setExtracte(expedientPeticioDto.getRegistre().getExtracte());
		command.setNumero(expedientPeticioDto.getRegistre().getIdentificador());
		command.setMetaExpedientId(expedientPeticioDto.getMetaExpedientId());
		command.setGrupId(expedientPeticioDto.getGrupId());
		model.addAttribute("expedientPeticioModificarCommand", command);
		model.addAttribute("rolActual", RolHelper.getRolActual(request));
		
		return "expedientPeticioModificarForm";

	}
	
	
	@RequestMapping(value = "/findGrups", method = RequestMethod.GET)
	@ResponseBody
	public List<GrupDto> findGrups(
			HttpServletRequest request,
			@RequestParam(value = "procedimentId", required = false) Long procedimentId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		
		List<GrupDto> grups = grupService.findGrups(
				entitatActual.getId(),
				RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null, 
				procedimentId);

		return grups;
	}
	
	@RequestMapping(value = "/findGrupById/{grupId}", method = RequestMethod.GET)
	@ResponseBody
	public GrupDto findGrupById(
			HttpServletRequest request,
			@PathVariable(value = "grupId") Long grupId,
			Model model) {
		
		GrupDto grup = grupService.findGrupById(grupId);

		return grup;
	}
	
	@RequestMapping(value = "/findGrupByProcedimentId/{expedientPeticioId}/{procedimentId}", method = RequestMethod.GET)
	@ResponseBody
	public GrupDto findGrupByProcedimentId(
			HttpServletRequest request,
			@PathVariable(value = "expedientPeticioId") Long expedientPeticioId,
			@PathVariable(value = "procedimentId") Long procedimentId,
			Model model) {
		
		GrupDto grup = grupService.findGrupByExpedientPeticioAndProcedimentId(expedientPeticioId, procedimentId);

		return grup;
	}



	@RequestMapping(value = "/canviarProcediment", method = RequestMethod.POST)
	public String canviarProcedimentPost(
			HttpServletRequest request,
			@Valid ExpedientPeticioModificarCommand command,
			BindingResult bindingResult,
			Model model) throws JsonMappingException {
		
		getEntitatActualComprovantPermisos(request);
		
		try {
			expedientPeticioService.canviarProcediment(
					command.getId(),
					command.getMetaExpedientId(), 
					command.getGrupId());

			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientPeticio",
					"metaexpedient.controller.modificat.ok",
					new Object[] { command.getNumero() });

		} catch (Exception e) {
			logger.error("Error al canvair procediemnt de expedient peticio", e);
			Throwable throwable = ExceptionHelper.getRootCauseOrItself(e);
			return getModalControllerReturnValueError(
					request,
					"redirect:expedientPeticio",
					"metaexpedient.controller.modificar.error",
					new String[] {command.getNumero(), throwable.getMessage()},
					throwable);
		}
		
	}



	@RequestMapping(value = "/canviarEstatDistribucio/{id}", method = RequestMethod.GET)
	public String canviarEstatDistribucio(HttpServletRequest request, @PathVariable Long id, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (id == null) {
			return getModalControllerReturnValueError(
					request,
					"redirect:../",
					"expedient.peticio.controller.canviar.estat.anotacio.distribucio.id.inexistent",
					null);
		}
		Throwable exception = expedientPeticioService.canviarEstatAnotacioDistribucio(
				entitatActual.getId(),
				id);
		if (exception == null) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../",
					"expedient.peticio.controller.canviar.estat.anotacio.distribucio.ok");
		} else {
			return getModalControllerReturnValueError(
					request,
					"redirect:../",
					"expedient.peticio.controller.canviar.estat.anotacio.distribucio.error",
					new Object[] { ExceptionHelper.getRootCauseOrItself(exception).getMessage() },
					exception);
		}
	}
	
	
	
	@RequestMapping(value = "/expedients/{entitatId}/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ExpedientDto> findByEntitatAndMetaExpedient(HttpServletRequest request, @PathVariable Long entitatId, @PathVariable Long metaExpedientId, Model model) {

		return expedientService.findByEntitatAndMetaExpedient(entitatId, metaExpedientId, RolHelper.getRolActual(request), EntitatHelper.getOrganGestorActualId(request));
	}
	
	@RequestMapping(value = "/comprovarInteressatsPeticio/{expedientId}/{expedientPeticioId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean comprovarInteressatsPeticio(HttpServletRequest request, @PathVariable Long expedientId, @PathVariable Long expedientPeticioId, Model model) {

		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		EntitatDto entitat = entitatService.findByUnitatArrel(expedientPeticioDto.getRegistre().getEntitatCodi());
		return expedientPeticioService.comprovarExistenciaInteressatsPeticio(entitat.getId(), expedientId, expedientPeticioId);
	}
	
	@RequestMapping(value = "/comprovarPermisCreate/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean comprovarPermisCreate(HttpServletRequest request, @PathVariable Long metaExpedientId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualAdministradorOrgan(request) ? true
				:  metaExpedientService.comprovarPermisosMetaExpedient(entitatActual.getId(), metaExpedientId, PermissionEnumDto.CREATE);
	}
	
	@RequestMapping(value = "/comprovarPermisWrite/{metaExpedientId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean comprovarPermisWrite(HttpServletRequest request, @PathVariable Long metaExpedientId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualAdministradorOrgan(request) ? true
				: metaExpedientService.comprovarPermisosMetaExpedient(entitatActual.getId(), metaExpedientId, PermissionEnumDto.WRITE);
	}

	private void addWarningDocumentExists(HttpServletRequest request) {

		List<DocumentDto> documentsAlreadyImported = expedientService.consultaExpedientsAmbImportacio();
		if (documentsAlreadyImported != null && !documentsAlreadyImported.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<ul>");
			for (DocumentDto documentAlreadyImported: documentsAlreadyImported) {
				List<ContingutDto> path = documentAlreadyImported.getPath();
				if (path != null) {
					sb.append("<li>");
					int idx = 0;
					for (ContingutDto pathElement: path) {
						sb.append("<b>/</b>" + pathElement.getNom());
						if (idx == path.size() - 1)
							sb.append("<b>/</b>" + documentAlreadyImported.getNom());
						idx++;
					}
					sb.append("</li>");
				}
			}
			sb.append("</ul>");
			MissatgesHelper.warning(request, getMessage(request, "expedient.peticio.controller.acceptat.duplicat.warning", new Object[] {sb.toString()}));
		}
	}
	
	@RequestMapping(value = "/descarregarAnnex/{annexId}", method = RequestMethod.GET)
	public String descarregarAnnex(HttpServletRequest request, HttpServletResponse response, @PathVariable Long annexId,
								   @RequestParam boolean versioImprimible) throws IOException {
		try{
			FitxerDto fitxer = expedientPeticioService.getAnnexContent(annexId, versioImprimible);
			writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
			return null;
		} catch (Exception ex) {
			logger.error("Error descarregant el document", ex);
			return getModalControllerReturnValueError(request, "/expedientPeticio", "contingut.controller.document.descarregar.error", ex);
		}
	}
	
	@RequestMapping(value = "/annex/{annexId}/content", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse descarregarBase64(HttpServletRequest request, HttpServletResponse response, @PathVariable Long annexId) throws Exception {

		try {
			FitxerDto fitxer = expedientPeticioService.getAnnexContent(annexId, true);
			return new JsonResponse(fitxer);
				
		} catch (Exception e) {
			logger.error("Errol al descarregarBase64", e);
			return new JsonResponse(true, e.getMessage());
		}
		
		
	}
	
	@RequestMapping(value = "/firmaInfo/{annexId}/content", method = RequestMethod.GET)
	@ResponseBody
	public List<ArxiuFirmaDto> firmaInfoContent(HttpServletRequest request, HttpServletResponse response, @PathVariable Long annexId, Model model) {
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromAnnexId(annexId));
		try {
			RegistreAnnexDto registreAnnexDto = expedientPeticioService.findAnnexById(annexId);
			return expedientPeticioService.annexFirmaInfo(registreAnnexDto.getUuid());
		} catch (Exception ex) {
			logger.error("Error recuperant informació de firma", ex);
		}
		return null;
	}
	
	@RequestMapping(value = "/descarregarJustificant/{registreId}", method = RequestMethod.GET)
	public String descarregarJustificant(HttpServletRequest request, HttpServletResponse response, @PathVariable Long registreId) throws IOException {
		
		try{
			RegistreDto registreDto = expedientPeticioService.findRegistreById(registreId);
			organGestorService.actualitzarOrganCodi(registreDto.getDestiCodi());
			FitxerDto fitxer = expedientPeticioService.getJustificantContent(registreDto.getJustificantArxiuUuid());
			writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
			return null;
		} catch (Exception ex) {
			logger.error("Error descarregant el document", ex);
			return getModalControllerReturnValueError(request, "/expedientPeticio", "contingut.controller.document.descarregar.error", ex);
		}
	}

	@RequestMapping(value = "/firmaInfo/{annexId}", method = RequestMethod.GET)
	public String firmaInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable Long annexId, Model model) {

		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromAnnexId(annexId));
		try {
			RegistreAnnexDto registreAnnexDto = expedientPeticioService.findAnnexById(annexId);
			model.addAttribute("annexId", registreAnnexDto.getId());
			model.addAttribute("firmes", expedientPeticioService.annexFirmaInfo(registreAnnexDto.getUuid()));
			return "registreAnnexFirmes";
		} catch (Exception ex) {
			logger.error("Error recuperant informació de firma", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}
	}
	
	@RequestMapping(value = "/justificantFirmaInfo/{registreId}", method = RequestMethod.GET)
	public String justificantFirmaInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable Long registreId, Model model) {

		try {
			RegistreDto registreDto = expedientPeticioService.findRegistreById(registreId);
			organGestorService.actualitzarOrganCodi(registreDto.getDestiCodi());
			model.addAttribute("firmes", expedientPeticioService.annexFirmaInfo(registreDto.getJustificantArxiuUuid()));
			return "registreAnnexFirmes";
		} catch (Exception ex) {
			logger.error("Error recuperant informació de firma", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}
	}

	@RequestMapping(value = "/descarregarFirma/{annexId}", method = RequestMethod.GET)
	public String descarregarFirma(HttpServletRequest request, HttpServletResponse response, @PathVariable Long annexId ) throws IOException {

		FitxerDto fitxer = expedientPeticioService.getAnnexFirmaContingut(annexId);
		writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
		return null;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

	/**
	 * Gets filtreCommand from session, if it doesnt exist it creates new one in session
	 * @param request
	 * @return 
	 */
	private ExpedientPeticioFiltreCommand getFiltreCommand(HttpServletRequest request) {

		ExpedientPeticioFiltreCommand filtreCommand = (ExpedientPeticioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ExpedientPeticioFiltreCommand();
			filtreCommand.setEstat(ExpedientPeticioEstatViewEnumDto.PENDENT);
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	private void omplirModel(Long expedientPeticioId, HttpServletRequest request, Model model, ExpedientPeticioAcceptarCommand command) {

		ExpedientPeticioDto expedientPeticioDto = expedientPeticioService.findOne(expedientPeticioId);
		RegistreDto registre = expedientPeticioService.findOne(expedientPeticioId).getRegistre();
		ExpedientDto expedient = null;
		EntitatDto entitat = entitatService.findByUnitatArrel(registre.getEntitatCodi());
		model.addAttribute("entitatId", entitat.getId());
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<MetaExpedientDto> metaExpedients =  metaExpedientService.findCreateWritePerm(entitat.getId(), rolActual);
		model.addAttribute("metaExpedients", metaExpedients);
		List<ExpedientDto> expedients = null;
		// if exists metaExpedient with matching codi procediment
		if (expedientPeticioDto.getMetaExpedientId() != null) {
			boolean hasPermissions = false;
			for(MetaExpedientDto metaExpDto : metaExpedients) {
				if (metaExpDto.getId().equals(expedientPeticioDto.getMetaExpedientId())) {
					hasPermissions = true;
				}
			}
			// if current user has create permissions for this metaexpedient
			if (hasPermissions) {
				if (command.getMetaExpedientId() == null) {
					command.setMetaExpedientId(expedientPeticioDto.getMetaExpedientId());
				}
				expedients = (List<ExpedientDto>) expedientService.findByEntitatAndMetaExpedient(entitat.getId(), expedientPeticioDto.getMetaExpedientId(), rolActual, EntitatHelper.getOrganGestorActualId(request));
				String expedientNumero = registre.getExpedientNumero();
				if (expedientNumero != null && !expedientNumero.isEmpty()) {
					expedient = expedientPeticioService.findByEntitatAndMetaExpedientAndExpedientNumero(entitat.getId(), expedientPeticioDto.getMetaExpedientId(), expedientNumero);
					if (expedient == null) {
						MissatgesHelper.warning(request, getMessage(request, "expedient.peticio.form.acceptar.expedient.noTorbat"));
					}
				}
			}
		}
		if (command.getAccio() == null) {
			command.setAccio(expedient != null ? ExpedientPeticioAccioEnumDto.INCORPORAR : ExpedientPeticioAccioEnumDto.CREAR);
		}

		command.setExpedientId(expedient != null ? expedient.getId() : null);
		model.addAttribute("expedients", expedients);
		model.addAttribute("accios", EnumHelper.getOptionsForEnum(ExpedientPeticioAccioEnumDto.class, "expedient.peticio.accio.enum."));
		command.setId(expedientPeticioDto.getId());
		command.setAssociarInteressats(true);
//		command.setNewExpedientTitol(expedientPeticioDto.getIdentificador());
		command.setAny(Calendar.getInstance().get(Calendar.YEAR));
		command.setGrupId(expedientPeticioDto.getGrupId());
		model.addAttribute("expedientPeticioAcceptarCommand", command);
		
		model.addAttribute("rolActual", rolActual);
	}

	private void carregaInteressats(ExpedientPeticioAcceptarCommand command, RegistreDto registre, ExpedientDto expedient) {
		if (command.getInteressats() == null || command.getInteressats().isEmpty()) {
			List<RegistreInteressatDto> interessatsDistribucio = getSortedInteressatsDistribucio(registre.getInteressats());
			List<RegistreInteressatsCommand> associacionsInteressats = new ArrayList<>();
			for (RegistreInteressatDto interessatDistribucio : interessatsDistribucio) {
				associacionsInteressats.add(RegistreInteressatsCommand.builder()
						.interessatDocNumero(interessatDistribucio.getDocumentNumero())
//						.accio(calculaAccioInteressat(interessatDistribucio, expedient != null ? expedient.getInteressats() : null))
						.accio(InteressatAssociacioAccioEnum.ASSOCIAR)
						.build());
			}
			command.setInteressats(associacionsInteressats);
		}
	}

//	private InteressatAssociacioAccioEnum calculaAccioInteressat(RegistreInteressatDto interessat, Set<InteressatDto> interessatsRipea) {
//		// Si s'està creant l'expedient, o l'expedient no té cap interessat associam els nous interessats
//		if (interessatsRipea == null || interessatsRipea.isEmpty()) return InteressatAssociacioAccioEnum.ASSOCIAR;
//		RegistreInteressatDto representant = interessat.getRepresentant();
//
//		String interessatDoc = interessat.getDocumentNumero();
//		String representantDoc = representant != null ? representant.getDocumentNumero() : null;
//		boolean interessatExists = false;
//		boolean representantExists = false;
//		InteressatDto interessatRipea = null;
//
//		// Obtenim l'interessat i representant ja existent a Ripea (si existeix)
//		for (InteressatDto interessatDto: interessatsRipea) {
//			if (interessatDoc.equalsIgnoreCase(interessatDto.getDocumentNum())) {
//				interessatExists = true;
//				interessatRipea = interessatDto;
//			}
//			if (representantDoc != null) {
//				if (representantDoc.equalsIgnoreCase(interessatDto.getDocumentNum())) {
//                    representantExists = true;
//                } else if (interessatDto.getRepresentant() != null && representantDoc.equalsIgnoreCase(interessatDto.getRepresentant().getDocumentNum())) {
//					representantExists = true;
//				}
//			}
//			if (representantExists && interessatExists)
//				break;
//
//		}
//
//		if (interessatExists) {
//			if (sameRepresentant(interessatRipea.getRepresentant(), representantDoc)){
//				return InteressatAssociacioAccioEnum.SOBREESCRIURE;
//			} else {
//				return InteressatAssociacioAccioEnum.SOBREESCRIURE_REPRESENTANT;
//			}
//		} else if (representantExists) {
//			return InteressatAssociacioAccioEnum.ASSOCIAR_SOBREESCRIURE_REPRESENTANT;
//		} else {
//			return InteressatAssociacioAccioEnum.ASSOCIAR;
//		}
//	}

	private boolean sameRepresentant(InteressatDto representant, String representantDoc) {
		if (representant == null)
			return representantDoc == null;
		return representant.getDocumentNum().equals(representantDoc);
	}

	private boolean isIncorporacioJustificantActiva() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.ripea.incorporar.justificant"));
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientPeticioController.class);

}
