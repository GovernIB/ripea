/**
 * 
 */
package es.caib.ripea.core.helper;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.rest.client.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.domini.Estat;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioInfoDto;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.ExpedientRepository;
import es.caib.ripea.core.repository.MetaExpedientRepository;
import es.caib.ripea.core.repository.OrganGestorRepository;
import es.caib.ripea.core.repository.RegistreAnnexRepository;
import es.caib.ripea.core.repository.RegistreInteressatRepository;
import es.caib.ripea.core.repository.RegistreRepository;


@Component
public class ExpedientPeticioHelper0 {
	
	@Autowired
	private ExpedientPeticioHelper expedientPeticioHelper;
	
	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private RegistreInteressatRepository registreInteressatRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;  
	@Autowired
	private EntitatRepository entitatRepository; 
	@Autowired
	private MetaExpedientRepository metaExpedientRepository;
	@Autowired
	private ExpedientRepository expedientRepository;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Resource
	private OrganGestorHelper organGestorHelper;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private EmailHelper emailHelper;
	
	

	public void consultarIGuardarAnotacioPeticioPendent(
			Long expedientPeticioId,
			boolean throwException) {

		long t1 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
			logger.info("anotacioGuardarAll  start (" + expedientPeticioId + ")");
		
		ExpedientPeticioInfoDto epInfo = expedientPeticioHelper.getExpedeintPeticiInfo(expedientPeticioId);
		
		if (epInfo.getEstat() == ExpedientPeticioEstatEnumDto.CREAT) {
			
			String identificador = epInfo.getIdentificador();
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setIndetificador(identificador);
			anotacioRegistreId.setClauAcces(epInfo.getClauAcces());
			try {
				
				boolean throwMockException = false; // throwMockException = true
				if (throwMockException) {
					throw new RuntimeException("Mock exception al descarregar anotacio de distribució");
				}
				
				long t2 = System.currentTimeMillis();
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar consulta start (" + identificador + ", " + expedientPeticioId + ")");
				
				// obtain anotació from DISTRIBUCIO
				AnotacioRegistreEntrada registre = DistribucioHelper.getBackofficeIntegracioRestClient().consulta(anotacioRegistreId);
				
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar consulta end (" + identificador + ", " + expedientPeticioId + "):  " + (System.currentTimeMillis() - t2) + " ms");

				
				long t3 = System.currentTimeMillis();
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar crearRegistrePerPeticio start (" + identificador + ", " + expedientPeticioId + ")");
				
				// create anotació in db and associate it with expedient peticion
				expedientPeticioHelper.crearRegistrePerPeticio(
						registre,
						expedientPeticioId);

				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar crearRegistrePerPeticio end (" + identificador + ", " + expedientPeticioId + "):  " + (System.currentTimeMillis() - t3) + " ms");
				
				
				long t4 = System.currentTimeMillis();
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar canviEstat start (" + identificador + ", " + expedientPeticioId + ")");
				

				// change state of anotació in DISTRIBUCIO to BACK_REBUDA
				try {
					boolean throwMockException1 = false; // throwMockException1 = true
					if (throwMockException1) {
						throw new RuntimeException("Mock exception al canviar estat de l'anotació a BACK_REBUDA en distribució");
					}
					
					DistribucioHelper.getBackofficeIntegracioRestClient().canviEstat(
							anotacioRegistreId,
							Estat.REBUDA,
							"");
					expedientPeticioHelper.setEstatCanviatDistribucioNewTransaction(expedientPeticioId, true);
					
				} catch (Exception e) {
					expedientPeticioHelper.setEstatCanviatDistribucioNewTransaction(expedientPeticioId, false);
				}
				
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar canviEstat end (" + expedientPeticioId + "):  " + (System.currentTimeMillis() - t4) + " ms");
				
				
				long t5 = System.currentTimeMillis();
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar evictCountAnotacionsPendents start (" + identificador + ", " + expedientPeticioId + ")");
				
				EntitatEntity entitatAnotacio = entitatRepository.findByUnitatArrel(registre.getEntitatCodi());
				if (entitatAnotacio != null)
					cacheHelper.evictCountAnotacionsPendents(entitatAnotacio);
				
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar evictCountAnotacionsPendents end (" + identificador + ", " + expedientPeticioId + "):  " + (System.currentTimeMillis() - t5) + " ms");
				
				
				
				long t6 = System.currentTimeMillis();
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar evictCountAnotacionsPendents start (" + identificador + ", " + expedientPeticioId + ")");
				
				try {
					emailHelper.novaAnotacioPendent(expedientPeticioId);
				} catch (Exception e) {
					logger.error("Error al enviar email per nova anotació pendent", e);
				}
				
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar evictCountAnotacionsPendents end (" + identificador + ", " + expedientPeticioId + "):  " + (System.currentTimeMillis() - t6) + " ms");

			} catch (Throwable e) {

				logger.error("Error consultar i guardar anotació per petició: " + identificador + " RootCauseMessage: " + ExceptionUtils.getRootCauseMessage(e));
				
				long t7 = System.currentTimeMillis();
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar error start (" + identificador + ", " + expedientPeticioId + ")");
				try {
					// add error to peticio, so it will not be processed anymore until it will be resent from DISTRIBUCIO
					expedientPeticioHelper.addExpedientPeticioConsultaError(
							expedientPeticioId,
							StringUtils.abbreviate(
									ExceptionUtils.getStackTrace(e),
									3600));

					// change state of anotació in DISTRIBUCIO to BACK_ERROR
					DistribucioHelper.getBackofficeIntegracioRestClient().canviEstat(
							anotacioRegistreId,
							Estat.ERROR,
							StringUtils.abbreviate(
									ExceptionUtils.getStackTrace(e),
									3600));
					expedientPeticioHelper.setEstatCanviatDistribucioNewTransaction(expedientPeticioId, true);
					
				} catch (Exception e1) {
					expedientPeticioHelper.setEstatCanviatDistribucioNewTransaction(expedientPeticioId, false);
					logger.error("Error al enviar ERROR estat al distribució (" + identificador + ", " + expedientPeticioId + ")" , e1);
				}
				
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar error end (" + identificador + ", " + expedientPeticioId + "):  " + (System.currentTimeMillis() - t7) + " ms");
				
				if (throwException) {
					throw e;
				}
				
				
			} finally {
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar consultaAll end (" + expedientPeticioId + "):  " + (System.currentTimeMillis() - t1) + " ms");
			}
		} else {
			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
				logger.info("anotacioGuardar consultaAll ja guardat end (" + expedientPeticioId + "):  " + (System.currentTimeMillis() - t1) + " ms");
		}
		
	}
	
	public void comunicarAnotacioPendent(es.caib.distribucio.ws.backoffice.AnotacioRegistreId anotacioRegistreId) {

		long t3 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
			logger.info("Comunicant anotació start: " + anotacioRegistreId.getIndetificador());

		try {
			Long peticioId = expedientPeticioRepository.findIdByIdentificador(anotacioRegistreId.getIndetificador());
			
			if (peticioId == null) {
				expedientPeticioHelper.crearExpedientPeticion(anotacioRegistreId);
			} else {
				synchronized (SynchronizationHelper.get0To99Lock(peticioId, SynchronizationHelper.locksAnnotacions)) {
					expedientPeticioHelper.resetExpedientPeticion(peticioId);
				}
			}

			if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
				logger.info("Comunicant anotació end: " + anotacioRegistreId.getIndetificador() + ":  " + (System.currentTimeMillis() - t3) + " ms");

		} catch (Throwable e) {
			logger.error("Error comunicant anotació:" + anotacioRegistreId.getIndetificador() + ":  " + (System.currentTimeMillis() - t3) + " ms", e);
			throw e;
		}
			
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientPeticioHelper0.class);

}
	
	
	

