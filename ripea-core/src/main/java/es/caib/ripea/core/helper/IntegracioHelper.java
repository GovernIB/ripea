/**
 * 
 */
package es.caib.ripea.core.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.ripea.core.api.dto.IntegracioAccioDto;
import es.caib.ripea.core.api.dto.IntegracioAccioEstatEnumDto;
import es.caib.ripea.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.ripea.core.api.dto.IntegracioDto;
import es.caib.ripea.core.api.dto.IntegracioFiltreDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.core.entity.UsuariEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class IntegracioHelper {
	
	@Resource
	private UsuariHelper usuariHelper;
	
	@Resource
	private CacheHelper cacheHelper;
	
	@Resource
	private ConfigHelper configHelper;

	public static final int DEFAULT_MAX_ACCIONS = 20;

	public static final String INTCODI_USUARIS = "USUARIS";
	public static final String INTCODI_UNITATS = "ORGANISMES";
	public static final String INTCODI_CIUTADA = "CIUTADA"; //No implementat (Sede electrónica)
	public static final String INTCODI_PFIRMA = "PORTAFIRMES";
	public static final String INTCODI_FIRMASIMPLE = "FIRMA_SIMPLE_WEB";
	public static final String INTCODI_ARXIU = "ARXIU";
	public static final String INTCODI_PINBAL = "PINBAL";
	public static final String INTCODI_CONVERT = "CONVERSIO";
	public static final String INTCODI_CALLBACK = "CALLBACK";
	public static final String INTCODI_DADESEXT = "DADESEXT";
	public static final String INTCODI_VALIDASIG = "VALIDATE_SIGNATURE";
	public static final String INTCODI_NOTIFICACIO = "NOTIB";
	public static final String INTCODI_GESDOC = "GES_DOC";
	public static final String INTCODI_FIRMASERV = "FIRMA_SERVIDOR";
	public static final String INTCODI_VIAFIRMA = "FIRMA_VIAFIRMA";
	public static final String INTCODI_DIGITALITZACIO = "DIGITALITZACIO";
	public static final String INTCODI_PROCEDIMENT = "GESCONADM";
	public static final String INTCODI_SUMMARIZE = "SUMMARIZE";
	
	private Map<String, LinkedList<IntegracioAccioDto>> accionsIntegracio = Collections.synchronizedMap(new HashMap<String, LinkedList<IntegracioAccioDto>>());
	private Map<String, Integer> maxAccionsIntegracio = new HashMap<String, Integer>();

	private static final Object lock = new Object();
	
	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		integracions.add(novaIntegracio(INTCODI_PFIRMA));
		integracions.add(novaIntegracio(INTCODI_FIRMASIMPLE));
		integracions.add(novaIntegracio(INTCODI_FIRMASERV));
		integracions.add(novaIntegracio(INTCODI_CALLBACK));
		integracions.add(novaIntegracio(INTCODI_ARXIU));
		integracions.add(novaIntegracio(INTCODI_GESDOC));
		integracions.add(novaIntegracio(INTCODI_PINBAL));
		integracions.add(novaIntegracio(INTCODI_USUARIS));
		integracions.add(novaIntegracio(INTCODI_CONVERT));
		integracions.add(novaIntegracio(INTCODI_DADESEXT));
		integracions.add(novaIntegracio(INTCODI_NOTIFICACIO));
		integracions.add(novaIntegracio(INTCODI_VIAFIRMA));
		integracions.add(novaIntegracio(INTCODI_DIGITALITZACIO));
		integracions.add(novaIntegracio(INTCODI_VALIDASIG));
		integracions.add(novaIntegracio(INTCODI_PROCEDIMENT));
		return integracions;
	}

	public List<IntegracioAccioDto> findAccionsByIntegracioCodi(String integracioCodi, IntegracioFiltreDto filtre) {
		synchronized(lock){

			List<IntegracioAccioDto> listaAccions = getLlistaAccions(integracioCodi);
			int index = 0;
			LinkedList<IntegracioAccioDto> accionsFiltered = new LinkedList<>();
			for(IntegracioAccioDto accio : listaAccions) {
				
				boolean shouldAddTList = true;
				if (filtre != null) {
					shouldAddTList = 
							(Utils.isEmpty(filtre.getEntitatCodi()) || Utils.containsIgnoreCase(accio.getEntitatCodi(), filtre.getEntitatCodi())) &&
							(filtre.getDataInici() == null || !filtre.getDataInici().after(accio.getData())) &&
							(filtre.getDataFi() == null || !DateHelper.toDateFinalDia(filtre.getDataFi()).before(accio.getData())) &&
							(filtre.getTipus() == null || filtre.getTipus() ==  accio.getTipus()) &&
							(Utils.isEmpty(filtre.getDescripcio()) || Utils.containsIgnoreCase(accio.getDescripcio(), filtre.getDescripcio())) &&
							(filtre.getEstat() == null || filtre.getEstat() ==  accio.getEstat());
				}
			
				if (shouldAddTList) {
					accio.setIndex(new Long(index++));
					accionsFiltered.add(accio);
				}
			}
			return accionsFiltered;
		}
	}

	public void addAccioOk(
			String integracioCodi,
			String descripcio,
			String endpoint,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setEndpoint(endpoint);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.OK);
		addAccio(integracioCodi, accio);
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

		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setEndpoint(endpoint);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		accio.setErrorDescripcio(errorDescripcio);
		if (throwable != null) {
			accio.setExcepcioMessage(ExceptionUtils.getMessage(throwable));
			accio.setExcepcioStacktrace(ExceptionUtils.getStackTrace(throwable));
		}
		addAccio(integracioCodi, accio);
	}

	private LinkedList<IntegracioAccioDto> getLlistaAccions(
			String integracioCodi) {
			LinkedList<IntegracioAccioDto> accions = accionsIntegracio.get(integracioCodi);
			if (accions == null) {
				accions = new LinkedList<IntegracioAccioDto>();
				accionsIntegracio.put(
						integracioCodi,
						accions);
			} else {
				int index = 0;
				
				Iterator<IntegracioAccioDto> iterator = accions.iterator();
				while (iterator.hasNext()) {
					IntegracioAccioDto accio = iterator.next();
					accio.setIndex(new Long(index++));
				}
			}
			return accions;
	}

	private int getMaxAccions(String integracioCodi) {
		Integer max = maxAccionsIntegracio.get(integracioCodi);
		if (max == null) {
			max = new Integer(DEFAULT_MAX_ACCIONS);
			maxAccionsIntegracio.put(integracioCodi, max);
		}
		return max.intValue();
	}

	private void addAccio(String integracioCodi, IntegracioAccioDto accio) {
		synchronized(lock){
			if (cacheHelper.mostrarLogsIntegracio()) 
				log.info("Nova integracio en monitor: integracioCodi= " + integracioCodi + ", accio=" + accio);
			afegirParametreUsuari(accio);
			//#1544 Mostar informació de l'endpoint al monitor d'integracions
			String entitatCodi = configHelper.getEntitatActualCodi();
//			if (entitatCodi!=null) {
//				String organCodi = configHelper.getOrganActualCodi();
//				Properties propietatsPlugin = configHelper.getGroupPropertiesOrganOrEntitatOrGeneral(integracioCodi, entitatCodi, organCodi);
//				accio.getIntegracio().setEndpoint(getEndpointNameFromProperties(propietatsPlugin));
//			}
			accio.setEntitatCodi(entitatCodi);
			LinkedList<IntegracioAccioDto> accions = getLlistaAccions(integracioCodi);
			int max = getMaxAccions(integracioCodi);
			while (accions.size() >= max) {
				accions.remove(accions.size() - 1);
			}
			accio.setTimestamp(System.currentTimeMillis());
			accions.add(0, accio);
		}
	}

	private void afegirParametreUsuari(IntegracioAccioDto accio) {

		String usuariNomCodi = null;
		UsuariEntity usuari = usuariHelper.getUsuariAutenticat();
		if (usuari != null) {
			usuariNomCodi = usuari.getNom() + " (" + usuari.getCodi() + ")";
		} else {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				usuariNomCodi = auth.getName();
			}
		}
		if (usuariNomCodi != null) {
			if (accio.getParametres() == null) {
				accio.setParametres(new HashMap<String, String>());
			}
			accio.getParametres().put("usuari", usuariNomCodi);
		}
	}

	//El endpoint nomes es carrega al guardar una acció (tant Ok com error), pero no en el findAll per carregar pes pipelles de Integracions
	public IntegracioDto novaIntegracio(String codi) {
		
		IntegracioDto integracio = new IntegracioDto();
		integracio.setCodi(codi);
		if (INTCODI_PFIRMA.equals(codi)) {
			integracio.setNom("Portafirmes");			
		} else if (INTCODI_ARXIU.equals(codi)) {
			integracio.setNom("Arxiu digital");
		} else if (INTCODI_PINBAL.equals(codi)) {
			integracio.setNom("PINBAL");
		} else if (INTCODI_CONVERT.equals(codi)) {
			integracio.setNom("Conversió doc.");
		} else if (INTCODI_USUARIS.equals(codi)) {
			integracio.setNom("Usuaris");
		}  else if (INTCODI_CALLBACK.equals(codi)) {
			integracio.setNom("Callback PF");
		} else if (INTCODI_DADESEXT.equals(codi)) {
			integracio.setNom("Dades ext.");
		} else if (INTCODI_NOTIFICACIO.equals(codi)) {
			integracio.setNom("Notificació");
		} else if (INTCODI_FIRMASERV.equals(codi)) {
			integracio.setNom("Firma servidor");
		} else if (INTCODI_VIAFIRMA.equals(codi)) {
			integracio.setNom("ViaFirma");
		} else if (INTCODI_DIGITALITZACIO.equals(codi)) {
			integracio.setNom("Digitalització");
		}
		return integracio;
	}
}