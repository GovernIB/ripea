package es.caib.ripea.war.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.portafib.callback.beans.v1.Actor;
import es.caib.portafib.callback.beans.v1.PortaFIBEvent;
import es.caib.ripea.core.api.dto.IntegracioAccioBuilderDto;
import es.caib.ripea.core.api.dto.PortafirmesCalbackDto;
import es.caib.ripea.core.api.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.core.api.service.DocumentService;
import es.caib.ripea.core.api.utils.Utils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/rest/portafib/v1")
public class PortafibRestController {

	@Autowired
	private DocumentService documentService;
	
	@RequestMapping(value = "/event", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> event(@RequestBody PortaFIBEvent event) {

		try {

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
						portafirmesCalback.getDocumentId(),
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

		portafirmesCalbackDto.setDocumentId(event.getSigningRequest().getID());
		portafirmesCalbackDto.setEstat(event.getEventTypeID());
		Actor actor = event.getActor();
		if (actor != null) {
			portafirmesCalbackDto.setAdministrationId(actor.getAdministrationID());
			portafirmesCalbackDto.setName(actor.getName());
		}

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
			if (event.getSigningRequest() != null) {
				portafirmesCalbackDto.setMotiuRebuig(event.getSigningRequest().getRejectionReason());
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
		accioParams.put("documentId", String.valueOf(portafirmesCalback.getDocumentId()));
		accioParams.put("estat", String.valueOf(portafirmesCalback.getEstat()));
		if (Utils.isNotEmpty(portafirmesCalback.getMotiuRebuig())) {
			accioParams.put("motiuRebuig", String.valueOf(portafirmesCalback.getMotiuRebuig()));
		}
		
		return new IntegracioAccioBuilderDto("Processar petició rebuda al callback rest de portafirmes", accioParams);
	}
  

}