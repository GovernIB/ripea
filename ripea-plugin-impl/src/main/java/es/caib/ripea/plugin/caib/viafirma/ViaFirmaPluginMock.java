/**
 * 
 */
package es.caib.ripea.plugin.caib.viafirma;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.viafirma.ViaFirmaDispositiu;
import es.caib.ripea.plugin.viafirma.ViaFirmaDocument;
import es.caib.ripea.plugin.viafirma.ViaFirmaError;
import es.caib.ripea.plugin.viafirma.ViaFirmaParams;
import es.caib.ripea.plugin.viafirma.ViaFirmaPlugin;
import es.caib.ripea.plugin.viafirma.ViaFirmaResponse;

/**
 * Implementació de del plugin de viaFirma
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ViaFirmaPluginMock extends RipeaAbstractPluginProperties implements ViaFirmaPlugin {

	public ViaFirmaPluginMock() {
		super();
	}
	public ViaFirmaPluginMock(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public ViaFirmaResponse uploadDocument(ViaFirmaParams parametresViaFirma) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut enviar el document a viaFirma";
		ViaFirmaResponse response = new ViaFirmaResponse();
		ViaFirmaError error = new ViaFirmaError();
		try {
			response.setCodiMissatge("123456789123456");
			response.setViaFirmaError(error);
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
		return response;
	}

	@Override
	public ViaFirmaDocument downloadDocument(
			String codiUsuari,
			String contrasenya,
			String messageCode) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut recuperar el document de viaFirma";
		ViaFirmaDocument viaFirmaDocument = new ViaFirmaDocument();
		try {
			viaFirmaDocument.setNomFitxer("viaFirmaFile.pdf");
			viaFirmaDocument.setLink(getTestLink());
			viaFirmaDocument.setExpiracio(null);
		} catch (Exception ex) {
			throw new SistemaExternException(
					errorDescripcio,
					ex);
		}
		return viaFirmaDocument;
	}

	@Override
	public List<ViaFirmaDispositiu> getDeviceUser(
			String codiUsuari,
			String contrasenya) throws SistemaExternException {
		String errorDescripcio = "No s'ha pogut recuperar els dispositius de l'usuari (" +
				"usuariCodi=" + codiUsuari + ")";
		List<ViaFirmaDispositiu> viaFirmaDispositius = new ArrayList<ViaFirmaDispositiu>();
		try {
			
			ViaFirmaDispositiu viaFirmaDispositiu = new ViaFirmaDispositiu();
			viaFirmaDispositiu.setCodi("rrhh");
			viaFirmaDispositiu.setCodiUsuari("rrhh");
			viaFirmaDispositiu.setCodiAplicacio("com.viafirma.documents");
			viaFirmaDispositiu.setDescripcio("documents - 3.6.4 (89b38c39) - Android 10 - Xiaomi Redmi Note 9 Pro");
			viaFirmaDispositiu.setEmailUsuari("rrhh@notib.com");
			viaFirmaDispositiu.setEstat("INACTIVE");
			viaFirmaDispositiu.setIdentificador("4d7560a9b3f0015c15ds5r");
			viaFirmaDispositiu.setLocal("es_ES");
			viaFirmaDispositiu.setTipus("ANDROID");
			viaFirmaDispositiu.setToken("cuuQvSZcmh0:APA91bFm_MxVeZQGBtJyqWgaO8zJPi7TDSqtnpoZzch5Yln0Ssz94_SwiUWn3vuQYAJCOBJRHfw9");
			viaFirmaDispositiu.setIdentificadorNacional(null);
			viaFirmaDispositius.add(viaFirmaDispositiu);
			
		} catch (Exception ex) {
			logger.error(errorDescripcio, ex);
		}
		return viaFirmaDispositius;
	}
	
	private String getTestLink() {
		return getProperty("plugin.viafirma.mock.link");
	}
	private static final Logger logger = LoggerFactory.getLogger(ViaFirmaPluginMock.class);

}
