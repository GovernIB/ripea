package es.caib.ripea.service.resourceservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import es.caib.ripea.persistence.entity.MetaDocumentFluxPortafibEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentFluxPortafibResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaDocumentResourceEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientRepository;
import es.caib.ripea.persistence.repository.MetaDadaRepository;
import es.caib.ripea.persistence.repository.MetaDocumentFluxPortafibRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.MetaDocumentHelper;
import es.caib.ripea.service.helper.UsuariHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.base.model.FieldOption;
import es.caib.ripea.service.intf.base.model.FileReference;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.MetaDocumentDto;
import es.caib.ripea.service.intf.dto.PinbalServeiDto;
import es.caib.ripea.service.intf.dto.UsuariDto;
import es.caib.ripea.service.intf.model.ContingutResource;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaDocumentFluxPortafibResource;
import es.caib.ripea.service.intf.model.MetaDocumentResource;
import es.caib.ripea.service.intf.model.UsuariResource;
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
	private final MetaDocumentFluxPortafibRepository metaDocumentFluxPortafibRepository;
	private final TipusDocumentalRepository tipusDocumentalRepository;
	private final MetaDocumentHelper metaDocumentHelper;
	private final UsuariHelper usuariHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final ConfigHelper configHelper;

    @PostConstruct
    public void init() {
    	register(MetaDocumentResource.PERSPECTIVE_COUNT_METADADES,	new CountMetaDadesPerspectiveApplicator());
    	register(MetaDocumentResource.Fields.ntiTipoDocumental, 	new TipusDocFieldOptionsProvider());
    }
    
    public class TipusDocFieldOptionsProvider implements FieldOptionsProvider {
        public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
            List<FieldOption> resultat = new ArrayList<FieldOption>();
            List<TipusDocumentalEntity> tipusDocumentalEntity = tipusDocumentalRepository.findAll();
            if (tipusDocumentalEntity != null) {
                for (TipusDocumentalEntity tde : tipusDocumentalEntity) {
                	resultat.add(new FieldOption(
                			tde.getCodi(),
                			tde.getNomCatala()!=null?tde.getNomCatala():tde.getNomEspanyol()));
                }
            }
            resultat.sort(Comparator.comparing(FieldOption::getDescription));
            return resultat;
        }
    }
    
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
    
    @Override
    protected void afterConversion(MetaDocumentResourceEntity entity, MetaDocumentResource resource) {
    	if (entity.getPlantillaContingut()!=null && entity.getPlantillaContingut().length>0) {
    		FileReference plantilla = new FileReference(
    				entity.getPlantillaNom(),
    				entity.getPlantillaContingut(),
    				entity.getPlantillaContentType(),
    				new Long(entity.getPlantillaContingut().length));
    		resource.setPlantilla(plantilla);
    	}
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
	public MetaDocumentResource create(MetaDocumentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
		OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), configHelper.getOrganActualCodi());
		
		metaDocumentHelper.create(
				entitatEntity.getId(),
				resource.getMetaExpedient()!=null?resource.getMetaExpedient().getId():null,
				resourceToMetaDocumentDto(resource),
				resource.getPlantilla()!=null?resource.getPlantilla().getName():null,
				resource.getPlantilla()!=null?resource.getPlantilla().getContentType():null,
				resource.getPlantilla()!=null?resource.getPlantilla().getContent():null,
				configHelper.getRolActual(),
				ogEntity!=null?ogEntity.getId():null);
		
		return resource;
	}
    
	@Override
	public MetaDocumentResource update(
			Long id,
			MetaDocumentResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		
		metaDocumentHelper.update(
				resource.getMetaExpedient()!=null?resource.getMetaExpedient().getId():null,
				resourceToMetaDocumentDto(resource),
				resource.getPlantilla()!=null?resource.getPlantilla().getName():null,
				resource.getPlantilla()!=null?resource.getPlantilla().getContentType():null,
				resource.getPlantilla()!=null?resource.getPlantilla().getContent():null);
		
		return resource;
	}
	
	@Override
	protected MetaDocumentResource entityToResource(MetaDocumentResourceEntity entity) {
		MetaDocumentResource metaDocumentResource = objectMappingHelper.newInstanceMap(
				entity,
				getResourceClass(),
				"portafirmesResponsables");
		if (Utils.hasValue(entity.getPortafirmesResponsables())) {
			List<ResourceReference<UsuariResource, String>> responsables = new ArrayList<>();
			String[] pfResponsables = entity.getPortafirmesResponsables().split(",");
            for (String codi : pfResponsables) {
            	UsuariDto usuariResponsable = usuariHelper.findUsuariCarrecAmbCodiDades(codi);
                if (usuariResponsable != null) {
                	String txtDisplay = usuariResponsable.getNom() + " (" + Utils.nifMask(usuariResponsable.getNif()) +")";
                    responsables.add(ResourceReference.toResourceReference(usuariResponsable.getCodi(), txtDisplay));
                }
            }
            metaDocumentResource.setPortafirmesResponsables(responsables);
		}
		if (entity.getPinbalServei()!=null) {
			metaDocumentResource.setPinbalServei(ResourceReference.toResourceReference(
					entity.getPinbalServei().getId(),
					entity.getPinbalServei().getNom()));
		}
		if (entity.getFluxosFirma()!=null && entity.getFluxosFirma().size()>0) {
			List<ResourceReference<MetaDocumentFluxPortafibResource, Long>> fluxosFirma = new ArrayList<>();
			for (MetaDocumentFluxPortafibResourceEntity mdfpre: entity.getFluxosFirma()) {
				fluxosFirma.add(ResourceReference.toResourceReference(mdfpre.getId(), mdfpre.getPortafirmesFluxDesc()));
			}
			metaDocumentResource.setFluxosFirma(fluxosFirma);
		}
		return metaDocumentResource;
	}
	
	private MetaDocumentDto resourceToMetaDocumentDto(MetaDocumentResource resource) {
		MetaDocumentDto metaDocumentDto = objectMappingHelper.newInstanceMap(
				resource,
				MetaDocumentDto.class,
				"serialVersionUID", "createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate");
		if (resource.getPortafirmesResponsables()!=null && resource.getPortafirmesResponsables().size()>0) {
			String[] responsablesFirma = new String[resource.getPortafirmesResponsables().size()];
			for (int r=0; r<resource.getPortafirmesResponsables().size(); r++) {
				ResourceReference<UsuariResource, String> responsable = resource.getPortafirmesResponsables().get(r);
				responsablesFirma[r] = responsable.getId();
			}
			metaDocumentDto.setPortafirmesResponsables(responsablesFirma);
		}
		if (resource.getPinbalServei()!=null) {
			PinbalServeiDto pinbalServeiDto = new PinbalServeiDto();
			pinbalServeiDto.setId(resource.getPinbalServei().getId());
			metaDocumentDto.setPinbalServei(pinbalServeiDto);
		}
		if (resource.getFluxosFirma()!=null) {
			String[] fluxosFirma = new String[resource.getFluxosFirma().size()];
			for (int f=0; f<resource.getFluxosFirma().size(); f++) {
				MetaDocumentFluxPortafibEntity mdfpre = metaDocumentFluxPortafibRepository.findById(
						resource.getFluxosFirma().get(f).getId()).get();
				fluxosFirma[f] = mdfpre.getPortafirmesFluxId();
			}
			metaDocumentDto.setPortafirmesFluxosId(fluxosFirma);
		}
		return metaDocumentDto;
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
}