/**
 * 
 */
package es.caib.ripea.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.rest.client.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.domini.Estat;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.EventTipusEnumDto;
import es.caib.ripea.core.api.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.core.api.service.SegonPlaService;
import es.caib.ripea.core.config.PropertiesConstants;
import es.caib.ripea.core.config.SchedulingConfig;
import es.caib.ripea.core.entity.ContingutEntity;
import es.caib.ripea.core.entity.DocumentEntity;
import es.caib.ripea.core.entity.EmailPendentEnviarEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.ExpedientEntity;
import es.caib.ripea.core.entity.ExpedientPeticioEntity;
import es.caib.ripea.core.entity.InteressatEntity;
import es.caib.ripea.core.entity.MetaExpedientComentariEntity;
import es.caib.ripea.core.helper.CacheHelper;
import es.caib.ripea.core.helper.ConfigHelper;
import es.caib.ripea.core.helper.ConversioTipusHelper;
import es.caib.ripea.core.helper.DistribucioHelper;
import es.caib.ripea.core.helper.DocumentHelper;
import es.caib.ripea.core.helper.EmailHelper;
import es.caib.ripea.core.helper.ExpedientHelper;
import es.caib.ripea.core.helper.ExpedientInteressatHelper;
import es.caib.ripea.core.helper.ExpedientPeticioHelper;
import es.caib.ripea.core.helper.MetaExpedientHelper;
import es.caib.ripea.core.helper.OrganGestorHelper;
import es.caib.ripea.core.helper.SynchronizationHelper;
import es.caib.ripea.core.helper.TestHelper;
import es.caib.ripea.core.repository.ContingutRepository;
import es.caib.ripea.core.repository.EmailPendentEnviarRepository;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ExpedientPeticioRepository;
import es.caib.ripea.core.repository.InteressatRepository;
import es.caib.ripea.core.repository.MetaExpedientComentariRepository;
import lombok.extern.slf4j.Slf4j;


/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@Slf4j
public class SegonPlaServiceImpl implements SegonPlaService {

	@Autowired
	private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired
	private ExpedientPeticioHelper expedientPeticioHelper;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private CacheHelper cacheHelper;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private TestHelper testHelper;
	@Autowired
	private EmailPendentEnviarRepository emailPendentEnviarRepository;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private ExpedientInteressatHelper expedientInteressatHelper;
	@Autowired
	private InteressatRepository interessatRepository;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MetaExpedientHelper metaExpedientHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private MetaExpedientComentariRepository metaExpedientComentariRepository;
	@Autowired
	private SchedulingConfig schedulingConfig;


	private static final String PREFIX_RIPEA = "[RIPEA]";

