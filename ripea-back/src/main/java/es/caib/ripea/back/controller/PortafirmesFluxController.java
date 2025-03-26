
/**
 * 
 */
package es.caib.ripea.back.controller;

import es.caib.ripea.back.helper.RequestSessionHelper;
import es.caib.ripea.back.helper.RolHelper;
import es.caib.ripea.back.helper.SessioHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controlador per definir fluxos de firma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/document")
public class PortafirmesFluxController extends BaseUserOAdminOOrganController {

	private static final String SESSION_ATTRIBUTE_TRANSACCIOID = "DocumentController.session.transaccioID";

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PortafirmesFluxService portafirmesFluxService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private FluxFirmaUsuariService fluxFirmaUsuariService;
	
	@RequestMapping(value = "/portafirmes/iniciarTransaccio", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto iniciarTransaccio(
			HttpServletRequest request,
			@RequestParam(value="nom", required = false) String nom,
			@RequestParam(value="isPlantilla", required = false) Boolean isPlantilla,
			Model model) {
		organGestorService.actualitzarOrganCodi(SessioHelper.getOrganActual(request));
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;

		String urlReturn = aplicacioService.propertyBaseUrl() + "/document/portafirmes/flux/returnurl/";
		try {
			transaccioResponse = portafirmesFluxService.iniciarFluxFirma(
					urlReturn,
					isPlantilla == null ? false : isPlantilla);
		} catch (Exception ex) {
			logger.error("Error al iniciar transacio", ex);
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
		return transaccioResponse;
	}
	
	@RequestMapping(value = "/portafirmes/flux/mostrar", method = RequestMethod.GET)
	@ResponseBody
	public PortafirmesIniciFluxRespostaDto mostrarFlux(
			HttpServletRequest request,
			@RequestParam(value = "plantillaId", required = false) String plantillaId,
			Model model) {
		organGestorService.actualitzarOrganCodi(SessioHelper.getOrganActual(request));
		PortafirmesIniciFluxRespostaDto transaccioResponse = null;
		try {
			if (plantillaId != null && !plantillaId.isEmpty()) {
				transaccioResponse = new PortafirmesIniciFluxRespostaDto();
				String urlEdicio = portafirmesFluxService.recuperarUrlMostrarPlantilla(plantillaId);
				transaccioResponse.setUrlRedireccio(urlEdicio);
			}
		} catch (Exception ex) {
			transaccioResponse = new PortafirmesIniciFluxRespostaDto();
			transaccioResponse.setError(true);
			transaccioResponse.setErrorDescripcio(ex.getMessage());
		}
		return transaccioResponse;
	}
	
	@RequestMapping(value = "/portafirmes/tancarTransaccio/{idTransaccio}", method = RequestMethod.GET)
	@ResponseBody
	public void tancarTransaccio(
			HttpServletRequest request,
			@PathVariable String idTransaccio,
			Model model) {
		portafirmesFluxService.tancarTransaccio(idTransaccio);
	}

	@RequestMapping(value = "/portafirmes/flux/returnurl/{transactionId}", method = RequestMethod.GET)
	public String transaccioEstat(
			HttpServletRequest request,
			@PathVariable String transactionId,
			Model model) {
		organGestorService.actualitzarOrganCodi(SessioHelper.getOrganActual(request));
		PortafirmesFluxRespostaDto resposta = portafirmesFluxService.recuperarFluxFirma(transactionId);
		
		if (resposta.isError() && resposta.getEstat() != null) {
			model.addAttribute(
						"FluxError",
						getMessage(
						request,
						"metadocument.form.camp.portafirmes.flux.enum." + resposta.getEstat()));
		} else {
			if (resposta.getFluxId() != null) {
				EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
				FluxFirmaUsuariDto flux = new FluxFirmaUsuariDto();
				flux.setNom(resposta.getNom());
				flux.setDescripcio(resposta.getDescripcio());
				flux.setPortafirmesFluxId(resposta.getFluxId());
				fluxFirmaUsuariService.create(
						entitatActual.getId(), 
						flux,
						null);
			}
			model.addAttribute(
					"FluxCreat",
					getMessage(
					request,
					"metadocument.form.camp.portafirmes.flux.enum." + resposta.getEstat()));
			model.addAttribute(
					"FluxNom", resposta.getNom());
			model.addAttribute(
					"FluxDescripcio", resposta.getDescripcio());
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_TRANSACCIOID,
					transactionId);
		}
		return "portafirmesModalTancar";
	}
	
	
	
	@RequestMapping(value = "/{documentId}/portafirmes/flux/plantilles", method = RequestMethod.GET)
	@ResponseBody
	public List<PortafirmesFluxRespostaDto> getPlantillesDisponibles(HttpServletRequest request, @PathVariable Long documentId, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		organGestorService.actualitzarOrganCodi(organGestorService.getOrganCodiFromContingutId(documentId));
		List<PortafirmesFluxRespostaDto> resposta;
		
		Boolean filtrarPerUsuariActual = aplicacioService.propertyBooleanFindByKey(PropertyConfig.FILTRAR_USUARI_DESCRIPCIO);
		if (filtrarPerUsuariActual == null || filtrarPerUsuariActual.equals(true)) {
			
			resposta = portafirmesFluxService.recuperarPlantillesDisponibles(entitatActual.getId(), RolHelper.getRolActual(request), true);
			String fluxPerDefecteId = documentService.findById(entitatActual.getId(), documentId, null).getMetaDocument().getPortafirmesFluxId();
			if (fluxPerDefecteId != null && !fluxPerDefecteId.isEmpty()) {
				PortafirmesFluxInfoDto portafirmesFluxInfoDto = portafirmesFluxService.recuperarDetallFluxFirma(fluxPerDefecteId, false);
				
				boolean isAlreadyOnList = false;
				for (PortafirmesFluxRespostaDto respostaDto : resposta) {
					if (respostaDto.getFluxId().equals(fluxPerDefecteId)) {
						isAlreadyOnList = true;
					}
				}
				if (!isAlreadyOnList) {
					PortafirmesFluxRespostaDto portafirmesFluxRespostaDto = new PortafirmesFluxRespostaDto();
					portafirmesFluxRespostaDto.setFluxId(fluxPerDefecteId);
					portafirmesFluxRespostaDto.setNom(portafirmesFluxInfoDto.getNom());
					resposta.add(0, portafirmesFluxRespostaDto);
				}
			}
		} else {
			resposta = portafirmesFluxService.recuperarPlantillesDisponibles(entitatActual.getId(), RolHelper.getRolActual(request), false);
		}

		
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(PortafirmesFluxController.class);
}