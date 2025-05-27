package es.caib.ripea.back.resourcecontroller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.sse.AnotacionsPendentsEvent;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.FirmaFinalitzadaEvent;
import es.caib.ripea.service.intf.model.sse.ScanFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.TasquesPendentsEvent;
import es.caib.ripea.service.intf.service.EventService;
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
    private final Map<String, SseEmitter> clientsUsuaris = new HashMap<>();
    //En cas de un expedient, el emmiter es una llista, perque pot estar obert per varis usuaris simultaniament.
    private final Map<Long, List<SseEmitter>> clientsExpedient = new HashMap<>();
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
     * S O C K E T   P E R   U S U A R I
     */
    
    @GetMapping("/subscribe/user/{usuariCodi}")
    public SseEmitter stream(@PathVariable String usuariCodi) {
        SseEmitter emitter = new SseEmitter(0L);
        clientsUsuaris.put(usuariCodi, emitter);
        emitter.onCompletion(() -> clientsUsuaris.remove(usuariCodi)); // quan el client tanca o es desconnecta
        emitter.onTimeout(() -> clientsUsuaris.remove(usuariCodi)); // també per si la connexió cau
        emitter.onError((e) -> clientsUsuaris.remove(usuariCodi)); // per si hi ha error de xarxa
        onSubscribeEmisorGlobal(usuariCodi, emitter);
        return emitter;
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
        } else {
        	emisorsExpedient = new ArrayList<SseEmitter>();
        	emisorsExpedient.add(emitter);
        }
        clientsExpedient.put(expedientId, emisorsExpedient);
        emitter.onCompletion(() -> clientsExpedient.remove(expedientId)); // quan el client tanca o es desconnecta
        emitter.onTimeout(() -> clientsExpedient.remove(expedientId)); // també per si la connexió cau
        emitter.onError((e) -> clientsExpedient.remove(expedientId));  // per si hi ha error de xarxa
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
            clientsExpedient.remove(expedientId);
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
    		//Empram iterator per poder eliminar sense problemes elements del mapa mentre el recorrem
    		Iterator<Map.Entry<String, SseEmitter>> iterator = clientsUsuaris.entrySet().iterator();
    		//Els avisos s'envien a tots els usuaris connectats
    		while (iterator.hasNext()) {
    			Map.Entry<String, SseEmitter> entry = iterator.next();
                try {
                	entry.getValue().send(SseEmitter.event().name(UserEventType.AVISOS.getEventName()).data(avisos));
                } catch (IOException e) {
                	clientsUsuaris.remove(entry.getKey());
                }
            }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventTasques(TasquesPendentsEvent tasques) {
    	if (tasques!=null && tasques.getTasquesPendentsUsuaris()!=null) {
			//Empram iterator per poder eliminar sense problemes elements del mapa mentre el recorrem
			Iterator<Map.Entry<String, SseEmitter>> iterator = clientsUsuaris.entrySet().iterator();
			//Els avisos s'envien a tots els usuaris connectats
			while (iterator.hasNext()) {
				Map.Entry<String, SseEmitter> usuariClient = iterator.next();
	            try {
	            	Iterator<Map.Entry<String, Long>> tascaInterator = tasques.getTasquesPendentsUsuaris().entrySet().iterator();
	            	Map.Entry<String, Long> usuariTasca = tascaInterator.next();
	            	if (usuariTasca.getKey().equals(usuariClient.getKey())) {
	            		usuariClient.getValue().send(SseEmitter.event().name(UserEventType.TASQUES.getEventName()).data(usuariTasca.getValue()));
	            	}
	            } catch (IOException e) {
	            	clientsUsuaris.remove(usuariClient.getKey());
	            }
	        }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventNotificacions(AnotacionsPendentsEvent anotacions) {
    	if (anotacions!=null && anotacions.getAnotacionsPendentsUsuaris()!=null) {
			//Empram iterator per poder eliminar sense problemes elements del mapa mentre el recorrem
			Iterator<Map.Entry<String, SseEmitter>> iterator = clientsUsuaris.entrySet().iterator();
			//Els avisos s'envien a tots els usuaris connectats
			while (iterator.hasNext()) {
				Map.Entry<String, SseEmitter> usuariClient = iterator.next();
	            try {
	            	Iterator<Map.Entry<String, Long>> tascaInterator = anotacions.getAnotacionsPendentsUsuaris().entrySet().iterator();
	            	Map.Entry<String, Long> usuariTasca = tascaInterator.next();
	            	if (usuariTasca.getKey().equals(usuariClient.getKey())) {
	            		usuariClient.getValue().send(SseEmitter.event().name(UserEventType.NOTIFICACIONS.getEventName()).data(usuariTasca.getValue()));
	            	}
	            } catch (IOException e) {
	            	clientsUsuaris.remove(usuariClient.getKey());
	            }
	        }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventFlux(CreacioFluxFinalitzatEvent fluxEvent) {
    	if (fluxEvent!=null && fluxEvent.getExpedientId()!=null) {
			//Empram iterator per poder eliminar sense problemes elements del mapa mentre el recorrem
			Iterator<Map.Entry<Long, List<SseEmitter>>> iterator = clientsExpedient.entrySet().iterator();
			//Els avisos s'envien a tots els usuaris connectats
			while (iterator.hasNext()) {
				Map.Entry<Long, List<SseEmitter>> expedientClient = iterator.next();
	            try {
	            	if (fluxEvent.getExpedientId().equals(expedientClient.getKey())) {
	            		for(SseEmitter emisor: expedientClient.getValue()) {
	            			emisor.send(SseEmitter.event().name(ExpedientEventType.FLUX_CREAT.getEventName()).data(fluxEvent.getFluxCreat()));
	            		}
	            	}
	            } catch (IOException e) {
	            	clientsExpedient.remove(expedientClient.getKey());
	            }
	        }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventFirma(FirmaFinalitzadaEvent firmaEvent) {
    	if (firmaEvent!=null && firmaEvent.getExpedientId()!=null) {
			//Empram iterator per poder eliminar sense problemes elements del mapa mentre el recorrem
			Iterator<Map.Entry<Long, List<SseEmitter>>> iterator = clientsExpedient.entrySet().iterator();
			//Els avisos s'envien a tots els usuaris connectats
			while (iterator.hasNext()) {
				Map.Entry<Long, List<SseEmitter>> expedientClient = iterator.next();
	            try {
	            	if (firmaEvent.getExpedientId().equals(expedientClient.getKey())) {
	            		for(SseEmitter emisor: expedientClient.getValue()) {
	            			emisor.send(SseEmitter.event().name(ExpedientEventType.FIRMA_FINALITZADA.getEventName()).data(firmaEvent.getFirmaResultat()));
	            		}
	            	}
	            } catch (IOException e) {
	            	clientsExpedient.remove(expedientClient.getKey());
	            }
	        }
    	}
    }
    
    @Async
    @EventListener
    public void handleEventScan(ScanFinalitzatEvent scanEvent) {
    	if (scanEvent!=null && scanEvent.getExpedientId()!=null) {
			//Empram iterator per poder eliminar sense problemes elements del mapa mentre el recorrem
			Iterator<Map.Entry<Long, List<SseEmitter>>> iterator = clientsExpedient.entrySet().iterator();
			//Els avisos s'envien a tots els usuaris connectats
			while (iterator.hasNext()) {
				Map.Entry<Long, List<SseEmitter>> expedientClient = iterator.next();
	            try {
	            	if (scanEvent.getExpedientId().equals(expedientClient.getKey())) {
	            		for(SseEmitter emisor: expedientClient.getValue()) {
	            			emisor.send(SseEmitter.event().name(ExpedientEventType.SCAN_FINALITZAT.getEventName()).data(scanEvent.getResposta()));
	            		}
	            	}
	            } catch (IOException e) {
	            	clientsExpedient.remove(expedientClient.getKey());
	            }
	        }
    	}
    }
}