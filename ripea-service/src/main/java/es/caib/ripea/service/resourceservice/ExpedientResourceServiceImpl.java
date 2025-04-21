package es.caib.ripea.service.resourceservice;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.model.*;
import es.caib.ripea.service.intf.service.ContingutService;
import es.caib.ripea.service.resourcehelper.ContingutResourceHelper;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientSequenciaResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientSequenciaResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.model.ExpedientResource.ExpedientFilterForm;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientResourceServiceImpl extends BaseMutableResourceService<ExpedientResource, Long, ExpedientResourceEntity> implements ExpedientResourceService {

    private final MetaExpedientResourceRepository metaExpedientResourceRepository;
    private final MetaExpedientSequenciaResourceRepository metaExpedientSequenciaResourceRepository;
    private final ContingutResourceHelper contingutResourceHelper;
    private final PluginHelper pluginHelper;
    private final ConfigHelper configHelper;

    @PostConstruct
    public void init() {
        register(ExpedientResource.PERSPECTIVE_COUNT, new CountPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_INTERESSATS_CODE, new InteressatsPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_ESTAT_CODE, new EstatPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_RELACIONAT_CODE, new RelacionatPerspectiveApplicator());
        register(ExpedientResource.PERSPECTIVE_ARXIU_EXPEDIENT, new ArxiuExpedientPerspectiveApplicator());
        register(ExpedientResource.Fields.metaExpedient, new MetaExpedientOnchangeLogicProcessor());
        register(ExpedientResource.Fields.any, new AnyOnchangeLogicProcessor());
        register(ExpedientResource.FILTER_CODE, new FilterOnchangeLogicProcessor());
    }

    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
        Filter filter = FilterBuilder.and(
                FilterBuilder.equal(ContingutResource.Fields.entitat + ".codi", configHelper.getEntitatActualCodi()),
                FilterBuilder.equal(ExpedientResource.Fields.organGestor + ".codi", configHelper.getOrganActualCodi())
        );

        return filter.generate();
    }

    @Override
    protected void afterConversion(ExpedientResourceEntity entity, ExpedientResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
        resource.setNumSeguidors(entity.getSeguidors().size());
    }

    @Override
    protected void beforeCreateSave(
            ExpedientResourceEntity entity,
            ExpedientResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        entity.setMetaNode(entity.getMetaExpedient());

        entity.setCodi(entity.getMetaExpedient().getCodi());

        /** TODO: cambiar (ExpedientHelper.calcularNumero()) */
        entity.setNumero(entity.getCodi() + "/" + entity.getSequencia() + "/" + entity.getAny());

        entity.setEntitat(entity.getMetaExpedient().getEntitat());
        entity.setTipus(ContingutTipusEnumDto.EXPEDIENT);
        entity.setNtiIdentificador(Long.toString(System.currentTimeMillis()));
        entity.setNtiOrgano(entity.getMetaExpedient().getEntitat().getUnitatArrel());
        entity.setNtiFechaApertura(new Date());
        entity.setNtiClasificacionSia(entity.getMetaExpedient().getClassificacio());
    }

    @Override
    protected void afterCreateSave(
            ExpedientResourceEntity entity,
            ExpedientResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers,
            boolean anyOrderChanged) {
        Optional<MetaExpedientSequenciaResourceEntity> metaExpedientSequenciaResourceEntity
                = metaExpedientSequenciaResourceRepository.findByMetaExpedientAndAny(entity.getMetaExpedient(), resource.getAny());

        metaExpedientSequenciaResourceEntity.ifPresentOrElse(
                (metaExpedientSequencia) -> {
                    metaExpedientSequencia.setValor(metaExpedientSequencia.getValor() + 1);
                    metaExpedientSequenciaResourceRepository.save(metaExpedientSequencia);
                },
                () -> {
                    MetaExpedientSequenciaResourceEntity metaExpedientSequencia = new MetaExpedientSequenciaResourceEntity();
                    metaExpedientSequencia.setMetaExpedient(entity.getMetaExpedient());
                    metaExpedientSequencia.setAny(resource.getAny());
                    metaExpedientSequencia.setValor(resource.getSequencia() + 1);

                    metaExpedientSequenciaResourceRepository.save(metaExpedientSequencia);
                }
        );
    }

    @Override
    protected void beforeUpdateSave(ExpedientResourceEntity entity, ExpedientResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        entity.setMetaNode(entity.getMetaExpedient());
    }

    // PerspectiveApplicator
    private class CountPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            resource.setNumInteressats((int) entity.getInteressats().stream().filter(interessatResourceEntity -> !interessatResourceEntity.isEsRepresentant()).count());
            resource.setNumTasques(entity.getTasques().size());
            resource.setNumAnotacions(entity.getPeticions().size());
            resource.setNumPublicacions(entity.getPublicacions().size());
            resource.setNumRemeses(entity.getNotificacions().size());
            resource.setNumMetaDades(entity.getMetaNode().getMetaDades().size());
        }
    }

    private class InteressatsPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            List<InteressatResource> interessats = entity.getInteressats().stream()
                    .map(interessatResourceEntity -> objectMappingHelper.newInstanceMap(interessatResourceEntity, InteressatResource.class))
                    .collect(Collectors.toList());
            resource.setInteressats(interessats);
        }
    }

    private class EstatPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            if (entity.getEstatAdditional()!=null) {
                resource.setEstatAdditionalInfo(objectMappingHelper.newInstanceMap(Hibernate.unproxy(entity.getEstatAdditional()), ExpedientEstatResource.class));
            }
        }
    }

    private class RelacionatPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            List<ResourceReference<ExpedientResource, Long>> relacionatsAmb = entity.getRelacionatsAmb().stream()
                    .map(expedientResourceEntity -> objectMappingHelper.newInstanceMap(expedientResourceEntity, ExpedientResource.class))
                            .map(expedientResource -> ResourceReference.<ExpedientResource, Long>toResourceReference(
                                    expedientResource.getId(),
                                    "["+ expedientResource.getSequencia() +"/"+ expedientResource.getAny() +"] "+ expedientResource.getNom()
                            ))
                    .collect(Collectors.toList());
            resource.setRelacionatsAmb(relacionatsAmb);

            List<ResourceReference<ExpedientResource, Long>> relacionatsPer = entity.getRelacionatsPer().stream()
                    .map(expedientResourceEntity -> objectMappingHelper.newInstanceMap(expedientResourceEntity, ExpedientResource.class))
                            .map(expedientResource -> ResourceReference.<ExpedientResource, Long>toResourceReference(
                                    expedientResource.getId(),
                                    "["+ expedientResource.getSequencia() +"/"+ expedientResource.getAny() +"] "+ expedientResource.getNom()
                            ))
                    .collect(Collectors.toList());
            resource.setRelacionatsPer(relacionatsPer);
        }
    }
    private class ArxiuExpedientPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            Expedient arxiuExpedient = pluginHelper.arxiuExpedientConsultar(
                    entity.getId(), entity.getNom(), entity.getMetaExpedient().getNom(), entity.getArxiuUuid());
            ArxiuDetallDto arxiu = contingutResourceHelper.getArxiuExpedientDetall(arxiuExpedient);
