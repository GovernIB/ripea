/**
 * 
 */
package es.caib.ripea.war.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.NodeDto;
import es.caib.ripea.core.api.registre.RegistreTipusEnum;
import es.caib.ripea.core.api.service.AlertaService;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContingutMoureCopiarEnviarCommand;
import es.caib.ripea.war.helper.BeanGeneratorHelper;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.SessioHelper;

/**
 * Controlador per a la gestió de contenidors i mètodes compartits entre
 * diferents tipus de contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ContingutController extends BaseUserController {

	private static final String CONTENIDOR_VISTA_ICONES = "icones";
	private static final String CONTENIDOR_VISTA_LLISTAT = "llistat";

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private ExpedientInteressatService interessatService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private AlertaService alertaService;

	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;



	@RequestMapping(value = "/contingut/{contingutId}", method = RequestMethod.GET)
	public String contingutGet(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				true);
		omplirModelPerMostrarContingut(
				request,
				entitatActual,
				contingut,
				SessioHelper.desmarcarLlegit(request),
				model);
		return "contingut";
	}

	@RequestMapping(value = "/contingut/{contingutId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false);
		contingutService.deleteReversible(
				entitatActual.getId(),
				contingutId);
		return getAjaxControllerReturnValueSuccess(
				request,
				(contingut.getPare() != null) ? "redirect:../../contingut/" + contingut.getPare().getId() : "redirect:../../escriptori",
				"contingut.controller.element.esborrat.ok");
	}

	@RequestMapping(value = "/contingut/{contingutId}/canviVista/icones", method = RequestMethod.GET)
	public String canviVistaLlistat(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		SessioHelper.updateContenidorVista(
				request,
				CONTENIDOR_VISTA_ICONES);
		return "redirect:../../" + contingutId;
	}
	@RequestMapping(value = "/contingut/{contingutId}/canviVista/llistat", method = RequestMethod.GET)
	public String canviVistaIcones(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		SessioHelper.updateContenidorVista(
				request,
				CONTENIDOR_VISTA_LLISTAT);
		return "redirect:../../" + contingutId;
	}

	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure", method = RequestMethod.GET)
	public String moureForm(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerMoureOCopiar(
				entitatActual,
				contingutOrigenId,
				model);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		command.setOrigenId(contingutOrigenId);
		model.addAttribute(command);
		return "contingutMoureForm";
	}
	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure", method = RequestMethod.POST)
	public String moure(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@Valid ContingutMoureCopiarEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerMoureOCopiar(
					entitatActual,
					contingutOrigenId,
					model);
			return "contingutMoureForm";
		}
		contingutService.move(
				entitatActual.getId(),
				contingutOrigenId,
				command.getDestiId());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutOrigenId,
				"contingut.controller.element.mogut.ok");
	}
	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure/{contingutDestiId}", method = RequestMethod.GET)
	public String moureDragDrop(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@PathVariable Long contingutDestiId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingutOrigen = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutOrigenId,
				true,
				false);
		contingutService.move(
				entitatActual.getId(),
				contingutOrigenId,
				contingutDestiId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutOrigen.getPare().getId(),
				"contingut.controller.element.mogut.ok");
	}

	@RequestMapping(value = "/contingut/{contingutOrigenId}/copiar", method = RequestMethod.GET)
	public String copiarForm(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerMoureOCopiar(
				entitatActual,
				contingutOrigenId,
				model);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		command.setOrigenId(contingutOrigenId);
		model.addAttribute(command);
		return "contingutCopiarForm";
	}
	@RequestMapping(value = "/contingut/{contingutOrigenId}/copiar", method = RequestMethod.POST)
	public String copiar(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@Valid ContingutMoureCopiarEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerMoureOCopiar(
					entitatActual,
					contingutOrigenId,
					model);
			return "contingutCopiarForm";
		}
		ContingutDto contingutCreat = contingutService.copy(
				entitatActual.getId(),
				contingutOrigenId,
				command.getDestiId(),
				true);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutCreat.getId(),
				"contingut.controller.element.copiat.ok");
	}

	@RequestMapping(value = "/contingut/{contingutId}/errors", method = RequestMethod.GET)
	public String errors(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						contingutId,
						true,
						false));
		model.addAttribute(
				"errors",
				contingutService.findErrorsValidacio(
						entitatActual.getId(),
						contingutId));
		return "contingutErrors";
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/errors/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse errorsDatatable(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		return DatatablesHelper.getDatatableResponse(
				request,
				alertaService.findPaginatByLlegida(
						false,
						contingutId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/errors/{alertaId}/llegir", method = RequestMethod.GET)
	@ResponseBody
	public void llegirAlerta(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long alertaId,
			Model model) {
		AlertaDto alerta = alertaService.find(alertaId);
		alerta.setLlegida(true);
		alertaService.update(alerta);
	}

	@RequestMapping(value = "/contingut/{contingutId}/interessat/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse interessatDatatable(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<InteressatDto> interessats = null;
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false);
		if (contingut instanceof ExpedientDto) {
			interessats = interessatService.findByExpedient(
					entitatActual.getId(),
					contingutId);
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				interessats);
	}

	@RequestMapping(value = "/contingut/{contingutId}/log", method = RequestMethod.GET)
	public String log(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						contingutId,
						true,
						false));
		model.addAttribute(
				"logs",
				contingutService.findLogsPerContingutUser(
						entitatActual.getId(),
						contingutId));
		model.addAttribute(
				"moviments",
				contingutService.findMovimentsPerContingutUser(
						entitatActual.getId(),
						contingutId));
		model.addAttribute(
				"logTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						LogTipusEnumDto.class,
						"log.tipus.enum."));
		model.addAttribute(
				"logObjecteTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						LogObjecteTipusEnumDto.class,
						"log.objecte.tipus.enum."));
		return "contingutLog";
	}

	@RequestMapping(value = "/contingut/{contingutId}/log/{contingutLogId}/detalls", method = RequestMethod.GET)
	@ResponseBody
	public ContingutLogDetallsDto logDetalls(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long contingutLogId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return contingutService.findLogDetallsPerContingutUser(
				entitatActual.getId(),
				contingutId,
				contingutLogId);
	}

	@RequestMapping(value = "/contingut/{contingutId}/arxiu")
	public String arxiu(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				false,
				false);
		model.addAttribute("contingut", contingut);
		if (contingut.isReplicatDinsArxiu()) {
			model.addAttribute(
					"arxiuDetall",
					contingutService.getArxiuDetall(
							entitatActual.getId(),
							contingutId));
		}
		return "contingutArxiu";
	}

	@RequestMapping(value = "/contingut/{contingutId}/exportar", method = RequestMethod.GET)
	public String exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = contingutService.exportacioEni(
				entitatActual.getId(),
				contingutId);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}



	private void omplirModelPerMostrarContingut(
			HttpServletRequest request,
			EntitatDto entitatActual,
			ContingutDto contingut,
			boolean pipellaAnotacionsRegistre,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		model.addAttribute("contingut", contingut);
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
		model.addAttribute(
				"metaDocuments",
				metaDocumentService.findActiveByEntitatAndContenidorPerCreacio(
						entitatActual.getId(),
						contingut.getId()));
		if (contingut instanceof ExpedientDto) {
			model.addAttribute(
					"interessats",
					interessatService.findByExpedient(
							entitatActual.getId(),
							contingut.getId()));
			model.addAttribute("relacionats", expedientService.relacioFindAmbExpedient(
					entitatActual.getId(),
					contingut.getId()));
		}
		/*if (contingut instanceof DocumentDto) {
			model.addAttribute(
					"documentVersions",
					documentService.findVersionsByDocument(
							entitatActual.getId(),
							contingut.getId()));
		}*/
		if (contingut instanceof NodeDto) {
			model.addAttribute(
					"metaDades",
					metaDadaService.findByNode(
							entitatActual.getId(),
							contingut.getId()));
			model.addAttribute(
					"dadesCommand",
					beanGeneratorHelper.generarCommandDadesNode(
							entitatActual.getId(),
							contingut.getId(),
							((NodeDto)contingut).getDades()));
		}
		String contingutVista = SessioHelper.getContenidorVista(request);
		if (contingutVista == null)
			contingutVista = CONTENIDOR_VISTA_ICONES;
		model.addAttribute(
				"vistaIcones",
				new Boolean(CONTENIDOR_VISTA_ICONES.equals(contingutVista)));
		model.addAttribute(
				"vistaLlistat",
				new Boolean(CONTENIDOR_VISTA_LLISTAT.equals(contingutVista)));
		model.addAttribute(
				"registreTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						RegistreTipusEnum.class,
						"registre.anotacio.tipus.enum."));
		model.addAttribute(
				"notificacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"notificacio.estat.enum.",
						new Enum<?>[] {DocumentEnviamentEstatEnumDto.PROCESSAT}));
		model.addAttribute(
				"publicacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"publicacio.estat.enum.",
						new Enum<?>[] {
							DocumentEnviamentEstatEnumDto.ENVIAT,
							DocumentEnviamentEstatEnumDto.PROCESSAT,
							DocumentEnviamentEstatEnumDto.CANCELAT}));
		model.addAttribute(
				"interessatTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						InteressatTipusEnumDto.class,
						"interessat.tipus.enum."));
		model.addAttribute(
				"pluginArxiuActiu",
				aplicacioService.isPluginArxiuActiu());
		model.addAttribute("pipellaAnotacionsRegistre", pipellaAnotacionsRegistre);
	}

	private void omplirModelPerMoureOCopiar(
			EntitatDto entitatActual,
			Long contingutOrigenId,
			Model model) {
		ContingutDto contingutOrigen = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutOrigenId,
				true,
				false);
		model.addAttribute(
				"contingutOrigen",
				contingutOrigen);
	}

}