	/*
	 * Obtain registres from DISTRIBUCIO for created peticions and save them in DB
	 */
	@Override
	public void consultarIGuardarAnotacionsPeticionsPendents() {

		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució de tasca periòdica: consultar i guardar anotacions per peticions pedents de creacio del expedients");


		// find peticions with no anotació associated and with no errors from previous invocation of this method
		List<ExpedientPeticioEntity> peticions = expedientPeticioRepository.findByEstatAndConsultaWsErrorIsFalse(ExpedientPeticioEstatEnumDto.CREAT);


		if (peticions != null && !peticions.isEmpty()) {
			for (ExpedientPeticioEntity expedientPeticioEntity : peticions) {

				AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
				anotacioRegistreId.setIndetificador(expedientPeticioEntity.getIdentificador());
				anotacioRegistreId.setClauAcces(expedientPeticioEntity.getClauAcces());
				try {

					// obtain anotació from DISTRIBUCIO
					AnotacioRegistreEntrada registre = DistribucioHelper.getBackofficeIntegracioRestClient().consulta(anotacioRegistreId);

					// create anotació in db and associate it with expedient peticion
					expedientPeticioHelper.crearRegistrePerPeticio(
							registre,
							expedientPeticioEntity);

					// change state of anotació in DISTRIBUCIO to BACK_REBUDA
					try {
						DistribucioHelper.getBackofficeIntegracioRestClient().canviEstat(
								anotacioRegistreId,
								Estat.REBUDA,
								"");
						expedientPeticioHelper.setEstatCanviatDistribucioNewTransaction(expedientPeticioEntity.getId(), true);
						
					} catch (Exception e) {
						expedientPeticioHelper.setEstatCanviatDistribucioNewTransaction(expedientPeticioEntity.getId(), false);
					}
					EntitatEntity entitatAnotacio = entitatRepository.findByUnitatArrel(registre.getEntitatCodi());
					if (entitatAnotacio != null)
						cacheHelper.evictCountAnotacionsPendents(entitatAnotacio);

				} catch (Throwable e) {

					logger.error("Error consultar i guardar anotació per petició: " + expedientPeticioEntity.getIdentificador() + " RootCauseMessage: " + ExceptionUtils.getRootCauseMessage(e));
					
					try {
						// add error to peticio, so it will not be processed anymore until it will be resent from DISTRIBUCIO
						expedientPeticioHelper.addExpedientPeticioConsultaError(
								expedientPeticioEntity.getId(),
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
						expedientPeticioHelper.setEstatCanviatDistribucioNewTransaction(expedientPeticioEntity.getId(), true);
						
					} catch (Exception e1) {
						expedientPeticioHelper.setEstatCanviatDistribucioNewTransaction(expedientPeticioEntity.getId(), false);
						logger.error(ExceptionUtils.getStackTrace(e1));
					}
				}
			}
		}
	}

	@Override
	@Transactional
	public void reintentarCanviEstatDistribucio() {

		List<Long> idsPendents = expedientPeticioRepository.findIdsPendentsCanviEstat(getMaxReintentsCanviEstatRebudaDistribucio());
		for (Long idPendent : idsPendents) {
			expedientPeticioHelper.reintentarCanviEstatDistribucio(idPendent);
		}
	}

	@Override
	public void buidarCacheDominis() {
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Buidar cachés dominis");
		try {
			//Consulta
			cacheHelper.evictFindDominisByConsutla();
		} catch (Exception ex) {
			logger.error("No s'ha pogut buidar la cache de dominis", ex);
		}
	}

	@Override
	@Transactional
//	@Scheduled(fixedDelayString = "5000")
	public void testEmailsAgrupats() {
		testHelper.testCanviEstatDocumentPortafirmes();
		testHelper.testCanviEstatNotificacio();

	}
	
	@Override
	@Transactional
	public void enviarEmailPerComentariMetaExpedient() {
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Enviar email per comentari metaexpedient");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -7);
		Date dateNowMinus7Days = cal.getTime();
		List<MetaExpedientComentariEntity> metaExpComnts = metaExpedientComentariRepository.findByEmailEnviatFalseAndCreatedDateGreaterThan(dateNowMinus7Days);
		
		for (MetaExpedientComentariEntity metaExpComnt : metaExpComnts) {
			try {
				emailHelper.comentariMetaExpedient(metaExpComnt);
			} catch (Exception e) {
				logger.error("Error enviant l'email per comentari comentariId=" + metaExpComnt.getId() + ", metaexpedientId=" + metaExpComnt.getMetaExpedient().getId() + ": " + e.getMessage());
			}
		}
	}
	
	
	@Override
	@Transactional
	public void enviarEmailsPendentsAgrupats() {
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Enviar correus pendents agrupats");

		List<EmailPendentEnviarEntity> emailPendentsList = emailPendentEnviarRepository.findByOrderByDestinatariAscEventTipusEnumAsc();
		
		// Agrupa per destinataris
		Map<String, List<EmailPendentEnviarEntity>> emailsPendentsMap = new HashMap<String, List<EmailPendentEnviarEntity>>();
		for (EmailPendentEnviarEntity contingutEmail : emailPendentsList) {
			if (emailsPendentsMap.containsKey(contingutEmail.getDestinatari())) {
				emailsPendentsMap.get(contingutEmail.getDestinatari()).add(contingutEmail);
			} else {
				List<EmailPendentEnviarEntity> lContingutEmails = new ArrayList<EmailPendentEnviarEntity>();
				lContingutEmails.add(contingutEmail);
				emailsPendentsMap.put(contingutEmail.getDestinatari(), lContingutEmails);
			}
		}
		// Envia i esborra per agrupació
		for (String email: emailsPendentsMap.keySet()) {
			
			emailPendentsList = emailsPendentsMap.get(email);
			try {
				enviarEmailsPendentsAgrupats(
						email, 
						emailPendentsList);
				if (cacheHelper.mostrarLogsSegonPla())
					logger.info("Enviat l'email d'avis de " + emailPendentsList.size() + " moviments agrupats al destinatari " + email);
				
			} catch (Exception e) {
				logger.error("Error enviant l'email d'avis de " + emailPendentsList.size() + " moviments agrupats al destinatari " + email + ": " + e.getMessage());
				
				for (EmailPendentEnviarEntity moviment : emailPendentsList) {
					// remove pending email if it is older that one week
					Date formattedToday = new Date();
					Date formattedExpired = moviment.getCreatedDate().toDate();
					int diffInDays = (int)( (formattedToday.getTime() - formattedExpired.getTime()) / (1000 * 60 * 60 * 24) );
					if (diffInDays > 7) {
						emailPendentEnviarRepository.delete(moviment);
					}
				}
			}

		}

	}
	
	public void enviarEmailsPendentsAgrupats(
			String emailDestinatari,
			List<EmailPendentEnviarEntity> emailPendents) {
		
			
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(emailPendents.get(0).getRemitent());
		missatge.setSubject(PREFIX_RIPEA + " Emails agrupats");
		
		
		// Agrupa per event tipus
		Map<EventTipusEnumDto, List<EmailPendentEnviarEntity>> eventTipos = new HashMap<EventTipusEnumDto, List<EmailPendentEnviarEntity>>();
		for (EmailPendentEnviarEntity contingutEmail : emailPendents) {
			if (eventTipos.containsKey(contingutEmail.getEventTipusEnum())) {
				eventTipos.get(contingutEmail.getEventTipusEnum()).add(contingutEmail);
			} else {
				List<EmailPendentEnviarEntity> lContingutEmails = new ArrayList<EmailPendentEnviarEntity>();
				lContingutEmails.add(contingutEmail);
				eventTipos.put(contingutEmail.getEventTipusEnum(), lContingutEmails);
			}
		}
		
		
		String text = "";

		for (Map.Entry<EventTipusEnumDto, List<EmailPendentEnviarEntity>> entry : eventTipos.entrySet()) {

			String header = "";
			if (entry.getKey() == EventTipusEnumDto.AGAFAT_ALTRE_USUARI) {
				header = "Elements de l'escriptori agafats per un altre usuari";
			} else if (entry.getKey() == EventTipusEnumDto.CANVI_ESTAT_PORTAFIRMES) {
				header = "Canvi d'estat de documents enviat a portafirmes";
			} else if(entry.getKey() == EventTipusEnumDto.CANVI_ESTAT_NOTIFICACIO) {
				header = "Canvi d'estat de notificacions";
			} else if(entry.getKey() == EventTipusEnumDto.CANVI_ESTAT_TASCA) {
				header = "Canvi d'estat de tasques";
			} else if(entry.getKey() == EventTipusEnumDto.CANVI_ESTAT_VIAFIRMA) {
				header = "Canvi d'estat de documents enviat a ViaFirma";
			} else if(entry.getKey() == EventTipusEnumDto.CANVI_ESTAT_REVISIO) {
				header = "Canvi d'estat de revisio de procediments";
			} else if(entry.getKey() == EventTipusEnumDto.PROCEDIMENT_COMENTARI) {
				header = "Nous comentaris per procediments";
			}
			
			text += header + "\n";
			text += "--------------------------------------------------------------------------\n\n";

			for (EmailPendentEnviarEntity emailPendentEnviarEntity : entry.getValue()) {
				text += emailPendentEnviarEntity.getText() + "\n\n";
			}
			text += "\n";
		}
		

		missatge.setText(text);
		
		mailSender.send(missatge);
		
		
		for (EmailPendentEnviarEntity emailPendent : emailPendents) {
			emailPendentEnviarRepository.delete(emailPendent);
		}
		
	}

	@Override
	@Transactional
	public void guardarExpedientsDocumentsArxiu() {
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Guardar expedients i documents en arxiu");
		
		int arxiuMaxReintentsExpedients = getArxiuMaxReintentsExpedients();
		int arxiuMaxReintentsDocuments = getArxiuMaxReintentsDocuments();
		
		List<ContingutEntity> pendents = contingutRepository.findContingutsPendentsArxiu(
				arxiuMaxReintentsExpedients,
				arxiuMaxReintentsDocuments);
		
		for (ContingutEntity contingut : pendents) {
			EntitatDto entitat = conversioTipusHelper.convertir(contingut.getEntitat(), EntitatDto.class);
			ConfigHelper.setEntitat(entitat);

			if (contingut instanceof ExpedientEntity) {
				synchronized (SynchronizationHelper.get0To99Lock(contingut.getId(), SynchronizationHelper.locksGuardarExpedientArxiu)) {
					expedientHelper.guardarExpedientArxiu(contingut.getId());
				}

			} else if (contingut instanceof DocumentEntity) {
				synchronized (SynchronizationHelper.get0To99Lock(contingut.getId(), SynchronizationHelper.locksGuardarDocumentArxiu)) {
					documentHelper.guardarDocumentArxiu(contingut.getId());
				}
			}
		}

	}

	@Override
	@Transactional
	public void guardarInteressatsArxiu() {
		if (cacheHelper.mostrarLogsSegonPla())
			logger.debug("Execució tasca periòdica: Guardar interessats en arxiu");
		
		List<InteressatEntity> pendents = interessatRepository.findInteressatsPendentsArxiu(getArxiuMaxReintentsInteressats());

		for (InteressatEntity interessat : pendents) {
			EntitatDto entitat = conversioTipusHelper.convertir(interessat.getExpedient().getEntitat(), EntitatDto.class);
			ConfigHelper.setEntitat(entitat);

			synchronized (SynchronizationHelper.get0To99Lock(interessat.getExpedient().getId(), SynchronizationHelper.locksGuardarExpedientArxiu)) {
				expedientInteressatHelper.guardarInteressatsArxiu(interessat.getExpedient().getId());
			}
		}
	}

    @Override
	@Transactional
    public void actualitzarProcediments() {

    	if (cacheHelper.mostrarLogsSegonPla())
    		logger.info("Execució tasca periòdica: Actualitzar procedimetns");

		if (configHelper.getConfig(PropertiesConstants.ACTUALITZAR_PROCEDIMENTS) == null)	// Tasca en segon pla no configurada
			return;
		List<EntitatDto> entitats = conversioTipusHelper.convertirList(entitatRepository.findAll(), EntitatDto.class);
		for(EntitatDto entitat: entitats) {
			try {
				ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitat, EntitatDto.class));
				metaExpedientHelper.actualitzarProcediments(entitat, new Locale("ca"), null);
			} catch (Exception e) {
				logger.error("Error al actualitzar procediments en segon pla", e);
			}
		}
    }

	@Override
	@Transactional
	public void consultaCanvisOrganigrama() {
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Actualitzar procedimetns");

		if (configHelper.getConfig(PropertiesConstants.CONSULTA_CANVIS_ORGANIGRAMA) == null)	// Tasca en segon pla no configurada
			return;
		List<EntitatEntity> entitats = entitatRepository.findAll();
		for(EntitatEntity entitat: entitats) {
			ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitat, EntitatDto.class));
			organGestorHelper.consultaCanvisOrganigrama(entitat);
		}
	}

	
	@Override
	public void restartSchedulledTasks() {
		schedulingConfig.restartSchedulledTasks();
	}
	
	
	private int getArxiuMaxReintentsExpedients() {
		String arxiuMaxReintentsExpedients = configHelper.getConfig("es.caib.ripea.segonpla.guardar.arxiu.max.reintents.expedients");
		return arxiuMaxReintentsExpedients != null && !arxiuMaxReintentsExpedients.isEmpty() ? Integer.valueOf(arxiuMaxReintentsExpedients) : 0;
	}
	
	private int getArxiuMaxReintentsDocuments() {
		String arxiuMaxReintentsDocuments = configHelper.getConfig("es.caib.ripea.segonpla.guardar.arxiu.max.reintents.documents");
		return arxiuMaxReintentsDocuments != null && !arxiuMaxReintentsDocuments.isEmpty() ? Integer.valueOf(arxiuMaxReintentsDocuments) : 0;
	}
	private int getArxiuMaxReintentsInteressats() {
		String arxiuMaxReintentsInteressats = configHelper.getConfig("es.caib.ripea.segonpla.guardar.arxiu.max.reintents.interessats");
		return arxiuMaxReintentsInteressats != null && !arxiuMaxReintentsInteressats.isEmpty() ? Integer.valueOf(arxiuMaxReintentsInteressats) : 0;
	}
	
	private int getMaxReintentsCanviEstatRebudaDistribucio() {
		String maxReintentsCanviEstatRebudaDistribucio = configHelper.getConfig("es.caib.ripea.segonpla.max.reintents.anotacions.pendents.enviar.distribucio");
		return maxReintentsCanviEstatRebudaDistribucio != null && !maxReintentsCanviEstatRebudaDistribucio.isEmpty() ? Integer.valueOf(maxReintentsCanviEstatRebudaDistribucio) : 0;
	}
	
	
	

	private static final Logger logger = LoggerFactory.getLogger(SegonPlaServiceImpl.class);

}
