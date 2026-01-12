package es.caib.ripea.service.resourceservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaDadaEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentResourceEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.MetaDocumentHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.resourceservice.MetaDocumentResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaDocumentResourceServiceImpl extends BaseMutableResourceService<MetaDocumentResource, Long, MetaDocumentResourceEntity> implements MetaDocumentResourceService {
	
	private final ExpedientRepository expedientRepository;
	private final MetaDadaRepository metaDadaRepository;
	private final DocumentRepository documentRepository;
	private final OrganGestorRepository organGestorRepository;
	private final MetaDocumentHelper metaDocumentHelper;
	private final MetaExpedientHelper metaExpedientHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final ConfigHelper configHelper;
	
    @PostConstruct
    public void init() {
    	register(MetaDocumentResource.PERSPECTIVE_COUNT_METADADES, new CountMetaDadesPerspectiveApplicator());
    }
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	List<String> namedQueriesList = namedQueries!=null ?Stream.of(namedQueries).collect(Collectors.toList()) : Collections.emptyList();

        String entitatActualCodi = configHelper.getEntitatActualCodi();
    	
        Filter filtreBase = FilterBuilder.and(
                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
                FilterBuilder.equal(ContingutResource.Fields.entitat + "." + EntitatResource.Fields.codi, 
                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
//                ,FilterBuilder.equal(ExpedientResource.Fields.organGestor + ".codi", organActualCodi)
        );

        Filter filtreResultat = null;
        List<MetaDocumentEntity> idsMetaDocsPermesos = null;

        for (String namedQuery : namedQueriesList) {
            String[] split = namedQuery.split("#");

            //Metadocuments disponibles per creació
            ExpedientEntity expedientEntity = null;

            switch (split[0]) {
                case "CREATE_NEW_DOC":
                    expedientEntity = expedientRepository.findById(Long.parseLong(split[1])).get();
                    idsMetaDocsPermesos = metaDocumentHelper.findMetaDocumentsDisponiblesPerCreacio(
                            expedientEntity.getEntitat(),
                            expedientEntity,
                            expedientEntity.getMetaExpedient(),
                            false);
                    break;
                case "UPDATE_DOC":
                    DocumentEntity documentEntity = documentRepository.findById(Long.parseLong(split[1])).get();
                    expedientEntity = documentEntity.getExpedient();
                    idsMetaDocsPermesos = metaDocumentHelper.findMetaDocumentsDisponiblesPerCreacio(
                            expedientEntity.getEntitat(),
                            expedientEntity,
                            expedientEntity.getMetaExpedient(),
                            false);
                    if (documentEntity.getMetaDocument()!=null) {
                    	idsMetaDocsPermesos.add(documentEntity.getMetaDocument());
                    }
                    break;
                case "PINBAL_DOC":
                    expedientEntity = expedientRepository.findById(Long.parseLong(split[1])).get();
                    idsMetaDocsPermesos = metaDocumentHelper.findMetaDocumentsPinbalDisponiblesPerCreacio(expedientEntity);
                    break;
            }

            Filter filtreTipusDocsPermesos = null;
            List<String> grupsTipusDocs = Utils.getIdsEnGruposMil(getIdsFromEntitats(idsMetaDocsPermesos));
            if (grupsTipusDocs!=null) {
                for (String aux: grupsTipusDocs) {
                    if (aux != null && !aux.isEmpty()) {
                        filtreTipusDocsPermesos = FilterBuilder.or(filtreTipusDocsPermesos, Filter.parse("id" + " IN (" + aux + ")"));
                    }
                }
            }

            if (filtreTipusDocsPermesos!=null) {
                filtreResultat = FilterBuilder.and(filtreResultat, filtreTipusDocsPermesos);
            }
        }
        
        return FilterBuilder.and(filtreBase, filtreResultat).generate();
    }
    
    private List<Long> getIdsFromEntitats(List<MetaDocumentEntity> metaDocsList) {
        List<Long> resultat = new ArrayList<>();
        if (metaDocsList != null) {
            for (MetaDocumentEntity resource : metaDocsList) {
                resultat.add(resource.getId());
            }
        }
        return resultat;
    }
    
    private class CountMetaDadesPerspectiveApplicator implements PerspectiveApplicator<MetaDocumentResourceEntity, MetaDocumentResource> {
		@Override
		public void applySingle(String code, MetaDocumentResourceEntity entity, MetaDocumentResource resource) throws PerspectiveApplicationException {
			//Es reaprofita un metode existent, tot i que no faria falta la ordenació
			List<MetaDadaEntity> mtdds = metaDadaRepository.findByMetaNodeIdOrderByOrdreAsc(entity.getId()); 
			resource.setNumMetadades(mtdds!=null?mtdds.size():0);
		}
    }
    
    @Override
    public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
    	try {
    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
    		OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), configHelper.getOrganActualCodi());
    		metaDocumentHelper.delete(entitatEntity.getId(), null, id, configHelper.getRolActual(), ogEntity!=null?ogEntity.getId():null);
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/metaDocumentResource/"+id+"/delete", ex);
    		throw new ResourceNotFoundException(getResourceClass(), ex.getMessage());
    	}
    }
    
    @Override
    protected void afterCreateSave(MetaDocumentResourceEntity entity, MetaDocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	
    	//Replicamos las acciones extra que se producen en MetaDocumentHelper.create a parte de la creación de la entidad en BBDD
    	
		if ("IPA_ORGAN_ADMIN".equals(configHelper.getRolActual())) {
			EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
			OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), configHelper.getOrganActualCodi());
			metaExpedientHelper.canviarRevisioADisseny(
					entity.getEntitat().getId(),
					entity.getMetaExpedient()!=null?entity.getMetaExpedient().getId():null,
					ogEntity!=null?ogEntity.getId():null);
		}
    	
		updateFluxosFirma(entity);
    }
    
    @Override
    protected void afterUpdateSave(MetaDocumentResourceEntity entity, MetaDocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
    	updateFluxosFirma(entity);
    }
    
    private void updateFluxosFirma(MetaDocumentResourceEntity entity) {
		if (entity.getFluxosFirma()!=null && entity.getFluxosFirma().size()>0) {
			String[] nombres = new String[3];
			for (int f=0; f<entity.getFluxosFirma().size(); f++) {
				nombres[f] = entity.getFluxosFirma().get(f).getPortafirmesFluxId();
			}
			metaDocumentHelper.updateFluxosFirmaMetaDoc(entity.getId(), nombres);
		}
    }
}