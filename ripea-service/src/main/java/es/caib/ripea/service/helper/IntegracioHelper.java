package es.caib.ripea.service.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.ripea.persistence.entity.resourceentity.IntegracioResourceEntity;
import es.caib.ripea.persistence.entity.resourcerepository.IntegracioResourceRepository;
import es.caib.ripea.service.intf.dto.IntegracioAccioDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.service.intf.dto.IntegracioCodiEnum;
import es.caib.ripea.service.intf.dto.IntegracioDto;
import es.caib.ripea.service.intf.dto.IntegracioFiltreDto;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IntegracioHelper {

	private final IntegracioResourceRepository integracioResourceRepository;
	private final CacheHelper cacheHelper;
	private final ConfigHelper configHelper;
	private final MessageHelper messageHelper;

	// Cal passar pel proxy perquè el guard de addAccioOk/addAccioError quedi FORA del límit
	// transaccional: l'error d'inserció es manifesta al commit, quan el mètode @Transactional
	// ja ha retornat i un try/catch intern ja no el veuria.
	@Lazy @Autowired private IntegracioHelper self;

	public static final String INTCODI_USUARIS        = IntegracioCodiEnum.USUARIS.name();
	public static final String INTCODI_UNITATS        = IntegracioCodiEnum.ORGANISMES.name();
	public static final String INTCODI_CIUTADA        = IntegracioCodiEnum.CIUTADA.name();
	public static final String INTCODI_PFIRMA         = IntegracioCodiEnum.PORTAFIRMES.name();
	public static final String INTCODI_FIRMASIMPLE    = IntegracioCodiEnum.FIRMA_SIMPLE_WEB.name();
	public static final String INTCODI_ARXIU          = IntegracioCodiEnum.ARXIU.name();
	public static final String INTCODI_CONCSV         = IntegracioCodiEnum.CONCSV.name();
	public static final String INTCODI_PINBAL         = IntegracioCodiEnum.PINBAL.name();
	public static final String INTCODI_CONVERT        = IntegracioCodiEnum.CONVERSIO.name();
	public static final String INTCODI_CALLBACK       = IntegracioCodiEnum.CALLBACK.name();
	public static final String INTCODI_DADESEXT       = IntegracioCodiEnum.DADESEXT.name();
	public static final String INTCODI_VALIDASIG      = IntegracioCodiEnum.VALIDATE_SIGNATURE.name();
	public static final String INTCODI_FIRMAAGIL      = IntegracioCodiEnum.FIRMA_AGIL.name();
	public static final String INTCODI_NOTIFICACIO    = IntegracioCodiEnum.NOTIB.name();
	public static final String INTCODI_GESDOC         = IntegracioCodiEnum.GES_DOC.name();
	public static final String INTCODI_FIRMASERV      = IntegracioCodiEnum.FIRMA_SERVIDOR.name();
	public static final String INTCODI_VIAFIRMA       = IntegracioCodiEnum.FIRMA_VIAFIRMA.name();
	public static final String INTCODI_DIGITALITZACIO = IntegracioCodiEnum.DIGITALITZACIO.name();
	public static final String INTCODI_PROCEDIMENT    = IntegracioCodiEnum.GESCONADM.name();
	public static final String INTCODI_SUMMARIZE      = IntegracioCodiEnum.SUMMARIZE.name();
	public static final String INTCODI_DISTRIBUCIO    = IntegracioCodiEnum.DISTRIBUCIO.name();
	public static final String INTCODI_COMANDA        = IntegracioCodiEnum.COMANDA.name();
	public static final String INTCODI_REGISTRE       = IntegracioCodiEnum.REGISTRE.name();

	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		integracions.add(novaIntegracio(INTCODI_PFIRMA));
		integracions.add(novaIntegracio(INTCODI_FIRMASIMPLE));
		integracions.add(novaIntegracio(INTCODI_FIRMASERV));
		integracions.add(novaIntegracio(INTCODI_CALLBACK));
		integracions.add(novaIntegracio(INTCODI_ARXIU));
		integracions.add(novaIntegracio(INTCODI_CONCSV));
		integracions.add(novaIntegracio(INTCODI_GESDOC));
		integracions.add(novaIntegracio(INTCODI_PINBAL));
		integracions.add(novaIntegracio(INTCODI_DISTRIBUCIO));
		integracions.add(novaIntegracio(INTCODI_USUARIS));
		integracions.add(novaIntegracio(INTCODI_CONVERT));
		integracions.add(novaIntegracio(INTCODI_DADESEXT));
		integracions.add(novaIntegracio(INTCODI_NOTIFICACIO));
		integracions.add(novaIntegracio(INTCODI_VIAFIRMA));
		integracions.add(novaIntegracio(INTCODI_DIGITALITZACIO));
		integracions.add(novaIntegracio(INTCODI_VALIDASIG));
		integracions.add(novaIntegracio(INTCODI_PROCEDIMENT));
		integracions.add(novaIntegracio(INTCODI_COMANDA));
		integracions.add(novaIntegracio(INTCODI_FIRMAAGIL));
		integracions.add(novaIntegracio(INTCODI_REGISTRE));
		return integracions;
	}

	public List<IntegracioAccioDto> findAccionsByIntegracioCodi(String integracioCodi, IntegracioFiltreDto filtre) {
		IntegracioCodiEnum codi;
		try {
			codi = IntegracioCodiEnum.valueOf(integracioCodi);
		} catch (IllegalArgumentException e) {
			log.warn("Codi d'integració desconegut: {}", integracioCodi);
			return new ArrayList<>();
		}
		List<IntegracioResourceEntity> entities = integracioResourceRepository.findByCodiOrderByDataDesc(codi);
		List<IntegracioAccioDto> result = new ArrayList<>();
		for (IntegracioResourceEntity entity : entities) {
			IntegracioAccioDto accio = toDto(entity);
			if (matchesFiltre(accio, filtre)) {
				result.add(accio);
			}
		}
		return result;
	}

	public void addAccioOk(
			String integracioCodi,
			String descripcio,
			String endpoint,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta) {
		try {
			self.guardarAccioOk(integracioCodi, descripcio, endpoint, parametres, tipus, tempsResposta);
		} catch (Throwable th) {
			logErrorMonitor(integracioCodi, descripcio, th);
		}
	}

	public void addAccioError(
			String integracioCodi,
			String descripcio,
			String endpoint,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {
		try {
			self.guardarAccioError(integracioCodi, descripcio, endpoint, parametres, tipus, tempsResposta, errorDescripcio, throwable);
		} catch (Throwable th) {
			logErrorMonitor(integracioCodi, descripcio, th);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void guardarAccioOk(
			String integracioCodi,
			String descripcio,
			String endpoint,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta) {
		IntegracioResourceEntity entity = buildEntity(integracioCodi, descripcio, endpoint, parametres, tipus, tempsResposta, IntegracioAccioEstatEnumDto.OK);
		if (entity != null) {
			integracioResourceRepository.save(entity);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void guardarAccioError(
			String integracioCodi,
			String descripcio,
			String endpoint,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {
		IntegracioResourceEntity entity = buildEntity(integracioCodi, descripcio, endpoint, parametres, tipus, tempsResposta, IntegracioAccioEstatEnumDto.ERROR);
		if (entity != null) {
			entity.setErrorDescripcio(errorDescripcio);
			if (throwable != null) {
				entity.setExcepcioMessage(ExceptionUtils.getMessage(throwable));
				entity.setExcepcioStacktrace(ExceptionUtils.getStackTrace(throwable));
			}
			integracioResourceRepository.save(entity);
		}
	}

	/**
	 * El registre al monitor d'integracions és informatiu: si falla (p.ex. ORA-01691 per manca
	 * d'espai al tablespace) no ha de tombar l'operació de negoci que l'ha provocat.
	 *
	 * Només es deixa constància al log de l'aplicació. No es fa servir ExcepcioLogHelper
	 * expressament: si no s'ha pogut inserir a IPA_INTEGRACIO_ACCIO, molt probablement
	 * tampoc es podria inserir a IPA_EXCEPCIO_LOG i l'error es tornaria a propagar.
	 */
	private void logErrorMonitor(String integracioCodi, String descripcio, Throwable th) {
		log.error("No s'ha pogut registrar l'acció al monitor d'integracions (integracioCodi={}, descripcio={})", integracioCodi, descripcio, th);
	}

	public IntegracioAccioDto findOne(Long id) {
		return integracioResourceRepository.findById(id).map(this::toDto).orElse(null);
	}

	@Transactional
	public int esborrarAccionsMesAntigues3Mesos() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);
		return integracioResourceRepository.deleteByDataBefore(cal.getTime());
	}

	private IntegracioResourceEntity buildEntity(
			String integracioCodi,
			String descripcio,
			String endpoint,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			IntegracioAccioEstatEnumDto estat) {
		IntegracioCodiEnum codi;
		try {
			codi = IntegracioCodiEnum.valueOf(integracioCodi);
		} catch (IllegalArgumentException e) {
			log.warn("Codi d'integració desconegut, no es guarda l'acció: {}", integracioCodi);
			return null;
		}
		if (cacheHelper.mostrarLogsIntegracio()) {
			log.info("Nova integracio en monitor: integracioCodi={}, descripcio={}", integracioCodi, descripcio);
		}
		IntegracioResourceEntity entity = new IntegracioResourceEntity();
		entity.setData(new Date());
		entity.setCodi(codi);
		entity.setDescripcio(descripcio);
		entity.setEndpoint(endpoint);
		entity.setParametres(afegirParametreUsuari(parametres));
		entity.setTipus(tipus);
		entity.setTempsResposta(tempsResposta);
		entity.setEstat(estat);
		entity.setEntitatCodi(configHelper.getEntitatActualCodi());
		return entity;
	}

	private boolean matchesFiltre(IntegracioAccioDto accio, IntegracioFiltreDto filtre) {
		if (filtre == null) return true;
		return (Utils.isEmpty(filtre.getEntitatCodi()) || Utils.containsIgnoreCase(accio.getEntitatCodi(), filtre.getEntitatCodi())) &&
			   (filtre.getDataInici() == null || !filtre.getDataInici().after(accio.getData())) &&
			   (filtre.getDataFi() == null || !DateHelper.toDateFinalDia(filtre.getDataFi()).before(accio.getData())) &&
			   (filtre.getTipus() == null || filtre.getTipus() == accio.getTipus()) &&
			   (Utils.isEmpty(filtre.getDescripcio()) || Utils.containsIgnoreCase(accio.getDescripcio(), filtre.getDescripcio())) &&
			   (filtre.getEstat() == null || filtre.getEstat() == accio.getEstat());
	}

	private IntegracioAccioDto toDto(IntegracioResourceEntity entity) {
		IntegracioAccioDto dto = new IntegracioAccioDto();
		dto.setId(entity.getId());
		dto.setData(entity.getData());
		dto.setDescripcio(entity.getDescripcio());
		dto.setEndpoint(entity.getEndpoint());
		dto.setParametres(entity.getParametres());
		dto.setTipus(entity.getTipus());
		dto.setTempsResposta(entity.getTempsResposta());
		dto.setEstat(entity.getEstat());
		dto.setEntitatCodi(entity.getEntitatCodi());
		dto.setErrorDescripcio(entity.getErrorDescripcio());
		dto.setExcepcioMessage(entity.getExcepcioMessage());
		dto.setExcepcioStacktrace(entity.getExcepcioStacktrace());
		if (entity.getCodi() != null) {
			dto.setIntegracio(novaIntegracio(entity.getCodi().name()));
		}
		return dto;
	}

	private Map<String, String> afegirParametreUsuari(Map<String, String> parametres) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getName() == null) {
			return parametres;
		}
		Map<String, String> result = parametres != null ? new HashMap<>(parametres) : new HashMap<>();
		result.put("usuari", auth.getName());
		return result;
	}

	public IntegracioDto novaIntegracio(String codi) {
		IntegracioDto integracio = new IntegracioDto();
		integracio.setCodi(codi);
		integracio.setNom(messageHelper.getMessage("sistema.extern.codi." + codi));
		return integracio;
	}
}
