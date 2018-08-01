/**
 * 
 */
package es.caib.ripea.plugin.caib.signatura;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.signatura.SignaturaPlugin;

/**
 * Implementació mock del plugin de signatura. Retorna una signatura falsa 
 * quan se signa. Si l'id és igual a "e" llavors retorna una excepció de sistema
 * estern, si no retorna una firma falsa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class SignaturaPluginMock implements SignaturaPlugin {	  
		  
	@Override
	public byte[] signar(
			String id,
			String nom,
			String motiu,
			String tipusFirma,
			byte[] contingut) throws SistemaExternException {
		
		if (id != null && "e".equals(id)) {
			// Cas per provocar una excepció
			String errMsg = "Excepció provocada per paràmetre a SignaturaPluginMock";
			Logger.getLogger(SignaturaPluginMock.class.getName()).log(Level.SEVERE, errMsg);
			throw new SistemaExternException(errMsg);
		}

		// Retorna una firma falsa
		byte[] firmaContingut = null;
		try {
			firmaContingut = IOUtils.toByteArray(this.getClass().getResourceAsStream("/es/caib/ripea/plugin/caib/signatura/firma_document_mock.xml"));
		} catch (IOException e) {
			String errMsg = "Error llegint el fitxer mock de firma XAdES: " + e.getMessage();
			Logger.getLogger(SignaturaPluginMock.class.getName()).log(Level.SEVERE, errMsg, e);
			e.printStackTrace();
		}		
		return firmaContingut;
	}  
}
