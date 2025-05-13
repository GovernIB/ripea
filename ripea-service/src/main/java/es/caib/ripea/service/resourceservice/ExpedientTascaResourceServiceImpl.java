package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientTascaEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.MetaExpedientTascaResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.UsuariResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ExpedientTascaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.MetaExpedientTascaResourceRepository;
import es.caib.ripea.persistence.entity.resourcerepository.UsuariResourceRepository;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.EntityComprovarHelper;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.TascaHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.PerspectiveApplicationException;
import es.caib.ripea.service.intf.base.model.ResourceReference;
import es.caib.ripea.service.intf.dto.ExpedientTascaDto;
import es.caib.ripea.service.intf.dto.MetaExpedientTascaValidacioDto;
import es.caib.ripea.service.intf.dto.PrioritatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.model.ExpedientTascaResource;
import es.caib.ripea.service.intf.model.ExpedientTascaResource.DelegarTascaFormAction;
import es.caib.ripea.service.intf.model.ExpedientTascaResource.ReassignarTascaFormAction;
import es.caib.ripea.service.intf.model.MetaExpedientTascaResource;
import es.caib.ripea.service.intf.model.UsuariResource;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de gestió de tasques.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedientTascaResourceServiceImpl extends BaseMutableResourceService<ExpedientTascaResource, Long, ExpedientTascaResourceEntity> implements ExpedientTascaResourceService {

    private final ExpedientTascaResourceRepository expedientTascaResourceRepository;
    private final MetaExpedientTascaResourceRepository metaExpedientTascaResourceRepository;
    private final UsuariResourceRepository usuariResourceRepository;

    private final ConfigHelper configHelper;
    private final TascaHelper tascaHelper;
    private final ExcepcioLogHelper excepcioLogHelper;
    private final EntityComprovarHelper entityComprovarHelper;
    
	@PostConstruct
	public void init() {
		register(ExpedientTascaResource.PERSPECTIVE_RESPONSABLES_CODE, new ResponsablesPerspectiveApplicator());
        register(ExpedientTascaResource.Fields.metaExpedientTasca, new MetaExpedientTascaOnchangeLogicProcessor());
        register(ExpedientTascaResource.Fields.duracio, new DuracioOnchangeLogicProcessor());
        register(ExpedientTascaResource.Fields.dataLimit, new DataLimitOnchangeLogicProcessor());
        register(ExpedientTascaResource.ACTION_CHANGE_ESTAT_CODE, new ChangeEstatActionExecutor());
        register(ExpedientTascaResource.ACTION_CHANGE_PRIORITAT_CODE, new ChangePrioritatActionExecutor());
        register(ExpedientTascaResource.ACTION_CHANGE_DATALIMIT_CODE, new ChangeDataLimitActionExecutor());
        register(ExpedientTascaResource.ACTION_REABRIR_CODE, new ReobrirActionExecutor());
        register(ExpedientTascaResource.ACTION_REBUTJAR_CODE, new RebutjarActionExecutor());
        register(ExpedientTascaResource.ACTION_RETOMAR_CODE, new RetomarActionExecutor());
        register(ExpedientTascaResource.ACTION_REASSIGNAR_CODE, new ReassignarActionExecutor());
        register(ExpedientTascaResource.ACTION_DELEGAR_CODE, new DelegarActionExecutor());
	}

    @Override
    public ExpedientTascaResource create(ExpedientTascaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
    	try {
    		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(configHelper.getEntitatActualCodi(), false, false, false, true, false);
    		ExpedientTascaEntity tascaCreada = tascaHelper.createTasca(entitatEntity.getId(), resource.getExpedient().getId(), toTascaDto(resource));
    		resource.setId(tascaCreada.getId());
    		return resource;
    	} catch (Exception ex) {
    		excepcioLogHelper.addExcepcio("/document/"+resource.getId()+"/create", ex);
    	}
    	return null;
    }
    
    private ExpedientTascaDto toTascaDto(ExpedientTascaResource resource) {
    	ExpedientTascaDto resultat = new ExpedientTascaDto();
    	resultat.setMetaExpedientTascaId(resource.getMetaExpedientTasca().getId());
    	resultat.setResponsablesCodi(getIdsFromUsuarisResources(resource.getResponsables()));
    	resultat.setObservadorsCodi(getIdsFromUsuarisResources(resource.getObservadors()));
    	resultat.setDataLimit(resource.getDataLimit());
    	resultat.setTitol(resource.getTitol());
    	resultat.setDuracio(resource.getDuracio());
    	resultat.setPrioritat(resource.getPrioritat());
    	resultat.setObservacions(resource.getObservacions());
    	resultat.setComentari(resource.getComentari());
    	return resultat;
    }

    @Override
    protected void afterConversion(ExpedientTascaResourceEntity entity, ExpedientTascaResource resource) {
        resource.setNumComentaris(entity.getComentaris().size());
        resource.setMetaExpedientTascaDescription(entity.getMetaExpedientTasca().getDescripcio());
        resource.setResponsables(entity.getResponsables()
                .stream().map(obs->ResourceReference.<UsuariResource, String>toResourceReference(obs.getId(), obs.getCodiAndNom()))
                .collect(Collectors.toList()));
        resource.setObservadors(entity.getObservadors()
                .stream().map(obs->ResourceReference.<UsuariResource, String>toResourceReference(obs.getId(), obs.getCodiAndNom()))
                .collect(Collectors.toList()));
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean usuariActualResponsable = false;
        if (resource.getResponsables()!=null) {
        	for (ResourceReference<UsuariResource,String> resp: resource.getResponsables()) {
        		if (resp.getId().equals(user)) {
        			usuariActualResponsable = true;
        			break;
        		}
        	}
        }
       	resource.setUsuariActualResponsable(usuariActualResponsable);
        resource.setUsuariActualDelegat(resource.getDelegat()!=null && Objects.equals(resource.getDelegat().getId(), user));
    }

    // PerspectiveApplicator
	private class ResponsablesPerspectiveApplicator implements PerspectiveApplicator<ExpedientTascaResourceEntity, ExpedientTascaResource> {
		@Override
		public void applySingle(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource resource) throws PerspectiveApplicationException {
			if (entity.getResponsables() != null && !entity.getResponsables().isEmpty()) {
				List<String> responsablesStr = new ArrayList<>();
				for (UsuariResourceEntity responsable: entity.getResponsables()) {
					responsablesStr.add(responsable.getCodi());
				}
				resource.setResponsablesStr(StringUtils.join(responsablesStr, ","));
			}
		}
	}

    // OnChangeLogicProcessor
    private class MetaExpedientTascaOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientTascaResource> {
        @Override
        public void onChange(
				Serializable id,
                ExpedientTascaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientTascaResource target) {

            if (fieldValue != null) {
                ResourceReference<MetaExpedientTascaResource, Long> metaExpedientTasca = (ResourceReference<MetaExpedientTascaResource, Long>) fieldValue;
                Optional<MetaExpedientTascaResourceEntity> resourceOptional = metaExpedientTascaResourceRepository.findById(metaExpedientTasca.getId());
                resourceOptional.ifPresent((resource) -> {
                    target.setDuracio(resource.getDuracio());
                    target.setPrioritat(resource.getPrioritat());
                    target.setMetaExpedientTascaDescription(resource.getDescripcio());

                    if(resource.getResponsable()!=null){
                        target.setResponsableActual(ResourceReference.toResourceReference(
                                resource.getResponsable().getCodi(),
                                resource.getResponsable().getCodiAndNom()
                        ));
                    }
                });
            } else {
                target.setDuracio(null);
                target.setPrioritat(PrioritatEnumDto.B_NORMAL);
                target.setResponsableActual(null);
                target.setMetaExpedientTascaDescription(null);
            }
        }
    }
    private class DuracioOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientTascaResource> {
        @Override
        public void onChange(
		        Serializable id,
                ExpedientTascaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientTascaResource target) {

            if (fieldValue != null) {
                Date dataLimit= DateUtils.addDays(previous.getDataInici()!=null ?previous.getDataInici() :new Date(), (Integer) fieldValue);
                if (previous.getDataLimit() == null || !DateUtils.isSameDay(previous.getDataLimit(), dataLimit)) {
                    target.setDataLimit(dataLimit);
                }
            } else {
                if (previous.getDataLimit()!=null) {
                    target.setDataLimit(null);
                }
            }
        }
    }
    private class DataLimitOnchangeLogicProcessor implements OnChangeLogicProcessor<ExpedientTascaResource> {
        @Override
        public void onChange(
		        Serializable id,
                ExpedientTascaResource previous,
                String fieldName,
                Object fieldValue,
                Map<String, AnswerRequiredException.AnswerValue> answers,
                String[] previousFieldNames,
                ExpedientTascaResource target) {

            if (fieldValue != null) {
                LocalDate start =previous.getDataInici()!=null
                        ?(previous.getDataInici()).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        :LocalDate.now();
                LocalDate end = ((Date)fieldValue).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                int dias = (int) start.until(end, ChronoUnit.DAYS);

                if (!Objects.equals(previous.getDuracio(), dias)) {
                    target.setDuracio(dias);
                }
            } else {
                if (previous.getDuracio()!=null) {
                    target.setDuracio(null);
                }
            }
        }
    }

    private void changeEstat(ExpedientTascaResourceEntity entity, TascaEstatEnumDto estat, String motiu) {
    	List <MetaExpedientTascaValidacioDto> validacionsPendents = null;
    	if (TascaEstatEnumDto.FINALITZADA.equals(estat)) {
    		validacionsPendents = tascaHelper.getValidacionsPendentsTasca(entity.getId());
    	}

		if (validacionsPendents==null || validacionsPendents.size()==0) {
			tascaHelper.canviarEstatTasca(entity.getId(), estat, motiu, configHelper.getRolActual());
		} else {
			throw new ActionExecutionException(getResourceClass(), entity.getId(), null, "La tasca té validacions pendents, no es pot finalitzar.");
		}
    }

    private class ChangeEstatActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.ChangeEstatFormAction, ExpedientTascaResource> {

        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.ChangeEstatFormAction params) throws ActionExecutionException {
            changeEstat(entity, params.getEstat(), null);
            return objectMappingHelper.newInstanceMap(entity, ExpedientTascaResource.class);
        }

        @Override
        public void onChange(Serializable id, ExpedientTascaResource.ChangeEstatFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.ChangeEstatFormAction target) {}
    }
    
    private class ChangePrioritatActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.ChangePrioritatFormAction, ExpedientTascaResource> {

        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.ChangePrioritatFormAction params) throws ActionExecutionException {
            entity.setPrioritat(params.getPrioritat());
            expedientTascaResourceRepository.save(entity);
            return objectMappingHelper.newInstanceMap(entity, ExpedientTascaResource.class);
        }

        @Override
        public void onChange(Serializable id, ExpedientTascaResource.ChangePrioritatFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.ChangePrioritatFormAction target) {}
    }
    
    private class ChangeDataLimitActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.ChangeDataLimitFormAction, ExpedientTascaResource> {

        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.ChangeDataLimitFormAction params) throws ActionExecutionException {
        	try {
        		tascaHelper.updateDataLimit(entity.getId(), params.getDataLimit(), params.getDuracio());
        		return objectMappingHelper.newInstanceMap(entity, ExpedientTascaResource.class);
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/tasca/"+entity.getId()+"/ChangeDataLimitActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "S'ha produit un error al actualitzar la data límit de la tasca.");
			}	
        }

        @Override
        public void onChange(Serializable id, ExpedientTascaResource.ChangeDataLimitFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.ChangeDataLimitFormAction target) {}
    }
    
    
    private class RebutjarActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.MotiuFormAction, ExpedientTascaResource> {
        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.MotiuFormAction params) throws ActionExecutionException {
            changeEstat(entity, TascaEstatEnumDto.REBUTJADA, params.getMotiu());
            return objectMappingHelper.newInstanceMap(entity, ExpedientTascaResource.class);
        }
        @Override
        public void onChange(Serializable id, ExpedientTascaResource.MotiuFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.MotiuFormAction target) {

        }
    }
    
    private class ReobrirActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.ReobrirFormAction, ExpedientTascaResource> {
        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.ReobrirFormAction params) throws ActionExecutionException {
			try {
				tascaHelper.reobrirTasca(
						entity.getId(),
						getIdsFromUsuarisResources(params.getResponsables()),
						params.getMotiu(),
						configHelper.getRolActual());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/tasca/"+entity.getId()+"/ReobrirActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "S'ha produit un error al reobrir la tasca.");
			}
			return null;
        }
        @Override
        public void onChange(Serializable id, ExpedientTascaResource.ReobrirFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.ReobrirFormAction target) {

        }
    }
    
    private class RetomarActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.MotiuFormAction, ExpedientTascaResource> {

        @Override
        public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ExpedientTascaResource.MotiuFormAction params) throws ActionExecutionException {
			try {
				tascaHelper.retomarTasca(entity.getId(), params.getMotiu());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/tasca/"+entity.getId()+"/RetomarActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "S'ha produit un error al retomar la tasca.");
			}
			return null;
        }

        @Override
        public void onChange(Serializable id, ExpedientTascaResource.MotiuFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ExpedientTascaResource.MotiuFormAction target) {}
    }
    
    private class ReassignarActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.ReassignarTascaFormAction, ExpedientTascaResource> {

		@Override
		public void onChange(Serializable id, ReassignarTascaFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, ReassignarTascaFormAction target) {	}

		@Override
		public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, ReassignarTascaFormAction params) throws ActionExecutionException {
			try {
				tascaHelper.reassignarTasca(entity.getId(), getIdsFromUsuarisResources(params.getUsuaris()));
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/tasca/"+entity.getId()+"/ReassignarActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "S'ha produit un error al reassignar la tasca.");
			}
			return null;
		}
    }
    
    private class DelegarActionExecutor implements ActionExecutor<ExpedientTascaResourceEntity, ExpedientTascaResource.DelegarTascaFormAction, ExpedientTascaResource> {

		@Override
		public void onChange(Serializable id, DelegarTascaFormAction previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers, String[] previousFieldNames, DelegarTascaFormAction target) {}

		@Override
		public ExpedientTascaResource exec(String code, ExpedientTascaResourceEntity entity, DelegarTascaFormAction params) throws ActionExecutionException {
			try {
				tascaHelper.delegarTasca(entity.getId(), params.getUsuari().getId(), params.getMotiu());
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/tasca/"+entity.getId()+"/DelegarActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, "S'ha produit un error al delegar la tasca.");
			}
			return null;
		}
    }
    
    private List<String> getIdsFromUsuarisResources(List<ResourceReference<UsuariResource, String>> usuaris) {
        List<String> resultat = new ArrayList<>();
        if (usuaris != null) {
            for (ResourceReference<UsuariResource, String> resource : usuaris) {
                resultat.add(resource.getId());
            }
        }
        return resultat;
    }
}