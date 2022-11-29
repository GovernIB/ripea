/**
 * 
 */
package es.caib.ripea.war.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ArxiuPendentTipusEnumDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.core.api.exception.ArxiuJaGuardatException;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.SeguimentService;
import es.caib.ripea.war.command.SeguimentArxiuPendentsFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de seguiment de elements pendents de guardar a dins l'arxiu
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/seguimentArxiuPendents")
public class SeguimentArxiuPendentsController extends BaseUserOAdminOOrganController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS = "SeguimentPortafirmesController.session.filtre.expedients";
	private static final String SESSION_ATTRIBUTE_FILTRE_DOCUMENTS = "SeguimentPortafirmesController.session.filtre.documents";
	private static final String SESSION_ATTRIBUTE_FILTRE_INTERESSATS = "SeguimentPortafirmesController.session.filtre.interessats";
	private static final String SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS = "SeguimentPortafirmesController.session.seleccio.expedients";
	private static final String SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS = "SeguimentPortafirmesController.session.seleccio.documents";
	private static final String SESSION_ATTRIBUTE_SELECCIO_INTERESSATS = "SeguimentPortafirmesController.session.seleccio.interessats";

    @Autowired
    private SeguimentService seguimentService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private ExpedientInteressatService expedientInteressatService;
	@Autowired
	private MetaExpedientService metaExpedientService;



    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
    	
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
    	SeguimentArxiuPendentsFiltreCommand commandExp = getFiltreCommandExpedients(request);
    	SeguimentArxiuPendentsFiltreCommand commandDoc = getFiltreCommandDocuments(request);
    	SeguimentArxiuPendentsFiltreCommand commandInt = getFiltreCommandInteressats(request);
		model.addAttribute("commandExpedients", commandExp);
		model.addAttribute("commandDocuments", commandDoc);
		model.addAttribute("commandInteressats", commandInt);

		model.addAttribute(
				"expSeleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS));
		model.addAttribute(
				"docSeleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS));
		model.addAttribute(
				"intSeleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO_INTERESSATS));
		
		List<MetaExpedientDto> metaExpedients = metaExpedientService.findByEntitat(
				entitatActual.getId());
		model.addAttribute(
				"metaExpedients",
				metaExpedients);
    	
        return "seguimentArxiuPendentsList";
    }
    
	@RequestMapping(value = "/filtrar/expedients", method = RequestMethod.POST)
	public String postExpedients(
			HttpServletRequest request,
			@Valid SeguimentArxiuPendentsFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS,
						filtreCommand);
			}
		}
		return "redirect:../../seguimentArxiuPendents#expedients";
	}

    @RequestMapping(value = "/expedients/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatableExpedients(HttpServletRequest request) {
		PaginaDto<SeguimentArxiuPendentsDto> docsPortafirmes = new PaginaDto<SeguimentArxiuPendentsDto>();

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);

        SeguimentArxiuPendentsFiltreCommand filtreCommand = getFiltreCommandExpedients(request);

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
        docsPortafirmes = seguimentService.findPendentsArxiu(
				entitat.getId(),
				SeguimentArxiuPendentsFiltreCommand.asDto(filtreCommand),
				DatatablesHelper.getPaginacioDtoFromRequest(request),
				rolActual,
				ResultEnumDto.PAGE,
				ArxiuPendentTipusEnumDto.EXPEDIENT).getPagina();
		
        return DatatablesHelper.getDatatableResponse(
				request,
				docsPortafirmes,
				"id",
				SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS);
    }
    
	private SeguimentArxiuPendentsFiltreCommand getFiltreCommandExpedients(
			HttpServletRequest request) {
		SeguimentArxiuPendentsFiltreCommand filtreCommand = (SeguimentArxiuPendentsFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS);
		if (filtreCommand == null) {
			filtreCommand = new SeguimentArxiuPendentsFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS,
					filtreCommand);
		}
		return filtreCommand;
	}

	
	
	@RequestMapping(value = "/filtrar/documents", method = RequestMethod.POST)
	public String postDocuments(
			HttpServletRequest request,
			@Valid SeguimentArxiuPendentsFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_DOCUMENTS);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE_DOCUMENTS,
						filtreCommand);
			}
		}
		return "redirect:../../seguimentArxiuPendents#documents";
	}

    @RequestMapping(value = "/documents/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatableDocuments(HttpServletRequest request) {
		PaginaDto<SeguimentArxiuPendentsDto> docs = new PaginaDto<SeguimentArxiuPendentsDto>();

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);

        SeguimentArxiuPendentsFiltreCommand filtreCommand = getFiltreCommandDocuments(request);

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
        
		docs = seguimentService.findPendentsArxiu(
				entitat.getId(),
				SeguimentArxiuPendentsFiltreCommand.asDto(filtreCommand),
				DatatablesHelper.getPaginacioDtoFromRequest(request),
				rolActual,
				ResultEnumDto.PAGE,
				ArxiuPendentTipusEnumDto.DOCUMENT).getPagina();
		
        return DatatablesHelper.getDatatableResponse(
				request,
				docs,
				"id",
				SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS);
    }
    
	private SeguimentArxiuPendentsFiltreCommand getFiltreCommandDocuments(
			HttpServletRequest request) {
		SeguimentArxiuPendentsFiltreCommand filtreCommand = (SeguimentArxiuPendentsFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE_DOCUMENTS);
		if (filtreCommand == null) {
			filtreCommand = new SeguimentArxiuPendentsFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_DOCUMENTS,
					filtreCommand);
		}
		return filtreCommand;
	}
	
	
	
	
	@RequestMapping(value = "/filtrar/interessats", method = RequestMethod.POST)
	public String postInteressats(
			HttpServletRequest request,
			@Valid SeguimentArxiuPendentsFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_INTERESSATS);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE_INTERESSATS,
						filtreCommand);
			}
		}
		return "redirect:../../seguimentArxiuPendents#interessats";
	}

    @RequestMapping(value = "/interessats/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatableInteressats(HttpServletRequest request) {
		PaginaDto<SeguimentArxiuPendentsDto> docsPortafirmes = new PaginaDto<SeguimentArxiuPendentsDto>();

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);

        SeguimentArxiuPendentsFiltreCommand filtreCommand = getFiltreCommandInteressats(request);

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
        
        docsPortafirmes = seguimentService.findPendentsArxiu(
				entitat.getId(),
				SeguimentArxiuPendentsFiltreCommand.asDto(filtreCommand),
				DatatablesHelper.getPaginacioDtoFromRequest(request), 
				rolActual,
				ResultEnumDto.PAGE,
				ArxiuPendentTipusEnumDto.INTERESSAT).getPagina();
		
        return DatatablesHelper.getDatatableResponse(
				request,
				docsPortafirmes,
				"id",
				SESSION_ATTRIBUTE_SELECCIO_INTERESSATS);
    }
    
	private SeguimentArxiuPendentsFiltreCommand getFiltreCommandInteressats(
			HttpServletRequest request) {
		SeguimentArxiuPendentsFiltreCommand filtreCommand = (SeguimentArxiuPendentsFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE_INTERESSATS);
		if (filtreCommand == null) {
			filtreCommand = new SeguimentArxiuPendentsFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_INTERESSATS,
					filtreCommand);
		}
		return filtreCommand;
	}
    


	// Accions massives
	// /////////////////////////////////////////////////////////////

	@RequestMapping(value = "/expedients/select", method = RequestMethod.GET)
	@ResponseBody
	public int selectExpedients(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {

		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			SeguimentArxiuPendentsFiltreCommand filtreCommand = getFiltreCommandExpedients(request);
			seleccio.addAll(
					seguimentService.findPendentsArxiu(
							entitatActual.getId(),
							SeguimentArxiuPendentsFiltreCommand.asDto(filtreCommand), 
							null,
							rolActual,
							ResultEnumDto.IDS,
							ArxiuPendentTipusEnumDto.EXPEDIENT).getIds());
		}
		return seleccio.size();
	}
	@RequestMapping(value = "/expedients/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselectExpedients(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS,
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

	@RequestMapping(value = "/documents/select", method = RequestMethod.GET)
	@ResponseBody
	public int selectDocuments(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {

		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			SeguimentArxiuPendentsFiltreCommand filtreCommand = getFiltreCommandDocuments(request);
			seleccio.addAll(
					seguimentService.findPendentsArxiu(
							entitatActual.getId(),
							SeguimentArxiuPendentsFiltreCommand.asDto(filtreCommand), 
							null,
							rolActual,
							ResultEnumDto.IDS,
							ArxiuPendentTipusEnumDto.DOCUMENT).getIds());
		}
		return seleccio.size();
	}
	@RequestMapping(value = "/documents/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselectDocuments(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS,
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

	@RequestMapping(value = "/interessats/select", method = RequestMethod.GET)
	@ResponseBody
	public int selectInteressats(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {

		
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_INTERESSATS);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_INTERESSATS,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			SeguimentArxiuPendentsFiltreCommand filtreCommand = getFiltreCommandInteressats(request);
			seleccio.addAll(
					seguimentService.findPendentsArxiu(
							entitatActual.getId(),
							SeguimentArxiuPendentsFiltreCommand.asDto(filtreCommand), 
							null,
							rolActual,
							ResultEnumDto.IDS,
							ArxiuPendentTipusEnumDto.INTERESSAT).getIds());
		}
		return seleccio.size();
	}
	@RequestMapping(value = "/interessats/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselectInteressats(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_INTERESSATS);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_INTERESSATS,
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


	@RequestMapping(value = "/expedients/reintentar", method = RequestMethod.GET)
	public String expedientsReintentar(
			HttpServletRequest request) {

		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS));

		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/seguimentArxiuPendents",
					"accio.massiva.seleccio.buida",
					null);
		}

		int errors = 0;
		int correctes = 0;

		for (Long expedientId : seleccio) {
			Exception exception = expedientService.guardarExpedientArxiu(expedientId);

			if (exception instanceof ArxiuJaGuardatException) {
				exception = null;
			}
			if (exception != null ) {
				logger.error("Error guardant expedient en arxiu", exception);
				errors++;
			} else {
				correctes++;
			}

		}

		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "seguiment.controller.expedients.massiu.correctes", new Object[]{correctes}));
		}
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "seguiment.controller.expedients.massiu.errors", new Object[]{errors}), null);
		}

		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS,
				seleccio);

		return "redirect:/seguimentArxiuPendents";
	}

	@RequestMapping(value = "/documents/reintentar", method = RequestMethod.GET)
	public String documentsReintentar(
			HttpServletRequest request) {

		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS));

		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/seguimentArxiuPendents/#documents",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		int errors = 0;
		int correctes = 0;

		for (Long documentId : seleccio) {
			
			DocumentDto document = documentService.findById(entitatActual.getId(), documentId);
			Exception exception = null;
			if (document.getArxiuUuid() == null) {
				exception = documentService.guardarDocumentArxiu(documentId);
				if (exception instanceof ArxiuJaGuardatException) {
					exception = null;
				}
				if (exception != null ) {
					logger.error("Error guardant document en arxiu", exception);
					errors++;
				} else {
					correctes++;
				}
			} else if (document.isPendentMoverArxiu()) {
				exception = expedientService.retryMoverAnnexArxiu(document.getAnnexId());
				if (exception != null ) {
					logger.error("Error mover annex en arxiu", exception);
					errors++;
				} else {
					correctes++;
				}
			}

		}

		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "seguiment.controller.documents.massiu.correctes", new Object[]{correctes}));
		}
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "seguiment.controller.documents.massiu.errors", new Object[]{errors}), null);
		}

		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS,
				seleccio);

		return "redirect:/seguimentArxiuPendents/#documents";
	}

	@RequestMapping(value = "/interessats/reintentar", method = RequestMethod.GET)
	public String interessatsReintentar(
			HttpServletRequest request) {

		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_INTERESSATS));

		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/seguimentArxiuPendents/#interessats",
					"accio.massiva.seleccio.buida",
					null);
		}

		int errors = 0;
		int correctes = 0;

		for (Long interessatId : seleccio) {
			Exception exception = null;
			try {
				Long expedientId = expedientInteressatService.findExpedientIdByInteressat(interessatId);
				exception = expedientInteressatService.guardarInteressatsArxiu(expedientId);
			} catch (Exception ex) {
				exception = ex;
			}
			if (exception != null ) {
				logger.error("Error guardant interessat en arxiu", exception);
				errors++;
			} else {
				correctes++;
			}

		}

		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "seguiment.controller.interessats.massiu.correctes", new Object[]{correctes}));
		}
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "seguiment.controller.interessats.massiu.errors", new Object[]{errors}), null);
		}

		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_INTERESSATS,
				seleccio);

		return "redirect:/seguimentArxiuPendents/#interessats";
	}

	private static final Logger logger = LoggerFactory.getLogger(SeguimentArxiuPendentsController.class);
}
