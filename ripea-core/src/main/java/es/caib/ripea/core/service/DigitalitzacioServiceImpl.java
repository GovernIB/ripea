/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.ripea.core.api.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.core.api.dto.DigitalitzacioResultatDto;
import es.caib.ripea.core.api.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.DigitalitzacioService;
import es.caib.ripea.core.helper.PluginHelper;

/**
 * Implementació del servei de gestió de meta-dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DigitalitzacioServiceImpl implements DigitalitzacioService {

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private AplicacioService aplicacioService;
	
	@Override
	public List<DigitalitzacioPerfilDto> getPerfilsDisponibles() {
		logger.debug("Recuperant perfils disponibles");
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		
		if (idioma != null)
			idioma = idioma.toLowerCase();
		
		List<DigitalitzacioPerfilDto> perfilsDisponibles = pluginHelper.digitalitzacioPerfilsDisponibles(idioma);
		return perfilsDisponibles;
	}
	
	@Override
	public DigitalitzacioTransaccioRespostaDto iniciarDigitalitzacio(
			String codiPerfil,
			String urlReturn) {
		logger.debug("Iniciant el procés d'escaneig");
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		String idioma = usuariActual.getIdioma();
		
		if (idioma != null)
			idioma = idioma.toLowerCase();
		
		DigitalitzacioTransaccioRespostaDto respostaDto = pluginHelper.digitalitzacioIniciarProces(
				idioma, 
				codiPerfil, 
				usuariActual, 
				urlReturn);
		return respostaDto;
	}
	
	@Override
	public DigitalitzacioResultatDto recuperarResultat(
			String idTransaccio,
			boolean returnScannedFile,
			boolean returnSignedFile) {
		logger.debug("Recuperant resultat escaneig");
		DigitalitzacioResultatDto resultat = pluginHelper.digitalitzacioRecuperarResultat(
				idTransaccio, 
				returnScannedFile, 
				returnSignedFile);
		return resultat;
	}
	
	@Override
	public void tancarTransaccio(
			String idTransaccio) {
		logger.debug("Tancant la transacció: " + idTransaccio);
		pluginHelper.digitalitzacioTancarTransaccio(idTransaccio);
	}


	private static final Logger logger = LoggerFactory.getLogger(DigitalitzacioServiceImpl.class);




}