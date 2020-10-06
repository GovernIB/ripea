/**
 * 
 */
package es.caib.ripea.core.service.ws.callbackportafib;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.portafib.ws.callback.api.v1.CallBackException;
import es.caib.portafib.ws.callback.api.v1.CallBackFault;
import es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs;
import es.caib.portafib.ws.callback.api.v1.PortaFIBEvent;
import es.caib.ripea.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.helper.IntegracioHelper;

/**
 * Implementació dels mètodes per al servei de callback del portafirmes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "PortaFIBCallBackWs",
		serviceName = "PortaFIBCallBackWsService",
		portName = "PortaFIBCallBackWs",
		targetNamespace = "http://v1.server.callback.ws.portafib.caib.es/",
		endpointInterface = "es.caib.portafib.ws.callback.api.v1.PortaFIBCallBackWs")
public class PortaFIBCallBackWsImpl implements PortaFIBCallBackWs {

	@Resource
	private DocumentService documentService;
	@Resource
	private IntegracioHelper integracioHelper;
	
	@Override
	public int getVersionWs() {
		return 1;
	}

	@Override
	public void event(PortaFIBEvent event) throws CallBackException {
		long documentId = event.getSigningRequest().getID();
		int estat = event.getEventTypeID();
		String motiuRebuig = null;
		logger.debug("Rebuda petició al callback de portafirmes (" +
				"documentId:" + documentId + ", " +
				"estat:" + estat + ")");
		String accioDescripcio = "Petició rebuda al callback";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("documentId", new Long(documentId).toString());
		accioParams.put("estat", new Integer(estat).toString());
		
		PortafirmesCallbackEstatEnumDto estatEnum;
		switch (estat) {
		case 0:
			estatEnum = PortafirmesCallbackEstatEnumDto.INICIAT;
			break;
		case 50:
			estatEnum = PortafirmesCallbackEstatEnumDto.PARCIAL;
			break;
		case 60:
			estatEnum = PortafirmesCallbackEstatEnumDto.FIRMAT;
			break;
		case 70:
			estatEnum = PortafirmesCallbackEstatEnumDto.REBUTJAT;
			if (event.getSigningRequest() != null) {
				logger.debug("Motiu rebuig: " + event.getSigningRequest().getRejectionReason());
				motiuRebuig = event.getSigningRequest().getRejectionReason();
			}
			break;
		case 80:
			estatEnum = PortafirmesCallbackEstatEnumDto.PAUSAT;
			break;
		default:
			String errorDescripcio = "No es reconeix el codi d'estat (" + estat + ")";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_CALLBACK,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					0,
					errorDescripcio);
			throw new CallBackException(errorDescripcio, new CallBackFault());
		}
		if (estatEnum != null) {
			try {
				String administrationId = null;
				if (event.getActor() != null) {
					administrationId = event.getActor().getAdministrationID();
				}
				Exception ex = documentService.portafirmesCallback(
						documentId,
						estatEnum,
						motiuRebuig,
						administrationId);
				if (ex == null) {
					integracioHelper.addAccioOk(
							IntegracioHelper.INTCODI_CALLBACK,
							accioDescripcio,
							accioParams,
							IntegracioAccioTipusEnumDto.RECEPCIO,
							0);
				} else {
					throwCallBackException(
							documentId, 
							estat, 
							accioDescripcio, 
							accioParams, 
							ex);
				}
			} catch (Exception ex) {
				throwCallBackException(
						documentId, 
						estat, 
						accioDescripcio, 
						accioParams, 
						ex);
			}
		}
	}
	
	private void throwCallBackException(
			Long documentId,
			int estat,
			String accioDescripcio,
			Map<String, String> accioParams,
			Exception ex) throws CallBackException {
		logger.error(
				"Error al processar petició rebuda al callback de portafirmes (" +
				"documentId:" + documentId + ", " +
				"estat:" + estat + ")",
				ex);
		String errorDescripcio = "Error al processar petició rebuda al callback de portafirmes";
		integracioHelper.addAccioError(
				IntegracioHelper.INTCODI_CALLBACK,
				accioDescripcio,
				accioParams,
				IntegracioAccioTipusEnumDto.RECEPCIO,
				0,
				errorDescripcio,
				ex);
		throw new CallBackException(
				"Excepcio al processar petició rebuda al callback de portafirmes",
				new CallBackFault(),
				ex);
	}

	private static final Logger logger = LoggerFactory.getLogger(PortaFIBCallBackWsImpl.class);

}
