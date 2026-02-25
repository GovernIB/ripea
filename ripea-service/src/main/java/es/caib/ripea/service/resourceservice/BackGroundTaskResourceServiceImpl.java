package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.caib.ripea.persistence.entity.resourceentity.BackGroundTaskResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.config.SchedulingConfig;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.dto.MonitorTascaEstatEnum;
import es.caib.ripea.service.intf.dto.MonitorTascaInfo;
import es.caib.ripea.service.intf.model.BackGroundTaskResource;
import es.caib.ripea.service.intf.model.BackGroundTaskResource.MassiveRestartTaskForm;
import es.caib.ripea.service.intf.resourceservice.BackGroundTaskResourceService;
import es.caib.ripea.service.intf.service.MonitorTasquesService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackGroundTaskResourceServiceImpl extends BaseMutableResourceService<BackGroundTaskResource, String, BackGroundTaskResourceEntity> implements BackGroundTaskResourceService {
	
	private final MonitorTasquesService monitorTasquesService;
	private final SchedulingConfig schedulingConfig;
	private final ExcepcioLogHelper excepcioLogHelper;
	private final MessageHelper messageHelper;
	
    @PostConstruct
    public void init() {
    	register(BackGroundTaskResource.ACTION_RESTART_TASK,	new RestartTaskActionExecutor());
    }
	
	@Override
	public Page<BackGroundTaskResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {

		List<BackGroundTaskResource> resultat = new ArrayList<BackGroundTaskResource>();
		
		List<MonitorTascaInfo> monitorTasques = monitorTasquesService.findAll();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		for(MonitorTascaInfo monitorTasca : monitorTasques) {
			
			String nomTasca = messageHelper.getMessage("monitor.tasques.tasca.codi." + monitorTasca.getCodi());
			
			if (!Utils.hasValue(quickFilter) || nomTasca.contains(quickFilter)) {
			
				BackGroundTaskResource btr = new BackGroundTaskResource();
				btr.setId(monitorTasca.getCodi());
				btr.setNom(nomTasca);
				btr.setEstat(messageHelper.getMessage("monitor.tasques.estat." + monitorTasca.getEstat()));
				
				String strDataInici = "-";
				if (monitorTasca.getDataInici() != null) {
					strDataInici = sdf.format(monitorTasca.getDataInici());
				}
				btr.setDataInici(strDataInici);
				
				btr.setTempsExecucio(monitorTasca.getTempsExecucio());
	
				String strProperaExecucio = "-";
				if ( ! MonitorTascaEstatEnum.EN_EXECUCIO.equals(monitorTasca.getEstat()) 
						&& monitorTasca.getProperaExecucio() != null) {
					strProperaExecucio = sdf.format(monitorTasca.getProperaExecucio());
				}
				btr.setProperaExecucio(strProperaExecucio);
				
				btr.setObservacions(monitorTasca.getObservacions());
				
				resultat.add(btr);
			}
		}
		
		Page<BackGroundTaskResource> page = new PageImpl<>(resultat, pageable, resultat.size());
		return page;
	}
	
	@Override
	public BackGroundTaskResource getOne(String id, String[] perspectives) throws ResourceNotFoundException {
		return null;
	}
	
	private class RestartTaskActionExecutor implements ActionExecutor<BackGroundTaskResourceEntity, BackGroundTaskResource.MassiveRestartTaskForm, Serializable> {

		@Override
		public Serializable exec(String code, BackGroundTaskResourceEntity entity, BackGroundTaskResource.MassiveRestartTaskForm params) throws ActionExecutionException {
			try {
				if (params.getIds()!=null) {
					for (String id: params.getIds()) {
						monitorTasquesService.reiniciarTasquesEnSegonPla(id);
						schedulingConfig.restartSchedulledTasks(id);
					}
				}
				return "{\"resultat\": \"OK\"}";
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/backGroundTask/"+entity.getId()+"/RestartTaskActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}

		@Override
		public void onChange(Serializable id, MassiveRestartTaskForm previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, MassiveRestartTaskForm target) {}
	}

	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}
}