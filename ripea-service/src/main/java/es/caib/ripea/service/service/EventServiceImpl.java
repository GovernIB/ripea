package es.caib.ripea.service.service;

import es.caib.ripea.persistence.repository.AvisRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.intf.dto.AvisDto;
import es.caib.ripea.service.intf.model.sse.AvisosActiusEvent;
import es.caib.ripea.service.intf.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei per a la gestió d'esdeveniments.
 * Permet enviar canvis en la informació de la sessió i alertes de l'aplicació als clients connectats.
 *
 * @author RIPEA
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final ApplicationEventPublisher eventPublisher;

    private final AvisRepository avisRepository;
    private final EntitatRepository entitatRepository;

    private final ConversioTipusHelper conversioTipusHelper;



    // ENVIAMENT AVISOS
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void notifyAvisosActius() {

        var event = getAvisosActiusEvent();

        // Notificar a tots els listeners
        log.debug("Enviant avisos a clients");
        eventPublisher.publishEvent(event);
    }

    @Override
    public AvisosActiusEvent getAvisosActiusEvent() {
        var avisosUsuari = conversioTipusHelper.convertirList(
                avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE)),
                AvisDto.class);

        Map<String, List<AvisDto>> avisosAdmin = new HashMap<>();
        entitatRepository.findByActiva(true).forEach(entitat -> {
            var avisos = conversioTipusHelper.convertirList(
                    avisRepository.findActiveAdmin(DateUtils.truncate(new Date(), Calendar.DATE), entitat.getId()),
                    AvisDto.class);
            if (avisos != null && !avisos.isEmpty())
                avisosAdmin.put(entitat.getCodi(), avisos);
        });

        var event = AvisosActiusEvent.builder()
                .avisosUsuari(avisosUsuari)
                .avisosAdmin(avisosAdmin)
                .build();
        return event;
    }

    @Scheduled(cron = "1 0 0 * * *")
    public void notifyAvisosActiusCron() {
        log.debug("Notificació dels avisos actius cron");
        notifyAvisosActius();
    }

}