package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.Date;
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
import es.caib.ripea.core.api.dto.ElementTipusEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaContingutDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaEstatDto;
import es.caib.ripea.core.api.dto.ExecucioMassivaTipusDto;
import es.caib.ripea.core.api.dto.MetaExpedientDto;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.ResultEnumDto;
import es.caib.ripea.core.api.dto.SeguimentArxiuPendentsDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.SeguimentService;
import es.caib.ripea.war.command.SeguimentArxiuPendentsFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;
import es.caib.ripea.war.helper.RolHelper;

/**
 * Controlador per al manteniment de seguiment de elements pendents de guardar a dins l'arxiu
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/seguimentArxiuPendents")
public class SeguimentArxiuPendentsController extends BaseUserOAdminOOrganController {
	
	public static final String SESSION_ATTRIBUTE_FILTRE_EXPEDIENTS = "SeguimentPortafirmesController.session.filtre.expedients";
	public static final String SESSION_ATTRIBUTE_FILTRE_DOCUMENTS = "SeguimentPortafirmesController.session.filtre.documents";
	public static final String SESSION_ATTRIBUTE_FILTRE_INTERESSATS = "SeguimentPortafirmesController.session.filtre.interessats";
	private static final String SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS = "SeguimentPortafirmesController.session.seleccio.expedients";
	private static final String SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS = "SeguimentPortafirmesController.session.seleccio.documents";
	private static final String SESSION_ATTRIBUTE_SELECCIO_INTERESSATS = "SeguimentPortafirmesController.session.seleccio.interessats";

    @Autowired private SeguimentService seguimentService;
	@Autowired private MetaExpedientService metaExpedientService;
	@Autowired private ExecucioMassivaService execucioMassivaService;
	@Autowired private AplicacioService aplicacioService;

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
				ArxiuPendentTipusEnumDto.EXPEDIENT,
				EntitatHelper.getOrganGestorActualId(request)).getPagina();
		
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
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
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
				ArxiuPendentTipusEnumDto.DOCUMENT,
				EntitatHelper.getOrganGestorActualId(request)).getPagina();
		
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
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
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
				ArxiuPendentTipusEnumDto.INTERESSAT,
				EntitatHelper.getOrganGestorActualId(request)).getPagina();
		
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
			filtreCommand.setMetaExpedientId(aplicacioService.getProcedimentPerDefecte(EntitatHelper.getEntitatActual(request).getId(), RolHelper.getRolActual(request)));
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_INTERESSATS,
					filtreCommand);
		}
		return filtreCommand;
	}

	@RequestMapping(value = "/expedients/select", method = RequestMethod.GET)
	@ResponseBody
	public int selectExpedients(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {

		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
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
							ArxiuPendentTipusEnumDto.EXPEDIENT,
							EntitatHelper.getOrganGestorActualId(request)).getIds());
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
		
		String rolActual = (String)request.getSession().getAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
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
							ArxiuPendentTipusEnumDto.DOCUMENT,
							EntitatHelper.getOrganGestorActualId(request)).getIds());
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
							ArxiuPendentTipusEnumDto.INTERESSAT,
							EntitatHelper.getOrganGestorActualId(request)).getIds());
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
	public String expedientsReintentar(HttpServletRequest request) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS));

		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/seguimentArxiuPendents",
					"accio.massiva.seleccio.buida",
					null);
		}

		Date dataInici = new Date();
		List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<>();

		for (Long expedientId : seleccio) {	
			execucioMassivaElements.add(new ExecucioMassivaContingutDto(dataInici, null, expedientId, ExecucioMassivaEstatDto.ESTAT_PENDENT));
		}
		
		execucioMassivaService.saveExecucioMassiva(
				entitatActual.getId(),
				new ExecucioMassivaDto(
						ExecucioMassivaTipusDto.CUSTODIAR_ELEMENTS_PENDENTS,
						dataInici,
						null,
						RolHelper.getRolActual(request)),
				execucioMassivaElements, 
				ElementTipusEnumDto.EXPEDIENT);

		MissatgesHelper.success(request, getMessage(request, "seguiment.controller.expedients.massiu.correctes", new Object[]{seleccio.size()}));
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO_EXPEDIENTS, seleccio);
		return "redirect:/seguimentArxiuPendents";
	}

	@RequestMapping(value = "/documents/reintentar", method = RequestMethod.GET)
	public String documentsReintentar(HttpServletRequest request) {

		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS));

		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/seguimentArxiuPendents/#documents",
					"accio.massiva.seleccio.buida",
					null);
		}
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Date dataInici = new Date();
		List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<>();
		
		for (Long documentId : seleccio) {
			execucioMassivaElements.add(new ExecucioMassivaContingutDto(dataInici, null, documentId, ExecucioMassivaEstatDto.ESTAT_PENDENT));
		}
		
		execucioMassivaService.saveExecucioMassiva(
				entitatActual.getId(),
				new ExecucioMassivaDto(
						ExecucioMassivaTipusDto.CUSTODIAR_ELEMENTS_PENDENTS,
						dataInici,
						null,
						RolHelper.getRolActual(request)),
				execucioMassivaElements, 
				ElementTipusEnumDto.DOCUMENT);
		
		MissatgesHelper.success(request, getMessage(request, "seguiment.controller.documents.massiu.correctes", new Object[]{seleccio.size()}));
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO_DOCUMENTS, seleccio);
		return "redirect:/seguimentArxiuPendents/#documents";
	}

	@RequestMapping(value = "/interessats/reintentar", method = RequestMethod.GET)
	public String interessatsReintentar(HttpServletRequest request) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = ((Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO_INTERESSATS));

		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/seguimentArxiuPendents/#interessats",
					"accio.massiva.seleccio.buida",
					null);
		}

		Date dataInici = new Date();
		List<ExecucioMassivaContingutDto> execucioMassivaElements = new ArrayList<>();

		for (Long interessatId : seleccio) {
			execucioMassivaElements.add(new ExecucioMassivaContingutDto(dataInici, null, interessatId, ExecucioMassivaEstatDto.ESTAT_PENDENT));
		}
		
		execucioMassivaService.saveExecucioMassiva(
				entitatActual.getId(),
				new ExecucioMassivaDto(
						ExecucioMassivaTipusDto.CUSTODIAR_ELEMENTS_PENDENTS,
						dataInici,
						null,
						RolHelper.getRolActual(request)),
				execucioMassivaElements, 
				ElementTipusEnumDto.INTERESSAT);

		MissatgesHelper.success(request, getMessage(request, "seguiment.controller.interessats.massiu.correctes", new Object[]{seleccio.size()}));
		seleccio.clear();
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO_INTERESSATS, seleccio);
		return "redirect:/seguimentArxiuPendents/#interessats";
	}

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SeguimentArxiuPendentsController.class);
}