/**
 * 
 */
package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import es.caib.ripea.core.api.dto.ArbreDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.DocumentDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientCarpetaArbreDto;
import es.caib.ripea.core.api.dto.TipusImportEnumDto;
import es.caib.ripea.core.api.exception.ContingutNotUniqueException;
import es.caib.ripea.core.api.exception.DocumentAlreadyImportedException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.CarpetaService;
import es.caib.ripea.core.api.service.ImportacioService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.ImportacioCommand;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.ExceptionHelper;
import es.caib.ripea.war.helper.MissatgesHelper;

/**
 * Controlador per al manteniment d'importació de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingut")
public class ContingutImportacioController extends BaseUserController {

	@Autowired
	private ImportacioService importacioService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private CarpetaService carpetaService;
	@Autowired
	private AplicacioService aplicacioService;
	
	private Map<Long, Semaphore> semafors = new HashMap<Long, Semaphore>();

	@RequestMapping(value = "/{contingutId}/importacio/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		return get(request, contingutId, null, model);
	}
	
	public String get(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long carpetaId,
			Model model) {
		ImportacioCommand command = new ImportacioCommand();
		command.setTipusImportacio(TipusImportEnumDto.NUMERO_REGISTRE);
		emplenarModelImportacio(request, contingutId, command, model);
		return "contingutImportacioForm";
	}

	@RequestMapping(value = "/{contingutId}/importacio/new", method = RequestMethod.POST)
	public String postNew(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Valid ImportacioCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		return postUpdate(
				request,
				contingutId,
				command,
				bindingResult,
				model);
	}
	
	public String postUpdate(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@Valid ImportacioCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(contingutId));
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelImportacio(request, contingutId, command, model);
			return "contingutImportacioForm";
		}
		int documentsRepetits = 0;
		try {
			this.entrarSemafor(contingutId);
			
			documentsRepetits = importacioService.importarDocuments(
						entitatActual.getId(), 
						contingutId,
						ImportacioCommand.asDto(command));
			
		} catch (Exception ex) {
			emplenarModelImportacio(request, contingutId, command, model);
			// Excepció si d'alguna forma s'intenta importar el document dues vegades al mateix moment
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, DocumentAlreadyImportedException.class)) {
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../../../contingut/" + contingutId,
						null);
			}
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, ContingutNotUniqueException.class)) {
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"NomCarpetaNoRepetit"),
						ex);
			} else {
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"document.controller.importat.ko"),
						ex);
			}
			return "contingutImportacioForm";
		} finally {
			this.sortirSemafor(contingutId);
		}
		if (documentsRepetits > 0) {
			addWarningDocumentExists(request);
			return modalUrlTancar();
		}
		
		addWarningDocumentWithExpedients(request);
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../contingut/" + contingutId,
				"document.controller.importat.ok");
		
	}
	
	protected void entrarSemafor(Long contingutId) throws InterruptedException {
		Semaphore semafor = null;
		synchronized(semafors) {
			if (semafors.containsKey(contingutId)) {
				semafor = semafors.get(contingutId);
			} else {
				semafor = new Semaphore(1);
				semafors.put(contingutId, semafor);
			}
		}
		semafor.acquire();
	}
	
	protected void sortirSemafor(Long contingutId) {
		synchronized(semafors) {
			Semaphore semafor = semafors.get(contingutId);
			if (semafor != null) {
				if (semafor.getQueueLength()==0) {
					semafors.remove(contingutId);
				}
				semafor.release();
			}
		}
	}
	
	private void emplenarModelImportacio(
			HttpServletRequest request,
			Long contingutId,
			ImportacioCommand command, 
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		command.setPareId(contingutId);
		model.addAttribute(command);
		
		long t0 = System.currentTimeMillis();
		
		if (aplicacioService.mostrarLogsRendiment())
			logger.info("ContingutImportacioController.emplenarModelImportacio start ( contingutId=" + contingutId +  ")");
		
		List<ArbreDto<ExpedientCarpetaArbreDto>> carpetes = carpetaService.findArbreCarpetesExpedient(
				entitatActual.getId(),
				null,
				contingutId);
		model.addAttribute("carpetes", carpetes);
		model.addAttribute("jstreeJson", command.getEstructuraCarpetesJson());
		model.addAttribute("selectedCarpeta", command.getDestiId());
		
		if (aplicacioService.mostrarLogsRendiment())
    		logger.info("ContingutImportacioController.emplenarModelImportacio end:  " + (System.currentTimeMillis() - t0) + " ms");
		
		model.addAttribute(
				"tipusImportacioOptions",
				EnumHelper.getOptionsForEnum(
						TipusImportEnumDto.class,
						"contingut.importacio.tipus.enum."));
	}

	private void addWarningDocumentExists(HttpServletRequest request) {
		List<DocumentDto> documentsAlreadyImported = importacioService.consultaExpedientsAmbImportacio();
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
			MissatgesHelper.warning(
					request, 
					getMessage(
						request, 
						"expedient.peticio.controller.acceptat.duplicat.warning",
						new Object[] {sb.toString()}));
		}
	}
	
	private void addWarningDocumentWithExpedients(HttpServletRequest request) {
		Map<String, String> documentsWithExpedient = importacioService.consultaDocumentsWithExpedient();
		if (documentsWithExpedient != null && !documentsWithExpedient.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<ul>");
			for (Map.Entry<String, String> documentExpedient: documentsWithExpedient.entrySet()) {
				sb.append("<li>");
				sb.append(documentExpedient.getKey() + "<br>&nbsp;&nbsp;&nbsp;<strong>Expedient: " + documentExpedient.getValue() + "</strong>");
				sb.append("</li>");
			}
			sb.append("</ul>");
			MissatgesHelper.warning(
					request, 
					getMessage(
						request, 
						"document.controller.importacio.exists.expedient",
						new Object[] {sb.toString()}));
		}
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutImportacioController.class);

}
