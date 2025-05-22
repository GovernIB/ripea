package es.caib.ripea.back.resourcecontroller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
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
    private final List<SseEmitter> clients = new CopyOnWriteArrayList<>();
    private final Map<Long, List<SseEmitter>> clientsExpedient = new HashMap<>();
    private enum EventType {
        CONNECT, AVISOS, NOTIFICACIONS, TASQUES;
        public String getEventName() { return name().toLowerCase(); }
        public static EventType fromEventName(String name) { return EventType.valueOf(name.toUpperCase()); }
    }

    @GetMapping("/subscribe")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(0L);
        clients.add(emitter);
        emitter.onCompletion(() -> clients.remove(emitter)); // quan el client tanca o es desconnecta
        emitter.onTimeout(() -> clients.remove(emitter)); // també per si la connexió cau
        emitter.onError((e) -> clients.remove(emitter)); // per si hi ha error de xarxa
        onSubscribeEmisorGlobal(emitter);
        return emitter;
    }
    
    @GetMapping("/subscribe/{expedientId}")
    public SseEmitter streamExpedient(Long expedientId) {
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
        emitter.onCompletion(() -> clientsExpedient.get(expedientId).remove(emitter)); // quan el client tanca o es desconnecta
        emitter.onTimeout(() -> clientsExpedient.get(expedientId).remove(emitter)); // també per si la connexió cau
        emitter.onError((e) -> clientsExpedient.get(expedientId).remove(emitter));  // per si hi ha error de xarxa
        onSubscribeEmisorExpedient(expedientId, emitter);
        return emitter;
    }

    private void onSubscribeEmisorExpedient(Long expedientId, SseEmitter emitter) {
        // Al moment de subscriure enviem un missatge de connexió
        try {
            emitter.send(SseEmitter.event()
                    .name(EventType.CONNECT.getEventName())
                    .data("Connexió establerta a " + LocalDateTime.now())
                    .id(String.valueOf(System.currentTimeMillis())));
            // No hi ha en principi dades inicials per l'expedient.
        } catch (IOException e) {
            log.error("Error enviant esdeveniment inicial SSE", e);
            emitter.complete();
            clients.remove(emitter);
        } catch (Exception e) {
            log.error("Error inesperat onSubscribe", e);
            emitter.completeWithError(e);
        }
    }
    
    private void onSubscribeEmisorGlobal(SseEmitter emitter) {
        // Al moment de subscriure enviem un missatge de connexió
        try {
            emitter.send(SseEmitter.event()
                    .name(EventType.CONNECT.getEventName())
                    .data("Connexió establerta a " + LocalDateTime.now())
                    .id(String.valueOf(System.currentTimeMillis())));
            // Un cop renviat el missatge de connexió correctament, enviem un missatge amb les dades inicials
            var avisosActius = eventService.getAvisosActiusEvent();
            var anotacionsPendents = eventService.getAnotacionsPendents();
            var tasquesPendents = eventService.getTasquesPendents();
            emitter.send(SseEmitter.event().name(EventType.AVISOS.getEventName()).data(avisosActius));
            emitter.send(SseEmitter.event().name(EventType.NOTIFICACIONS.getEventName()).data(anotacionsPendents));
            emitter.send(SseEmitter.event().name(EventType.TASQUES.getEventName()).data(tasquesPendents));
        } catch (IOException e) {
            log.error("Error enviant esdeveniment inicial SSE", e);
            emitter.complete();
            clients.remove(emitter);
        } catch (Exception e) {
            log.error("Error inesperat onSubscribe", e);
            emitter.completeWithError(e);
        }
    }

    @Async
    @EventListener
    public void handleAlertesEvent(AvisosActiusEvent event) {
        broadcast("avisos", event);
    }

    private void broadcast(String eventName, Object data) {
        for (SseEmitter emitter : clients) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                clients.remove(emitter);
            }
        }
    }
}