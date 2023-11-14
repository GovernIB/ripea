/**
 *
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.core.api.dto.PortafirmesCarrecDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.exception.SistemaExternException;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.PortafirmesFluxService;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.FluxFirmaUsuariEntity;
import es.caib.ripea.core.entity.UsuariEntity;
import es.caib.ripea.core.helper.EntityComprovarHelper;
import es.caib.ripea.core.helper.PluginHelper;
import es.caib.ripea.core.repository.FluxFirmaUsuariRepository;
import es.caib.ripea.core.repository.UsuariRepository;

/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PortafirmesFluxServiceImpl implements PortafirmesFluxService {

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private FluxFirmaUsuariRepository fluxFirmaUsuariRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	
	@Override
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn,
			boolean isPlantilla) throws SistemaExternException {
		logger.debug("(Iniciant flux de firma (" +
				"urlRedireccio=" + urlReturn + ")");
		
		UsuariDto usuariDto = aplicacioService.getUsuariActual();
		String idioma = usuariDto.getIdioma();
		String usuariCodi = usuariDto.getCodi();
		
		Boolean filtrarPerUsuariActual = aplicacioService.propertyBooleanFindByKey("es.caib.ripea.plugin.portafirmes.flux.filtrar.usuari.descripcio");
		boolean saveUserActual = false;
		if (filtrarPerUsuariActual == null || filtrarPerUsuariActual.equals(true)) {
			saveUserActual = true;
		}
		
		PortafirmesIniciFluxRespostaDto transaccioResponse = pluginHelper.portafirmesIniciarFluxDeFirma(
				idioma,
				isPlantilla,
				null,
				saveUserActual ? "user=" + usuariCodi : null,
				!saveUserActual,
				urlReturn);
		
		return transaccioResponse;
	}
	
	@Override
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String idTransaccio) {
		logger.debug("(Recuperant flux de firma (" + 
				"idTransaccio=" + idTransaccio +")");
		return pluginHelper.portafirmesRecuperarFluxDeFirma(idTransaccio);
	}
	
	@Override
	public void tancarTransaccio(String idTransaccio) {
		logger.debug("(Tancant flux de firma (" + 
				"idTransaccio=" + idTransaccio +")");
		pluginHelper.portafirmesTancarFluxDeFirma(idTransaccio);
	}

	@Override
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String plantillaFluxId, boolean signerInfo) {
		logger.debug("Recuperant detall flux de firma (" +
				"plantillaFluxId=" + plantillaFluxId +")");
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		return pluginHelper.portafirmesRecuperarInfoFluxDeFirma(
				plantillaFluxId, 
				idioma,
				signerInfo);
	}
	
	@Override
	public String recuperarUrlMostrarPlantilla(String plantillaFluxId) {
		logger.debug("Recuperant url visualització plantilla (" +
				"plantillaId=" + plantillaFluxId +")");
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		return pluginHelper.portafirmesRecuperarUrlPlantilla(
				plantillaFluxId, 
				idioma,
				null,
				false);
	}

	@Override
	public String recuperarUrlEdicioPlantilla(
			String plantillaFluxId,
			String returnUrl) {
		logger.debug("Recuperant url edició plantilla (" +
				"plantillaId=" + plantillaFluxId +")");
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		return pluginHelper.portafirmesRecuperarUrlPlantilla(
				plantillaFluxId, 
				idioma,
				returnUrl,
				true);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<PortafirmesFluxRespostaDto> recuperarPlantillesDisponibles(Long entitatId, String rolActual, boolean filtrar) {
		logger.debug("Recuperant plantilles disponibles per l'usuari aplicació");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false,
				false, 
				false);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		
		List<PortafirmesFluxRespostaDto> plantillesFiltrades = new ArrayList<PortafirmesFluxRespostaDto>();
		List<PortafirmesFluxRespostaDto> plantilles = pluginHelper.portafirmesRecuperarPlantillesDisponibles(aplicacioService.getUsuariActual(), filtrar);
		
		List<FluxFirmaUsuariEntity> plantillesUsuari = fluxFirmaUsuariRepository.findByEntitat(entitat);

		for (PortafirmesFluxRespostaDto plantilla : plantilles) {
			boolean isCurrentUserTemplate = false;
			boolean isUserTemplate = false;

			for (FluxFirmaUsuariEntity fluxFirmaUsuari : plantillesUsuari) {
				if (plantilla.getFluxId().equals(fluxFirmaUsuari.getPortafirmesFluxId())
						&& fluxFirmaUsuari.getUsuari().equals(usuari)) {
					// Plantilla usuari actual
					isCurrentUserTemplate = true;
					break;
				} else if (plantilla.getFluxId().equals(fluxFirmaUsuari.getPortafirmesFluxId())
						&& !isCurrentUserTemplate) {
					// Plantilla d'un altre usuari (no mostrar al llistat)
					isUserTemplate = true;
					break;
				}
			}

			// Plantilles usuari actual i plantilles comuns
			if (isCurrentUserTemplate
					|| (!isCurrentUserTemplate && !plantillesFiltrades.contains(plantilla)) && !isUserTemplate) {
				plantilla.setUsuariActual(isCurrentUserTemplate);
				plantillesFiltrades.add(plantilla);
			}
		}
		
		return plantillesFiltrades;
	}
	
	@Transactional
	@Override
	public boolean esborrarPlantilla(String plantillaFluxId) {
		logger.debug("Esborrant la plantilla amb id=" + plantillaFluxId);
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		boolean resposta = pluginHelper.portafirmesEsborrarPlantillaFirma(idioma, plantillaFluxId);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuari = usuariRepository.findOne(auth.getName());
		FluxFirmaUsuariEntity entity = fluxFirmaUsuariRepository.findByUsuariAndPortafirmesFluxId(usuari, plantillaFluxId);
		
		if (entity != null) {
			fluxFirmaUsuariRepository.delete(entity);
		}
		
		return resposta;
	}

	@Override
	public List<PortafirmesCarrecDto> recuperarCarrecs() {
		logger.debug("Recuperant els càrrecs disponibles");
		return pluginHelper.portafirmesRecuperarCarrecs();
	}
	
//	private String generarNomFlux(String documentNom) {		
//		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
//		Date date = new Date();
//		documentNom = documentNom.replace(" ", "_");
//		
//		String nomFlux = "Flux_" + documentNom + "_" + dateFormat.format(date);
//		return nomFlux;
//	}
	
	private static final Logger logger = LoggerFactory.getLogger(PortafirmesFluxServiceImpl.class);


}