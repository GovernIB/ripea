package es.caib.ripea.back.controller;

import es.caib.ripea.back.command.MetaExpedientTascaCommand;
import es.caib.ripea.back.helper.*;
import es.caib.ripea.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador pel llistat de tasques del meta-expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaExpedient")
public class MetaExpedientTascaController extends BaseAdminController {

	@Autowired private MetaExpedientService metaExpedientService;
	@Autowired private MetaDadaService metaDadaService;
	@Autowired private MetaDocumentService metaDocumentService;
	@Autowired private ExpedientEstatService expedientEstatService;
	@Autowired private ConfigService configService;
	
	public static final String SESSION_ATTRIBUTE_VALIDACIONS = "ValidacionsTasca.UserActual";
	
	@RequestMapping(value = "/{metaExpedientId}/tasca", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);

		String rolActual = RolHelper.getRolActual(request);
		model.addAttribute("esRevisor",	rolActual.equals("IPA_REVISIO"));
		if (!rolActual.equals("IPA_REVISIO")) {
			comprovarAccesMetaExpedient(request, metaExpedientId);
		}
		MetaExpedientDto metaExpedient = metaExpedientService.findByIdAmbElements(
				entitatActual.getId(),
				metaExpedientId, 
				RolHelper.isRolActualAdministradorOrgan(request) ? EntitatHelper.getOrganGestorActualId(request) : null);
		model.addAttribute("metaExpedient", metaExpedient);
		
		if (metaExpedient != null && metaExpedientService.isRevisioActiva()) { // es tracta d'una modificació
			if (RolHelper.isRolActualAdministradorOrgan(request)  && metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT){
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
				model.addAttribute("bloquejarCamps", true);
			} else if (RolHelper.isRolActualRevisor(request)){
				model.addAttribute("bloquejarCamps", true);
				model.addAttribute("consultar", true);
				model.addAttribute("isRolActualRevisor", true);
			}
		}
		//TODO rename to metaExpedientTascaList
		return "metaExpedientTasca";
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		model.addAttribute(
				"esRevisor",
				rolActual.equals("IPA_REVISIO"));
		
