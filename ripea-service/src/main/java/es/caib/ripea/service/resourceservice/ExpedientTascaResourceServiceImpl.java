package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaComentariResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ExpedientTascaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientTascaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.model.ExpedientTascaResource;
import es.caib.ripea.service.intf.model.MetaExpedientTascaResource;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientTascaResourceServiceImpl extends BaseMutableResourceService<ExpedientTascaResource, Long, ExpedientTascaResourceEntity> implements ExpedientTascaResourceService {

    private final ExpedientTascaResourceRepository expedientTascaResourceRepository;
    private final MetaExpedientTascaResourceRepository metaExpedientTascaResourceRepository;
    private final UsuariResourceRepository usuariResourceRepository;

	@PostConstruct
	public void init() {
		register(ExpedientTascaResource.PERSPECTIVE_RESPONSABLES_CODE, new ResponsablesPerspectiveApplicator());
        register(ExpedientTascaResource.Fields.metaExpedientTasca, new MetaExpedientTascaOnchangeLogicProcessor());
        register(ExpedientTascaResource.Fields.duracio, new DuracioOnchangeLogicProcessor());
        register(ExpedientTascaResource.Fields.dataLimit, new DataLimitOnchangeLogicProcessor());
        register(ExpedientTascaResource.ACTION_CHANGE_ESTAT_CODE, new ChangeEstatActionExecutor());
        register(ExpedientTascaResource.ACTION_REABRIR_CODE, new ReobrirActionExecutor());
        register(ExpedientTascaResource.ACTION_REBUTJAR_CODE, new RebutjarActionExecutor());
        register(ExpedientTascaResource.ACTION_RETOMAR_CODE, new RetomarActionExecutor());
	}

    @Override
    protected void beforeCreateSave(ExpedientTascaResourceEntity entity, ExpedientTascaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        List<String> ids = resource.getObservadors().stream()
                .map(ResourceReference::getId)
                .collect(Collectors.toList());
        List<UsuariResourceEntity> entidades = usuariResourceRepository.findAllById(ids);
        entity.setObservadors(entidades);
        entity.setDataInici(new Date());
    }

    @Override
    protected void afterConversion(ExpedientTascaResourceEntity entity, ExpedientTascaResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
        resource.setMetaExpedientTascaDescription(entity.getMetaExpedientTasca().getDescripcio());

        resource.setObservadors(entity.getObservadors()
                .stream().map(obs->ResourceReference.<UsuariResource, String>toResourceReference(obs.getId(), obs.getCodiAndNom()))
                .collect(Collectors.toList()));

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        resource.setUsuariActualResponsable(Objects.equals(resource.getResponsableActual().getId(), user));
        resource.setUsuariActualDelegat(resource.getDelegat() != null && Objects.equals(resource.getDelegat().getId(), user));
    }

    // PerspectiveApplicator
	private class ResponsablesPerspectiveApplicator implements PerspectiveApplicator<ExpedientTascaResourceEntity, ExpedientTascaResource> {
		@Override
		public void applySingle(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource resource) throws PerspectiveApplicationException {
			if (entity.getResponsables() != null && !entity.getResponsables().isEmpty()) {
				List<String> responsablesStr = new ArrayList<>();
				for (UsuariResourceEntity responsable: entity.getResponsables()) {
					responsablesStr.add(responsable.getCodi());
				}
				resource.setResponsablesStr(StringUtils.join(responsablesStr, ","));
			}
		}
	}

    // OnChangeLogicProcessor
    private class MetaExpedientTascaOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientTascaResource> {
        @Override
        public void onChange(
                ExpedientTascaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientTascaResource target) {

            if (fieldValue != null) {
                ResourceReference<MetaExpedientTascaResource, Long> metaExpedientTasca = (ResourceReference<MetaExpedientTascaResource, Long>) fieldValue;
                Optional<MetaExpedientTascaResourceEntity> resourceOptional = metaExpedientTascaResourceRepository.findById(metaExpedientTasca.getId());
                resourceOptional.ifPresent((resource) -> {
                    target.setDuracio(resource.getDuracio());
                    target.setPrioritat(resource.getPrioritat());
                    target.setMetaExpedientTascaDescription(resource.getDescripcio());

                    if(resource.getResponsable()!=null){
                        target.setResponsableActual(ResourceReference.toResourceReference(
                                resource.getResponsable().getCodi(),
                                resource.getResponsable().getCodiAndNom()
                        ));
                    }
                });
            } else {
                target.setDuracio(null);
                target.setPrioritat(PrioritatEnumDto.B_NORMAL);
                target.setResponsableActual(null);
                target.setMetaExpedientTascaDescription(null);
            }
        }
    }
    private class DuracioOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientTascaResource> {
        @Override
        public void onChange(
                ExpedientTascaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientTascaResource target) {

            if (fieldValue != null) {
                Date dataLimit= DateUtils.addDays(previous.getDataInici()!=null ?previous.getDataInici() :new Date(), (Integer) fieldValue);
                if (previous.getDataLimit() == null || !DateUtils.isSameDay(previous.getDataLimit(), dataLimit)) {
                    target.setDataLimit(dataLimit);
                }
            } else {
                if (previous.getDataLimit()!=null) {
                    target.setDataLimit(null);
                }
            }
        }
    }
    private class DataLimitOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientTascaResource> {
        @Override
        public void onChange(
                ExpedientTascaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientTascaResource target) {

            if (fieldValue != null) {
                LocalDate start =previous.getDataInici()!=null
                        ?(previous.getDataInici()).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        :LocalDate.now();
                LocalDate end = ((Date)fieldValue).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                int dias = (int) start.until(end, ChronoUnit.DAYS);

                if (!Objects.equals(previous.getDuracio(), dias)) {
                    target.setDuracio(dias);
                }
            } else {
                if (previous.getDuracio()!=null) {
                    target.setDuracio(null);
                }
            }
        }
    }

    // ActionExecutor
    private void changeEstat(ExpedientTascaResourceEntity entity, TascaEstatEnumDto estat){
        switch (estat){
            case INICIADA:
                usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                        .ifPresent(entity::setResponsableActual);
                break;
            case AGAFADA:
                break;
            case PENDENT:
                break;
            case FINALITZADA:
                entity.getExpedient().setEstatAdditional(entity.getMetaExpedientTasca().getEstatFinalitzarTasca());
            case REBUTJADA:
            case CANCELLADA:
                entity.setDelegat(null);
                break;
        }

        entity.setEstat(estat);
    }

    private class ChangeEstatActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.ChangeEstatFormAction, ExpedientTascaResource> {

        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.ChangeEstatFormAction params) throws ActionExecutionException {
            changeEstat(entity, params.getEstat());
            expedientTascaResourceRepository.save(entity);
            return objectMappingHelper.newInstanceMap(entity, ExpedientTascaResource.class);
        }

        @Override
        public void onChange(ExpedientTascaResource.ChangeEstatFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.ChangeEstatFormAction target) {

        }
    }
    private class RebutjarActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.MotiuFormAction, ExpedientTascaResource> {

        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.MotiuFormAction params) throws ActionExecutionException {
            changeEstat(entity, TascaEstatEnumDto.REBUTJADA);
            entity.setMotiuRebuig(params.getMotiu());
            expedientTascaResourceRepository.save(entity);
            return objectMappingHelper.newInstanceMap(entity, ExpedientTascaResource.class);
        }

        @Override
        public void onChange(ExpedientTascaResource.MotiuFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.MotiuFormAction target) {

        }
    }
    private class ReobrirActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.ReobrirFormAction, ExpedientTascaResource> {

        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.ReobrirFormAction params) throws ActionExecutionException {
            if (params.getMotiu() != null) {
                ExpedientTascaComentariResourceEntity expedientTascaComentariResourceEntity = new ExpedientTascaComentariResourceEntity();
                expedientTascaComentariResourceEntity.setText(params.getMotiu());
                entity.getComentaris().add(expedientTascaComentariResourceEntity);
            }

            changeEstat(entity, TascaEstatEnumDto.PENDENT);
            usuariResourceRepository.findById(params.getResponsableActual().getId())
                    .ifPresent(entity::setResponsableActual);
            expedientTascaResourceRepository.save(entity);
            return objectMappingHelper.newInstanceMap(entity, ExpedientTascaResource.class);
        }

        @Override
        public void onChange(ExpedientTascaResource.ReobrirFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.ReobrirFormAction target) {

        }
    }
    private class RetomarActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.MotiuFormAction, ExpedientTascaResource> {

        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.MotiuFormAction params) throws ActionExecutionException {
            if (params.getMotiu() != null) {
                ExpedientTascaComentariResourceEntity expedientTascaComentariResourceEntity = new ExpedientTascaComentariResourceEntity();
                expedientTascaComentariResourceEntity.setText(params.getMotiu());
                entity.getComentaris().add(expedientTascaComentariResourceEntity);
            }

            changeEstat(entity, TascaEstatEnumDto.PENDENT);
            entity.setDelegat(null);
            expedientTascaResourceRepository.save(entity);
            return objectMappingHelper.newInstanceMap(entity, ExpedientTascaResource.class);
        }

        @Override
        public void onChange(ExpedientTascaResource.MotiuFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.MotiuFormAction target) {

        }
    }
}
