/**
 *
 */
package es.caib.ripea.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.ripea.core.api.dto.PortafirmesFluxInfoDto;
import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.dto.PortafirmesIniciFluxRespostaDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.PortafirmesFluxService;
import es.caib.ripea.core.helper.PluginHelper;

/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class PortafirmesFluxServiceImpl implements PortafirmesFluxService {

	@Autowired
	PluginHelper pluginHelper;
	@Autowired
	AplicacioService aplicacioService;
	
	@Override
	public PortafirmesIniciFluxRespostaDto iniciarFluxFirma(
			String urlReturn,
			String tipusDocumentNom,
			boolean isPlantilla) {
		logger.debug("(Iniciant flux de firma (" +
				"urlRedireccio=" + urlReturn + "," +
				"tipusDocumentNom=" + tipusDocumentNom + ")");
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		
		PortafirmesIniciFluxRespostaDto transaccioResponse = pluginHelper.portafirmesIniciarFluxDeFirma(
				idioma,
				isPlantilla,
				tipusDocumentNom + "_plantilla_flux",
				tipusDocumentNom + "_plantilla_flux_desc",
				true,
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
	public PortafirmesFluxInfoDto recuperarDetallFluxFirma(String plantillaFluxId) {
		logger.debug("Recuperant detall flux de firma (" +
				"plantillaFluxId=" + plantillaFluxId +")");
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		return pluginHelper.portafirmesRecuperarInfoFluxDeFirma(
				plantillaFluxId, 
				idioma);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(PortafirmesFluxServiceImpl.class);
}