		if (!rolActual.equals("IPA_REVISIO")) {
			comprovarAccesMetaExpedient(request, metaExpedientId);
		}
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaExpedientService.tascaFindPaginatByMetaExpedient(
						entitatActual.getId(),
						metaExpedientId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{metaExpedientId}/validacions/{metaExpedientTascaId}/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatableValidacions(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long metaExpedientTascaId,
			Model model) {
		List<MetaExpedientTascaValidacioDto> resultat = new ArrayList<MetaExpedientTascaValidacioDto>();
		//Estam creant una nova tasca, encara no disposam de IDs, per tant anam guardant les validacions en sessió
		if (metaExpedientTascaId==0) {
			Object aux = request.getSession().getAttribute(SESSION_ATTRIBUTE_VALIDACIONS);
			if (aux!=null) {
				resultat = (List<MetaExpedientTascaValidacioDto>)aux;
			}
		} else {
			//Ja tenim la tasca creada, per tant podem extreurer les validacions de BBDD (findByTascaId)
			resultat = metaExpedientService.findValidacionsTasca(metaExpedientTascaId);
		}
		
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(request, resultat, "id");
		return dtr;
	}
	
	@RequestMapping(value = "/{metaExpedientId}/getElements/{tipus}", method = RequestMethod.GET)
	@ResponseBody
	public List<GenericDto> onChangeTipusElement(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable ItemValidacioTascaEnum tipus,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		List<GenericDto> resultat = new ArrayList<GenericDto>();
		
		if (ItemValidacioTascaEnum.DADA.equals(tipus)) {
			List<MetaDadaDto> aux = metaDadaService.findActiveByMetaNode(entitatActual.getId(), metaExpedientId);
			if (aux!=null) {
				for (MetaDadaDto mD: aux) {
					resultat.add(new GenericDto(mD.getId(), null, mD.getNom()));
				}
			}
		} else {
			List<MetaDocumentDto> items = metaDocumentService. findByMetaExpedient(entitatActual.getId(), metaExpedientId);
			if (items!=null) {	
				for (MetaDocumentDto mD: items) {
					if (mD.isActiu()) {
						resultat.add(new GenericDto(mD.getId(), null, mD.getNom()));
					}
				}
			}
		}
		
		return resultat;
	}
	
	@RequestMapping(value = "/{metaExpedientId}/tasca/{tascaId}/validacioAccio", method = RequestMethod.GET)
	@ResponseBody
	public boolean validacioAccio(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long tascaId,
			@RequestParam(value="validacioId") Long validacioId,
			@RequestParam(value="accioRealitzar") String accioRealitzar,
			Model model) {
		
		MetaExpedientTascaValidacioDto validacioDto = null;
		if (tascaId==0) {
			validacioDto = updateValidacioSessioById(request, validacioId, accioRealitzar);
		} else {
			validacioDto = metaExpedientService.updateValidacioTasca(validacioId, accioRealitzar);
		}
		return true;
	}
	
	private MetaExpedientTascaValidacioDto updateValidacioSessioById(HttpServletRequest request, Long validacioId, String accioRealitzar) {
		
		Object aux = request.getSession().getAttribute(SESSION_ATTRIBUTE_VALIDACIONS);
		List<MetaExpedientTascaValidacioDto> resultat = new ArrayList<MetaExpedientTascaValidacioDto>();
		MetaExpedientTascaValidacioDto validacioModificada = null;
		
		if (aux!=null) {
			resultat = (List<MetaExpedientTascaValidacioDto>)aux;
		}
		
		for (MetaExpedientTascaValidacioDto validacioSessio: resultat) {
			if (validacioSessio.getId().equals(validacioId)) {
				validacioModificada = validacioSessio;
				if("DESACTIVAR".equals(accioRealitzar)) {
					validacioSessio.setActiva(false);
				} else if("ACTIVAR".equals(accioRealitzar)) {
					validacioSessio.setActiva(true);
				} else if("ELIMINAR".equals(accioRealitzar)) {
					resultat.remove(validacioSessio);
				}
				break;
			}
		}
		
		//Actualitza el objecte de sessió
		request.getSession().setAttribute(SESSION_ATTRIBUTE_VALIDACIONS, resultat);
		
		return validacioModificada;
	}
	
	@RequestMapping(value = "/{metaExpedientId}/validacions/{tascaId}/newValidacio", method = RequestMethod.GET)
	@ResponseBody
	public boolean newValidacio(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long tascaId,
			@RequestParam(value="tipus") ItemValidacioTascaEnum tipus,
			@RequestParam(value="itemId") Long itemId,
			@RequestParam(value="itemNom") String itemNom,
			@RequestParam(value="validacio") TipusValidacioTascaEnum validacio,
			Model model) {
		
		MetaExpedientTascaValidacioDto nouElement = new MetaExpedientTascaValidacioDto();
		nouElement.setActiva(true);
		nouElement.setItemId(itemId);
		nouElement.setItemNom(itemNom);
		nouElement.setItemValidacio(tipus);
		nouElement.setTipusValidacio(validacio);
		MetaExpedientTascaDto metaExpedientTascaDto = new MetaExpedientTascaDto();
		metaExpedientTascaDto.setId(tascaId);
		nouElement.setMetaExpedientTasca(metaExpedientTascaDto);
		
		if (tascaId==0) {
			
			//Guardam en sessio
			List<MetaExpedientTascaValidacioDto> resultat = new ArrayList<MetaExpedientTascaValidacioDto>();
			Object aux = request.getSession().getAttribute(SESSION_ATTRIBUTE_VALIDACIONS);
			
			if (aux!=null) {
				resultat = (List<MetaExpedientTascaValidacioDto>)aux;
			}
			
			boolean repetitEnSessio = false;
			Long identificadorTemporal = 0l;
			for (MetaExpedientTascaValidacioDto validacioSessio: resultat) {
				if (validacioSessio.getItemId().equals(nouElement.getItemId()) &&
					validacioSessio.getItemValidacio().equals(nouElement.getItemValidacio()) &&
					validacioSessio.getTipusValidacio().equals(nouElement.getTipusValidacio())) {
					repetitEnSessio = true;
				}
				validacioSessio.setId(identificadorTemporal++);
			}
			
			if (!repetitEnSessio) {
				nouElement.setId(identificadorTemporal++);
				resultat.add(nouElement);
				request.getSession().setAttribute(SESSION_ATTRIBUTE_VALIDACIONS, resultat);
				MissatgesHelper.success(request, getMessage(request, "metaexpedient.tasca.validacio.ok.sessio"));
			} else {
				MissatgesHelper.error(request, getMessage(request, "metaexpedient.tasca.validacio.repetida"));
			}				
			
		} else {
			//Guardam a BBDD
			if (metaExpedientService.createValidacioTasca(nouElement)) {
				MissatgesHelper.success(request, getMessage(request, "metaexpedient.tasca.validacio.ok"));
			} else {
				MissatgesHelper.error(request, getMessage(request, "metaexpedient.tasca.validacio.repetida"));
			}			
		}
		
		return true;
	}
	
	@RequestMapping(value = "/{metaExpedientId}/tasca/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			Model model) {
		return get(
				request,
				metaExpedientId,
				null,
				model);
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id,
			Model model) {
		
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_VALIDACIONS);
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		MetaExpedientDto metaExpedient = comprovarAccesMetaExpedient(request, metaExpedientId);
		model.addAttribute("metaExpedient", metaExpedientService.findById(entitatActual.getId(), metaExpedientId));
		MetaExpedientTascaDto tasca = null;
		
		if (id != null) {
			tasca = metaExpedientService.tascaFindById(entitatActual.getId(), metaExpedientId, id);
			model.addAttribute(tasca);
		} else {
			tasca = new MetaExpedientTascaDto();
			try {
				String duracioTascaConf = configService.getConfigValue(PropertyConfig.TASCA_DURACIO_DEFAULT);
				tasca.setDuracio(duracioTascaConf!=null?Integer.parseInt(duracioTascaConf):10);
			} catch (Exception ex) {
				tasca.setDuracio(10);
			}
		}
		
		List<ExpedientEstatDto> expedientEstats = expedientEstatService.findExpedientEstatsByMetaExpedient(
				entitatActual.getId(),
				metaExpedientId);
		
		model.addAttribute("expedientEstats", expedientEstats);
		model.addAttribute("itemValidacioOptions", EnumHelper.getOptionsForEnum(ItemValidacioTascaEnum.class, "metaexpedient.tasca.validacio.tipus."));
		model.addAttribute("itemsOptions", metaDadaService.findActiveByMetaNode(entitatActual.getId(), metaExpedientId));
		model.addAttribute("tipusValidacioOptions", EnumHelper.getOptionsForEnum(TipusValidacioTascaEnum.class, "metaexpedient.tasca.validacio.enum."));
		
		MetaExpedientTascaCommand command = null;
		if (tasca != null)
			command = MetaExpedientTascaCommand.asCommand(tasca);
		else
			command = new MetaExpedientTascaCommand();
		
		command.setEntitatId(entitatActual.getId());
		command.setMetaExpedientId(metaExpedientId);
		model.addAttribute(command);
		
		if (metaExpedient != null && metaExpedientService.isRevisioActiva()) { // es tracta d'una modificació
			if (RolHelper.isRolActualAdministradorOrgan(request)  && metaExpedient.getRevisioEstat() == MetaExpedientRevisioEstatEnumDto.REVISAT){
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.adminOrgan.bloquejada.alerta"));
				model.addAttribute("bloquejarCamps", true);
			} else if (RolHelper.isRolActualRevisor(request)){
				model.addAttribute("bloquejarCamps", true);
				model.addAttribute("consultar", true);
				model.addAttribute("isRolActualRevisor", true);
			}
		}
		return "metaExpedientTascaForm";
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@Valid MetaExpedientTascaCommand command,
			BindingResult bindingResult,
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		comprovarAccesMetaExpedient(request, metaExpedientId);

		if (bindingResult.hasErrors()) {
			model.addAttribute("metaExpedient", metaExpedientService.findById(entitatActual.getId(), metaExpedientId));
			request.getSession().setAttribute(MissatgesHelper.SESSION_ATTRIBUTE_BINDING_ERRORS, bindingResult.getGlobalErrors());
			return "metaExpedientTascaForm";
		}
		
		if (command.getDuracio()!=null && command.getDuracio()<0) { command.setDuracio(null); }
		
		if (command.getId() == null) {
			//CREATE
			MetaExpedientTascaDto tascaCreada = metaExpedientService.tascaCreate(
					entitatActual.getId(),
					metaExpedientId,
					MetaExpedientTascaCommand.asDto(command), rolActual, organActual != null ? organActual.getId() : null);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			
			Object aux = request.getSession().getAttribute(SESSION_ATTRIBUTE_VALIDACIONS);
			int validacionsCreades = 0;
			if (aux!=null) {
				validacionsCreades = metaExpedientService.createValidacionsTasca(entitatActual.getId(), tascaCreada.getId(), (List<MetaExpedientTascaValidacioDto>)aux);
			}

			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + metaExpedientId,
					"metaexpedient.controller.tasca.creada.ok",
					new Object[] { command.getNom(), validacionsCreades });
		
		} else {
			
			//UPDATE
			metaExpedientService.tascaUpdate(
					entitatActual.getId(),
					metaExpedientId,
					MetaExpedientTascaCommand.asDto(command), rolActual, organActual != null ? organActual.getId() : null);
			
			if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
				MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:expedientEstat/" + metaExpedientId,
					"metaexpedient.controller.tasca.modificada.ok",
					new Object[] { command.getNom() });
		}
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientTascaDto metaExpedientTascaDto = metaExpedientService.tascaUpdateActiu(
				entitatActual.getId(),
				metaExpedientId,
				id,
				true, rolActual, organActual != null ? organActual.getId() : null);
		

		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.tasca.activada.ok",
				new Object[] { metaExpedientTascaDto.getNom() });
	}
	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientTascaDto metaExpedientTascaDto = metaExpedientService.tascaUpdateActiu(
				entitatActual.getId(),
				metaExpedientId,
				id,
				false, rolActual, organActual != null ? organActual.getId() : null);
		

		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaExpedient",
				"metaexpedient.controller.tasca.desactivada.ok",
				new Object[] { metaExpedientTascaDto.getNom() });
	}

	@RequestMapping(value = "/{metaExpedientId}/tasca/{id}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaExpedientId,
			@PathVariable Long id) {
		OrganGestorDto organActual = EntitatHelper.getOrganGestorActual(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminEntitatOAdminOrganOrRevisor(request);
		String rolActual = RolHelper.getRolActual(request);
		boolean metaExpedientPendentRevisio = metaExpedientService.isMetaExpedientPendentRevisio(entitatActual.getId(), metaExpedientId);
		
		comprovarAccesMetaExpedient(request, metaExpedientId);
		MetaExpedientTascaDto metaExpedientTascaDto = metaExpedientService.tascaDelete(
				entitatActual.getId(),
				metaExpedientId,
				id, rolActual, organActual != null ? organActual.getId() : null);
		
		if (rolActual.equals("IPA_ORGAN_ADMIN") && !metaExpedientPendentRevisio && metaExpedientService.isRevisioActiva()) {
			MissatgesHelper.info(request, getMessage(request, "metaexpedient.revisio.modificar.alerta"));
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:expedientEstat",
				"metaexpedient.controller.tasca.esborrada.ok",
				new Object[] { metaExpedientTascaDto.getNom() });
	}

}
