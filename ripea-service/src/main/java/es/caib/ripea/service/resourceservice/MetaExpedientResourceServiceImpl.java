package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.turkraft.springfilter.FilterBuilder;
import com.turkraft.springfilter.parser.Filter;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.MetaExpedientRevisioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ProcedimentDto;
import es.caib.ripea.service.intf.dto.TipusClassificacioEnumDto;
import es.caib.ripea.service.intf.model.EntitatResource;
import es.caib.ripea.service.intf.model.MetaExpedientResource;
import es.caib.ripea.service.intf.model.MetaNodeResource;
import es.caib.ripea.service.intf.model.OrganGestorResource;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import es.caib.ripea.service.permission.ExtendedPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaExpedientResourceServiceImpl extends BaseMutableResourceService<MetaExpedientResource, Long, MetaExpedientResourceEntity> implements MetaExpedientResourceService {

	private final ConfigHelper configHelper;
	private final MetaExpedientHelper metaExpedientHelper;
	private final EntityComprovarHelper entityComprovarHelper;
	private final UsuariResourceRepository usuariResourceRepository;
	private final OrganGestorRepository organGestorRepository;
	private final PluginHelper pluginHelper;
	private final MessageHelper messageHelper;

    @PostConstruct
    public void init() {
    	register(MetaExpedientResource.PERSPECTIVE_AUDIT_CODE, new AuditoriaPerspectiveApplicator());
    	register(MetaExpedientResource.Fields.classificacio, new OnchangeLogicProcessor());
    	register(MetaExpedientResource.Fields.tipusClassificacio, new OnchangeLogicProcessor());
    	register(MetaExpedientResource.Fields.organGestor, new OnchangeLogicProcessor());
    	register(MetaExpedientResource.Fields.procedimentComu, new OnchangeLogicProcessor());
    }
	
	@Override
    protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

        String entitatActualCodi = configHelper.getEntitatActualCodi();
//        String organActualCodi	 = configHelper.getOrganActualCodi();
        String rolActual		 = configHelper.getRolActual();
//        String organGestorFiltre = Utils.getValorCampFiltre("organGestor.id", currentSpringFilter);
        
		boolean isAdmin = "IPA_ADMIN".equals(rolActual);
		boolean isAdminOrgan = "IPA_ORGAN_ADMIN".equals(rolActual);
//		boolean isDissenyador = "IPA_DISSENY".equals(rolActual);
//		boolean usuariFiltreOrgan = isAdminOrgan || isDissenyador;
        
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true,false);
    	Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
		
		Filter filtreBase = null;
		//Si ja ve un filtre definit per entitat, no aplicarem el filtre de entitat actual.
		if (currentSpringFilter==null || !currentSpringFilter.contains("entitat.id")) {
	        filtreBase = FilterBuilder.and(
	                (currentSpringFilter != null && !currentSpringFilter.isEmpty())?Filter.parse(currentSpringFilter):null,
	                FilterBuilder.equal(MetaNodeResource.Fields.entitat + "." + EntitatResource.Fields.codi,
	                		entitatActualCodi != null?entitatActualCodi:"................................................................................")
	        );
		}

        List<Long> procsPermesosIds = new ArrayList<Long>();
        List<MetaExpedientEntity> metaExpPermesos = null;
        Filter revisioActiva = null; //Dependrà de la propietat PropertyConfig.METAEXPEDIENT_REVISIO_ACTIVA
        if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("EXPEDIENT_CREATE")) {
            metaExpPermesos = metaExpedientHelper.findAmbPermis(
            		entitat.getId(),
            		ExtendedPermission.CREATE,
            		true, //nomesActius
            		null, //filtreNomOrCodiSia
            		isAdmin,
            		isAdminOrgan,
            		null, //organId
            		false); //comú
            
            if (metaExpedientHelper.isRevisioActiva()) {
            	revisioActiva = Filter.parse(MetaExpedientResource.Fields.revisioEstat+":'"+MetaExpedientRevisioEstatEnumDto.REVISAT.toString()+"'");
            }
            
        } else if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("EXPEDIENT_UPDATE")) {
            metaExpPermesos = metaExpedientHelper.findAmbPermis(
            		entitat.getId(),
            		ExtendedPermission.WRITE,
            		false, //nomesActius
            		null, //filtreNomOrCodiSia
            		isAdmin,
            		isAdminOrgan,
            		null, //organId
            		false); //comú
        } else if (mapaNamedQueries.size()>0 && mapaNamedQueries.containsKey("CONSULTA_REVISIO_ESTAT")) {
        	//Volem replicar metaExpedientServiceImpl.findByEntitat
        	//Nom comprova cap permis, ja que es un manteniment per admins
        	//Nomes filtra per entitat o els filtres del formulari de cerca.
        	return filtreBase.generate();
        } else { //Llistat de procediments
            metaExpPermesos = metaExpedientHelper.findAmbPermis(
            		entitat.getId(),
            		ExtendedPermission.READ,
            		false, //nomesActius
            		null, //filtreNomOrCodiSia
            		isAdmin,
            		isAdminOrgan,
            		null, //organId
            		false); //comú
        }
        
		if (metaExpPermesos==null || metaExpPermesos.size()==0) {
			return FilterBuilder.equal("id", 0).generate();
		} else {
			for (MetaExpedientEntity mee: metaExpPermesos) {
				procsPermesosIds.add(mee.getId());
			}
		}
		
		Filter filtrePermisos = null;
        List<String> permesosClausulesIn = Utils.getIdsEnGruposMil(procsPermesosIds);
        if (permesosClausulesIn!=null) {
	        for (String aux: permesosClausulesIn) {
		        if (aux != null && !aux.isEmpty()) {
		        	filtrePermisos = FilterBuilder.or(filtrePermisos, Filter.parse("id IN (" + aux + ")"));
		        }
	        }
        }
        
		Filter filtreResultat = FilterBuilder.and(filtreBase, revisioActiva, filtrePermisos);
        
        return filtreResultat.generate();
    }

    @Override
    protected void afterConversion(MetaExpedientResourceEntity entity, MetaExpedientResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
        resource.setProcedimentComu(entity.getOrganGestor()==null);
    }

    private class OnchangeLogicProcessor implements OnChangeLogicProcessor<MetaExpedientResource> {
		@Override
		public void onChange(Serializable id, MetaExpedientResource previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, MetaExpedientResource target) {
			
			if (MetaExpedientResource.Fields.procedimentComu.equals(fieldName)) {
				
				if (fieldValue==null || (boolean)fieldValue) {
					target.setOrganGestor(null);
					target.setClassificacio(null);
					target.setTipusClassificacio(TipusClassificacioEnumDto.SIA);
				}
				
			} else if (MetaExpedientResource.Fields.classificacio.equals(fieldName)) {
				
				if (TipusClassificacioEnumDto.SIA.equals(previous.getTipusClassificacio())) {
					
					if (fieldValue==null) {
						target.setMsgSiaRolsac(null);
					} else {
						
						String rolActual = configHelper.getRolActual();
						boolean rolOrgan = rolActual.equals("IPA_DISSENY") || rolActual.equals("IPA_ORGAN_ADMIN");
						String entitatActualCodi = configHelper.getEntitatActualCodi();
						EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatActualCodi, false, false, false, true, false);
						
						String codiDir3 = null;
						if (rolOrgan) {
							String organActualCodi	 = configHelper.getOrganActualCodi();
							OrganGestorEntity ogEntity	= organGestorRepository.findByEntitatIdAndCodi(entitatEntity.getId(), organActualCodi);
							if (ogEntity!=null) {
								codiDir3 = ogEntity.getCodi();
							}
						} else {
							codiDir3 = entitatEntity.getUnitatArrel();
						}
						ProcedimentDto procedimentDto = null;
						String msgSiaRolsac = null;
						try {
							if (codiDir3!=null) {
								procedimentDto = pluginHelper.procedimentFindByCodiSia(codiDir3, fieldValue.toString());
							}
						} catch (Exception e) {
							msgSiaRolsac = messageHelper.getMessage("MetaExpedientResource.msgSiaRolsac.NA");
						}
						
						if (procedimentDto == null) {
							msgSiaRolsac = messageHelper.getMessage("MetaExpedientResource.msgSiaRolsac.NO");
						}
						
						target.setMsgSiaRolsac(msgSiaRolsac);
					}
				}

			} else if (MetaExpedientResource.Fields.tipusClassificacio.equals(fieldName)) {
				 
				if (TipusClassificacioEnumDto.ID.equals(fieldValue)) {
					String idCalculat = null;
					if (previous.getOrganGestor()!=null) {
						idCalculat = getIdCalculadoOrganoGestor(previous.getOrganGestor().getId());
					}
					target.setClassificacio(idCalculat);
				} else {
					target.setClassificacio(null);
				}
				
			} else if (MetaExpedientResource.Fields.organGestor.equals(fieldName)) {
				if (TipusClassificacioEnumDto.ID.equals(previous.getTipusClassificacio())) {
					String idCalculat = null;
					if (fieldValue != null) {
						ResourceReference<OrganGestorResource, Long> resourceReference = (ResourceReference<OrganGestorResource, Long>) fieldValue;
						idCalculat = getIdCalculadoOrganoGestor(resourceReference.getId());
					}
					target.setClassificacio(idCalculat);
				}
			}
		}
    }
    
    private String getIdCalculadoOrganoGestor(Long organActualId) {
    	try {
    		OrganGestorEntity ogEntity	= organGestorRepository.findById(organActualId).get();
    		return ogEntity.getCodi() +  "_PRO_" + String.format("%030d", System.currentTimeMillis()) + "3F";
    	} catch (Exception e) {
    		return null;	
		}
    }
    
    private class AuditoriaPerspectiveApplicator implements PerspectiveApplicator<MetaExpedientResourceEntity, MetaExpedientResource> {
        @Override
        public void applySingle(String code, MetaExpedientResourceEntity entity, MetaExpedientResource resource) throws PerspectiveApplicationException {
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
}