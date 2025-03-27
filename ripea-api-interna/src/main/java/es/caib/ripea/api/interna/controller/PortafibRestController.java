package es.caib.ripea.api.interna.controller;

import es.caib.portafib.callback.beans.v1.Actor;
import es.caib.portafib.callback.beans.v1.PortaFIBEvent;
import es.caib.portafib.callback.beans.v1.SigningRequest;
import es.caib.ripea.service.intf.dto.IntegracioAccioBuilderDto;
import es.caib.ripea.service.intf.dto.PortafirmesCalbackDto;
import es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.DocumentService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/rest/portafib/v1")
public class PortafibRestController {

	@Autowired private DocumentService documentService;
	@Autowired private AplicacioService aplicacioService;
	
	@RequestMapping(value = "/event", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> event(@RequestBody PortaFIBEvent event) {

		try {

			//Guardam el usuari a la taula de BBDD, ja que sino algunes dades d'auditoria podrien donar error
			aplicacioService.processarAutenticacioUsuari();
			
			PortafirmesCalbackDto portafirmesCalback = getPortafirmesCallback(event);
			
			IntegracioAccioBuilderDto integracioAccio = getIntegraccioAccio(portafirmesCalback);

			if (portafirmesCalback.getCallbackEstat() == null) {
				throwCallBackException(
						integracioAccio,
						"No es reconeix el codi d'estat (" + portafirmesCalback.getEstat() + ")",
						null);
			}

			Exception ex = null;
			try {
				ex = documentService.portafirmesCallback(
						portafirmesCalback.getPortafirmesId(),
						portafirmesCalback.getCallbackEstat(),
						portafirmesCalback.getMotiuRebuig(),
						portafirmesCalback.getAdministrationId(),
						portafirmesCalback.getName());
			} catch (Exception e) {
				ex = e;
			}
			if (ex == null) {
				documentService.portafirmesCallbackIntegracioOk(
						integracioAccio.getDescripcio(),
						integracioAccio.getParametres());
			} else {
				throwCallBackException(
						integracioAccio,
						"Error al processar petició rebuda al callback rest de portafirmes",
						ex);
			}

			return new ResponseEntity<String>(
					"OK",
					HttpStatus.OK);
		} catch (Throwable th) {
			log.error("Error al callback portafib rest", th);
			return new ResponseEntity<String>(
					"Error desconegut processant event de Peticio de Firma REST: " + th.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
  
  
	private PortafirmesCalbackDto getPortafirmesCallback(PortaFIBEvent event) {
		PortafirmesCalbackDto portafirmesCalbackDto = new PortafirmesCalbackDto();

		SigningRequest signingRequest = event.getSigningRequest();
		if (signingRequest != null) {
			portafirmesCalbackDto.setPortafirmesId(signingRequest.getID());
			portafirmesCalbackDto.setTitle(signingRequest.getTitle());
			portafirmesCalbackDto.setAdditionalInformation(signingRequest.getAdditionalInformation());
			portafirmesCalbackDto.setCustodyURL(signingRequest.getCustodyURL());
			
		}

		portafirmesCalbackDto.setEstat(event.getEventTypeID());
		Actor actor = event.getActor();
		if (actor != null) {
			portafirmesCalbackDto.setAdministrationId(actor.getAdministrationID());
			portafirmesCalbackDto.setName(actor.getName());
		}
		
		portafirmesCalbackDto.setVersion(event.getVersion());
		portafirmesCalbackDto.setEventDate(event.getEventDate());
		portafirmesCalbackDto.setApplicationID(event.getApplicationID());
		portafirmesCalbackDto.setEntityID(event.getEntityID());
		

		switch (event.getEventTypeID()) {
		case 0:
			portafirmesCalbackDto.setCallbackEstat(PortafirmesCallbackEstatEnumDto.INICIAT);
			break;
		case 50:
			portafirmesCalbackDto.setCallbackEstat(PortafirmesCallbackEstatEnumDto.PARCIAL);
			break;
		case 60:
			portafirmesCalbackDto.setCallbackEstat(PortafirmesCallbackEstatEnumDto.FIRMAT);
			break;
		case 70:
			portafirmesCalbackDto.setCallbackEstat(PortafirmesCallbackEstatEnumDto.REBUTJAT);
			if (signingRequest != null) {
				portafirmesCalbackDto.setMotiuRebuig(signingRequest.getRejectionReason());
			}
			break;
		case 80:
			portafirmesCalbackDto.setCallbackEstat(PortafirmesCallbackEstatEnumDto.PAUSAT);
			break;
		default:
		}
		return portafirmesCalbackDto;
	}
  
	private void throwCallBackException(
			IntegracioAccioBuilderDto integracioAccio,
			String errorDescripcio,
			Exception ex){
		log.error(
				errorDescripcio + "(" +
				integracioAccio.getParametres() + ")",
				ex);
		
		documentService.portafirmesCallbackIntegracioError(
				integracioAccio.getDescripcio(),
				integracioAccio.getParametres(),
				errorDescripcio,
				ex);
		
		throw new RuntimeException(
				"Excepcio al processar petició rebuda al callback rest de portafirmes",
				ex);
	}
	
	private IntegracioAccioBuilderDto getIntegraccioAccio(PortafirmesCalbackDto portafirmesCalback) {
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("portafirmesId", String.valueOf(portafirmesCalback.getPortafirmesId()));
		accioParams.put("estat", String.valueOf(portafirmesCalback.getEstat()));
		if (Utils.isNotEmpty(portafirmesCalback.getMotiuRebuig())) {
			accioParams.put("motiuRebuig", String.valueOf(portafirmesCalback.getMotiuRebuig()));
		}
		
		accioParams.put("versió", String.valueOf(portafirmesCalback.getVersion()));
		
		if (portafirmesCalback.getEventDate() != null) {
			accioParams.put("eventData", String.valueOf(portafirmesCalback.getEventDate()));
		}
		if (Utils.isNotEmpty(portafirmesCalback.getApplicationID())) {
			accioParams.put("aplicacioID", portafirmesCalback.getApplicationID());
		}
		if (Utils.isNotEmpty(portafirmesCalback.getEntityID())) {
			accioParams.put("entitatID", portafirmesCalback.getEntityID());
		}
		if (Utils.isNotEmpty(portafirmesCalback.getTitle())) {
			accioParams.put("títol",portafirmesCalback.getTitle());
		}
//		if (Utils.isNotEmpty(portafirmesCalback.getAdditionalInformation())) {
//			accioParams.put("additionalInformation", String.valueOf(portafirmesCalback.getAdditionalInformation()));
//		}
//		if (Utils.isNotEmpty(portafirmesCalback.getCustodyURL())) {
//			accioParams.put("custodyURL", String.valueOf(portafirmesCalback.getCustodyURL()));
//		}
		
		return new IntegracioAccioBuilderDto("Processar petició rebuda al callback rest de portafirmes", accioParams);
	}
  

}