//            ArxiuDetallDto arxiu = contingutResourceHelper.getArxiuDetall(entity.getEntitat().getId(), entity.getId());
            resource.setArxiu(arxiu);
        }
    }

    // OnChangeLogicProcessor
    private class MetaExpedientOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientResource> {
        @Override
        public void onChange(
                ExpedientResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientResource target) {

            if (fieldValue != null) {
                ResourceReference<MetaExpedientResource, Long> reference =
                        (ResourceReference<MetaExpedientResource, Long>) fieldValue;
                Optional<MetaExpedientResourceEntity> metaExpedientResourceOptional =
                        metaExpedientResourceRepository.findById(reference.getId());

                metaExpedientResourceOptional.ifPresent((metaExpedientResourceEntity) -> {
                    MetaExpedientResource metaExpedientResource =
                            objectMappingHelper.newInstanceMap(metaExpedientResourceEntity, MetaExpedientResource.class);
                    if (metaExpedientResource.getOrganGestor() != null) {
                        target.setOrganGestor(metaExpedientResource.getOrganGestor());
                        target.setDisableOrganGestor(true);
                        if (previous.getAny() != null) {
                            Optional<Long> sequencia = metaExpedientSequenciaResourceRepository
                                    .findValorByMetaExpedientAndAny(metaExpedientResourceEntity, previous.getAny());

                            sequencia.ifPresentOrElse(
                                    (value) -> target.setSequencia(value + 1),
                                    () -> target.setSequencia(1L)
                            );
                        }
                    }
                });
            } else {
                target.setOrganGestor(null);
                target.setSequencia(null);
            }
        }
    }

    private class AnyOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientResource> {
        @Override
        public void onChange(
                ExpedientResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientResource target) {

            if (fieldValue != null && previous.getMetaExpedient() != null) {
                Optional<MetaExpedientResourceEntity> metaExpedientResourceOptional =
                        metaExpedientResourceRepository.findById(previous.getMetaExpedient().getId());

                metaExpedientResourceOptional.ifPresent((metaExpedientResourceEntity) -> {
                    MetaExpedientResource metaExpedientResource =
                            objectMappingHelper.newInstanceMap(metaExpedientResourceEntity, MetaExpedientResource.class);
                    if (metaExpedientResource.getOrganGestor() != null) {
                        Optional<Long> sequencia = metaExpedientSequenciaResourceRepository
                                .findValorByMetaExpedientAndAny(metaExpedientResourceEntity, (Integer) fieldValue);

                        sequencia.ifPresentOrElse(
                                (value) -> target.setSequencia(value + 1),
                                () -> target.setSequencia(1L)
                        );
                    }
                });
            } else {
                target.setSequencia(null);
            }
        }
    }

    private static class FilterOnchangeLogicProcessor implements FilterProcessor<ExpedientFilterForm> {

        @Override
        public void onChange(ExpedientFilterForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientFilterForm target) {
            switch (fieldName) {
                case ExpedientFilterForm.Fields.dataCreacioInici:
                    if (fieldValue != null && previous.getDataCreacioFinal() != null
                            && previous.getDataCreacioFinal().isBefore((ChronoLocalDateTime<?>) fieldValue)) {
                        target.setDataCreacioInici(null);
                    }
                    break;
                case ExpedientFilterForm.Fields.dataCreacioFinal:
                    if (fieldValue != null && previous.getDataCreacioInici() != null
                            && previous.getDataCreacioInici().isAfter((ChronoLocalDateTime<?>) fieldValue)) {
                        target.setDataCreacioFinal(null);
                    }
                    break;
            }
        }
    }
}
