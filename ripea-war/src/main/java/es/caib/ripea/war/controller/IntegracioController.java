/**
 * 
 */
package es.caib.ripea.war.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.entity.config.ConfigEntity;
import es.caib.ripea.war.command.IntegracioFiltreCommand;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.IntegracioAccioDto;
import es.caib.ripea.core.api.dto.IntegracioDto;
import es.caib.ripea.core.api.dto.IntegracioEnumDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per a la consulta d'accions de les integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/integracio")
public class IntegracioController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "IntegracioController.session.filtre";
	private static final String INTEGRACIO_FILTRE = "integracio_filtre";

	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		return getAmbCodi(request, null, model);
	}

	@RequestMapping(value="/{codi}", method = RequestMethod.POST)
	public String post(HttpServletRequest request, @PathVariable @NonNull String codi, IntegracioFiltreCommand command, Model model) {

		RequestSessionHelper.actualitzarObjecteSessio(request, INTEGRACIO_FILTRE, command);
		return getAmbCodi(request, codi, model);
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

	/*@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesPagina<IntegracioAccioDto> datatable(
			HttpServletRequest request,
			Model model) {
		String codi = (String)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		List<IntegracioAccioDto> accions = null;
		if (codi != null) {
			accions = integracioService.findDarreresAccionsByIntegracio(
					codi);
		} else {
			accions = new ArrayList<IntegracioAccioDto>();
		}
		return PaginacioHelper.getPaginaPerDatatables(
				request,
				accions);
	}*/
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {

		String codi = (String)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		if (Strings.isNullOrEmpty(codi)) {
			return DatatablesHelper.getDatatableResponse(request, new ArrayList<IntegracioAccioDto>());
		}
		IntegracioFiltreCommand filtre = IntegracioFiltreCommand.getFiltreCommand(request, INTEGRACIO_FILTRE);
		List<PaginacioParamsDto.FiltreDto> filtres = new ArrayList<>();
		filtres.add((new PaginacioParamsDto.FiltreDto("entitat_codi", filtre.getEntitatCodi())));
		params.setFiltres(filtres);
		return DatatablesHelper.getDatatableResponse(request, aplicacioService.integracioFindDarreresAccionsByCodiPaginat(codi, params, filtre.asDto()));
	}

	@RequestMapping(value = "/{codi}/{index}", method = RequestMethod.GET)
	public String detall(HttpServletRequest request, @PathVariable String codi, @PathVariable int index, Model model) {

		List<IntegracioAccioDto> accions = aplicacioService.integracioFindDarreresAccionsByCodi(codi);
		if (accions != null && index < accions.size()) {
			model.addAttribute("integracio", accions.get(index));
		}
		model.addAttribute("codiActual", codi);
		return "integracioDetall";
	}

}
