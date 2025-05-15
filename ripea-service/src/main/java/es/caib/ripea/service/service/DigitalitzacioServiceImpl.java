package es.caib.ripea.service.service;

import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.dto.DigitalitzacioPerfilDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;
import es.caib.ripea.service.intf.dto.DigitalitzacioTransaccioRespostaDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.DigitalitzacioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DigitalitzacioServiceImpl implements DigitalitzacioService {

	@Autowired private PluginHelper pluginHelper;
	@Autowired private AplicacioService aplicacioService;
	
	@Override
	public List<DigitalitzacioPerfilDto> getPerfilsDisponibles() {
		logger.debug("Recuperant perfils disponibles");
		return pluginHelper.digitalitzacioPerfilsDisponibles();
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
