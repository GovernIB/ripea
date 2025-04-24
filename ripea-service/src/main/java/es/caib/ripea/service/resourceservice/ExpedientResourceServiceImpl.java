package es.caib.ripea.service.resourceservice;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.ripea.persistence.entity.resourceentity.*;
import es.caib.ripea.persistence.entity.resourcerepository.*;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ArxiuDetallDto;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.model.*;
import es.caib.ripea.service.intf.model.ExpedientResource.ExpedientFilterForm;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import es.caib.ripea.service.permission.ExtendedPermission;
import es.caib.ripea.service.resourcehelper.ContingutResourceHelper;
import es.caib.ripea.service.resourcehelper.ExpedientResourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientResourceServiceImpl extends BaseMutableResourceService<ExpedientResource, Long, ExpedientResourceEntity> implements ExpedientResourceService {

    private final ExpedientResourceRepository expedientResourceRepository;
    private final UsuariResourceRepository usuariResourceRepository;
    private final MetaExpedientResourceRepository metaExpedientResourceRepository;
    private final MetaExpedientSequenciaResourceRepository metaExpedientSequenciaResourceRepository;

    private final ContingutResourceHelper contingutResourceHelper;
    private final PluginHelper pluginHelper;
    private final ConfigHelper configHelper;
    private final ExpedientResourceHelper expedientResourceHelper;
    private final EntityComprovarHelper entityComprovarHelper;

    @PostConstruct
    public void init() {
        register(ExpedientResource.ACTION_FOLLOW_CODE, new FollowActionExecutor());
        register(ExpedientResource.ACTION_UNFOLLOW_CODE, new UnFollowActionExecutor());
        register(ExpedientResource.ACTION_AGAFAR_CODE, new AgafarActionExecutor());
        register(ExpedientResource.ACTION_RETORNAR_CODE, new RetornarActionExecutor());
        register(ExpedientResource.PERSPECTIVE_FOLLOWERS, new FollowersPerspectiveApplicator());
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
        // En cas de no disposar d'entitat actual, filtrarem per un string "................................................................................"
        // amb una mida superior a la mida màxima del camp codi de manera que asseguram que no es retornin resultats un cop aplicat el filtre
        String entitatActualCodi = configHelper.getEntitatActualCodi();

        Filter filter = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())
                        ?Filter.parse(currentSpringFilter)
                        :null,
                FilterBuilder.equal(ContingutResource.Fields.entitat + ".codi", entitatActualCodi != null
                        ?entitatActualCodi
                        :"................................................................................"),
                FilterBuilder.equal(ExpedientResource.Fields.organGestor + ".codi", configHelper.getOrganActualCodi())
        );

        return filter.generate();
    }

    @Override
    protected void afterConversion(ExpedientResourceEntity entity, ExpedientResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
        resource.setNumSeguidors(entity.getSeguidors().size());
        usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .ifPresent(usuariResourceEntity -> resource.setSeguidor(entity.getSeguidors().contains(usuariResourceEntity)));

        /*/////////////////////////////////////////////////*/
        resource.setUsuariActualWrite(entityComprovarHelper.comprovarPermisExpedient(entity.getId(), ExtendedPermission.WRITE, "WRITE", false));
        expedientResourceHelper.setPotTancar(entity, resource);
        /*/////////////////////////////////////////////////*/
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
    private class FollowersPerspectiveApplicator implements PerspectiveApplicator<ExpedientResourceEntity, ExpedientResource> {
        @Override
        public void applySingle(String code, ExpedientResourceEntity entity, ExpedientResource resource) throws PerspectiveApplicationException {
            List<ResourceReference<UsuariResource, String>> seguidors = entity.getSeguidors().stream()
                    .map(usuariResourceEntity -> {
                        UsuariResource usuariResource = objectMappingHelper.newInstanceMap(usuariResourceEntity, UsuariResource.class);
                        return ResourceReference.<UsuariResource, String>toResourceReference(usuariResource.getId(), usuariResource.getCodiAndNom());
                    })
                    .collect(Collectors.toList());
            resource.setSeguidors(seguidors);
        }
    }

    // ActionExecutor
    private class FollowActionExecutor implements ActionExecutor<ExpedientResourceEntity, Serializable, ExpedientResource> {

        @Override
        public ExpedientResource exec(String code, ExpedientResourceEntity entity, Serializable params) throws ActionExecutionException {
            Optional<UsuariResourceEntity> optionalUsuariResource = usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());

            if (optionalUsuariResource.isPresent() && !entity.getSeguidors().contains(optionalUsuariResource.get())) {
                entity.getSeguidors().add(optionalUsuariResource.get());
                expedientResourceRepository.save(entity);
            }

            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
        }

        @Override
        public void onChange(Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

        }
    }
    private class UnFollowActionExecutor implements ActionExecutor<ExpedientResourceEntity, Serializable, ExpedientResource> {

        @Override
        public ExpedientResource exec(String code, ExpedientResourceEntity entity, Serializable params) throws ActionExecutionException {
            Optional<UsuariResourceEntity> optionalUsuariResource = usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());

            if (optionalUsuariResource.isPresent() && entity.getSeguidors().contains(optionalUsuariResource.get())) {
                entity.getSeguidors().remove(optionalUsuariResource.get());
                expedientResourceRepository.save(entity);
            }

            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
        }

        @Override
        public void onChange(Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

        }
    }
    private class AgafarActionExecutor implements ActionExecutor<ExpedientResourceEntity, Serializable, ExpedientResource> {

        @Override
        public ExpedientResource exec(String code, ExpedientResourceEntity entity, Serializable params) throws ActionExecutionException {
            Optional<UsuariResourceEntity> optionalUsuariResource = usuariResourceRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());

            if (optionalUsuariResource.isPresent() && entity.getAgafatPer() != optionalUsuariResource.get()) {
                entity.setAgafatPer(optionalUsuariResource.get());
                expedientResourceRepository.save(entity);
            }

            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
        }

        @Override
        public void onChange(Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

        }
    }
    private class RetornarActionExecutor implements ActionExecutor<ExpedientResourceEntity, Serializable, ExpedientResource> {

        @Override
        public ExpedientResource exec(String code, ExpedientResourceEntity entity, Serializable params) throws ActionExecutionException {
            Optional<UsuariResourceEntity> optionalUsuariResource = usuariResourceRepository.findById(
                    entity.getCreatedBy());

            if (optionalUsuariResource.isPresent() && entity.getAgafatPer() != optionalUsuariResource.get()) {
                entity.setAgafatPer(optionalUsuariResource.get());
                expedientResourceRepository.save(entity);
//                emailHelper.contingutAlliberat(expedient, usuariCreador, usuariActual);
            }

            return objectMappingHelper.newInstanceMap(entity, ExpedientResource.class);
        }

        @Override
        public void onChange(Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

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
