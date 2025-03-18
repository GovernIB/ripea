/**
 * 
 */
package es.caib.ripea.plugin.caib.firmaservidor;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.caib.ripea.plugin.firmaservidor.SignaturaResposta;
import org.apache.commons.io.IOUtils;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.firmaservidor.FirmaServidorPlugin;

/**
 * Implementació del plugin de firma en servidor emprant PortaFIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaServidorPluginMock extends RipeaAbstractPluginProperties implements FirmaServidorPlugin {

	public FirmaServidorPluginMock() {
		super();
	}
	public FirmaServidorPluginMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	@Override
	public SignaturaResposta firmar(
			String nom,
			String motiu,
			byte[] contingut,
			String idioma, 
			String contentType) throws SistemaExternException {
		if (motiu != null && "e".equals(motiu)) {
			// Cas per provocar una excepció
			String errMsg = "Excepció provocada per paràmetre a SignaturaPluginMock";
			Logger.getLogger(FirmaServidorPluginMock.class.getName()).log(Level.SEVERE, errMsg);
			throw new SistemaExternException(errMsg);
		}
		// Retorna una firma falsa
		byte[] firmaContingut = null;
		SignaturaResposta resposta = new SignaturaResposta();
		try {
			firmaContingut = IOUtils.toByteArray(
					this.getClass().getResourceAsStream("/es/caib/ripea/plugin/firmaservidor/firma_document_mock.xml"));
			resposta.setContingut(firmaContingut);
		} catch (IOException e) {
			String errMsg = "Error llegint el fitxer mock de firma XAdES: " + e.getMessage();
			Logger.getLogger(FirmaServidorPluginMock.class.getName()).log(Level.SEVERE, errMsg, e);
			e.printStackTrace();
		}
		return resposta;
	}
	
	@Override
	public String getEndpointURL() {
		return "FirmaServidorPluginMock";
	}
}