package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;

import es.caib.ripea.core.api.dto.DiagnosticFiltreDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.GenericDto;
import es.caib.ripea.core.api.dto.IntegracioAccioDto;
import es.caib.ripea.core.api.dto.IntegracioDto;
import es.caib.ripea.core.api.dto.IntegracioEnumDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.EntitatService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.war.command.IntegracioFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EntitatHelper;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

@Controller
@RequestMapping("/integracio")
public class IntegracioController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "IntegracioController.session.filtre";
	private static final String SESSION_ATTRIBUTE_FILTRE_DIAG = "IntegracioController.session.diagnostic.filtre";
	private static final String INTEGRACIO_FILTRE = "integracio_filtre";

	@Autowired private AplicacioService aplicacioService;
	@Autowired private EntitatService entitatService;
	@Autowired private OrganGestorService organGestorService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		return getAmbCodi(request, null, model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			IntegracioFiltreCommand command,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		return postAmbCodi(request, null, command, accio, model);
	}

	@RequestMapping(value="/{codi}", method = RequestMethod.POST)
	public String postAmbCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			IntegracioFiltreCommand command,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {

		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, INTEGRACIO_FILTRE);
		} else { 
			RequestSessionHelper.actualitzarObjecteSessio(request, INTEGRACIO_FILTRE, command);
		}
		
		return codi != null ? "redirect:/integracio/{codi}" : "redirect:/integracio";
	}
	
	@RequestMapping(value = "/{codi}", method = RequestMethod.GET)
	public String getAmbCodi(HttpServletRequest request, @PathVariable String codi, Model model) {

		List<IntegracioDto> integracions = aplicacioService.integracioFindAll();
		for (IntegracioDto integracio : integracions) {
			for (IntegracioEnumDto integracioEnum : IntegracioEnumDto.values()) {
				if (integracio.getCodi() == integracioEnum.name()) {
					integracio.setNom(EnumHelper.getOneOptionForEnum(IntegracioEnumDto.class, "integracio.list.pipella." + integracio.getCodi()).getText());
				}
			}
		}
		IntegracioFiltreCommand command = IntegracioFiltreCommand.getFiltreCommand(request, INTEGRACIO_FILTRE);
		model.addAttribute(command);
		RequestSessionHelper.actualitzarObjecteSessio(request, INTEGRACIO_FILTRE, command);
		model.addAttribute("integracions", integracions);
		if (codi != null) {
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, codi);
		} else if (integracions.size() > 0) {
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, integracions.get(0).getCodi());
		}
		model.addAttribute("codiActual", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE));
		return "integracioList";
	}

	@RequestMapping(value = "/diagnostic", method = RequestMethod.GET)
	public String integracioDiagnostic(HttpServletRequest request, Model model) {
		List<IntegracioDto> integracions = aplicacioService.integracioFindAll();
		for (IntegracioDto integracio : integracions) {
			for (IntegracioEnumDto integracioEnum : IntegracioEnumDto.values()) {
				if (integracioEnum.name().equals(integracio.getCodi())) {
					integracio.setNom(EnumHelper.getOneOptionForEnum(IntegracioEnumDto.class, "integracio.list.pipella." + integracio.getCodi()).getText());
				}
			}
		}
		for (int i=integracions.size()-1; i>=0; i--) {
			if (IntegracioEnumDto.CALLBACK.name().equals(integracions.get(i).getCodi())) {
				integracions.remove(i);
			}
		}
		
		model.addAttribute("integracions", integracions);
		model.addAttribute("entitatsDiagnostic", entitatService.findAll());
		model.addAttribute("organsDiagnostic", organGestorService.findAll());
		
		Object filtreObj = RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE_DIAG);
		DiagnosticFiltreDto filtre = new DiagnosticFiltreDto();
		if (filtreObj!=null) {
			filtre = (DiagnosticFiltreDto)filtreObj;
		} else {
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
			if (entitatActual!=null) {
				filtre.setEntitatCodi(entitatActual.getCodi());
				OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
				if (organActual!=null) {
					filtre.setOrganCodi(organActual.getCodi());
				}
			}
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE_DIAG, filtre);
		}

		model.addAttribute("diagnosticFiltreDto", filtre);
		
		return "integracioDiagnostic";
	}

	@RequestMapping(value = "/diagnostic", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid DiagnosticFiltreDto filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE_DIAG);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE_DIAG, filtreCommand);
			}
		}
		return integracioDiagnostic(request, model);
	}
	
	@RequestMapping(value = "/diagnostic/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public GenericDto integracioDiagnosticAmbCodi(HttpServletRequest request, @PathVariable String codi, Model model) {
		
		Object filtreObj = RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE_DIAG);
		DiagnosticFiltreDto filtre = new DiagnosticFiltreDto();
		if (filtreObj!=null) {
			filtre = (DiagnosticFiltreDto)filtreObj;
		}
		
		GenericDto resultat = aplicacioService.integracioDiagnostic(codi, filtre);
		String missatge = getMessage(request, resultat.getCodi(), resultat.getArguments());
		resultat.setCodi(Utils.abbreviate(missatge, 200));
		return resultat;
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {

		String codi = (String)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		if (Strings.isNullOrEmpty(codi)) {
			return DatatablesHelper.getDatatableResponse(request, new ArrayList<IntegracioAccioDto>());
		}
		IntegracioFiltreCommand filtre = IntegracioFiltreCommand.getFiltreCommand(request, INTEGRACIO_FILTRE);
		
		return DatatablesHelper.getDatatableResponse(request, aplicacioService.integracioFindDarreresAccionsByCodiPaginat(codi, params, filtre.asDto()));
	}

	@RequestMapping(value = "/{codi}/{timestamp}", method = RequestMethod.GET)
	public String detall(HttpServletRequest request, @PathVariable String codi, @PathVariable long timestamp, Model model) {
		List<IntegracioAccioDto> accions = aplicacioService.integracioFindDarreresAccionsByCodi(codi);
		if (accions != null) {
			IntegracioAccioDto found = null;
			for (IntegracioAccioDto accio: accions) {
				if (accio.getTimestamp() != null && accio.getTimestamp() == timestamp) {
					found = accio;
					break;
				}
			}
			if (found != null) {
				model.addAttribute("integracio", found);
			}
		}
		model.addAttribute("codiActual", codi);
		return "integracioDetall";
	}

}
