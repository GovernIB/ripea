/**
 *
 */
package es.caib.ripea.core.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.ripea.core.api.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.MetaDocumentFluxService;
import es.caib.ripea.core.helper.PluginHelper;

/**
 * Implementació del servei de gestió de meta-documents.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MetaDocumentFluxServiceImpl implements MetaDocumentFluxService {

	@Autowired
	PluginHelper pluginHelper;
	@Autowired
	AplicacioService aplicacioService;
	
	@Override
	public Map<String, String> iniciarFluxFirma(
			String urlReturn,
			String tipusDocumentNom) {
		logger.info("Iniciant flux de firma");
		String idioma = aplicacioService.getUsuariActual().getIdioma();
		
		Map<String, String> transaccioResponse = pluginHelper.portafirmesIniciarFluxDeFirma(
				idioma,
				true,
				tipusDocumentNom + "_plantilla_flux",
				tipusDocumentNom + "_plantilla_flux_desc",
				false,
				urlReturn);
		
		return transaccioResponse;
	}
	
	@Override
	public PortafirmesFluxRespostaDto recuperarFluxFirma(String transaccioId) {
		logger.info("Recuperant flux de firma");
		return pluginHelper.portafirmesRecuperarFluxDeFirma(transaccioId);
	}
	
	@Override
	public void tancarTransaccio(String idTransaccio) {
		pluginHelper.portafirmesTancarFluxDeFirma(idTransaccio);
	}

	private static final Logger logger = LoggerFactory.getLogger(MetaDocumentFluxServiceImpl.class);
}