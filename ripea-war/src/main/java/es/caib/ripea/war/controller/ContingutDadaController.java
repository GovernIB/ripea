/**
 * 
 */
package es.caib.ripea.war.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.MetaNodeMetaDadaDto;
import es.caib.ripea.core.api.dto.NodeDto;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.war.helper.AjaxHelper;
import es.caib.ripea.war.helper.AjaxHelper.AjaxFormResponse;
import es.caib.ripea.war.helper.BeanGeneratorHelper;

/**
 * Controlador per a la gestió de contenidors i mètodes compartits entre
 * diferents tipus de contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingutDada")
public class ContingutDadaController extends BaseUserController {

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private MetaDadaService metaDadaService;

	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;
	
	/*@Autowired
	private Validator validator;*/



	@ModelAttribute("dadesCommand")
	public Object addDadesCommand(
			@PathVariable Long contingutId,
			HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (contingutId == null)
			return null;
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return beanGeneratorHelper.generarCommandDadesNode(
				entitatActual.getId(),
				contingutId,
				null);
	}

	@RequestMapping(value = "/{contingutId}/save", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse dadaSavePost(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@ModelAttribute("dadesCommand") Object dadesCommand,
			BindingResult bindingResult,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (bindingResult.hasErrors()) {
			return AjaxHelper.generarAjaxFormErrors(
					null,
					bindingResult);
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			List<MetaNodeMetaDadaDto> contingutMetaDades = metaDadaService.findByNode(
					entitatActual.getId(),
					contingutId);
			Map<String, Object> valors = new HashMap<String, Object>();
			for (int i = 0; i < contingutMetaDades.size(); i++) {
				MetaNodeMetaDadaDto metaDada = contingutMetaDades.get(i);
				Object valor = PropertyUtils.getSimpleProperty(
						dadesCommand,
						metaDada.getMetaDada().getCodi());
				if (valor != null && (!(valor instanceof String) || !((String)valor).isEmpty())) {
					valors.put(
							metaDada.getMetaDada().getCodi(),
							valor);
				}
			}
			contingutService.dadaSave(
					entitatActual.getId(),
					contingutId,
					valors);
			return AjaxHelper.generarAjaxFormOk();
		}
	}

	@RequestMapping(value = "/{contingutId}/count")
	@ResponseBody
	public int dadaCount(
			HttpServletRequest request,
			@PathVariable Long contingutId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut =  contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				false,
				false);
		if (contingut instanceof NodeDto) {
			return ((NodeDto)contingut).getDadesCount();
		} else {
			return 0;
		}
	}

	/*@RequestMapping(value = "/contingut/{contingutId}/dada/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse dadaDatatable(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<DadaDto> dades = null;
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true);
		if (contingut instanceof NodeDto) {
			NodeDto node = (NodeDto)contingut;
			dades = node.getDades();
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				dades);
	}

	@RequestMapping(value = "/contingut/{contingutId}/dada/new", method = RequestMethod.GET)
	public String dadaNewGet(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws Exception {
		return dadaGet(
				request,
				contingutId,
				null,
				model);
	}
	@RequestMapping(value = "/contingut/{contingutId}/dada/{dadaId}", method = RequestMethod.GET)
	public String dadaGet(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long dadaId,
			Model model) throws Exception {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true);
		DadaDto dada = null;
		if (contingut instanceof NodeDto) {
			NodeDto node = (NodeDto)contingut;
			if (dadaId != null) {
				dada = contingutService.dadaFindById(
						entitatActual.getId(),
						contingutId,
						dadaId);
				List<MetaDadaDto> metaDades = new ArrayList<MetaDadaDto>();
				metaDades.add(dada.getMetaDada());
				model.addAttribute("metaDades", metaDades);
			} else {
				model.addAttribute(
						"metaDades",
						metaDadaService.findByNodePerCreacio(
								entitatActual.getId(),
								node.getId()));
			}
		}
		DadaCommand command;
		if (dada != null) {
			command = DadaCommand.asCommand(dada);
		} else {
			command = new DadaCommand();
			command.setId(dadaId);
		}
		command.setEntitatId(entitatActual.getId());
		command.setNodeId(contingutId);
		model.addAttribute(command);
		return "contingutDadaForm";
	}
	@RequestMapping(value = "/contingut/{contingutId}/dada/new", method = RequestMethod.POST)
	public String dadaNewPost(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@ModelAttribute DadaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<Class<?>> grups = new ArrayList<Class<?>>();
		if (command.getMetaDadaId() != null) {
			List<MetaDadaDto> metaDades = metaDadaService.findByNodePerCreacio(
					entitatActual.getId(),
					contingutId);
			for (MetaDadaDto metaDada: metaDades) {
				if (command.getMetaDadaId().equals(metaDada.getId())) {
					if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.TEXT))
						grups.add(DadaTipusText.class);
					if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DATA))
						grups.add(DadaTipusData.class);
					if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.SENCER))
						grups.add(DadaTipusSencer.class);
					if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.FLOTANT))
						grups.add(DadaTipusFlotant.class);
					if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.IMPORT))
						grups.add(DadaTipusImport.class);
					if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.BOOLEA))
						grups.add(DadaTipusBoolea.class);
					break;
				}
			}
		}
		new ValidationHelper(validator).isValid(
				command,
				bindingResult,
				grups.toArray(new Class[grups.size()]));
		if (bindingResult.hasErrors()) {
			ContingutDto contingut = contingutService.findAmbIdUser(
					entitatActual.getId(),
					contingutId,
					true);
			if (contingut instanceof NodeDto) {
				NodeDto node = (NodeDto)contingut;
				model.addAttribute(
						"metaDades",
						metaDadaService.findByNodePerCreacio(
								entitatActual.getId(),
								node.getId()));
			}
			return "contingutDadaForm";
		}
		if (command.getId() != null) {
			contingutService.dadaUpdate(
					entitatActual.getId(),
					contingutId,
					command.getId(),
					command.getValor());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + contingutId,
					"contingut.controller.dada.modificada.ok");
		} else {
			contingutService.dadaCreate(
					entitatActual.getId(),
					contingutId,
					command.getMetaDadaId(),
					command.getValor());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../contingut/" + contingutId,
					"contingut.controller.dada.creada.ok");
		}
	}

	@RequestMapping(value = "/contingut/{contingutId}/dada/{dadaId}/delete", method = RequestMethod.GET)
	public String dadaDelete(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long dadaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		contingutService.dadaDelete(
				entitatActual.getId(),
				contingutId,
				dadaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../contingut/" + contingutId,
				"contingut.controller.dada.esborrada.ok");
	}

	@RequestMapping(value = "/contingut/{contingutId}/llistaMetaDadesCrear", method = RequestMethod.GET)
	@ResponseBody
	public List<MetaDadaDto> llistaMetas(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true);
		if (contingut instanceof NodeDto)
			return metaDadaService.findByNodePerCreacio(
					entitatActual.getId(),
					contingutId);
		else
			return new ArrayList<MetaDadaDto>();
	}*/

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	    binder.registerCustomEditor(
	    		BigDecimal.class,
	    		new CustomNumberEditor(
	    				BigDecimal.class,
	    				NumberFormat.getInstance(new Locale("es","ES")),
	    				true));
	    binder.registerCustomEditor(
	    		Double.class,
	    		new CustomNumberEditor(
	    				Double.class,
	    				NumberFormat.getInstance(new Locale("es","ES")),
	    				true));
	}

}
