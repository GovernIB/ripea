/**
 * 
 */
package es.caib.ripea.core.service;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
import es.caib.distribucio.ws.backofficeintegracio.Estat;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.service.SegonPlaService;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.DistribucioHelper;
import es.caib.ripea.core.helper.ExpedientPeticioHelper;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;

/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class SegonPlaServiceImpl implements SegonPlaService {

	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private ExpedientPeticioHelper expedientPeticioHelper;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private CacheHelper cacheHelper;

	/*
	 * Obtain registres from DISTRIBUCIO for created peticions and save them in DB
	 */
	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.ripea.tasca.consulta.anotacio.temps.espera.execucio}")
	public void consultarIGuardarAnotacionsPeticionsPendents() {
		logger.debug(
				"Execució de tasca programada: consultar i guardar anotacions per peticions pedents de creacio del expedients");

		// find peticions with no registre associated and with no errors from previous invocation of this method
		List<ExpedientPeticioEntity> peticions = expedientPeticioRepository.findByEstatAndConsultaWsErrorIsFalse(
				ExpedientPeticioEstatEnumDto.CREAT);

		if (peticions != null &&
				!peticions.isEmpty()) {
			for (ExpedientPeticioEntity expedientPeticioEntity : peticions) {

				AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
				anotacioRegistreId.setIndetificador(
						expedientPeticioEntity.getIdentificador());
				anotacioRegistreId.setClauAcces(
						expedientPeticioEntity.getClauAcces());

				try {
					
					boolean throwException = false;
					if(throwException)
						throw new RuntimeException("EXCEPION BEFORE CONSULTING ANOTACIO!!!!!! ");
					
					
					// obtain registre from DISTRIBUCIO
					AnotacioRegistreEntrada registre = DistribucioHelper.getBackofficeIntegracioServicePort().consulta(
							anotacioRegistreId);

					// create registre in db and associate it with expedient peticion
					expedientPeticioHelper.crearRegistrePerPeticio(
							registre,
							expedientPeticioEntity);
					
					// change state of expedient peticion to pendent of acceptar or rebutjar
					expedientPeticioHelper.canviEstatExpedientPeticio(
							expedientPeticioEntity.getId(),
							ExpedientPeticioEstatEnumDto.PENDENT);

					// change state of registre in DISTRIBUCIO to BACK_REBUDA
					DistribucioHelper.getBackofficeIntegracioServicePort().canviEstat(
							anotacioRegistreId,
							Estat.REBUDA,
							"");
					

				} catch (Throwable e) {
					logger.error(
							"Error consultar i guardar anotació per petició: " +
									expedientPeticioEntity.getIdentificador() + 
									" RootCauseMessage: " + ExceptionUtils.getRootCauseMessage(e));
					try {
						boolean isRollbackException = true;
						while (isRollbackException) {
							if (e.getClass().toString().contains("RollbackException")) {
								e = e.getCause();
							} else {
								isRollbackException = false;
							}
						}

						// add error to peticio, so it will not be processed anymore until it will be resent from DISTRIBUCIO 
						expedientPeticioHelper.addExpedientPeticioConsultaError(
								expedientPeticioEntity.getId(),
								StringUtils.abbreviate(
										ExceptionUtils.getStackTrace(e),
										4000));
						
						// change state of registre in DISTRIBUCIO to BACK_ERROR
						DistribucioHelper.getBackofficeIntegracioServicePort().canviEstat(
								anotacioRegistreId,
								Estat.ERROR,
								StringUtils.abbreviate(
										ExceptionUtils.getStackTrace(e),
										4000));
						
					} catch (IOException e1) {
						logger.error(ExceptionUtils.getStackTrace(e1));
					}
				}
			}
		}
	}
	
	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.ripea.dominis.cache.execucio}")
	public void buidarCacheDominis() {
		try {
			//Connexió amb la BBDD del domini
			cacheHelper.evictCreateDominiConnexio();
			//Consulta
			cacheHelper.evictFindDominisByConsutla();
		} catch (Exception ex) {
			logger.error("No s'ha pogut buidar la cache de dominis", ex);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(
			SegonPlaServiceImpl.class);

}
