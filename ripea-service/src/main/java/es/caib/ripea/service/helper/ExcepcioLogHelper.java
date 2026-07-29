package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.ExcepcioLogResourceRepository;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExcepcioLogDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.PermissionDeniedException;
import es.caib.ripea.service.intf.exception.ValidationException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExcepcioLogHelper {

	private final ExcepcioLogResourceRepository excepcioLogResourceRepository;

	public List<ExcepcioLogDto> findAll() {
		List<ExcepcioLogResourceEntity> entities = excepcioLogResourceRepository.findAll(Sort.by(Sort.Direction.DESC, "data"));
		List<ExcepcioLogDto> result = new ArrayList<>();
		long index = 0;
		for (ExcepcioLogResourceEntity entity : entities) {
			result.add(toDto(entity, index++));
		}
		return result;
	}

	@Transactional
	public int esborrarExcepcionsMesAntigues3Mesos() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);
		return excepcioLogResourceRepository.deleteByDataBefore(cal.getTime());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addExcepcio(String uri, Throwable exception) {
		if (exception == null) return;
		excepcioLogResourceRepository.save(toEntity(uri, exception, null, null));
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addExcepcio(String uri, Throwable exception, String param1, String param2) {
		if (exception == null) return;
		excepcioLogResourceRepository.save(toEntity(uri, exception, param1, param2));
	}

	private ExcepcioLogResourceEntity toEntity(String uri, Throwable exception, String param1, String param2) {
		ExcepcioLogResourceEntity newExcepcioLog = new ExcepcioLogResourceEntity();
		newExcepcioLog.setData(new Date());
		newExcepcioLog.setUri(uri);
		ThreadLocal<EntitatDto> entitatActual = ConfigHelper.getEntitat();
		newExcepcioLog.setEntitatId((entitatActual!=null && entitatActual.get()!=null)?entitatActual.get().getId():null);
		newExcepcioLog.setTipus(exception.getClass().getName());
		newExcepcioLog.setMessage(exception.getMessage());
		newExcepcioLog.setStacktrace(ExceptionUtils.getStackTrace(exception));
		if (param1 != null) newExcepcioLog.setParam1(param1);
		if (param2 != null) newExcepcioLog.setParam2(param2);
		if (exception instanceof NotFoundException) {
			NotFoundException ex = (NotFoundException) exception;
			if (ex.getObjectId() != null) newExcepcioLog.setObjectId(ex.getObjectId().toString());
			if (ex.getObjectClass() != null) newExcepcioLog.setObjectClass(ex.getObjectClass().getName());
		} else if (exception instanceof PermissionDeniedException) {
			PermissionDeniedException ex = (PermissionDeniedException) exception;
			if (ex.getObjectId() != null) newExcepcioLog.setObjectId(ex.getObjectId().toString());
			if (ex.getObjectClass() != null) newExcepcioLog.setObjectClass(ex.getObjectClass().getName());
			newExcepcioLog.setParam1(ex.getUserName());
			newExcepcioLog.setParam2(ex.getPermissionName());
		} else if (exception instanceof ValidationException) {
			ValidationException ex = (ValidationException) exception;
			if (ex.getObjectId() != null) newExcepcioLog.setObjectId(ex.getObjectId().toString());
			if (ex.getObjectClass() != null) newExcepcioLog.setObjectClass(ex.getObjectClass().getName());
			newExcepcioLog.setParam1(ex.getError());
		}
		return newExcepcioLog;
	}

	private ExcepcioLogDto toDto(ExcepcioLogResourceEntity entity, long index) {
		ExcepcioLogDto dto = new ExcepcioLogDto();
		dto.setIndex(index);
		dto.setData(entity.getData());
		dto.setUri(entity.getUri());
		dto.setMessage(entity.getMessage());
		dto.setStacktrace(entity.getStacktrace());
		dto.setParam1(entity.getParam1());
		dto.setParam2(entity.getParam2());
		if (entity.getTipus() != null) {
			try { dto.setTipus(Class.forName(entity.getTipus())); } catch (ClassNotFoundException ignored) {}
		}
		if (entity.getObjectClass() != null) {
			try { dto.setObjectClass(Class.forName(entity.getObjectClass())); } catch (ClassNotFoundException ignored) {}
		}
		dto.setObjectId(entity.getObjectId());
		return dto;
	}
}
