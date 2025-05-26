package es.caib.ripea.service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.FluxFirmaUsuariEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.FluxFirmaUsuariRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.dto.PortafirmesCarrecDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.PortafirmesFluxService;

@Service
public class PortafirmesFluxServiceImpl implements PortafirmesFluxService {

	@Autowired private PluginHelper pluginHelper;
	@Autowired private AplicacioService aplicacioService;
	@Autowired private FluxFirmaUsuariRepository fluxFirmaUsuariRepository;
	@Autowired private UsuariRepository usuariRepository;
	
	@Override
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn,
			boolean isPlantilla) throws SistemaExternException {
		logger.debug("(Iniciant flux de firma (urlRedireccio=" + urlReturn + ")");
		
		PortafirmesIniciFluxRespostaDto transaccioResponse = pluginHelper.portafirmesIniciarFluxDeFirma(
				isPlantilla,
				urlReturn);
		
		return transaccioResponse;
	}
	
	@Override
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String idTransaccio) {
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
		return pluginHelper.portafirmesRecuperarPlantillesDisponibles(entitatId, filtrar);
	}
	
	@Transactional
	@Override
	public boolean esborrarPlantilla(String plantillaFluxId) {
		logger.debug("Esborrant la plantilla amb id=" + plantillaFluxId);
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		boolean resposta = pluginHelper.portafirmesEsborrarPlantillaFirma(idioma, plantillaFluxId);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity usuari = usuariRepository.getOne(auth.getName());
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
