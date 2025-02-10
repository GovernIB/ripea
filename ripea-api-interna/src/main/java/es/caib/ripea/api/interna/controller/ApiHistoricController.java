package es.caib.ripea.api.interna.controller;

import es.caib.ripea.api.interna.command.HistoricFiltreCommand;
import es.caib.ripea.api.interna.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.historic.HistoricExpedientDto;
import es.caib.ripea.service.intf.dto.historic.HistoricInteressatDto;
import es.caib.ripea.service.intf.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.service.intf.dto.historic.HistoricUsuariDto;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricApiResponse;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer.RegistreOrganGestor;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricOrganGestorSerializer.RegistresOrganGestor;
import es.caib.ripea.service.intf.dto.historic.serializer.HistoricSerializers.RegistreExpedient;
import es.caib.ripea.service.intf.service.HistoricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("api/historic")
@Tag(
		name = "Consulta d'històrics",
		description = "API de consulta dels històrics d'ús de l'aplicació")
public class ApiHistoricController {

	@Autowired
	private HistoricService historicService;
	
//	@RequestMapping(value = "/generate", method = RequestMethod.GET, produces = "application/json")
//	public String generate() {
//		historicService.generateOldHistorics();
//		return "";
//	}
	
	
	/*
	 * DADES PER ENTITAT
	*/
	
