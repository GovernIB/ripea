package es.caib.ripea.service.resourceservice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        List<String> namedQueriesList = Stream.of(namedQueries).collect(Collectors.toList());

    	/**
    	 * Current spring filter, per defecte: documents actius, de la entitat actual.
    	 */
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
                    idsMetaDocsPermesos.add(documentEntity.getMetaDocument());
                    break;
                case "PINBAL_DOC":
                    expedientEntity = expedientRepository.findById(Long.parseLong(split[1])).get();
                    idsMetaDocsPermesos = metaDocumentHelper.findMetaDocumentsPinbalDisponiblesPerCreacio(expedientEntity.getMetaExpedient().getId());
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

        if (filtreResultat != null && !filtreResultat.isEmpty()) {
            Filter filtreBase = (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null;
            return FilterBuilder.and(filtreBase, filtreResultat).generate();
        }

    	return currentSpringFilter;
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