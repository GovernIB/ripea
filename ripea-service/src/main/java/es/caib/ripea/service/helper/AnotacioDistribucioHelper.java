package es.caib.ripea.service.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioInfoDto;

@Component
public class AnotacioDistribucioHelper {

	@Autowired private ExpedientPeticioHelper expedientPeticioHelper;
	@Autowired private EntitatRepository entitatRepository;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private EmailHelper emailHelper;
	@Autowired private DistribucioHelper distribucioHelper;

	public void consultarIGuardarAnotacioPeticioPendent(
			Long expedientPeticioId,
			boolean throwException) throws Throwable {

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
				AnotacioRegistreEntrada registre = distribucioHelper.getBackofficeIntegracioRestClient().consulta(anotacioRegistreId);
				
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar consulta end (" + identificador + ", " + expedientPeticioId + "):  " + (System.currentTimeMillis() - t2) + " ms");
				
				long t3 = System.currentTimeMillis();
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar crearRegistrePerPeticio start (" + identificador + ", " + expedientPeticioId + ")");

				/**
				 * Guarda les dades a:
				 *	> RegistreEntity = ipa_registre
				 *	> RegistreAnnexEntity = ipa_registre_annex
				 *	> RegistreInteressatEntity = ipa_registre_interessat
				 */
				expedientPeticioHelper.crearRegistrePerPeticio(
						registre,
						expedientPeticioId);

				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar crearRegistrePerPeticio end (" + identificador + ", " + expedientPeticioId + "):  " + (System.currentTimeMillis() - t3) + " ms");
				
				long t4 = System.currentTimeMillis();
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar canviEstat start (" + identificador + ", " + expedientPeticioId + ")");

				try {
					distribucioHelper.getBackofficeIntegracioRestClient().canviEstat(
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
					cacheHelper.evictAllCountAnotacionsPendents();
				
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar evictCountAnotacionsPendents end (" + identificador + ", " + expedientPeticioId + "):  " + (System.currentTimeMillis() - t5) + " ms");
				
				long t6 = System.currentTimeMillis();
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar novaAnotacioPendent start (" + identificador + ", " + expedientPeticioId + ")");
				
				try {
					emailHelper.novaAnotacioPendent(expedientPeticioId);
				} catch (Exception e) {
					logger.error("Error al enviar email per nova anotació pendent", e);
				}
				
				if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
					logger.info("anotacioGuardar novaAnotacioPendent end (" + identificador + ", " + expedientPeticioId + "):  " + (System.currentTimeMillis() - t6) + " ms");

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
					distribucioHelper.getBackofficeIntegracioRestClient().canviEstat(
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
	
	private static final Logger logger = LoggerFactory.getLogger(AnotacioDistribucioHelper.class);
}
	
	
	

