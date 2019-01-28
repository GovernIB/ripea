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
import es.caib.ripea.core.entity.UsuariEntity;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class IntegracioHelper {
	
	@Resource
	private UsuariHelper usuariHelper;

	public static final int DEFAULT_MAX_ACCIONS = 20;

	public static final String INTCODI_USUARIS = "USUARIS";
	public static final String INTCODI_UNITATS = "UNITATS";
	public static final String INTCODI_CIUTADA = "CIUTADA";
	public static final String INTCODI_PFIRMA = "PFIRMA";
	public static final String INTCODI_ARXIU = "ARXIU";
	public static final String INTCODI_CONVERT = "CONVERT";
	public static final String INTCODI_CALLBACK = "CALLBACK";
	public static final String INTCODI_DADESEXT = "DADESEXT";
	public static final String INTCODI_SIGNATURA = "SIGNATURA";
	public static final String INTCODI_VALIDASIG = "VALIDASIG";
	public static final String INTCODI_NOTIFICACIO = "NOTIFICACIO";
	public static final String INTCODI_GESDOC = "GESDOC";

	private Map<String, LinkedList<IntegracioAccioDto>> accionsIntegracio = Collections.synchronizedMap(new HashMap<String, LinkedList<IntegracioAccioDto>>());
	private Map<String, Integer> maxAccionsIntegracio = new HashMap<String, Integer>();



	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		integracions.add(
				novaIntegracio(
						INTCODI_PFIRMA));
		integracions.add(
				novaIntegracio(
						INTCODI_CALLBACK));
		integracions.add(
				novaIntegracio(
						INTCODI_ARXIU));
		integracions.add(
				novaIntegracio(
						INTCODI_CIUTADA));
		integracions.add(
				novaIntegracio(
						INTCODI_USUARIS));
		integracions.add(
				novaIntegracio(
						INTCODI_CONVERT));
		integracions.add(
				novaIntegracio(
						INTCODI_DADESEXT));
		integracions.add(
				novaIntegracio(
						INTCODI_SIGNATURA));
		integracions.add(
				novaIntegracio(
						INTCODI_NOTIFICACIO));
		return integracions;
	}

	public List<IntegracioAccioDto> findAccionsByIntegracioCodi(
			String integracioCodi) {
		return getLlistaAccions(integracioCodi);
	}

	public void addAccioOk(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.OK);
		addAccio(
				integracioCodi,
				accio);
	}
	public void addAccioError(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		accio.setErrorDescripcio(errorDescripcio);
		addAccio(
				integracioCodi,
				accio);
	}
	public void addAccioError(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		accio.setErrorDescripcio(errorDescripcio);
		accio.setExcepcioMessage(
				ExceptionUtils.getMessage(throwable));
		accio.setExcepcioStacktrace(
				ExceptionUtils.getStackTrace(throwable));
		addAccio(
				integracioCodi,
				accio);
	}



	private LinkedList<IntegracioAccioDto> getLlistaAccions(
			String integracioCodi) {
		synchronized(accionsIntegracio){
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
	}
	private int getMaxAccions(
			String integracioCodi) {
		Integer max = maxAccionsIntegracio.get(integracioCodi);
		if (max == null) {
			max = new Integer(DEFAULT_MAX_ACCIONS);
			maxAccionsIntegracio.put(
					integracioCodi,
					max);
		}
		return max.intValue();
	}

	private void addAccio(
			String integracioCodi,
			IntegracioAccioDto accio) {
		afegirParametreUsuari(accio);
		LinkedList<IntegracioAccioDto> accions = getLlistaAccions(integracioCodi);
		int max = getMaxAccions(integracioCodi);
		while (accions.size() >= max) {
			accions.remove(accions.size() - 1);
		}
		accions.add(
				0,
				accio);
	}
	
	private void afegirParametreUsuari(
			IntegracioAccioDto accio) {
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

	private IntegracioDto novaIntegracio(
			String codi) {
		IntegracioDto integracio = new IntegracioDto();
		integracio.setCodi(codi);
		if (INTCODI_PFIRMA.equals(codi)) {
			integracio.setNom("Portafirmes");
		} else if (INTCODI_ARXIU.equals(codi)) {
			integracio.setNom("Arxiu digital");
		} else if (INTCODI_CONVERT.equals(codi)) {
			integracio.setNom("Conversió doc.");
		} else if (INTCODI_USUARIS.equals(codi)) {
			integracio.setNom("Usuaris");
		} else if (INTCODI_CIUTADA.equals(codi)) {
			integracio.setNom("Com. ciutadà");
		} else if (INTCODI_CALLBACK.equals(codi)) {
			integracio.setNom("Callback PF");
		} else if (INTCODI_DADESEXT.equals(codi)) {
			integracio.setNom("Dades ext.");
		} else if (INTCODI_SIGNATURA.equals(codi)) {
			integracio.setNom("Signatura");
		} else if (INTCODI_NOTIFICACIO.equals(codi)) {
			integracio.setNom("Notificació");
		}
		return integracio;
	}

}
