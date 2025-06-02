package es.caib.ripea.back.resourcecontroller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.DigitalitzacioResultatDto;
import es.caib.ripea.service.intf.dto.FirmaResultatDto;
import es.caib.ripea.service.intf.dto.PortafirmesFluxRespostaDto;
import es.caib.ripea.service.intf.dto.StatusEnumDto;
import es.caib.ripea.service.intf.model.sse.AnotacionsPendentsEvent;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.FirmaFinalitzadaEvent;
import es.caib.ripea.service.intf.model.sse.ScanFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.TasquesPendentsEvent;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.utils.Utils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador per a enviar esdeveniments SSE (Server-Sent Events) al client.
 * Permet enviar canvis en la informació de la sessió i alertes de l'aplicació.
 *
 * @author RIPEA
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(BaseConfig.API_PATH + "/sse")
@Tag(name = "SSE", description = "Servei d'esdeveniments SSE per a canvis en la sessió i alertes")
public class SseResourceController {

    private final EventService eventService;
    private final AplicacioService aplicacioService;
    
    private final Map<String, SseEmitter> clientsUsuaris = new HashMap<>();
    //En cas de un expedient, el emmiter es una llista, perque pot estar obert per varis usuaris simultaniament.
    private final Map<Long, List<SseEmitter>> clientsExpedient = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SseResourceController.class);
    private enum UserEventType {
        USER_CONNECT, AVISOS, NOTIFICACIONS, TASQUES;
        public String getEventName() { return name().toLowerCase(); }
        public static UserEventType fromEventName(String name) { return UserEventType.valueOf(name.toUpperCase()); }
    }
    
    private enum ExpedientEventType {
        EXP_CONNECT, FLUX_CREAT, FIRMA_FINALITZADA, SCAN_FINALITZAT;
        public String getEventName() { return name().toLowerCase(); }
        public static ExpedientEventType fromEventName(String name) { return ExpedientEventType.valueOf(name.toUpperCase()); }
    }

    /**
     * T E S T I N G
     */
    @GetMapping("/test/{eventType}/{idExpedient}/send")
    @ResponseBody
    public ResponseEntity<String> stream(
    		@PathVariable String eventType,
    		@PathVariable Long idExpedient) {
    	if (!"PRO".equalsIgnoreCase(aplicacioService.propertyFindByNom(PropertyConfig.ENTORN))) {
	    	switch (eventType) {
			case "FIRMA_FINALITZADA":
				FirmaResultatDto frd = new FirmaResultatDto(StatusEnumDto.OK, "Firma ok.");
				frd.setUsuari("rip_user");
				FirmaFinalitzadaEvent ffe = new FirmaFinalitzadaEvent(idExpedient, frd);
				handleEventFirma(ffe);
				break;
			case "FLUX_CREAT":
				PortafirmesFluxRespostaDto pfrd = new PortafirmesFluxRespostaDto();
				pfrd.setUsuari("rip_user");
				pfrd.setFluxId("flux1234ID");
				pfrd.setDescripcio("Flux fake 1234");
				CreacioFluxFinalitzatEvent cffe = new CreacioFluxFinalitzatEvent(idExpedient, pfrd);
				handleEventFlux(cffe);
				break;
			case "SCAN_FINALITZAT":
				DigitalitzacioResultatDto drd = new DigitalitzacioResultatDto();
				drd.setNomDocument("Document buid");
				drd.setUsuari("rip_user");
				ScanFinalitzatEvent sfe = new ScanFinalitzatEvent(idExpedient, drd);
				handleEventScan(sfe);
			default:
				break;
			}
	    	return ResponseEntity.ok().header("Content-Type", "text/plain; charset=UTF-8").body("OK");
    	} else {
    		return ResponseEntity.ok().header("Content-Type", "text/plain; charset=UTF-8").body("Aquesta URL no esta disponible a l'entorn de producció.");
    	}
    }
    
    /**
     * S O C K E T   P E R   U S U A R I
     */
    
    @GetMapping("/subscribe/user/{usuariCodi}")
    public SseEmitter stream(@PathVariable String usuariCodi) {
    	if (Utils.hasValue(usuariCodi) && !"undefined".equalsIgnoreCase(usuariCodi)) {
	        SseEmitter emitter = new SseEmitter(0L);
	        clientsUsuaris.put(usuariCodi, emitter);
	        onSubscribeEmisorGlobal(usuariCodi, emitter);
	        logger.error("Usuari "+usuariCodi+" suscrit a events globals a travers del Emisor "+emitter.hashCode());
	        return emitter;
    	} else {
    		return null;
    	}
    }
    
    private void onSubscribeEmisorGlobal(String usuariCodi, SseEmitter emitter) {
        // Al moment de subscriure enviem un missatge de connexió
        try {
            emitter.send(SseEmitter.event()
                    .name(UserEventType.USER_CONNECT.getEventName())
                    .data("Connexió establerta a " + LocalDateTime.now())
                    .id(String.valueOf(System.currentTimeMillis())));
            // Un cop renviat el missatge de connexió correctament, enviem un missatge amb les dades inicials
            var avisosActius = eventService.getAvisosActiusEvent();
            var anotacionsPendents = eventService.getAnotacionsPendents(usuariCodi);
            var tasquesPendents = eventService.getTasquesPendents(usuariCodi);
            emitter.send(SseEmitter.event().name(UserEventType.AVISOS.getEventName()).data(avisosActius));
            emitter.send(SseEmitter.event().name(UserEventType.NOTIFICACIONS.getEventName()).data(anotacionsPendents));
            emitter.send(SseEmitter.event().name(UserEventType.TASQUES.getEventName()).data(tasquesPendents));
        } catch (IOException e) {
            log.error("Error enviant esdeveniment inicial SSE", e);
            emitter.complete();
            clientsUsuaris.remove(usuariCodi);
        } catch (Exception e) {
            log.error("Error inesperat onSubscribe", e);
            emitter.completeWithError(e);
        }
    }
    
    /**
     * S O C K E T   P E R   E X P E D I E N T
     */
    
    @GetMapping("/subscribe/exp/{expedientId}")
    public SseEmitter streamExpedient(@PathVariable Long expedientId) {
        SseEmitter emitter = new SseEmitter(0L);
        List<SseEmitter> emisorsExpedient = new ArrayList<SseEmitter>();
        if (clientsExpedient.containsKey(expedientId)) {
        	emisorsExpedient = clientsExpedient.get(expedientId);
        	emisorsExpedient.add(emitter);
        	logger.error("Nou expedient "+expedientId+" suscrit a events a travers del Emisor "+emitter.hashCode());
        } else {
        	emisorsExpedient = new ArrayList<SseEmitter>();
        	emisorsExpedient.add(emitter);
        	logger.error("Expedient existent "+expedientId+" suscrit a events a travers del Emisor "+emitter.hashCode());
        }
        clientsExpedient.put(expedientId, emisorsExpedient);
        onSubscribeEmisorExpedient(expedientId, emitter);
        return emitter;
    }

    private void onSubscribeEmisorExpedient(Long expedientId, SseEmitter emitter) {
        // Al moment de subscriure enviem un missatge de connexió
        try {
            emitter.send(SseEmitter.event()
                    .name(ExpedientEventType.EXP_CONNECT.getEventName())
                    .data("Connexió establerta a " + LocalDateTime.now())
                    .id(String.valueOf(System.currentTimeMillis())));
            // No hi ha en principi dades inicials per l'expedient.
        } catch (IOException e) {
            log.error("Error enviant esdeveniment inicial SSE", e);
            emitter.complete();
            clientsExpedient.get(expedientId).remove(emitter);
        } catch (Exception e) {
            log.error("Error inesperat onSubscribe", e);
            emitter.completeWithError(e);
        }
    }
    
    /**
     * M E T O D E S   R E C E P T O R S   D E   E V E N T S 
     */

    @Async
    @EventListener
    public void handleEventAvisos(AvisosActiusEvent avisos) {
    	if (avisos!=null) {
    		logger.error("Actualització de AvisosActiusEvent a usuaris...");
    		//Empram iterator per poder eliminar sense problemes elements del mapa mentre el recorrem
    		Iterator<Map.Entry<String, SseEmitter>> iterator = clientsUsuaris.entrySet().iterator();
    		//Els avisos s'envien a tots els usuaris connectats
    		while (iterator.hasNext()) {
    			Map.Entry<String, SseEmitter> entry = iterator.next();
                try {
                	entry.getValue().send(SseEmitter.event().name(UserEventType.AVISOS.getEventName()).data(avisos));
                	logger.error("... comunicats AvisosActiusEvent a travers del emissor "+entry.getValue().hashCode()+".");
                } catch (Exception e) {
                	clientsUsuaris.remove(entry.getKey());
                	logger.error("... eliminat emisor de AvisosActiusEvent "+entry.getValue().hashCode()+" del usuari "+entry.getKey()+" per error: "+e.getMessage()+".");
                }
            }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventTasques(TasquesPendentsEvent tasques) {
    	if (tasques!=null && tasques.getTasquesPendentsUsuaris()!=null) {
    		logger.error("Actualització de TasquesPendentsEvent a usuaris...");
			//Empram iterator per poder eliminar sense problemes elements del mapa mentre el recorrem
			Iterator<Map.Entry<String, SseEmitter>> iterator = clientsUsuaris.entrySet().iterator();
			//Els avisos s'envien a tots els usuaris connectats
			while (iterator.hasNext()) {
				Map.Entry<String, SseEmitter> usuariClient = iterator.next();
            	Iterator<Map.Entry<String, Long>> tascaInterator = tasques.getTasquesPendentsUsuaris().entrySet().iterator();
            	Map.Entry<String, Long> usuariTasca = tascaInterator.next();
            	if (usuariTasca.getKey().equals(usuariClient.getKey())) {
            		try {
            			usuariClient.getValue().send(SseEmitter.event().name(UserEventType.TASQUES.getEventName()).data(usuariTasca.getValue()));
            			logger.error("... comunicats TasquesPendentsEvent al usuari "+usuariClient.getKey()+" a travers del emissor "+usuariClient.getValue().hashCode()+".");
    	            } catch (Exception e) {
    	            	clientsUsuaris.remove(usuariClient.getKey());
    	            	logger.error("... eliminat emisor de TasquesPendentsEvent "+usuariClient.getValue().hashCode()+" del usuari "+usuariClient.getKey()+" per error: "+e.getMessage()+".");
    	            }	            		
            	}
	        }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventNotificacions(AnotacionsPendentsEvent anotacions) {
    	if (anotacions!=null && anotacions.getAnotacionsPendentsUsuaris()!=null) {
    		logger.error("Actualització de AnotacionsPendentsEvent a usuaris...");
			//Empram iterator per poder eliminar sense problemes elements del mapa mentre el recorrem
			Iterator<Map.Entry<String, SseEmitter>> iterator = clientsUsuaris.entrySet().iterator();
			//Els avisos s'envien a tots els usuaris connectats
			while (iterator.hasNext()) {
				Map.Entry<String, SseEmitter> usuariClient = iterator.next();
            	Iterator<Map.Entry<String, Long>> tascaInterator = anotacions.getAnotacionsPendentsUsuaris().entrySet().iterator();
            	Map.Entry<String, Long> usuariTasca = tascaInterator.next();
            	if (usuariTasca.getKey().equals(usuariClient.getKey())) {
            		try {
            			usuariClient.getValue().send(SseEmitter.event().name(UserEventType.NOTIFICACIONS.getEventName()).data(usuariTasca.getValue()));
            			logger.error("... comunicats AnotacionsPendentsEvent al usuari "+usuariClient.getKey()+" a travers del emissor "+usuariClient.getValue().hashCode()+".");
            		} catch (Exception e) {
    	            	clientsUsuaris.remove(usuariClient.getKey());
    	            	logger.error("... eliminat emisor de AnotacionsPendentsEvent "+usuariClient.getValue().hashCode()+" del usuari "+usuariClient.getKey()+" per error: "+e.getMessage()+".");
    	            }
            	}
	        }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventFlux(CreacioFluxFinalitzatEvent fluxEvent) {
    	if (fluxEvent!=null && fluxEvent.getExpedientId()!=null) {
    		logger.error("Actualització de CreacioFluxFinalitzatEvent a expedients...");
			Iterator<Map.Entry<Long, List<SseEmitter>>> iterator = clientsExpedient.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Long, List<SseEmitter>> expedientClient = iterator.next();
            	if (fluxEvent.getExpedientId().equals(expedientClient.getKey())) {
            		List<SseEmitter> emisorsExpedient  = expedientClient.getValue();
            		List<SseEmitter> emisoresAEliminar = new ArrayList<>();
            		for (SseEmitter emisor : emisorsExpedient) {
            			try {
            				emisor.send(SseEmitter.event().name(ExpedientEventType.FLUX_CREAT.getEventName()).data(fluxEvent.getFluxCreat()));
            				logger.error("... comunicats CreacioFluxFinalitzatEvent al expedient "+expedientClient.getKey()+" a travers del emissor "+emisor.hashCode()+".");
        	            } catch (Exception e) {
        	            	emisoresAEliminar.add(emisor); //Eliminam el emisor de la llista de emisors del expedient
        	            	logger.error("... eliminat emisor de CreacioFluxFinalitzatEvent "+emisor.hashCode()+" per error "+e.getMessage()+".");
        	            }
            		}
            		emisorsExpedient.removeAll(emisoresAEliminar);
            		//Si ja no queden emisors per l'expedient, eliminam l'entrada del mapa
            		if (emisorsExpedient==null || emisorsExpedient.size()==0) {
            			clientsExpedient.remove(expedientClient.getKey());
            			logger.error("... eliminat expedient "+expedientClient.getKey()+" de la llista de events per no tenir cap emisor actiu.");
            		} else {
            			clientsExpedient.put(expedientClient.getKey(), emisorsExpedient);
            		}
            	}
	        }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventFirma(FirmaFinalitzadaEvent firmaEvent) {
    	if (firmaEvent!=null && firmaEvent.getExpedientId()!=null) {
    		logger.error("Actualització de FirmaFinalitzadaEvent a expedients...");
			Iterator<Map.Entry<Long, List<SseEmitter>>> iterator = clientsExpedient.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Long, List<SseEmitter>> expedientClient = iterator.next();
            	if (firmaEvent.getExpedientId().equals(expedientClient.getKey())) {
            		List<SseEmitter> emisorsExpedient  = expedientClient.getValue();
            		List<SseEmitter> emisoresAEliminar = new ArrayList<>();
            		for (SseEmitter emisor : emisorsExpedient) {
            			try {
            				emisor.send(SseEmitter.event().name(ExpedientEventType.FIRMA_FINALITZADA.getEventName()).data(firmaEvent.getFirmaResultat()));
            				logger.error("... comunicats FirmaFinalitzadaEvent al expedient "+expedientClient.getKey()+" a travers del emissor "+emisor.hashCode()+".");
        	            } catch (Exception e) {
        	            	emisoresAEliminar.add(emisor); //Eliminam el emisor de la llista de emisors del expedient
        	            	logger.error("... eliminat emisor de FirmaFinalitzadaEvent "+emisor.hashCode()+" per error "+e.getMessage()+".");
        	            }
            		}
            		emisorsExpedient.removeAll(emisoresAEliminar);
            		//Si ja no queden emisors per l'expedient, eliminam l'entrada del mapa
            		if (emisorsExpedient==null || emisorsExpedient.size()==0) {
            			clientsExpedient.remove(expedientClient.getKey());
            			logger.error("... eliminat expedient "+expedientClient.getKey()+" de la llista de events per no tenir cap emisor actiu.");
            		} else {
            			clientsExpedient.put(expedientClient.getKey(), emisorsExpedient);
            		}
            	}
	        }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventScan(ScanFinalitzatEvent scanEvent) {
    	if (scanEvent!=null && scanEvent.getExpedientId()!=null) {
    		logger.error("Actualització de ScanFinalitzatEvent a expedients...");
			Iterator<Map.Entry<Long, List<SseEmitter>>> iterator = clientsExpedient.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Long, List<SseEmitter>> expedientClient = iterator.next();
            	if (scanEvent.getExpedientId().equals(expedientClient.getKey())) {
            		List<SseEmitter> emisorsExpedient  = expedientClient.getValue();
            		List<SseEmitter> emisoresAEliminar = new ArrayList<>();
            		for (SseEmitter emisor : emisorsExpedient) {
            			try {
            				emisor.send(SseEmitter.event().name(ExpedientEventType.SCAN_FINALITZAT.getEventName()).data(scanEvent.getResposta()));
            				logger.error("... comunicats ScanFinalitzatEvent al expedient "+expedientClient.getKey()+" a travers del emissor "+emisor.hashCode()+".");
        	            } catch (Exception e) {
        	            	emisoresAEliminar.add(emisor); //Eliminam el emisor de la llista de emisors del expedient
        	            	logger.error("... eliminat emisor de ScanFinalitzatEvent "+emisor.hashCode()+" per error "+e.getMessage()+".");
        	            }
            		}
            		emisorsExpedient.removeAll(emisoresAEliminar);
            		//Si ja no queden emisors per l'expedient, eliminam l'entrada del mapa
            		if (emisorsExpedient==null || emisorsExpedient.size()==0) {
            			clientsExpedient.remove(expedientClient.getKey());
            			logger.error("... eliminat expedient "+expedientClient.getKey()+" de la llista de events per no tenir cap emisor actiu.");
            		} else {
            			clientsExpedient.put(expedientClient.getKey(), emisorsExpedient);
            		}
            	}
	        }
    	}
    }
}