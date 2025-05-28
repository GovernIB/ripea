package es.caib.ripea.service.resourceservice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentResourceEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.MetaDocumentHelper;
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
	private final DocumentRepository documentRepository;
	private final MetaDocumentHelper metaDocumentHelper;
	
    @Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
    	
    	/**
    	 * Current spring filter, per defecte: documents actius, de la entitat actual.
    	 */
    	Filter filtreResultat = Filter.parse(currentSpringFilter);
    	if (namedQueries!=null && namedQueries.length>0) {
    		
    		String[] split = namedQueries[0].split("#");
    		
    		//Metadocuments disponibles per creació
    		List<MetaDocumentEntity> idsMetaDocsPermesos = null;
    		ExpedientEntity expedientEntity = null;
    				
	    	if (Stream.of(split[0]).anyMatch("CREATE_NEW_DOC"::equals)) {
	    		expedientEntity = expedientRepository.findById(Long.parseLong(split[1])).get();
	    		idsMetaDocsPermesos = metaDocumentHelper.findMetaDocumentsDisponiblesPerCreacio(
	    				expedientEntity.getEntitat(),
	    				expedientEntity,
	    				expedientEntity.getMetaExpedient(),
	    				false);	    		
	    	} else if (Stream.of(split[0]).anyMatch("UPDATE_DOC"::equals)) {
	    		DocumentEntity documentEntity = documentRepository.findById(Long.parseLong(split[1])).get();
	    		expedientEntity = documentEntity.getExpedient();
	    		idsMetaDocsPermesos = metaDocumentHelper.findMetaDocumentsDisponiblesPerCreacio(
	    				expedientEntity.getEntitat(),
	    				expedientEntity,
	    				expedientEntity.getMetaExpedient(),
	    				false);	
	    		idsMetaDocsPermesos.add(documentEntity.getMetaDocument());
	    	} else if (Stream.of(split[0]).anyMatch("PINBAL_DOC"::equals)) {
	    		expedientEntity = expedientRepository.findById(Long.parseLong(split[1])).get();
	    		idsMetaDocsPermesos = metaDocumentHelper.findMetaDocumentsPinbalDisponiblesPerCreacio(expedientEntity.getMetaExpedient().getId());
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

	    	filtreResultat = FilterBuilder.and(filtreResultat, filtreTipusDocsPermesos);
    	}
    	
    	return filtreResultat.generate();
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
}