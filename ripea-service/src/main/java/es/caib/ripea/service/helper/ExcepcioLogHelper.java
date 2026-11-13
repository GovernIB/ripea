package es.caib.ripea.service.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.ExcepcioLogDto;
import es.caib.ripea.service.intf.exception.NotFoundException;
import es.caib.ripea.service.intf.exception.PermissionDeniedException;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcepcioLogHelper {

	/** Longituds de les columnes de IPA_EXCEPCIO_LOG. Els valors s'han de retallar abans de desar-los:
	 *  un missatge d'error massa llarg feia fallar l'insert i, com que addExcepcio es crida des dels
	 *  blocs catch, aquesta fallada substituïa l'error original i l'usuari es quedava sense missatge. */
	private static final int LONGITUD_TIPUS = 512;
	private static final int LONGITUD_OBJECT = 512;
	private static final int LONGITUD_URI = 1024;
	private static final int LONGITUD_PARAM = 512;
	private static final int LONGITUD_MESSAGE = 4000;
	/** Amb AL32UTF8 un VARCHAR2 no admet més de 4000 bytes encara que la columna sigui de 4000 CHAR:
	 *  retallar només per caràcters no basta si el missatge té accents (ORA-01461). */
	private static final int MAX_BYTES_VARCHAR = 4000;

	private final ExcepcioLogResourceRepository excepcioLogResourceRepository;
	private final ConfigHelper configHelper;

	public List<ExcepcioLogDto> findAll() {
		List<ExcepcioLogResourceEntity> entities = excepcioLogResourceRepository.findAll(Sort.by(Sort.Direction.DESC, "data"));
		List<ExcepcioLogDto> result = new ArrayList<>();
		long index = 0;
		for (ExcepcioLogResourceEntity entity : entities) {
			result.add(toDto(entity, index++));
		}
		return result;
	}

	/** Dies de retenció per defecte si la propietat no està definida o no és vàlida. */
	static final int RETENCIO_DIES_DEFECTE = 30;
	/** Finestra de deduplicació per defecte (minuts). */
	static final int DEDUP_MINUTS_DEFECTE = 10;

	/** Dies que es conserven les excepcions (propietat {@link PropertyConfig#EXCEPCIONS_RETENCIO_DIES}). */
	public int getRetencioDies() {
		int dies = configHelper.getAsInt(PropertyConfig.EXCEPCIONS_RETENCIO_DIES, RETENCIO_DIES_DEFECTE);
		return dies > 0 ? dies : RETENCIO_DIES_DEFECTE;
	}

	@Transactional
	public int esborrarExcepcionsMesAntigues(int dies) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -dies);
		return excepcioLogResourceRepository.deleteByDataBefore(cal.getTime());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addExcepcio(String uri, Throwable exception) {
		addExcepcio(uri, exception, null, null);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addExcepcio(String uri, Throwable exception, String param1, String param2) {
		if (exception == null) return;
		String signatura = null;
		try {
			signatura = calcularSignatura(exception);
			if (incrementarSiRepetida(signatura)) return;
		} catch (Exception e) {
			// La deduplicació és una optimització: si falla, es registra l'excepció com sempre.
			log.warn("No s'ha pogut comprovar si l'excepció està repetida: {}", e.toString());
		}
		ExcepcioLogResourceEntity entity = toEntity(uri, exception, param1, param2);
		entity.setSignatura(signatura);
		entity.setDataDarrera(entity.getData());
		excepcioLogResourceRepository.save(entity);
	}

	/** Si ja hi ha una excepció amb la mateixa signatura dins la finestra, n'incrementa el comptador. */
	private boolean incrementarSiRepetida(String signatura) {
		int minuts = configHelper.getAsInt(PropertyConfig.EXCEPCIONS_DEDUP_MINUTS, DEDUP_MINUTS_DEFECTE);
		if (minuts <= 0) return false;
		Date ara = new Date();
		Date desde = new Date(ara.getTime() - minuts * 60_000L);
		Long id = excepcioLogResourceRepository.findIdRecentBySignatura(signatura, desde);
		if (id == null) return false;
		return excepcioLogResourceRepository.incrementarOcurrencies(id, ara) > 0;
	}

	/**
	 * Hash que identifica "la mateixa excepció": classe, classe de la causa arrel, missatge sense
	 * dígits (ids, comptadors...) i primer frame de codi de RIPEA de la causa arrel.
	 */
	static String calcularSignatura(Throwable exception) {
		Throwable arrel = ExceptionUtils.getRootCause(exception);
		if (arrel == null) arrel = exception;
		String missatge = exception.getMessage() == null ? "" : exception.getMessage().replaceAll("\\d+", "#");
		StringBuilder sb = new StringBuilder();
		sb.append(exception.getClass().getName()).append('|')
			.append(arrel.getClass().getName()).append('|')
			.append(missatge).append('|');
		StackTraceElement[] frames = arrel.getStackTrace();
		StackTraceElement frame = null;
		for (StackTraceElement f : frames) {
			if (f.getClassName().startsWith("es.caib.ripea")) { frame = f; break; }
		}
		if (frame == null && frames.length > 0) frame = frames[0];
		if (frame != null) sb.append(frame.getClassName()).append('.').append(frame.getMethodName());
		try {
			byte[] hash = MessageDigest.getInstance("SHA-256").digest(sb.toString().getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder();
			for (byte b : hash) hex.append(String.format("%02x", b));
			return hex.toString().substring(0, 64);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private ExcepcioLogResourceEntity toEntity(String uri, Throwable exception, String param1, String param2) {
		ExcepcioLogResourceEntity newExcepcioLog = new ExcepcioLogResourceEntity();
		newExcepcioLog.setData(new Date());
		newExcepcioLog.setUri(Utils.left(uri, LONGITUD_URI));
		ThreadLocal<EntitatDto> entitatActual = ConfigHelper.getEntitat();
		newExcepcioLog.setEntitatId((entitatActual!=null && entitatActual.get()!=null)?entitatActual.get().getId():null);
		newExcepcioLog.setTipus(Utils.left(exception.getClass().getName(), LONGITUD_TIPUS));
		newExcepcioLog.setMessage(Utils.leftBytes(exception.getMessage(), LONGITUD_MESSAGE, MAX_BYTES_VARCHAR));
		newExcepcioLog.setStacktrace(ExceptionUtils.getStackTrace(exception));
		if (param1 != null) newExcepcioLog.setParam1(Utils.left(param1, LONGITUD_PARAM));
		if (param2 != null) newExcepcioLog.setParam2(Utils.left(param2, LONGITUD_PARAM));
		if (exception instanceof NotFoundException) {
			NotFoundException ex = (NotFoundException) exception;
			if (ex.getObjectId() != null) newExcepcioLog.setObjectId(Utils.left(ex.getObjectId().toString(), LONGITUD_OBJECT));
			if (ex.getObjectClass() != null) newExcepcioLog.setObjectClass(Utils.left(ex.getObjectClass().getName(), LONGITUD_OBJECT));
		} else if (exception instanceof PermissionDeniedException) {
			PermissionDeniedException ex = (PermissionDeniedException) exception;
			if (ex.getObjectId() != null) newExcepcioLog.setObjectId(Utils.left(ex.getObjectId().toString(), LONGITUD_OBJECT));
			if (ex.getObjectClass() != null) newExcepcioLog.setObjectClass(Utils.left(ex.getObjectClass().getName(), LONGITUD_OBJECT));
			newExcepcioLog.setParam1(Utils.left(ex.getUserName(), LONGITUD_PARAM));
			newExcepcioLog.setParam2(Utils.left(ex.getPermissionName(), LONGITUD_PARAM));
		} else if (exception instanceof ValidationException) {
			ValidationException ex = (ValidationException) exception;
			if (ex.getObjectId() != null) newExcepcioLog.setObjectId(Utils.left(ex.getObjectId().toString(), LONGITUD_OBJECT));
			if (ex.getObjectClass() != null) newExcepcioLog.setObjectClass(Utils.left(ex.getObjectClass().getName(), LONGITUD_OBJECT));
			newExcepcioLog.setParam1(Utils.left(ex.getError(), LONGITUD_PARAM));
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
