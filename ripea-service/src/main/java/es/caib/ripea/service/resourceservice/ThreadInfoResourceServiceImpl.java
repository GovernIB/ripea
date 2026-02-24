package es.caib.ripea.service.resourceservice;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.ripea.persistence.entity.resourceentity.ThreadInfoResourceEntity;
import es.caib.ripea.service.base.service.BaseMutableResourceService;
import es.caib.ripea.service.helper.ExcepcioLogHelper;
import es.caib.ripea.service.helper.MessageHelper;
import es.caib.ripea.service.intf.base.exception.ActionExecutionException;
import es.caib.ripea.service.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.ripea.service.intf.base.exception.ResourceNotFoundException;
import es.caib.ripea.service.intf.model.SystemInfoResource;
import es.caib.ripea.service.intf.model.ThreadInfoResource;
import es.caib.ripea.service.intf.resourceservice.ThreadInfoResourceService;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreadInfoResourceServiceImpl extends BaseMutableResourceService<ThreadInfoResource, Long, ThreadInfoResourceEntity> implements ThreadInfoResourceService {

	private final ExcepcioLogHelper excepcioLogHelper;
	private final MessageHelper messageHelper;
	
    @PostConstruct
    public void init() {
    	register(ThreadInfoResource.ACTION_SYSTEM_INFO,	new SystemInfoActionExecutor());
    }
	
	@Override
	public Page<ThreadInfoResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {

		List<ThreadInfoResource> aux = new ArrayList<ThreadInfoResource>();
		
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		if (bean.isThreadCpuTimeSupported()) {
			long[] ids = bean.getAllThreadIds();
			ThreadInfo[] info = bean.getThreadInfo(ids);
			Set hs = new HashSet();
			for (int a = 0; a < ids.length; ++a) {
				hs.add(bean.getThreadCpuTime(ids[a]));
			}
			long tiempoCPUTotal =  ((Long)Collections.max(hs)).longValue();
			for (int a = 0; a < ids.length; ++a) {
				String nombre = (info[a].getLockName() == null ? info[a].getThreadName() : info[a].getLockName());
				if (!"main".equals(nombre) && (!Utils.hasValue(quickFilter) || nombre.contains(quickFilter))) {
					ThreadInfoResource tir = new ThreadInfoResource();
					tir.setThreadId(info[a].getThreadId());
					tir.setThreadName(nombre);
					tir.setThreadState(messageHelper.getMessage("monitor."+info[a].getThreadState()));
					long tiempoCPU = (long) ((float)100*((float) bean.getThreadCpuTime(ids[a]) / (float) tiempoCPUTotal));
					tir.setTiempoCPU(((tiempoCPU>100)?100:tiempoCPU) + " %");
					tir.setPrioritat(info[a].getPriority());
					tir.setSuspended(info[a].isSuspended());
					tir.setLock(info[a].getLockInfo());
					tir.setWaitedTime(((info[a].getWaitedTime() == -1)? 0:info[a].getWaitedTime()) + " ns");
					tir.setBlockedTime(((info[a].getBlockedTime() == -1)? 0:info[a].getBlockedTime()) + " ns");
					aux.add(tir);
				}
			}
		}
		
		Page<ThreadInfoResource> page = new PageImpl<>(aux, pageable, aux.size());
		return page;
	}
	
	@Override
	public ThreadInfoResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		return null;
	}
	
	private class SystemInfoActionExecutor implements ActionExecutor<ThreadInfoResourceEntity, Serializable, SystemInfoResource> {

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue,
				Map<String, AnswerValue> answers, String[] previousFieldNames, Serializable target) {}

		@Override
		public SystemInfoResource exec(String code, ThreadInfoResourceEntity entity, Serializable params) throws ActionExecutionException {

			try {
				SystemInfoResource sir = new SystemInfoResource();
				sir.setSystemInfo(MonitorHelper.getSystemInfo());
				sir.setInformacioSistema(MonitorHelper.getInfoSistema());
				sir.setJvmInfo(MonitorHelper.getJvmInfo());
				sir.setJvmMemory(MonitorHelper.getJvmMemory());
				sir.setCpuUsage(MonitorHelper.getCpuUsage());
				sir.setRootDiskUsage(MonitorHelper.getRootDiskUsage());
				sir.setDisksUsage(MonitorHelper.getDisksUsage());
				sir.setPhisicalMemory(MonitorHelper.getPhisicalMemory());
				sir.setApplicationServerInfo(MonitorHelper.getApplicationServerInfo());
				sir.setJbossVersion(MonitorHelper.getJBossVersion());
				return sir;
			} catch (Exception e) {
				excepcioLogHelper.addExcepcio("/thread/"+entity.getId()+"/SystemInfoActionExecutor", e);
				throw new ActionExecutionException(getResourceClass(), entity.getId(), code, messageHelper.getMessage("message.common.action.error")+": "+e.getMessage());
			}
		}
	}
	
	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}
}
