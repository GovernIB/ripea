package es.caib.ripea.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.OrganGestorDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.dto.historic.HistoricExpedientDto;
import es.caib.ripea.core.api.dto.historic.HistoricInteressatDto;
import es.caib.ripea.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.ripea.core.api.dto.historic.HistoricUsuariDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.HistoricService;
import es.caib.ripea.core.api.service.OrganGestorService;
import es.caib.ripea.war.command.HistoricFiltreCommand;
import es.caib.ripea.war.historic.DAOHistoric;
import es.caib.ripea.war.historic.DAOHistoric.RegistreOrganGestor;
import es.caib.ripea.war.historic.DAOHistoric.RootEntitat;
import es.caib.ripea.war.historic.DAOHistoric.RootInteressats;
import es.caib.ripea.war.historic.DAOHistoric.RootOrganGestors;
import es.caib.ripea.war.historic.DAOHistoric.RootUsuaris;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("api/historic")
@Api(value="/rest/historic", description="API de consulta dels històrics d'ús de l'aplicació")
public class ApiHistoricController extends BaseAdminController {

	@Autowired
	private HistoricService historicService;

	@Autowired
	private OrganGestorService organGestorService;

	@Autowired
	private AplicacioService aplicacioService;
	
	@RequestMapping(value = "/generate", method = RequestMethod.GET, produces = "application/json")
	public String generate() {
		historicService.generateOldHistorics();
		return "";
	}
	/*
	 * DADES PER ENTITAT
	*/
	
