package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ContingutResourceRepository;
import es.caib.ripea.persistence.repository.CarpetaRepository;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.service.helper.*;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.NodeResource.MassiveAction;
import es.caib.ripea.service.intf.resourceservice.ContingutResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContingutResourceServiceImpl 
extends BaseMutableResourceService<ContingutResource, Long, ContingutResourceEntity<ContingutResource>> 
implements ContingutResourceService {
	
	private final ConfigHelper configHelper;
	private final UsuariResourceRepository usuariResourceRepository;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final MessageHelper messageHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final ContingutHelper contingutHelper;
    private final ContingutResourceRepository contingutResourceRepository;
    private final DocumentRepository documentRepository;
    private final CarpetaRepository carpetaRepository;
    private final PluginHelper pluginHelper;
    private final ContingutRepository contingutRepository;

    @PostConstruct
    public void init() {
    	register(ContingutResource.PERSPECTIVE_AUDIT_CODE, new AuditoriaPerspectiveApplicator());
        register(ContingutResource.ACTION_DELETE_CODE, new DeleteDefinitiuActionExecutor());
        register(ContingutResource.ACTION_RECUPERAR_CODE, new RecuperarActionExecutor());
        register(ContingutResource.ACTION_REORDER, new ReorderActionExecutor());
    }
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	String entitatActualCodi = configHelper.getEntitatActualCodi();
//        String rolActual		 = configHelper.getRolActual();
//    	EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
        );
        
        return filtreBase.generate();
    }

    @Override
    protected List<ContingutResourceEntity<ContingutResource>> reorderFindLinesWithParentAndSorted(Serializable parentId) {
        return contingutResourceRepository.findAllByPareIdAndEsborratOrderByOrdreAsc((Long)parentId, 0);
    }

    private class ReorderActionExecutor implements ActionExecutor<ContingutResourceEntity<ContingutResource>, ContingutResource.ReordenarForm, Serializable> {
        @Override
        public void onChange(Serializable id, ContingutResource.ReordenarForm previous, String fieldName, Object fieldValue,
                             Map<String, AnswerValue> answers, String[] previousFieldNames, ContingutResource.ReordenarForm target) {}

        @Override
        public Serializable exec(String code, ContingutResourceEntity entity, ContingutResource.ReordenarForm resource) throws ActionExecutionException {
            Long reorderPreviousSequence = reorderGetPreviousSequence(entity);
            Long reorderPreviousParentId = reorderGetParentId(entity);
            ContingutResource contingutResource = objectMappingHelper.newInstanceMap(entity, ContingutResource.class);
            contingutResource.setOrdre(Math.toIntExact(resource.getOrdre()));
            contingutResource.setPare(ResourceReference.toResourceReference(resource.getPare()));
            Long reorderNewSequence = reorderGetNewSequence(contingutResource);
            if (!Objects.equals(resource.getPare(), entity.getPare().getId())) {
                entity.setPare(contingutResourceRepository.findById(resource.getPare()).get());
            }
            reorderIfReorderable(
                    entity,
                    reorderPreviousSequence,
		            reorderNewSequence,
                    reorderPreviousParentId,
                    false);

            boolean parentIdChanged = !Objects.equals(entity.getOrderParentId(), reorderPreviousParentId);
            if (parentIdChanged) {
                if (ContingutTipusEnumDto.DOCUMENT.equals(entity.getTipus())) {
                    DocumentEntity documentActual = documentRepository.findById(entity.getId()).get();
                    if (Utils.hasValue(documentActual.getArxiuUuid())) {
	                    contingutHelper.arxiuDocumentPropagarMoviment(
	                            entity.getArxiuUuid(),
	                            documentActual.getPare(),
	                            entity.getExpedient().getArxiuUuid());
                    }
                } else if (ContingutTipusEnumDto.CARPETA.equals(entity.getTipus())) {
                    CarpetaEntity carpetaActual = carpetaRepository.findById(entity.getId()).get();
                    //mourer també al arxiu
                    if (Utils.hasValue(carpetaActual.getArxiuUuid())) {
	                    pluginHelper.arxiuCarpetaMoure(
	                            carpetaActual,
	                            contingutRepository.findById(entity.getOrderParentId()).get().getArxiuUuid());
                    }
                }
            }

            return contingutResource;
        }
    }
    
    private class AuditoriaPerspectiveApplicator implements PerspectiveApplicator<ContingutResourceEntity<ContingutResource>, ContingutResource> {
        @Override
        public void applySingle(String code, ContingutResourceEntity<ContingutResource> entity, ContingutResource resource) throws PerspectiveApplicationException {
        	if (entity.getCreatedBy()!=null) {
        		UsuariResourceEntity usuariResourceEntity = usuariResourceRepository.findById(entity.getCreatedBy()).orElse(null);
        		if (usuariResourceEntity!=null) {
        			resource.setCreatedByFullName(usuariResourceEntity.getNom() + " (" + usuariResourceEntity.getCodi() + ")");
        		}
        	}
        	if (entity.getLastModifiedBy()!=null) {
        		UsuariResourceEntity usuariResourceEntity = usuariResourceRepository.findById(entity.getLastModifiedBy()).orElse(null);
        		resource.setLastModifiedByFullName(usuariResourceEntity.getNom() + " (" + usuariResourceEntity.getCodi() + ")");
        	}
        }
    }
    
    private class RecuperarActionExecutor implements ActionExecutor<ContingutResourceEntity<ContingutResource>, MassiveAction, Serializable> {

		@Override
		public Serializable exec(String code, ContingutResourceEntity<ContingutResource> entity, MassiveAction params) throws ActionExecutionException {
			try {
				entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), true, false, false, false, false);
				if (params.getIds()!=null) {
					for (Long id: params.getIds()) {
						contingutHelper.undelete(id);
					}
				}
				int numElem = params!=null && params.getIds()!=null?params.getIds().size():0;
				return "{\"num\": \""+numElem+"\"}";
			} catch (Exception e) {
				String docIdStr = Utils.getIdsSeparatsComa(params.getIds());
				excepcioLogHelper.addExcepcio("/contingut/RecuperarActionExecutor", e, docIdStr, "massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), docIdStr, code, message);
			}
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {
		}
    }
    
    private class DeleteDefinitiuActionExecutor implements ActionExecutor<ContingutResourceEntity<ContingutResource>, MassiveAction, Serializable> {

		@Override
		public Serializable exec(String code, ContingutResourceEntity<ContingutResource> entity, MassiveAction params) throws ActionExecutionException {
			try {
				entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), true, false, false, false, false);
				if (params.getIds()!=null) {
					for (Long id: params.getIds()) {
						contingutHelper.deleteDefinitiu(id);
					}
				}
				int numElem = params!=null && params.getIds()!=null?params.getIds().size():0;
				return "{\"num\": \""+numElem+"\"}";
			} catch (Exception e) {
				String docIdStr = Utils.getIdsSeparatsComa(params.getIds());
				excepcioLogHelper.addExcepcio("/contingut/DeleteDefinitiuActionExecutor", e, docIdStr, "massiu="+params.isMassivo());
				String message = messageHelper.getMessage("message.common.action.error")+": "+e.getMessage();
				throw new ActionExecutionException(getResourceClass(), docIdStr, code, message);
			}
		}

		@Override
		public void onChange(Serializable id, MassiveAction previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveAction target) {
		}
    }
    
}