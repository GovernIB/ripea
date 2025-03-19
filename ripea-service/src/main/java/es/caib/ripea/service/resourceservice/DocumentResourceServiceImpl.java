package es.caib.ripea.service.resourceservice;

import es.caib.ripea.persistence.entity.resourceentity.*;
import es.caib.ripea.persistence.entity.resourcerepository.MetaDocumentResourceRepository;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotUpdatedException;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.*;
import es.caib.ripea.service.intf.model.InteressatResource;
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
        register(DocumentResource.Fields.adjunt, new AdjuntOnchangeLogicProcessor());
        register(DocumentResource.Fields.firmaAdjunt, new FirmaAdjuntOnchangeLogicProcessor());
        register(DocumentResource.Fields.hasFirma, new HasFirmaOnchangeLogicProcessor());
    }

    @Override
    protected void beforeCreateSave(DocumentResourceEntity entity, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        beforeSave(entity, resource, answers);

        entity.setEstat(entity.getDocumentFirmaTipus() == DocumentFirmaTipusEnumDto.SENSE_FIRMA ? DocumentEstatEnumDto.REDACCIO : DocumentEstatEnumDto.FIRMAT);
        entity.setTipus(ContingutTipusEnumDto.DOCUMENT);
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
        beforeSave(entity, resource, answers);
    }

    private void beforeSave(DocumentResourceEntity entity, DocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotUpdatedException {
        Optional<MetaDocumentResourceEntity> optionalDocumentResource = metaDocumentResourceRepository.findById(resource.getMetaDocument().getId());
        optionalDocumentResource.ifPresent((metaDocumentResourceEntity -> {
            entity.setMetaNode(metaDocumentResourceEntity);
            entity.setNtiTipoDocumental(metaDocumentResourceEntity.getNtiTipoDocumental());
        }));

        if (resource.getDocumentFirmaTipus() == DocumentFirmaTipusEnumDto.FIRMA_SEPARADA){
            /* TODO: (PluginHelper.gestioDocumentalCreate) */
        }
    }

    @Override
    protected void afterConversion(DocumentResourceEntity entity, DocumentResource resource) {
        if(entity.getMetaNode()!=null) {
            resource.setMetaDocument(ResourceReference.toResourceReference(entity.getMetaNode().getId(), entity.getMetaNode().getNom()));
        }
        resource.setAdjunt(new FileReference(
                entity.getFitxerNom(),
                entity.getFitxerContingut(),
                entity.getFitxerContentType(),
                entity.getFitxerTamany()
        ));
        resource.setFirmaAdjunt(new FileReference(
                entity.getNomFitxerFirmat(),
                null,
                null,
                null
        ));
        resource.setHasFirma(resource.getDocumentFirmaTipus()!=DocumentFirmaTipusEnumDto.SENSE_FIRMA);
    }

    private class PathPerspectiveApplicator implements PerspectiveApplicator<DocumentResourceEntity, DocumentResource> {
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
        public void onChange(
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
    private class AdjuntOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {

        private static final String ERROR_SIGNATURE_VALIDATION= "ERROR_SIGNATURE_VALIDATION";

        @Override
        public void onChange(
                DocumentResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                DocumentResource target) {

            if (fieldValue != null) {
                FileReference adjunt = (FileReference) fieldValue;

                target.setFitxerNom(adjunt.getName());
                target.setFitxerContingut(adjunt.getContent());
                target.setFitxerTamany(adjunt.getContentLength());
                target.setFitxerContentType(adjunt.getContentType());

                // TODO: cambiar (DocumentService.checkIfSignedAttached())
                SignatureInfoDto signatureInfoDto = new SignatureInfoDto(false);

                target.setHasFirma(signatureInfoDto.isSigned());
                target.setValidacioFirmaCorrecte(!signatureInfoDto.isError());
                target.setValidacioFirmaErrorMsg(signatureInfoDto.getErrorMsg());

                if (signatureInfoDto.isSigned()) {
//                    target.setNtiTipoFirma();
                    target.setDocumentFirmaTipus(DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA);

                    if (signatureInfoDto.isError() && !answers.containsKey(ERROR_SIGNATURE_VALIDATION)) {
                        throw new AnswerRequiredException(InteressatResource.class, ERROR_SIGNATURE_VALIDATION, signatureInfoDto.getErrorMsg());
                    }
                }
            } else {
                target.setFitxerNom(null);
                target.setFitxerContingut(null);
                target.setFitxerTamany(null);
                target.setFitxerContentType(null);
                target.setValidacioFirmaCorrecte(false);
                target.setValidacioFirmaErrorMsg("");
                target.setHasFirma(false);
            }
        }
    }
    private class FirmaAdjuntOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void onChange(
                DocumentResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                DocumentResource target) {

            if (fieldValue != null) {
                FileReference adjunt = (FileReference) fieldValue;
                target.setNomFitxerFirmat(adjunt.getName());
            } else {
                target.setNomFitxerFirmat(null);
            }
        }
    }
    private class HasFirmaOnchangeLogicProcessor implements OnChangeLogicProcessor<DocumentResource> {
        @Override
        public void onChange(
                DocumentResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                DocumentResource target) {

            if (target.getDocumentFirmaTipus()!=DocumentFirmaTipusEnumDto.FIRMA_ADJUNTA){
                target.setDocumentFirmaTipus((fieldValue != null && (Boolean) fieldValue)
                        ?DocumentFirmaTipusEnumDto.FIRMA_SEPARADA
                        :DocumentFirmaTipusEnumDto.SENSE_FIRMA);
            }
        }
    }
}