	@RequestMapping(value = "/entitat/{entitatId}", method = RequestMethod.GET, produces = "application/json")
	@Operation(
			summary = "Consulta l'històric d'ús de l'aplicació d'una entitat concreta",
			description = "Retorna una llista amb la suma dels històrics agrupats per data segons el tipus d'agrupament",
			tags = "HistoricsEntitat")
	@ResponseBody
	public HistoricApiResponse getHistoricEntitat(
			HttpServletRequest request,
			@Parameter(name = "entitatId", description = "Identificador de l'entitat a consultar", required = true)
			@PathVariable(value = "entitatId") Long entitatId,
			
			@Parameter(name = "dataInici", description = "Data inicial a consultar", required = false)
			@RequestParam(value = "dataInici", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataInici,
			
			@Parameter(name = "dataFi", description = "Data final a consultar", required = false)
			@RequestParam(value = "dataFi", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataFi,
			
			@Parameter(name = "organGestorsIds", description = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) 
			List<Long> organGestorsIds,
			
			@Parameter(name = "metaExpedientsIds", description = "Procediments dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) 
			List<Long> metaExpedientsIds,
			
			@Parameter(name = "incorporarExpedientsComuns",
					description = "Indica si la consulta ha d'incorporar les dades dels expedients comuns",
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) 
			Boolean incorporarExpedientsComuns,
			
			@Parameter(name = "tipusAgrupament", description = "Tipus d'agrupament, DIARI/MENSUAL", required = false)
			@RequestParam(value = "tipusAgrupament", required = false) HistoricTipusEnumDto tipusAgrupament
			) {

		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(dataInici, dataFi, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, tipusAgrupament);

		List<HistoricExpedientDto> data = historicService.getDadesEntitat(entitatId, null, filtre.asDto());
		
		List<RegistreExpedient> response = ConversioTipusHelper.convertirList(data, RegistreExpedient.class);
		return new HistoricApiResponse(filtre.asDto(), response);
	}

	@RequestMapping(value = "/entitat/{entitatId}/actuals", method = RequestMethod.GET, produces = "application/json")
	@Operation(
			summary = "Consulta l'històric d'ús de l'aplicació per cada procediment per una entitat concreta",
			description = "Retorna una llista dels històrics del dia d'avui per cada procediment ",
			tags = "HistoricsEntitat")
	@ResponseBody
	public List<HistoricExpedientDto> getHistoricEntitatActual(
			HttpServletRequest request,
			@Parameter(name = "entitatId", description = "Identificador de l'entitat a consultar", required = true)
			@PathVariable(value = "entitatId") Long entitatId,
						
			@Parameter(name = "organGestorsIds", description = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@Parameter(name = "metaExpedientsIds", description = "Procediments dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@Parameter(name = "incorporarExpedientsComuns",
					description = "Indica si la consulta ha d'incorporar les dades dels expedients comuns",
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns) {
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(null, null, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, null);
		List<HistoricExpedientDto> response = historicService.getDadesActualsEntitat(
				entitatId,
				null,
				filtre.asDto());
		return response;
	}

	/*
	 * DADES PER ORGAN GESTOR
	*/
	
	@RequestMapping(value = "/organgestors", method = RequestMethod.GET, produces = "application/json")
	@Operation(
			summary = "Consulta l'històric d'ús de l'aplicació pels distints organs gestors de l'aplicació",
			description = "Retorna els històrics agrupats per data segons el tipus d'agrupament per a cada organ gestor consultat ",
			tags = "HistoricsOrganGestor")
	@ResponseBody
	public HistoricApiResponse getHistoricsByOrganGestor(
			HttpServletRequest request,
			
			@Parameter(name = "dataInici", description = "Data inicial a consultar", required = false)
			@RequestParam(value = "dataInici", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataInici,
			
			@Parameter(name = "dataFi", description = "Data final a consultar", required = false)
			@RequestParam(value = "dataFi", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataFi,
			
			@Parameter(name = "organGestorsIds", description = "Òrgans gestors dels quals consultar dades", required = true)
			@RequestParam(value = "organGestorsIds", required = true) 
			List<Long> organGestorsIds,
			
			@Parameter(name = "metaExpedientsIds", description = "Procediments dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) 
			List<Long> metaExpedientsIds,
			
			@Parameter(name = "incorporarExpedientsComuns",
					description = "Indica si la consulta ha d'incorporar les dades dels expedients comuns",
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) 
			Boolean incorporarExpedientsComuns,
			
			@Parameter(name = "tipusAgrupament", description = "Tipus d'agrupament, DIARI/MENSUAL", required = false)
			@RequestParam(value = "tipusAgrupament", required = false) 
			
			HistoricTipusEnumDto tipusAgrupament) {

		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(dataInici, dataFi, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, tipusAgrupament);

		// Check params
//		for (Long organId : filtre.getOrganGestorsIds()) {
//			organGestorService.findItem(organId); // Throw exception when item not found
//		}

		// Perform query
		List<RegistresOrganGestor> registres = historicService.getRegistresDadesOrgansGestors(null, null, filtre.asDto(), tipusAgrupament);

		
//		// ordena els registres per data
//		Collections.sort(registres, new Comparator<RegistresOrganGestor>() {
//
//			@Override
//			public int compare(RegistresOrganGestor o1, RegistresOrganGestor o2) {
//				return o2.data.compareTo(o1.data);
//			}
//		});
		
		return new HistoricApiResponse(filtre.asDto(), registres);
		 
	}

	@RequestMapping(value = "/organgestors/actual", method = RequestMethod.GET, produces = "application/json")
	@Operation(
			summary = "Consulta l'històric d'ús de l'aplicació del dia d'avui pels distints organs gestors de l'aplicació",
			description = "Retorna els històrics d'ús del dia d'avui per a cada organ gestor consultat ",
			tags = "HistoricsOrganGestor")
	@ResponseBody
	public List<RegistreOrganGestor> getHistoricsActualsByOrganGestor(
			HttpServletRequest request,

			@Parameter(name = "organGestorsIds", description = "Òrgans gestors dels quals consultar dades", required = true)
			@RequestParam(value = "organGestorsIds", required = true) List<Long> organGestorsIds,
			
			@Parameter(name = "metaExpedientsIds", description = "Procediments dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@Parameter(name = "incorporarExpedientsComuns",
					description = "Indica si la consulta ha d'incorporar les dades dels expedients comuns",
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns) {

		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(null, null, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, null);

		// Check params
//		for (Long organId : filtre.getOrganGestorsIds()) {
//			organGestorService.findItem(organId); // Throw exception when item not found
//		}
		
		// Perform query
		return historicService.getRegistresDadesActualsOrgansGestors(null, null, filtre.asDto());
	}

	/*
	 * DADES PER USUARI
	*/
	
	@RequestMapping(value = "/usuaris", method = RequestMethod.GET, produces = "application/json")
	@Operation(
			summary = "Consulta l'històric d'ús de l'aplicació pels distints usuaris de l'aplicació",
			description = "Retorna els històrics agrupats per data segons el tipus d'agrupament per a cada usuari consultat ",
			tags = "HistoricsUsuaris")
	@ResponseBody
	public HistoricApiResponse getHistoricsByUsuari(
			HttpServletRequest request,
			
			@Parameter(name = "dataInici", description = "Data inicial a consultar", required = false)
			@RequestParam(value = "dataInici", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataInici,
			
			@Parameter(name = "dataFi", description = "Data final a consultar", required = false)
			@RequestParam(value = "dataFi", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataFi,
			
			@Parameter(name = "organGestorsIds", description = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@Parameter(name = "metaExpedientsIds", description = "Procediments dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@Parameter(name = "incorporarExpedientsComuns",
					description = "Indica si la consulta ha d'incorporar les dades dels expedients comuns",
					required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns,
			
			@Parameter(name = "tipusAgrupament", description = "Tipus d'agrupament, per dia(1)/mes(0)", required = false)
			@RequestParam(value = "tipusAgrupament", required = false) HistoricTipusEnumDto tipusAgrupament,
			
			@Parameter(name = "usuaris", description = "Codi dels usuaris que es volen consultar", required = true)
			@RequestParam(value = "usuaris", required = true) List<String> usuarisCodi) {

		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(dataInici, dataFi, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, tipusAgrupament);
		
		// Check params
//		for (Long organId : filtre.getOrganGestorsIds()) {
//			organGestorService.findItem(organId); // Throw exception when item not found
//		}
//		
//		for (String userCode : usuarisCodi) {
//			if (aplicacioService.findUsuariAmbCodi(userCode) == null) {
//				throw new NotFoundException(userCode, UsuariDto.class);
//			}
//		}
		
		// Perform query
		return new HistoricApiResponse(filtre.asDto(), historicService.getRegistresDadesUsuaris(usuarisCodi, null, null, filtre.asDto(), tipusAgrupament));

	}

	@RequestMapping(value = "/usuaris/actual", method = RequestMethod.GET, produces = "application/json")
	@Operation(
			summary = "Consulta l'històric d'ús de l'aplicació del dia d'avui pels distints usuaris de l'aplicació",
			description = "Retorna els històrics del dia d'avui per a cada usuari consultat ",
			tags = "HistoricsUsuaris")
	@ResponseBody
	public Map<String, List<HistoricUsuariDto>> getHistoricsActualsByUsuari(
			HttpServletRequest request,
			
			@Parameter(name = "organGestorsIds", description = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@Parameter(name = "metaExpedientsIds", description = "Procediments dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@Parameter(name = "incorporarExpedientsComuns",
					description = "Indica si la consulta ha d'incorporar les dades dels expedients comuns",
					required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns,
			
			@Parameter(name = "usuaris", description = "Codi dels usuaris que es volen consultar", required = true)
			@RequestParam(value = "usuaris", required = true) List<String> usuarisCodi) {

		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(null, null, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, null);
		
		// Check params
//		for (Long organId : filtre.getOrganGestorsIds()) {
//			organGestorService.findItem(organId); // Throw exception when item not found
//		}
//		
//		for (String userCode : usuarisCodi) {
//			if (aplicacioService.findUsuariAmbCodi(userCode) == null) {
//				throw new NotFoundException(userCode, UsuariDto.class);
//			}
//		}
		
		// Perform query
		Map<String, List<HistoricUsuariDto>> results = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			results.put(codiUsuari, historicService.getDadesActualsUsuari(null, null, codiUsuari, filtre.asDto()));
		}

		return results;
	}


	/*
	 * DADES PER INTERESSAT
	*/
	
	@RequestMapping(value = "/interessats", method = RequestMethod.GET, produces = "application/json")
	@Operation(
			summary = "Consulta l'històric d'ús de l'aplicació pels distints interessats de l'aplicació",
			description = "Retorna els històrics agrupats per data segons el tipus d'agrupament per a cada interessat consultat ",
			tags = "HistoricsIteressats")
	@ResponseBody
	public HistoricApiResponse getHistoricsByInteressat(
			HttpServletRequest request,
			
			@Parameter(name = "dataInici", description = "Data inicial a consultar", required = false)
			@RequestParam(value = "dataInici", required = false) Date dataInici,
			
			@Parameter(name = "dataFi", description = "Data final a consultar", required = false)
			@RequestParam(value = "dataFi", required = false) Date dataFi,
			
			@Parameter(name = "organGestorsIds", description = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@Parameter(name = "metaExpedientsIds", description = "Procediments dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@Parameter(name = "incorporarExpedientsComuns",
					description = "Indica si la consulta ha d'incorporar les dades dels expedients comuns",
					required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns,
			
			@Parameter(name = "tipusAgrupament", description = "Tipus d'agrupament, per dia(1)/mes(0)", required = false)
			@RequestParam(value = "tipusAgrupament", required = false) HistoricTipusEnumDto tipusAgrupament,
			
			@Parameter(name = "interessats", description = "Codi dels interessats que es volen consultar", required = true)
			@RequestParam("interessats") List<String> interessatsDocNum) {

		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(dataInici, dataFi, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, tipusAgrupament);
		
		// Check params
//		for (Long organId : filtre.getOrganGestorsIds()) {
//			organGestorService.findItem(organId); // Throw exception when item not found
//		}
		
		// TODO: fa falta un servei per interessat
//		for (String interessatDoc : interessatsDocNum) {
//			if (aplicacioService.findUsuariAmbCodi(userCode) == null) {
//				throw new NotFoundException(userCode, UsuariDto.class);
//			}
//		}
		
		// Perform query
//		Map<String, List<HistoricInteressatDto>> results = new HashMap<String, List<HistoricInteressatDto>>();
//		for (String docNum : interessatsDocNum) {
//			List<HistoricInteressatDto> historics = historicService.getDadesInteressat(
//					null,
//					null,
//					docNum,
//					filtre.asDto());
//			results.put(docNum, historics);
//		}
//
//		return new HistoricApiResponse(filtre.asDto(), DAOHistoric.mapRegistresInteressats(results, tipusAgrupament).registres);
		return new HistoricApiResponse(filtre.asDto(), historicService.getRegistresDadesInteressat(interessatsDocNum, null, null, filtre.asDto(), tipusAgrupament));
	}

	@RequestMapping(value = "/interessats/actual", method = RequestMethod.GET, produces = "application/json")
	@Operation(
			summary = "Consulta l'històric d'ús de l'aplicació del dia d'avui pels distints interessats de l'aplicació",
			description = "Retorna els històrics del dia d'avui per a cada interessat consultat ",
			tags = "HistoricsIteressats")
	@ResponseBody
	public Map<String, List<HistoricInteressatDto>> getHistoricsActualsByInteressat(
			HttpServletRequest request,
			
			@Parameter(name = "organGestorsIds", description = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@Parameter(name = "metaExpedientsIds", description = "Procediments dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@Parameter(name = "incorporarExpedientsComuns",
					description = "Indica si la consulta ha d'incorporar les dades dels expedients comuns",
					required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns,
			
			@Parameter(name = "interessats", description = "Codi dels interessats que es volen consultar", required = true)
			@RequestParam("interessats") List<String> interessatsDocNum) {

		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(null, null, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, null);
		
		Map<String, List<HistoricInteressatDto>> results = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			results.put(docNum, historicService.getDadesActualsInteressat(null, null, docNum, filtre.asDto()));
		}
		return results;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class,
				new CustomDateEditor(
						new SimpleDateFormat("dd/MM/yyyy"),
						true));
	}

}
