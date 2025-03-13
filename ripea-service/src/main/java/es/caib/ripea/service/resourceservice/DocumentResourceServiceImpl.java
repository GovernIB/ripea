package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.*;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDocumentResourceRepository;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotUpdatedException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.dto.DocumentEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentFirmaTipusEnumDto;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.model.DocumentResource;
import es.caib.ripea.service.intf.model.DocumentResource.ParentPath;
import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió d'expedients.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentResourceServiceImpl extends BaseMutableResourceService<DocumentResource, Long, DocumentResourceEntity> implements DocumentResourceService {

    private final MetaDocumentResourceRepository metaDocumentResourceRepository;

    @PostConstruct
    public void init() {
        register(DocumentResource.PERSPECTIVE_PATH_CODE, new PathPerspectiveApplicator());
        register(DocumentResource.Fields.metaDocument, new MetaDocumentOnchangeLogicProcessor());
    }

    @Override
    protected void beforeCreateSave(DocumentResourceEntity entity, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        Optional<MetaDocumentResourceEntity> optionalDocumentResource = metaDocumentResourceRepository.findById(resource.getMetaDocument().getId());
        optionalDocumentResource.ifPresent(entity::setMetaNode);
        optionalDocumentResource.ifPresent((metaDocumentResourceEntity -> entity.setNtiTipoDocumental(metaDocumentResourceEntity.getNtiTipoDocumental())));

        entity.setEstat(entity.getDocumentFirmaTipus() == DocumentFirmaTipusEnumDto.SENSE_FIRMA ? DocumentEstatEnumDto.REDACCIO : DocumentEstatEnumDto.FIRMAT);
        entity.setTipus(ContingutTipusEnumDto.DOCUMENT);
//        entity.setValidacioFirmaCorrecte(true);
        entity.setData(new Date());
        // TODO: revisar
        entity.setPare(entity.getExpedient());
        entity.setEntitat(entity.getMetaNode().getEntitat());
        entity.setNtiIdentificador(Long.toString(System.currentTimeMillis()));
        entity.setNtiOrgano(entity.getExpedient().getNtiOrgano());
        entity.setExpedientEstatAdditional(entity.getExpedient().getEstatAdditional());
    }

    @Override
    protected void beforeUpdateSave(DocumentResourceEntity entity, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotUpdatedException {
        Optional<MetaDocumentResourceEntity> optionalDocumentResource = metaDocumentResourceRepository.findById(resource.getMetaDocument().getId());
        optionalDocumentResource.ifPresent(entity::setMetaNode);
    }

    @Override
    protected void afterConversion(DocumentResourceEntity entity, DocumentResource resource) {
        if(entity.getMetaNode()!=null) {
            resource.setMetaDocument(ResourceReference.toResourceReference(entity.getMetaNode().getId(), entity.getMetaNode().getNom()));
        }
    }

    private class PathPerspectiveApplicator implements PerspectiveApplicator<DocumentResource, DocumentResourceEntity> {
        @Override
        public void applySingle(String code, DocumentResourceEntity entity, DocumentResource resource) throws PerspectiveApplicationException {
            if (entity.getPare()!=null){
                List<ParentPath> parentPaths = getPath(entity).stream()
                        .filter((a)-> !Objects.equals(a.getId(), entity.getExpedient().getId()))
                        .collect(Collectors.toList());
                parentPaths.forEach(
                        (a)->setTreePath(a, parentPaths)
                );

                resource.setParentPath(parentPaths);
                resource.setTreePath(parentPaths.stream()
                        .map(ParentPath::getNom)
                        .collect(Collectors.toList()));
            }
        }

        public <E extends ContingutResourceEntity> List<ParentPath> getPath(E entity) {
            List<ParentPath> path = new ArrayList<ParentPath>();
            getPathPare(entity, path);
            Collections.reverse(path);
            return path;
        }
        public <E extends ContingutResourceEntity> void getPathPare(E entity, List<ParentPath> path) {
            if (entity != null) {
                ParentPath pathEntry = new ParentPath(entity.getId(), entity.getNom(), entity.getCreatedBy(), entity.getCreatedDate(), entity.getTipus());
                pathEntry.setId(entity.getId());
                path.add(pathEntry);
                getPathPare(entity.getPare(), path);
            }
        }
        public void setTreePath(ParentPath entity, List<ParentPath> path){
            Boolean notFound = true;
            int arrayIndex = 0;
            List<String> result = new ArrayList<>();
            while (notFound && path.size()>arrayIndex){
                result.add(path.get(arrayIndex).getNom());
                notFound = !Objects.equals(entity.getId(), path.get(arrayIndex).getId());
                arrayIndex++;
            }
            entity.setTreePath(result);
        }
    }

    private class MetaDocumentOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void processOnChangeLogic(
                DocumentResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                DocumentResource target) {

            if (fieldValue != null) {
                ResourceReference<MetaDocumentResource, Long> resourceReference = (ResourceReference<MetaDocumentResource, Long>) fieldValue;
                Optional<MetaDocumentResourceEntity> optionalDocumentResource = metaDocumentResourceRepository.findById(resourceReference.getId());
                optionalDocumentResource.ifPresent(metaDocumentResourceEntity -> {
                    target.setNtiOrigen(metaDocumentResourceEntity.getNtiOrigen());
                    target.setNtiEstadoElaboracion(metaDocumentResourceEntity.getNtiEstadoElaboracion());
                });
            } else {
                target.setNtiOrigen(null);
                target.setNtiEstadoElaboracion(null);
            }
        }
    }
}