	@RequestMapping(value = "/entitat/{entitatId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RootEntitat expedientsEntitatChartData(
			HttpServletRequest request,
			@ApiParam(name = "entitatId", value = "Identificador de l'entitat a consultar", required = true)
			@PathVariable(value = "entitatId") Long entitatId,
			
			@ApiParam(name = "dataInici", value = "Data inicial a consultar", required = false)
			@RequestParam(value = "dataInici", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataInici,
			
			@ApiParam(name = "dataFi", value = "Data final a consultar", required = false)
			@RequestParam(value = "dataFi", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataFi,
			
			@ApiParam(name = "organGestorsIds", value = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) 
			List<Long> organGestorsIds,
			
			@ApiParam(name = "metaExpedientsIds", value = "Tipus d'expedients dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) 
			List<Long> metaExpedientsIds,
			
			@ApiParam(name = "incorporarExpedientsComuns", 
					  value = "Indica si la consulta ha d'incorporar les dades dels expedients comuns", 
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) 
			Boolean incorporarExpedientsComuns,
			
			@ApiParam(name = "tipusAgrupament", value = "Tipus d'agrupament, DIARI/MENSUAL", required = true)
			@RequestParam(value = "tipusAgrupament", required = true) HistoricTipusEnumDto tipusAgrupament
			) {
		log.debug("uep com anam?");
//		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(dataInici, dataFi, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, tipusAgrupament);

		List<HistoricExpedientDto> response = historicService.getDadesEntitat(
				entitatId,
				filtre.asDto());
		return DAOHistoric.mapRegistresEntitat(response);
	}

	@RequestMapping(value = "/entitat/{entitatId}/actuals", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<HistoricExpedientDto> getHistoricExpedientActual(
			HttpServletRequest request,
			@ApiParam(name = "entitatId", value = "Identificador de l'entitat a consultar", required = true)
			@PathVariable(value = "entitatId") Long entitatId,
						
			@ApiParam(name = "organGestorsIds", value = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@ApiParam(name = "metaExpedientsIds", value = "Tipus d'expedients dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@ApiParam(name = "incorporarExpedientsComuns", 
					  value = "Indica si la consulta ha d'incorporar les dades dels expedients comuns", 
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns
			) {
//		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(null, null, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, null);
		List<HistoricExpedientDto> response = historicService.getDadesActualsEntitat(
				entitatId,
				filtre.asDto());
		return response;
	}

	/*
	 * DADES PER ORGAN GESTOR
	*/
	
	@RequestMapping(value = "/organgestors", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(
			value = "Consulta totes les comunicacions d'un titular donat el seu dni",
			notes = "Retorna informació de totes les comunicacions d'un titular, i el seu estat",
			position = 0,
			response = RootOrganGestors.class,
			produces = "application/json",
			tags = "OrgansGestors")
	@ResponseBody
	public RootOrganGestors getHistoricsByOrganGestor(
			HttpServletRequest request,
			
			@ApiParam(name = "dataInici", value = "Data inicial a consultar", required = false)
			@RequestParam(value = "dataInici", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataInici,
			
			@ApiParam(name = "dataFi", value = "Data final a consultar", required = false)
			@RequestParam(value = "dataFi", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataFi,
			
			@ApiParam(name = "organGestorsIds", value = "Òrgans gestors dels quals consultar dades", required = true)
			@RequestParam(value = "organGestorsIds", required = true) 
			List<Long> organGestorsIds,
			
			@ApiParam(name = "metaExpedientsIds", value = "Tipus d'expedients dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) 
			List<Long> metaExpedientsIds,
			
			@ApiParam(name = "incorporarExpedientsComuns", 
					  value = "Indica si la consulta ha d'incorporar les dades dels expedients comuns", 
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) 
			Boolean incorporarExpedientsComuns,
			
			@ApiParam(name = "tipusAgrupament", value = "Tipus d'agrupament, DIARI/MENSUAL", required = false)
			@RequestParam(value = "tipusAgrupament", required = false) 
			
			HistoricTipusEnumDto tipusAgrupament) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);

		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(dataInici, dataFi, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, tipusAgrupament);

		// Check params
		for (Long organId : filtre.getOrganGestorsIds()) {
			organGestorService.findItem(organId); // Throw exception when item not found
		}
		
		// Perform query
		Map<Date, Map<OrganGestorDto, HistoricExpedientDto>> dades = historicService.getDadesOrgansGestors(
				filtre.asDto());

		return DAOHistoric.mapRegistreOrganGestor(dades);
	}


	
	@RequestMapping(value = "/organgestors/actual", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<RegistreOrganGestor> getDadesActualsOrgansGestors(
			HttpServletRequest request,

			@ApiParam(name = "organGestorsIds", value = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@ApiParam(name = "metaExpedientsIds", value = "Tipus d'expedients dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@ApiParam(name = "incorporarExpedientsComuns", 
					  value = "Indica si la consulta ha d'incorporar les dades dels expedients comuns", 
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns) {

		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(null, null, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, null);

		// Check params
		for (Long organId : filtre.getOrganGestorsIds()) {
			organGestorService.findItem(organId); // Throw exception when item not found
		}
		
		// Perform query
		Map<OrganGestorDto, HistoricExpedientDto> dades = historicService.getDadesActualsOrgansGestors(
				filtre.asDto());

		return DAOHistoric.mapRegistresActualsOrganGestors(dades);
	}

	/*
	 * DADES PER USUARI
	*/
	
	@RequestMapping(value = "/usuaris", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RootUsuaris usuarisData(
			HttpServletRequest request,
			
			@ApiParam(name = "dataInici", value = "Data inicial a consultar", required = false)
			@RequestParam(value = "dataInici", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataInici,
			
			@ApiParam(name = "dataFi", value = "Data final a consultar", required = false)
			@RequestParam(value = "dataFi", required = false) 
			@DateTimeFormat(pattern="dd/MM/yyyy")
			Date dataFi,
			
			@ApiParam(name = "organGestorsIds", value = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@ApiParam(name = "metaExpedientsIds", value = "Tipus d'expedients dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@ApiParam(name = "incorporarExpedientsComuns", 
					  value = "Indica si la consulta ha d'incorporar les dades dels expedients comuns", 
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns,
			
			@ApiParam(name = "tipusAgrupament", value = "Tipus d'agrupament, per dia(1)/mes(0)", required = false)
			@RequestParam(value = "tipusAgrupament", required = false) HistoricTipusEnumDto tipusAgrupament,
			
			@ApiParam(name = "usuaris", value = "Codi dels usuaris que es volen consultar", required = true)
			@RequestParam(value = "usuaris", required = true) List<String> usuarisCodi) {

		getEntitatActualComprovantPermisAdminEntitat(request);
		
		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(dataInici, dataFi, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, tipusAgrupament);
		
		// Check params
		for (Long organId : filtre.getOrganGestorsIds()) {
			organGestorService.findItem(organId); // Throw exception when item not found
		}
		
		for (String userCode : usuarisCodi) {
			if (aplicacioService.findUsuariAmbCodi(userCode) == null) {
				throw new NotFoundException(userCode, UsuariDto.class);
			}
		}
		
		// Perform query
		Map<String, List<HistoricUsuariDto>> results = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			results.put(codiUsuari, historicService.getDadesUsuari(codiUsuari, filtre.asDto()));
		}

		return DAOHistoric.mapRegistresUsuaris(results);
	}

	@RequestMapping(value = "/usuaris/actual", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, List<HistoricUsuariDto>> usuarisDataActual(
			HttpServletRequest request,
			
			@ApiParam(name = "organGestorsIds", value = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@ApiParam(name = "metaExpedientsIds", value = "Tipus d'expedients dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@ApiParam(name = "incorporarExpedientsComuns", 
					  value = "Indica si la consulta ha d'incorporar les dades dels expedients comuns", 
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns,
			
			@ApiParam(name = "usuaris", value = "Codi dels usuaris que es volen consultar", required = true)
			@RequestParam(value = "usuaris", required = true) List<String> usuarisCodi) {

		getEntitatActualComprovantPermisAdminEntitat(request);

		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(null, null, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, null);
		
		// Check params
		for (Long organId : filtre.getOrganGestorsIds()) {
			organGestorService.findItem(organId); // Throw exception when item not found
		}
		
		for (String userCode : usuarisCodi) {
			if (aplicacioService.findUsuariAmbCodi(userCode) == null) {
				throw new NotFoundException(userCode, UsuariDto.class);
			}
		}
		
		// Perform query
		Map<String, List<HistoricUsuariDto>> results = new HashMap<String, List<HistoricUsuariDto>>();
		for (String codiUsuari : usuarisCodi) {
			results.put(codiUsuari, historicService.getDadesActualsUsuari(codiUsuari, filtre.asDto()));
		}

		return results;
	}


	/*
	 * DADES PER INTERESSAT
	*/
	
	@RequestMapping(value = "/interessats", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RootInteressats interessatsData(
			HttpServletRequest request,
			
			@ApiParam(name = "dataInici", value = "Data inicial a consultar", required = false)
			@RequestParam(value = "dataInici", required = false) Date dataInici,
			
			@ApiParam(name = "dataFi", value = "Data final a consultar", required = false)
			@RequestParam(value = "dataFi", required = false) Date dataFi,
			
			@ApiParam(name = "organGestorsIds", value = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@ApiParam(name = "metaExpedientsIds", value = "Tipus d'expedients dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@ApiParam(name = "incorporarExpedientsComuns", 
					  value = "Indica si la consulta ha d'incorporar les dades dels expedients comuns", 
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns,
			
			@ApiParam(name = "tipusAgrupament", value = "Tipus d'agrupament, per dia(1)/mes(0)", required = false)
			@RequestParam(value = "tipusAgrupament", required = false) HistoricTipusEnumDto tipusAgrupament,
			
			@ApiParam(name = "interessats", value = "Codi dels interessats que es volen consultar", required = true)
			@RequestParam("interessats") List<String> interessatsDocNum) {

		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		
		// Load parameters inside filtreCommand object 
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(dataInici, dataFi, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, tipusAgrupament);
		
		// Check params
		for (Long organId : filtre.getOrganGestorsIds()) {
			organGestorService.findItem(organId); // Throw exception when item not found
		}
		
		// TODO: fa falta un servei per interessat
//		for (String interessatDoc : interessatsDocNum) {
//			if (aplicacioService.findUsuariAmbCodi(userCode) == null) {
//				throw new NotFoundException(userCode, UsuariDto.class);
//			}
//		}
		
		// Perform query
		Map<String, List<HistoricInteressatDto>> results = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			List<HistoricInteressatDto> historics = historicService.getDadesInteressat(
					docNum,
					filtre.asDto());
			results.put(docNum, historics);
		}

		return DAOHistoric.mapRegistresInteressats(results);
	}

	@RequestMapping(value = "/interessats/actual", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, List<HistoricInteressatDto>> interessatsDataActual(
			HttpServletRequest request,
			
			@ApiParam(name = "organGestorsIds", value = "Òrgans gestors dels quals consultar dades", required = false)
			@RequestParam(value = "organGestorsIds", required = false) List<Long> organGestorsIds,
			
			@ApiParam(name = "metaExpedientsIds", value = "Tipus d'expedients dels quals consultar dades", required = false)
			@RequestParam(value = "metaExpedientsIds", required = false) List<Long> metaExpedientsIds,
			
			@ApiParam(name = "incorporarExpedientsComuns", 
					  value = "Indica si la consulta ha d'incorporar les dades dels expedients comuns", 
					  required = false)
			@RequestParam(value = "incorporarExpedientsComuns", required = false) Boolean incorporarExpedientsComuns,
			
			@ApiParam(name = "interessats", value = "Codi dels interessats que es volen consultar", required = true)
			@RequestParam("interessats") List<String> interessatsDocNum) {

		EntitatDto entitat = getEntitatActualComprovantPermisAdminEntitat(request);
		HistoricFiltreCommand filtre = new HistoricFiltreCommand();
		filtre.updateConditional(null, null, organGestorsIds, metaExpedientsIds, incorporarExpedientsComuns, null);
		
		Map<String, List<HistoricInteressatDto>> results = new HashMap<String, List<HistoricInteressatDto>>();
		for (String docNum : interessatsDocNum) {
			results.put(docNum, historicService.getDadesActualsInteressat(docNum, filtre.asDto()));
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
