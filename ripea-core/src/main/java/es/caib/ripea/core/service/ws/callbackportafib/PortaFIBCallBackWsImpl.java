/**
 * 
 */
package es.caib.ripea.core.service.ws.callbackportafib;

import java.util.HashMap;
import java.util.List;
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
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.entity.DocumentPortafirmesEntity;
import es.caib.ripea.core.entity.PortafirmesBlockEntity;
import es.caib.ripea.core.entity.PortafirmesBlockInfoEntity;
import es.caib.ripea.core.helper.IntegracioHelper;
import es.caib.ripea.core.repository.DocumentPortafirmesRepository;
import es.caib.ripea.core.repository.PortafirmesBlockInfoRepository;
import es.caib.ripea.core.repository.PortafirmesBlockRepository;

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
	@Resource
	PortafirmesBlockRepository portafirmesBlockRepository;
	@Resource
	PortafirmesBlockInfoRepository portafirmesBlockInfoRepository;
	@Resource
	DocumentPortafirmesRepository documentPortafirmesRepository;

	@Override
	public int getVersionWs() {
		return 1;
	}

	@Override
	public void event(PortaFIBEvent event) throws CallBackException {
		long documentId = event.getSigningRequest().getID();
		int estat = event.getEventTypeID();
		String motiuRebuig = null;
		List<PortafirmesBlockEntity> portafirmesBlocks = null;
		logger.debug("Rebuda petició al callback de portafirmes (" +
				"documentId:" + documentId + ", " +
				"estat:" + estat + ")");
		String accioDescripcio = "Petició rebuda al callback";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("documentId", new Long(documentId).toString());
		accioParams.put("estat", new Integer(estat).toString());
		
		DocumentPortafirmesEntity documentPortafirmes = documentPortafirmesRepository.findByPortafirmesId(
				new Long(documentId).toString());
		if (documentPortafirmes == null) {
			Exception ex = new NotFoundException(
					"(portafirmesId=" + documentId + ")",
					DocumentPortafirmesEntity.class);
			throwCallBackException(
					documentId, 
					estat, 
					accioDescripcio, 
					accioParams, 
					ex);
		}
		
		PortafirmesCallbackEstatEnumDto estatEnum;
		switch (estat) {
		case 0:
		case 50:
			estatEnum = PortafirmesCallbackEstatEnumDto.PENDENT;
			if (event.getActor() != null && event.getActor().getAdministrationID() != null) {
				PortafirmesBlockInfoEntity portafirmesBlockInfoEntity = portafirmesBlockInfoRepository.findBySignerId(event.getActor().getAdministrationID());
				portafirmesBlockInfoEntity.updateSigned(true);
			}
			break;
		case 60:
			estatEnum = PortafirmesCallbackEstatEnumDto.FIRMAT;
			portafirmesBlocks = portafirmesBlockRepository.findByEnviament(documentPortafirmes);
			if (portafirmesBlocks != null) {
				for (PortafirmesBlockEntity portafirmesBlock : portafirmesBlocks) {
					portafirmesBlockRepository.delete(portafirmesBlock);
				}
			} else {
				logger.error(
						"No s'ha trobat cap block de firma relacionat amb aquest enviament", 
						new NotFoundException(
								"(portafirmesId=" + documentId + ")",
								PortafirmesBlockEntity.class));
			}
			break;
		case 70:
			estatEnum = PortafirmesCallbackEstatEnumDto.REBUTJAT;
			if (event.getSigningRequest() != null) {
				logger.debug("Motiu rebuig: " + event.getSigningRequest().getRejectionReason());
				motiuRebuig = event.getSigningRequest().getRejectionReason();
			}
			
			portafirmesBlocks = portafirmesBlockRepository.findByEnviament(documentPortafirmes);
			if (portafirmesBlocks != null) {
				for (PortafirmesBlockEntity portafirmesBlock : portafirmesBlocks) {
					portafirmesBlockRepository.delete(portafirmesBlock);
				}
			} else {
				logger.error(
						"No s'ha trobat cap block de firma relacionat amb aquest enviament", 
						new NotFoundException(
								"(portafirmesId=" + documentId + ")",
								PortafirmesBlockEntity.class));
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
				Exception ex = documentService.portafirmesCallback(
						documentId,
						estatEnum,
						motiuRebuig);
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
