package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.resourceentity.AlertaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.AlertaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.ExpedientResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.model.AlertaResource;
import es.caib.ripea.service.intf.model.ExpedientResource;
import es.caib.ripea.service.intf.resourceservice.AlertaResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.resourcehelper.AlertaPermisosFiltreHelper;
import es.caib.ripea.service.resourcehelper.PerspectivesLotHelper;
import es.caib.ripea.service.resourcehelper.inici.RecomptesRecurs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió d'alertes.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaResourceServiceImpl extends BaseMutableResourceService<AlertaResource, Long, AlertaResourceEntity> implements AlertaResourceService, RecomptesRecurs<AlertaResourceEntity> {

	private final AlertaResourceRepository alertaResourceRepository;
	private final ExpedientResourceRepository expedientResourceRepository;
	private final AlertaPermisosFiltreHelper alertaPermisosFiltreHelper;

    @PostConstruct
    public void init() {
        register(AlertaResource.ACTION_MASSIVE_LLEGIT_CODE,	new LlegitActionExecutor());
        register(AlertaResource.PERSPECTIVE_EXPEDIENT_CODE,	new ExpedientPerspectiveApplicator());
    }

    /**
     * Amb la named query de la consulta «Alertes d'expedients» s'apliquen els permisos del llistat d'expedients.
     * Sense, es manté el comportament anterior (la graella d'alertes del detall de l'expedient, que ja filtra
     * per l'expedient).
     */
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        if (Utils.namedQueriesToMap(namedQueries).containsKey(AlertaResource.NAMED_QUERY_CONSULTA)) {
            return alertaPermisosFiltreHelper.generarFiltre();
        }
        return super.additionalSpringFilter(currentSpringFilter, namedQueries);
    }

    /**
     * Recomptes de la pantalla d'inici. Amb la named query de la consulta «Alertes d'expedients» compten el mateix
     * que la consulta (veure RecomptesRecurs).
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> comptar(Map<String, Specification<AlertaResourceEntity>> criteris, String[] namedQueries) {
        return RecomptesRecurs.comptar(criteris, this.<AlertaResourceEntity>toFindProcessedSpecification(null, null, namedQueries), entityRepository);
    }

    protected class LlegitActionExecutor implements ActionExecutor<AlertaResourceEntity, ExpedientResource.MassiveAction, Serializable> {

        @Override
        public Serializable exec(String code, AlertaResourceEntity entity, ExpedientResource.MassiveAction params) throws ActionExecutionException {
            if (params!=null) {
            	for (Long id: params.getIds()) {
            		alertaResourceRepository.findById(id).get().setLlegida(true);
            	}
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, ExpedientResource.MassiveAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientResource.MassiveAction target) {}
    }

    /**
     * Número, títol i procediment de l'expedient de cada alerta. Els expedients de la pàgina es carreguen tots
     * d'una vegada pel contingut de l'alerta; les alertes que no són d'un expedient (documents) queden sense dades.
     */
    private class ExpedientPerspectiveApplicator implements PerspectiveApplicator<AlertaResourceEntity, AlertaResource> {

        @Override
        public boolean applyMultiple(String code, List<AlertaResourceEntity> entities, List<AlertaResource> resources) throws PerspectiveApplicationException {
            Set<Long> contingutIds = entities.stream()
                    .map(e -> e.getContingut() != null ? e.getContingut().getId() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            Map<Long, ExpedientResourceEntity> expedients = new HashMap<>();
            for (List<Long> lot: PerspectivesLotHelper.lots(contingutIds)) {
                for (ExpedientResourceEntity expedient: expedientResourceRepository.findAllById(lot)) {
                    expedients.put(expedient.getId(), expedient);
                }
            }
            for (int i = 0; i < entities.size(); i++) {
                AlertaResourceEntity entity = entities.get(i);
                ExpedientResourceEntity expedient = entity.getContingut() != null ? expedients.get(entity.getContingut().getId()) : null;
                if (expedient != null) {
                    AlertaResource resource = resources.get(i);
                    resource.setExpedientNumero(expedient.getNumero());
                    resource.setExpedientNom(expedient.getNom());
                    resource.setProcedimentNom(expedient.getMetaExpedientNom());
                }
            }
            return true;
        }

        @Override
        public void applySingle(String code, AlertaResourceEntity entity, AlertaResource resource) throws PerspectiveApplicationException {
            applyMultiple(code, Collections.singletonList(entity), Collections.singletonList(resource));
        }
    }
}
