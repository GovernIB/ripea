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
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import es.caib.ripea.service.intf.dto.UsuariAnotacioDto;
import es.caib.ripea.service.intf.model.sse.AnotacionsPendentsEvent;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.model.sse.CreacioFluxFinalitzatEvent;
import es.caib.ripea.service.intf.model.sse.ErrorsValidacioChangedEvent;
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
        USER_CONNECT, AVISOS, NOTIFICACIONS, TASQUES, FIRMA_FINALITZADA, FLUX_FINALITZAT;
        public String getEventName() { return name().toLowerCase(); }
        public static UserEventType fromEventName(String name) { return UserEventType.valueOf(name.toUpperCase()); }
    }

    private enum ExpedientEventType {
        EXP_CONNECT, FLUX_CREAT, SCAN_FINALITZAT, VALIDACIO_CHANGE;
        public String getEventName() { return name().toLowerCase(); }
        public static ExpedientEventType fromEventName(String name) { return ExpedientEventType.valueOf(name.toUpperCase()); }
    }

    /**
     * E N V I A M E N T   D ' E V E N T S   ( H E L P E R S )
     */

    /** Envia un event a un usuari concret. Si l'emitter falla, l'elimina del mapa. */
    private void sendToUser(String usuariCodi, SseEmitter.SseEventBuilder event) {
        SseEmitter emitter = clientsUsuaris.get(usuariCodi);
        if (emitter == null) return;
        try {
            emitter.send(event);
            logger.debug("... comunicat event al usuari {} a través del emisor {}.", usuariCodi, emitter.hashCode());
        } catch (Exception e) {
            clientsUsuaris.remove(usuariCodi);
            logger.debug("... eliminat emisor {} del usuari {} per error: {}.", emitter.hashCode(), usuariCodi, e.getMessage());
        }
    }

    /** Envia un event a tots els usuaris connectats. Elimina els emitters que fallin. */
    private void sendToAllUsers(SseEmitter.SseEventBuilder event) {
        Iterator<Map.Entry<String, SseEmitter>> iterator = clientsUsuaris.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SseEmitter> entry = iterator.next();
            try {
                entry.getValue().send(event);
                logger.debug("... comunicat event al usuari {} a través del emisor {}.", entry.getKey(), entry.getValue().hashCode());
            } catch (Exception e) {
                iterator.remove();
                logger.debug("... eliminat emisor {} del usuari {} per error: {}.", entry.getValue().hashCode(), entry.getKey(), e.getMessage());
            }
        }
    }

    /** Envia un event a tots els emitters d'un expedient. Elimina els que fallin i neteja l'entrada del mapa si queda buida. */
    private void sendToExpedient(Long expedientId, SseEmitter.SseEventBuilder event) {
        List<SseEmitter> emisors = clientsExpedient.get(expedientId);
        if (emisors == null) return;
        List<SseEmitter> toRemove = new ArrayList<>();
        for (SseEmitter emisor : emisors) {
            try {
                emisor.send(event);
                logger.debug("... comunicat event a l'expedient {} a través del emisor {}.", expedientId, emisor.hashCode());
            } catch (Exception e) {
                toRemove.add(emisor);
                logger.debug("... eliminat emisor {} de l'expedient {} per error: {}.", emisor.hashCode(), expedientId, e.getMessage());
            }
        }
        emisors.removeAll(toRemove);
        if (emisors.isEmpty()) {
            clientsExpedient.remove(expedientId);
            logger.debug("... eliminat expedient {} de la llista per no tenir cap emisor actiu.", expedientId);
        }
    }

    /**
     * T E S T I N G
     */

    @GetMapping("/testFlux/{fluxId}/{idMetaDoc}/{userCode}/send")
    @ResponseBody
    public ResponseEntity<String> testFlux(
    		@PathVariable String fluxId,
    		@PathVariable Long idMetaDoc,
    		@PathVariable String userCode) {
		PortafirmesFluxRespostaDto pfr = new PortafirmesFluxRespostaDto();
		pfr.setFluxId(fluxId!=null?fluxId:"fluxIdTest_SSE_"+System.currentTimeMillis());
		pfr.setNom("Flux test SSE");
		CreacioFluxFinalitzatEvent cffe = new CreacioFluxFinalitzatEvent(null, pfr);
		cffe.setEntitatCodi(aplicacioService.getEntitatActualCodi());
		cffe.setUsuariCodi(userCode);
		cffe.setMetaDocumentId(idMetaDoc);
		handleEventFluxCreatEditat(cffe);
		return ResponseEntity.ok().header("Content-Type", "text/plain; charset=UTF-8").body("OK");
    }

    @GetMapping("/test/{eventType}/{idExpedient}/{userCode}/send")
    @ResponseBody
    public ResponseEntity<String> test(
    		@PathVariable String eventType,
    		@PathVariable Long idExpedient,
    		@PathVariable String userCode) {
    	if (!"PRO".equalsIgnoreCase(aplicacioService.propertyFindByNom(PropertyConfig.ENTORN))) {
	    	switch (eventType) {
			case "FIRMA_FINALITZADA":
				FirmaResultatDto frd = new FirmaResultatDto(StatusEnumDto.OK, "Firma ok.");
				frd.setUsuari(userCode);
				frd.setStatus(StatusEnumDto.OK);
				frd.setMsg("La firma ha finalizat correctament (SSE test)");
				FirmaFinalitzadaEvent ffe = new FirmaFinalitzadaEvent(0l, frd);
				eventService.notifyFirmaNavegadorFinalitzada(ffe);
//				handleEventFirmaNavegadorFinalitzada(ffe);
				break;
			case "FLUX_CREAT":
				PortafirmesFluxRespostaDto pfrd = new PortafirmesFluxRespostaDto();
				pfrd.setUsuari(userCode);
				pfrd.setFluxId("flux1234ID");
				pfrd.setDescripcio("Flux fake 1234");
				CreacioFluxFinalitzatEvent cffe = new CreacioFluxFinalitzatEvent(idExpedient, pfrd);
				handleEventFlux(cffe);
				break;
			case "SCAN_FINALITZAT":
				DigitalitzacioResultatDto drd = new DigitalitzacioResultatDto();
				drd.setNomDocument("Document buid");
				drd.setUsuari(userCode);
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
	        SseEmitter existing = clientsUsuaris.put(usuariCodi, emitter);
	        if (existing != null) {
	        	logger.debug("Substituint emisor anterior {} del usuari {}.", existing.hashCode(), usuariCodi);
	        	try { existing.complete(); } catch (Exception ignored) {}
	        }
	        emitter.onCompletion(() -> clientsUsuaris.remove(usuariCodi, emitter));
	        emitter.onError(e -> clientsUsuaris.remove(usuariCodi, emitter));
	        onSubscribeEmisorGlobal(usuariCodi, emitter);
	        logger.debug("Usuari {} suscrit a events globals a travers del Emisor {}.", usuariCodi, emitter.hashCode());
	        return emitter;
    	} else {
    		return null;
    	}
    }

    private void onSubscribeEmisorGlobal(String usuariCodi, SseEmitter emitter) {
        // Al moment de subscriure enviem un missatge de connexió
        try {
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        	if (auth!=null && !"anonymousUser".equals(auth.getName())) {
	            emitter.send(SseEmitter.event()
	                    .name(UserEventType.USER_CONNECT.getEventName())
	                    .data("Connexió establerta a " + LocalDateTime.now())
	                    .id(String.valueOf(System.currentTimeMillis())));
	            // Un cop renviat el missatge de connexió correctament, enviem un missatge amb les dades inicials
	            var avisosActius = eventService.getAvisosActiusEvent();
	            Long entitatActualId = aplicacioService.getEntitatActualId();
	            Long organActualId = aplicacioService.getOrganActualId();
	            String rolActualCodi = aplicacioService.getRolActualCodi();
	            UsuariAnotacioDto uaDto = new UsuariAnotacioDto(usuariCodi, rolActualCodi, organActualId, entitatActualId);
	            var anotacionsPendents = eventService.getAnotacionsPendents(uaDto);
	            var tasquesPendents = eventService.getTasquesPendents(usuariCodi);
	            emitter.send(SseEmitter.event().name(UserEventType.AVISOS.getEventName()).data(avisosActius));
	            emitter.send(SseEmitter.event().name(UserEventType.NOTIFICACIONS.getEventName()).data(anotacionsPendents));
	            emitter.send(SseEmitter.event().name(UserEventType.TASQUES.getEventName()).data(tasquesPendents));
        	}
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
        List<SseEmitter> emisorsExpedient = clientsExpedient.computeIfAbsent(expedientId, k -> new ArrayList<>());
        emisorsExpedient.add(emitter);
        emitter.onCompletion(() -> {
            List<SseEmitter> list = clientsExpedient.get(expedientId);
            if (list != null) {
                list.remove(emitter);
                if (list.isEmpty()) clientsExpedient.remove(expedientId);
            }
        });
        emitter.onError(e -> {
            List<SseEmitter> list = clientsExpedient.get(expedientId);
            if (list != null) {
                list.remove(emitter);
                if (list.isEmpty()) clientsExpedient.remove(expedientId);
            }
        });
        logger.debug("Expedient {} suscrit a events a travers del Emisor {}.", expedientId, emitter.hashCode());
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
            //Carregam els valors inicials de les validacions del expedient
            /**
             * 09/03/2026
             *
             *  No es necessari, ja es carreguen en el afterConversion de ExpedientResourceServiceImpl.
             *  A mes, els resultats que recupera aquí no estan actualitzats i apareixen errors que no haurien d'apareixer.
             */
//            ErrorsValidacioChangedEvent evce = new ErrorsValidacioChangedEvent(
//            		expedientId,
//            		eventService.getValidacionsInicialsExpedient(expedientId));
//            emitter.send(SseEmitter.event()
//            		.name(ExpedientEventType.VALIDACIO_CHANGE.getEventName())
//            		.data(evce)
//            		.id(String.valueOf(System.currentTimeMillis())));
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
    @JmsListener(destination = "avisos")
    public void handleEventAvisos(AvisosActiusEvent avisos) {
    	if (avisos != null) {
    		logger.debug("Actualització de AvisosActiusEvent a usuaris...");
    		sendToAllUsers(SseEmitter.event().name(UserEventType.AVISOS.getEventName()).data(avisos));
    	}
    }

    @Async
    @JmsListener(destination = "firmaNavegadorFinalitzada")
    public void handleEventFirmaNavegadorFinalitzada(FirmaFinalitzadaEvent firmaResultat) {
    	if (firmaResultat != null && firmaResultat.getFirmaResultat() != null && firmaResultat.getFirmaResultat().getUsuari() != null) {
    		logger.debug("Actualització de EventFirmaNavegadorMassiva a expedients...");
    		sendToExpedient(
    				firmaResultat.getExpedientId(),
    				SseEmitter.event().name(UserEventType.FIRMA_FINALITZADA.getEventName()).data(firmaResultat.getFirmaResultat()));
//    		sendToUser(
//    				firmaResultat.getFirmaResultat().getUsuari(),
//    				SseEmitter.event().name(UserEventType.FIRMA_FINALITZADA.getEventName()).data(firmaResultat.getFirmaResultat()));
    	}
    }

    @Async
    @JmsListener(destination = "tasques")
    public void handleEventTasques(TasquesPendentsEvent tasques) {
    	if (tasques != null && tasques.getTasquesPendentsUsuaris() != null) {
    		logger.debug("Actualització de TasquesPendentsEvent a usuaris...");
    		tasques.getTasquesPendentsUsuaris().forEach((usuariCodi, count) ->
    			sendToUser(usuariCodi, SseEmitter.event().name(UserEventType.TASQUES.getEventName()).data(count)));
    	}
    }

    @Async
    @JmsListener(destination = "anotacions")
    public void handleEventNotificacions(AnotacionsPendentsEvent anotacions) {
    	if (anotacions != null && anotacions.getAnotacionsPendentsUsuaris() != null) {
    		logger.debug("Actualització de AnotacionsPendentsEvent a usuaris...");
    		anotacions.getAnotacionsPendentsUsuaris().forEach((usuariCodi, count) ->
    			sendToUser(usuariCodi, SseEmitter.event().name(UserEventType.NOTIFICACIONS.getEventName()).data(count)));
    	}
    }

    @Async
    @JmsListener(destination = "fluxCreatEditat")
    public void handleEventFluxCreatEditat(CreacioFluxFinalitzatEvent fluxEvent) {
    	if (fluxEvent != null && fluxEvent.getUsuariCodi() != null) {
    		logger.debug("Actualització de CreacioFluxFinalitzatEvent a usuaris...");
    		sendToUser(fluxEvent.getUsuariCodi(),
    				SseEmitter.event().name(UserEventType.FLUX_FINALITZAT.getEventName()).data(fluxEvent));
    	}
    }

    @Async
    @JmsListener(destination = "errorsValidacioExp")
    public void handleEventErrorsValidacio(ErrorsValidacioChangedEvent errorsValidacioEvent) {
    	if (errorsValidacioEvent != null && errorsValidacioEvent.getExpedientId() != null) {
    		logger.debug("Actualització de ErrorsValidacioChangedEvent a expedients...");
    		sendToExpedient(errorsValidacioEvent.getExpedientId(),
    				SseEmitter.event().name(ExpedientEventType.VALIDACIO_CHANGE.getEventName()).data(errorsValidacioEvent));
    	}
    }

    @Async
    @JmsListener(destination = "flux")
    public void handleEventFlux(CreacioFluxFinalitzatEvent fluxEvent) {
    	if (fluxEvent != null && fluxEvent.getExpedientId() != null) {
    		logger.debug("Actualització de CreacioFluxFinalitzatEvent a expedients...");
    		sendToExpedient(fluxEvent.getExpedientId(),
    				SseEmitter.event().name(ExpedientEventType.FLUX_CREAT.getEventName()).data(fluxEvent.getFluxCreat()));
    	}
    }

    @Async
    @JmsListener(destination = "scan")
    public void handleEventScan(ScanFinalitzatEvent scanEvent) {
    	if (scanEvent != null && scanEvent.getExpedientId() != null) {
    		logger.debug("Actualització de ScanFinalitzatEvent a expedients...");
    		sendToExpedient(scanEvent.getExpedientId(),
    				SseEmitter.event().name(ExpedientEventType.SCAN_FINALITZAT.getEventName()).data(scanEvent.getResposta()));
    	}
    }
}
