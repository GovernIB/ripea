package es.caib.ripea.service.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.comanda.model.server.monitoring.Dimensio;
import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.EntitatDesc;
import es.caib.comanda.model.server.monitoring.Fet;
import es.caib.comanda.model.server.monitoring.Format;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistreEstadistic;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.ripea.persistence.entity.ContingutEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.EmailPendentEnviarEntity;
import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExplotacioDimensioEntity;
import es.caib.ripea.persistence.entity.ExplotacioFetsEntity;
import es.caib.ripea.persistence.entity.ExplotacioTempsEntity;
import es.caib.ripea.persistence.entity.InteressatEntity;
import es.caib.ripea.persistence.entity.MetaExpedientComentariEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.OrganGestorEntity;
import es.caib.ripea.persistence.entity.UsuariEntity;
import es.caib.ripea.persistence.repository.ContingutRepository;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.EmailPendentEnviarRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.ExplotacioDimensioRepository;
import es.caib.ripea.persistence.repository.ExplotacioFetsRepository;
import es.caib.ripea.persistence.repository.ExplotacioTempsRepository;
import es.caib.ripea.persistence.repository.InteressatRepository;
import es.caib.ripea.persistence.repository.MetaExpedientComentariRepository;
import es.caib.ripea.persistence.repository.MetaExpedientRepository;
import es.caib.ripea.persistence.repository.OrganGestorRepository;
import es.caib.ripea.persistence.repository.UsuariRepository;
import es.caib.ripea.service.config.SchedulingConfig;
import es.caib.ripea.service.helper.AnotacioDistribucioHelper;
import es.caib.ripea.service.helper.ApplicationHelper;
import es.caib.ripea.service.helper.CacheHelper;
import es.caib.ripea.service.helper.ConfigHelper;
import es.caib.ripea.service.helper.ConversioTipusHelper;
import es.caib.ripea.service.helper.DocumentHelper;
import es.caib.ripea.service.helper.EmailHelper;
import es.caib.ripea.service.helper.ExpedientHelper;
import es.caib.ripea.service.helper.ExpedientHelper2;
import es.caib.ripea.service.helper.ExpedientInteressatHelper;
import es.caib.ripea.service.helper.ExpedientPeticioHelper;
import es.caib.ripea.service.helper.MetaExpedientHelper;
import es.caib.ripea.service.helper.OrganGestorHelper;
import es.caib.ripea.service.helper.PluginHelper;
import es.caib.ripea.service.helper.SynchronizationHelper;
import es.caib.ripea.service.helper.TestHelper;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.ConsultaPinbalEstatEnumDto;
import es.caib.ripea.service.intf.dto.DocumentNotificacioEstatEnumDto;
import es.caib.ripea.service.intf.dto.EntitatDto;
import es.caib.ripea.service.intf.dto.EventTipusEnumDto;
import es.caib.ripea.service.intf.dto.ExpedientErrorTancamentDto;
import es.caib.ripea.service.intf.dto.ExpedientPeticioEstatEnumDto;
import es.caib.ripea.service.intf.dto.ExplotFetsAmbDimensioDto;
import es.caib.ripea.service.intf.dto.FitxerDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsAnotacionsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsObertsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsExpedientsTancatsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsNotificacionsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPinbalDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsPortafirmesDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsProcedimentsDto;
import es.caib.ripea.service.intf.dto.explotacio.ExplotFetsTasquesDto;
import es.caib.ripea.service.intf.dto.PortafirmesCallbackEstatEnumDto;
import es.caib.ripea.service.intf.dto.TascaEstatEnumDto;
import es.caib.ripea.service.intf.dto.TipusProcedimentServeiEnum;
import es.caib.ripea.service.intf.exception.ArxiuJaGuardatException;
import es.caib.ripea.service.intf.exception.SistemaExternException;
import es.caib.ripea.service.intf.service.SegonPlaService;
import es.caib.ripea.service.intf.utils.DateUtil;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SegonPlaServiceImpl implements SegonPlaService {

	@Autowired private MetaExpedientRepository metaExpedientRepository;
	@Autowired private ExpedientPeticioRepository expedientPeticioRepository;
	@Autowired private ExpedientPeticioHelper expedientPeticioHelper;
	@Autowired private EntitatRepository entitatRepository;
	@Autowired private OrganGestorRepository organGestorRepository;
	@Autowired private UsuariRepository usuariRepository;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private JavaMailSender mailSender;
	@Autowired private TestHelper testHelper;
	@Autowired private EmailPendentEnviarRepository emailPendentEnviarRepository;
	@Autowired private ContingutRepository contingutRepository;
	@Autowired private ExpedientHelper expedientHelper;
	@Autowired private ExpedientHelper2 expedientHelper2;
	@Autowired private DocumentHelper documentHelper;
	@Autowired private ExpedientInteressatHelper expedientInteressatHelper;
	@Autowired private InteressatRepository interessatRepository;
	@Autowired private ConfigHelper configHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;
	@Autowired private MetaExpedientHelper metaExpedientHelper;
	@Autowired private OrganGestorHelper organGestorHelper;
	@Autowired private EmailHelper emailHelper;
	@Autowired private MetaExpedientComentariRepository metaExpedientComentariRepository;
	@Autowired private ExplotacioFetsRepository explotacioFetsRepository;
	@Autowired private ExplotacioTempsRepository explotacioTempsRepository;
	@Autowired private ExplotacioDimensioRepository explotacioDimensioRepository;
	@Autowired private SchedulingConfig schedulingConfig;
	@Autowired private DocumentRepository documentRepository;
	@Autowired private AnotacioDistribucioHelper anotacioDistribucioHelper;
	@Autowired private ApplicationHelper applicationHelper;
	@Autowired private PluginHelper pluginHelper;

    /*
	 * Obtain registres from DISTRIBUCIO for created peticions and save them in DB
	 */
	@Override
	public int consultarIGuardarAnotacionsPeticionsPendents() throws Throwable {

		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució de tasca periòdica: consultar i guardar anotacions per peticions pedents de creacio del expedients");
		
		long t1 = System.currentTimeMillis();

		// find peticions with no anotació associated and with no errors from previous invocation of this method
		List<Long> peticionsId = expedientPeticioRepository.findIdByEstatAndConsultaWsErrorIsFalse(ExpedientPeticioEstatEnumDto.CREAT);
		
		if (cacheHelper.mostrarLogsRendimentDescarregarAnotacio())
			logger.info("Execució de tasca periòdica, Anotacions comunicades pendents de consulta: " + peticionsId.size());
		

		if (Utils.isNotEmpty(peticionsId)) {
			for (Long peticionId : peticionsId) {
				synchronized (SynchronizationHelper.get0To99Lock(peticionId, SynchronizationHelper.locksAnnotacions)) {
					anotacioDistribucioHelper.consultarIGuardarAnotacioPeticioPendent(peticionId, false);
				}
			}
		}
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: consultar i guardar anotacions per peticions pedents de creacio del expedients :  " + (System.currentTimeMillis() - t1) + " ms");
		
		return peticionsId==null?0:peticionsId.size();
	}

	@Override
	@Transactional
	public void reintentarCanviEstatDistribucio() {
		
		long t1 = System.currentTimeMillis();
    	if (cacheHelper.mostrarLogsSegonPla())
    		logger.info("Execució tasca periòdica: Reintentar canvi estat BACK_REBUDA a DISTRIBUCIO");

		List<Long> idsPendents = expedientPeticioRepository.findIdsPendentsCanviEstat(getMaxReintentsCanviEstatRebudaDistribucio());
		for (Long idPendent : idsPendents) {
			expedientPeticioHelper.reintentarCanviEstatDistribucio(idPendent);
		}
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Reintentar canvi estat BACK_REBUDA a DISTRIBUCIO :  " + (System.currentTimeMillis() - t1) + " ms");
		
	}

	@Override
	public void buidarCacheDominis() {
		long t1 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Buidar cachés dominis");
		try {
			//Consulta
			cacheHelper.evictFindDominisByConsulta();
		} catch (Exception ex) {
			logger.error("No s'ha pogut buidar la cache de dominis", ex);
		}
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Buidar cachés dominis :  " + (System.currentTimeMillis() - t1) + " ms");
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
		
		long t1 = System.currentTimeMillis();
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Enviar email per comentari metaexpedient");

		LocalDateTime dateNowMinus7Days = LocalDateTime.now().minusDays(7);
		List<MetaExpedientComentariEntity> metaExpComnts = metaExpedientComentariRepository.findByEmailEnviatFalseAndCreatedDateGreaterThan(dateNowMinus7Days);
		
		for (MetaExpedientComentariEntity metaExpComnt : metaExpComnts) {
			try {
				emailHelper.comentariMetaExpedient(metaExpComnt);
			} catch (Exception e) {
				logger.error("Error enviant l'email per comentari comentariId=" + metaExpComnt.getId() + ", metaexpedientId=" + metaExpComnt.getMetaExpedient().getId() + ": " + e.getMessage());
			}
		}
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Enviar email per comentari metaexpedient :  " + (System.currentTimeMillis() - t1) + " ms");
	}
	
	@Override
	@Transactional
	public int enviarEmailsPendentsAgrupats() {
		
		long t1 = System.currentTimeMillis();
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
					Date formattedExpired = Date.from(moviment.getCreatedDate().get().atZone(ZoneId.systemDefault()).toInstant());
					int diffInDays = (int)( (formattedToday.getTime() - formattedExpired.getTime()) / (1000 * 60 * 60 * 24) );
					if (diffInDays > 7) {
						emailPendentEnviarRepository.delete(moviment);
					}
				}
			}

		}
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Enviar correus pendents agrupats :  " + (System.currentTimeMillis() - t1) + " ms");

		return emailsPendentsMap.size();
	}
	
	public void enviarEmailsPendentsAgrupats(
			String emailDestinatari,
			List<EmailPendentEnviarEntity> emailPendents) {
		
			
//		SimpleMailMessage missatge = new SimpleMailMessage();
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(emailDestinatari);
            helper.setFrom(emailPendents.get(0).getRemitent());
            helper.setSubject(emailHelper.getPrefixRipea() + " Emails agrupats");


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
                switch (entry.getKey()) {
                    case AGAFAT_ALTRE_USUARI:
                        header = "Elements de l'escriptori agafats per un altre usuari";
                        break;
                    case CANVI_ESTAT_PORTAFIRMES:
                        header = "Canvi d'estat de documents enviat a portafirmes";
                        break;
                    case FIRMA_PARCIAL_PORTAFIB:
                        header = "Firma parcial de documents enviat a portafirmes";
                        break;
                    case CANVI_ESTAT_NOTIFICACIO:
                        header = "Canvi d'estat de notificacions";
                        break;
                    case CANVI_ESTAT_TASCA:
                        header = "Canvi d'estat de tasques";
                        break;
                    case CANVI_ESTAT_VIAFIRMA:
                        header = "Canvi d'estat de documents enviat a ViaFirma";
                        break;
                    case CANVI_ESTAT_REVISIO:
                        header = "Canvi d'estat de revisió de procediments";
                        break;
                    case PROCEDIMENT_COMENTARI:
                        header = "Nous comentaris en els procediments";
                        break;
                    case MENCIO_COMENTARI:
                        header = "Mencions en comentaris";
                        break;
                    case NOVA_ANOTACIO:
                        header = "Noves anotacions pendents";
                        break;
                    case CANVI_RESPONSABLES_TASCA:
                        header = "Canvi de responsables de tasques";
                        break;
                    case ALLIBERAT:
                        header = "Elements de l'escriptori alliberats";
                        break;
                    case MODIFICACIO_DATALIMIT_TASCA:
                        header = "Modificació data límit de tasques";
                        break;
                    case DELEGAT_TASCA:
                        header = "Assignació delegat de tasques";
                        break;
                    case CANCELAR_DELEGACIO_TASCA:
                        header = "Cancel·lació delegat de tasques";
                        break;
                    case EXEC_MASSIVA_FINALITZADA:
                        header = "Execucions massives finalitzades";
                        break;
                    case AVIS_ERROR_TANCAMENT_ARXIU:
                        header = "Errors en el tancament d'expedients a l'arxiu";
                        break;
                    default:
                        break;
                }

                text += header + "\n";
                text += "--------------------------------------------------------------------------\n\n";

                for (EmailPendentEnviarEntity emailPendentEnviarEntity : entry.getValue()) {
                    text += emailPendentEnviarEntity.getText() + "\n\n";

                    if (emailPendentEnviarEntity.getAdjuntId() != null) {
//                        Document fitxer = documentHelper.getFitxerById(
//                                emailPendentEnviarEntity.getAdjuntId(),
//                                emailPendentEnviarEntity.getEventTipusEnum());
                        FitxerDto fitxer = documentHelper.getFitxerAssociat(emailPendentEnviarEntity.getAdjuntId(), null);
                        if (fitxer != null) {
                            helper.addAttachment(fitxer.getNom(), new ByteArrayResource(fitxer.getContingut()));
                        }
                    }
                }
                text += "\n";
            }
            helper.setText(text);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Es propaga perquè el bucle que invoca aquest mètode aplica la lògica
            // de reintent/retenció; així no s'esborren correus que no s'han enviat.
            throw new RuntimeException("No s'ha pogut muntar o enviar el correu agrupat al destinatari " + emailDestinatari, e);
        }

		// Només s'esborren els pendents un cop s'ha enviat el correu correctament.
		for (EmailPendentEnviarEntity emailPendent : emailPendents) {
			emailPendentEnviarRepository.delete(emailPendent);
		}

	}

	@Override
	@Transactional
	public int guardarExpedientsDocumentsArxiu() {
		long t1 = System.currentTimeMillis();
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
				synchronized (SynchronizationHelper.get0To99Lock(contingut.getId(), SynchronizationHelper.locksExpedients)) {
					try {
						expedientHelper.guardarExpedientArxiu(contingut.getId());
					} catch (ArxiuJaGuardatException e) {
					} catch (Exception e) {
						logger.error("Error al guardar expedient en arxiu, segon pla ", e);
					}
				}

			} else if (contingut instanceof DocumentEntity) {
				Long expedientId = documentRepository.findExpedientId(contingut.getId());
				synchronized (SynchronizationHelper.get0To99Lock(expedientId, SynchronizationHelper.locksExpedients)) {
					try {
						documentHelper.guardarDocumentArxiu(contingut.getId());
					} catch (ArxiuJaGuardatException e) {
					} catch (Exception e) {
						logger.error("Error al guardar document en arxiu, segon pla ",
								e);
					}
				}
			}
		}
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Guardar expedients i documents en arxiu :  " + (System.currentTimeMillis() - t1) + " ms");

		return pendents==null?0:pendents.size();
	}

	@Override
	@Transactional
	public void guardarInteressatsArxiu() {
		long t1 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsSegonPla())
			logger.debug("Execució tasca periòdica: Guardar interessats en arxiu");
		
		List<InteressatEntity> pendents = interessatRepository.findInteressatsPendentsArxiu(getArxiuMaxReintentsInteressats());

		for (InteressatEntity interessat : pendents) {
			EntitatDto entitat = conversioTipusHelper.convertir(interessat.getExpedient().getEntitat(), EntitatDto.class);
			ConfigHelper.setEntitat(entitat);

			synchronized (SynchronizationHelper.get0To99Lock(interessat.getExpedient().getId(), SynchronizationHelper.locksExpedients)) {
				try {
					Exception exception = expedientInteressatHelper.guardarInteressatsArxiu(interessat.getExpedient().getId());
					if (exception != null) throw exception;
				} catch (Exception e) {
					logger.error("Error al guardar interessat en arxiu, segon pla ", e);
				}
			}
		}
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Guardar interessats en arxiu :  " + (System.currentTimeMillis() - t1) + " ms");
	}

    @Override
	@Transactional
    public void actualitzarProcediments() {

		long t1 = System.currentTimeMillis();

    	if (cacheHelper.mostrarLogsSegonPla())
    		logger.info("Execució tasca periòdica: Actualitzar procediments");

		if (configHelper.getConfig(PropertyConfig.ACTUALITZAR_PROCEDIMENTS) == null)
			return;

		List<EntitatEntity> entitats = entitatRepository.findAll();
		for(EntitatEntity entitat: entitats) {
			try {
				ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitat, EntitatDto.class));
				metaExpedientHelper.actualitzarProcediments(
						entitat,
						metaExpedientRepository.findByEntitatOrderByNomAsc(entitat),
						new Locale("ca"),
						null);
			} catch (Exception e) {
				logger.error("Error al actualitzar procediments en segon pla", e);
			}
		}
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Actualitzar procediments :  " + (System.currentTimeMillis() - t1) + " ms");
    }

	@Override
	@Transactional
	public void consultaCanvisOrganigrama() {
		
		long t1 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Consulta de canvis en l'organigrama");

		if (configHelper.getConfig(PropertyConfig.CONSULTA_CANVIS_ORGANIGRAMA) == null)	// Tasca en segon pla no configurada
			return;
		List<EntitatEntity> entitats = entitatRepository.findAll();
		for(EntitatEntity entitat: entitats) {
			ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitat, EntitatDto.class));
			organGestorHelper.consultaCanvisOrganigrama(entitat);
		}
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Consulta de canvis en l'organigrama :  " + (System.currentTimeMillis() - t1) + " ms");
	} 

	@Override
	@Transactional
	public void tancarExpedientsArxiu() {
		
		long t1 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Consulta expedients pendents de tancar a l'arxiu i que ha arribat l'hora programada");
		List<ExpedientErrorTancamentDto> errorsTancament = new ArrayList<ExpedientErrorTancamentDto>(); 
		List<EntitatEntity> entitats = entitatRepository.findAll();

		for (EntitatEntity entitat : entitats) {
			List<ExpedientEntity> expedientsPendentsTancar = expedientHelper.consultaExpedientsPendentsTancarArxiu(entitat);
			ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitat, EntitatDto.class));
			
			for (ExpedientEntity expedient : expedientsPendentsTancar) {
				synchronized (SynchronizationHelper.get0To99Lock(expedient.getId(), SynchronizationHelper.locksExpedients)) {
					try {
						expedientHelper2.closeExpedientArxiu(expedient.getId());
					} catch (SistemaExternException ex) {
						errorsTancament.add(new ExpedientErrorTancamentDto(expedient.getNumero(), expedient.getId(), ex.getMessage()));
					} catch (Exception e) {
						logger.error("Error inesperat tancant expedient [{}]", expedient.getId(), e);
						e.printStackTrace();
					}
				}
			}
		}
		
	    if (!errorsTancament.isEmpty()) {
	        emailHelper.avisarAdministradorsErrorTancament(errorsTancament);
	    }
	    
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Consulta expedients pendents de tancar a l'arxiu i que ha arribat l'hora programada :  " + (System.currentTimeMillis() - t1) + " ms");
		
	}
	
	@Override
	@Transactional
	public void generarJsonMetriques() throws Exception {
		
		long t1 = System.currentTimeMillis();
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Execució tasca periòdica: Consulta expedients pendents de tancar a l'arxiu i que ha arribat l'hora programada");

		String jsonMetrics = applicationHelper.getMetriquesJSON();
        InputStream contingut = new ByteArrayInputStream(jsonMetrics.getBytes(StandardCharsets.UTF_8));
        String baseDir = configHelper.getConfig(PropertyConfig.GESDOC_PLUGIN_FILESYSTEM_PATH);
        String agrupacio = "METRICS";
		if (baseDir.endsWith("/")) {
			baseDir = baseDir + agrupacio;
		} else {
			baseDir = baseDir + "/" + agrupacio;
		}
		//Si no el getGestioDocumentalPlugin dona un error
		List<EntitatEntity> entitats = entitatRepository.findAll();
		if (entitats!=null && entitats.size()>0) {
			ConfigHelper.setEntitat(conversioTipusHelper.convertir(entitats.get(0), EntitatDto.class));
	    	pluginHelper.gestioDocumentalCreate(agrupacio, contingut);
		}
		
		if (cacheHelper.mostrarLogsSegonPla())
			logger.info("Fin de tasca periòdica: Consulta expedients pendents de tancar a l'arxiu i que ha arribat l'hora programada :  " + (System.currentTimeMillis() - t1) + " ms");
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<DimensioDesc> getDimensionsInfo() {
		List<DimensioDesc> resultat = new ArrayList<DimensioDesc>();
		
		List<String> valorsEntitats = entitatRepository.findCodisOrdenats();
		DimensioDesc dimEntitat = new DimensioDesc("ENT", "Entitats").descripcio("Entitats de RIPEA").valors(valorsEntitats);
		resultat.add(dimEntitat);
		
		List<String> valorsProcediments = metaExpedientRepository.findCodisOrdenats();
		DimensioDesc dimProcediment = new DimensioDesc("PRO", "Procediment").descripcio("Procediments de RIPEA").valors(valorsProcediments);
		resultat.add(dimProcediment);
		
		List<String> valorsOGs = organGestorRepository.findCodisOrdenats();
		DimensioDesc dimOrgansGestors = new DimensioDesc("ORG", "Òrgan gestor").descripcio("Òrgans gestors de RIPEA").valors(valorsOGs);
		resultat.add(dimOrgansGestors);
		
		List<String> valorsUsuaris = usuariRepository.findCodisOrdenats();
		DimensioDesc dimUsuari = new DimensioDesc("USU", "Usuaris").descripcio("Usuaris de RIPEA").valors(valorsUsuaris);
		resultat.add(dimUsuari);
		
		return resultat;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<IndicadorDesc> getIndicadorsInfo() {
		
		List<IndicadorDesc> resultat = new ArrayList<IndicadorDesc>();
		
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.PROCEDIMENTS_ACTIUS_TOTAL.toString(), "Procediments actius totals").descripcio("Procediments actius a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.SERVEIS_ACTIUS_TOTAL.toString(), "Serveis actius totals").descripcio("Serveis actius a RIPEA").format(Format.LONG));
		
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.ANO_NOVES.toString(), "Anotacions noves").descripcio("Noves anotacions rebudes a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.ANO_NOVES_TOTAL.toString(), "Anotacions noves totals").descripcio("Total noves anotacions rebudes a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.ANO_PROCESSADES.toString(), "Anotacions processades").descripcio("Anotacions processades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.ANO_PROCESSADES_TOTAL.toString(), "Anotacions processades totals").descripcio("Anotacions processades totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.ANO_REBUTJADES.toString(), "Anotacions rebutjades").descripcio("Anotacions rebutjades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.ANO_REBUTJADES_TOTAL.toString(), "Anotacions rebutjades totals").descripcio("Anotacions rebutjades totals a RIPEA").format(Format.LONG));
		
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.EXP_OBERTS.toString(), "Expedients oberts").descripcio("Expedients oberts a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.EXP_OBERTS_TOTAL.toString(), "Expedients oberts totals").descripcio("Expedients oberts totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.EXP_TANCATS.toString(), "Expedients tancats").descripcio("Expedients tancats a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.EXP_TANCATS_TOTAL.toString(), "Expedients tancats totals").descripcio("Expedients tancats totals a RIPEA").format(Format.LONG));
		
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_FIRMADES.toString(), "Env. portafib firmats").descripcio("Env. portafib firmats a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_FIRMADES_TOTAL.toString(), "Env. portafib firmats totals").descripcio("Env. portafib firmats totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_ENVIADES.toString(), "Env. portafib enviats").descripcio("Env. portafib enviats a RIPEA pendents de resposta de portafirmes").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_ENVIADES_TOTAL.toString(), "Env. portafib enviats totals").descripcio("Env. portafib enviats totals a RIPEA pendents de resposta de portafirmes").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_INICIADES.toString(), "Env. portafib iniciats").descripcio("Env. portafib iniciats a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_INICIADES_TOTAL.toString(), "Env. portafib iniciats totals").descripcio("Env. portafib iniciats totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_PARCIALS.toString(), "Env. portafib parcials").descripcio("Env. portafib parcials a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_PARCIALS_TOTAL.toString(), "Env. portafib parcials totals").descripcio("Env. portafib parcials totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_PAUSADES.toString(), "Env. portafib pausats").descripcio("Env. portafib pausats a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_PAUSADES_TOTAL.toString(), "Env. portafib pausats totals").descripcio("Env. portafib pausats totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_REBUTJADES.toString(), "Env. portafib rebutjats").descripcio("Env. portafib rebutjats a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.FIR_REBUTJADES_TOTAL.toString(), "Env. portafib rebutjats totals").descripcio("Env. portafib rebutjats totals a RIPEA").format(Format.LONG));

		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_ENVIADES.toString(), "Notificacions enviades").descripcio("Notificacions enviades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_ENVIADES_TOTAL.toString(), "Notificacions enviades totals").descripcio("Notificacions enviades totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_PENDENTS.toString(), "Notificacions pendents").descripcio("Notificacions pendents a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_PENDENTS_TOTAL.toString(), "Notificacions pendents totals").descripcio("Notificacions pendents totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_REGISTRADES.toString(), "Notificacions registrades").descripcio("Notificacions pendents totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_REGISTRADES_TOTAL.toString(), "Notificacions registrades totals").descripcio("Notificacions registrades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_FINALITZADES.toString(), "Notificacions finalitzades").descripcio("Notificacions finalitzades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_FINALITZADES_TOTAL.toString(), "Notificacions finalitzades totals").descripcio("Notificacions finalitzades totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_PROCESSADES.toString(), "Notificacions processades").descripcio("Notificacions processades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_PROCESSADES_TOTAL.toString(), "Notificacions processades totals").descripcio("Notificacions processades totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_ENVIADES_ERROR.toString(), "Notificacions enviades amb error").descripcio("Notificacions enviades amb error a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_ENVIADES_ERROR_TOTAL.toString(), "Notificacions enviades amb error totals").descripcio("Notificacions enviades amb error totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_FINALITZADES_ERROR.toString(), "Notificacions finalitzades amb error").descripcio("Notificacions finalitzades amb error a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.NOT_FINALITZADES_ERROR_TOTAL.toString(), "Notificacions finalitzades amb error totals").descripcio("Notificacions finalitzades amb error totals a RIPEA").format(Format.LONG));

		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.PIN_ENVIAMENTS_OK.toString(), "Enviaments PINBAL processats OK").descripcio("Enviaments PINBAL processats OK a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.PIN_ENVIAMENTS_ERROR.toString(), "Enviaments PINBAL amb error").descripcio("Enviaments PINBAL amb error a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.PIN_ENVIAMENTS_OK_TOTAL.toString(), "Enviaments PINBAL totals processats OK").descripcio("Enviaments PINBAL totals processats a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.PIN_ENVIAMENTS_ERROR_TOTAL.toString(), "Enviaments PINBAL totals amb error").descripcio("Enviaments PINBAL totals amb error a RIPEA").format(Format.LONG));
		
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_AGAFADES.toString(), "Tasques afagades").descripcio("Tasques afagades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_AGAFADES_TOTAL.toString(), "Tasques afagades totals").descripcio("Tasques afagades totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_CANCELADES.toString(), "Tasques cancelades").descripcio("Tasques cancelades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_CANCELADES_TOTAL.toString(), "Tasques cancelades totals").descripcio("Tasques cancelades totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_CREADES.toString(), "Tasques creades").descripcio("Tasques creades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_CREADES_TOTAL.toString(), "Tasques creades totals").descripcio("Tasques creades totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_FIN_DINS_TERMINI.toString(), "Tasques finalitzades dins termini").descripcio("Tasques finalitzades dins termini a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_FIN_DINS_TERMINI_TOTAL.toString(), "Tasques finalitzades dins termini totals").descripcio("Tasques finalitzades dins termini totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_FIN_FORA_TERMINI.toString(), "Tasques finalitzades fora termini").descripcio("Tasques finalitzades fora termini a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_FIN_FORA_TERMINI_TOTAL.toString(), "Tasques finalitzades fora termini totals").descripcio("Tasques finalitzades fora termini totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_INICIADES.toString(), "Tasques iniciades").descripcio("Tasques iniciades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_INICIADES_TOTAL.toString(), "Tasques iniciades totals").descripcio("Tasques iniciades totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_NOTFIN_FORA_TERMINI.toString(), "Tasques no finalitzades fora termini").descripcio("Tasques no finalitzades amb la data limit superada a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_NOTFIN_FORA_TERMINI_TOTAL.toString(), "Tasques no finalitzades fora termini totals").descripcio("Tasques no finalitzades amb la data limit superada totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_PENDENTS.toString(), "Tasques pendents").descripcio("Tasques pendents a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_PENDENTS_TOTAL.toString(), "Tasques pendents totals").descripcio("Tasques pendents totals a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_REBUTJADES.toString(), "Tasques rebutjades").descripcio("Tasques rebutjades a RIPEA").format(Format.LONG));
		resultat.add(new IndicadorDesc(ExplotFetsAmbDimensioDto.FetsEnum.TAS_REBUTJADES_TOTAL.toString(), "Tasques rebutjades totals").descripcio("Tasques rebutjades totals a RIPEA").format(Format.LONG));
		
		return resultat;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<EntitatDesc> getEntitatsInfo() {
		List<EntitatDesc> resultat = new ArrayList<EntitatDesc>();
		List<EntitatEntity> entitats = entitatRepository.findAll();
		if (entitats!=null) {
			for (EntitatEntity entitat: entitats) {
				resultat.add(new EntitatDesc(entitat.getCodi(), entitat.getNom(), entitat.getUnitatArrel(), entitat.getCif()));
			}
		}
		return resultat;
	}
	
	@Override
	@Transactional
	public boolean existeixenEstadistiques(LocalDate date) {
		ExplotacioTempsEntity tempsDia = explotacioTempsRepository.findFirstByData(date);
		return (tempsDia!=null);
	}
	
	@Override
	@Transactional
	public RegistresEstadistics consultaEstadistiques(LocalDate date) {
	
		ExplotacioTempsEntity tempsDia = explotacioTempsRepository.findFirstByData(date);

		Date dateJava = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

		List<RegistreEstadistic> fets = new ArrayList<RegistreEstadistic>();
		List<ExplotacioFetsEntity> dadesByTemps = explotacioFetsRepository.findByTemps(tempsDia);
		
		if (dadesByTemps!=null) {
			for (ExplotacioFetsEntity efe: dadesByTemps) {
				RegistreEstadistic reComanda = new RegistreEstadistic()
						.dimensions(toDimensioComanda(efe.getDimensio()))
						.fets(toFetComanda(efe));
				fets.add(reComanda);
			}
		}
		
		RegistresEstadistics resultat = new RegistresEstadistics();
		resultat.setFets(fets);
		resultat.setTemps(DateUtil.toOffsetDateTime(dateJava));
		return resultat;
	}

	private List<Dimensio> toDimensioComanda(ExplotacioDimensioEntity ede) {
		List<Dimensio> resultat = new ArrayList<Dimensio>();
		resultat.add(new Dimensio().codi("ENT").valor(ede.getEntitatCodi()));
		resultat.add(new Dimensio().codi("PRO").valor(ede.getProcedimentCodi()));
		resultat.add(new Dimensio().codi("ORG").valor(ede.getOrganCodi()));
		resultat.add(new Dimensio().codi("USU").valor(ede.getUsuari()!=null?ede.getUsuari().getCodi():null));
		return resultat;		
	}
	
	private List<Fet> toFetComanda(ExplotacioFetsEntity efe) {
		List<Fet> resultat = new ArrayList<Fet>();
		
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.PROCEDIMENTS_ACTIUS_TOTAL.toString()).valor(efe.getProcedimentsActiusTotal()!=null?efe.getProcedimentsActiusTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.SERVEIS_ACTIUS_TOTAL.toString()).valor(efe.getServeisActiusTotal()!=null?efe.getServeisActiusTotal().doubleValue():null));
		
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.ANO_NOVES.toString()).valor(efe.getAnotacionsNoves()!=null?efe.getAnotacionsNoves().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.ANO_NOVES_TOTAL.toString()).valor(efe.getAnotacionsNovesTotal()!=null?efe.getAnotacionsNovesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.ANO_PROCESSADES.toString()).valor(efe.getAnotacionsProcessades()!=null?efe.getAnotacionsProcessades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.ANO_PROCESSADES_TOTAL.toString()).valor(efe.getAnotacionsProcessadesTotal()!=null?efe.getAnotacionsProcessadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.ANO_REBUTJADES.toString()).valor(efe.getAnotacionsRebutjades()!=null?efe.getAnotacionsRebutjades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.ANO_REBUTJADES_TOTAL.toString()).valor(efe.getAnotacionsRebutjadesTotal()!=null?efe.getAnotacionsRebutjadesTotal().doubleValue():null));
		
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.EXP_OBERTS.toString()).valor(efe.getExpedientsOberts()!=null?efe.getExpedientsOberts().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.EXP_OBERTS_TOTAL.toString()).valor(efe.getExpedientsObertsTotal()!=null?efe.getExpedientsObertsTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.EXP_TANCATS.toString()).valor(efe.getExpedientsTancats()!=null?efe.getExpedientsTancats().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.EXP_TANCATS_TOTAL.toString()).valor(efe.getExpedientsTancatsTotal()!=null?efe.getExpedientsTancatsTotal().doubleValue():null));
		
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_FIRMADES.toString()).valor(efe.getFirmesFirmades()!=null?efe.getFirmesFirmades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_FIRMADES_TOTAL.toString()).valor(efe.getFirmesFirmadesTotal()!=null?efe.getFirmesFirmadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_ENVIADES.toString()).valor(efe.getFirmesEnviades()!=null?efe.getFirmesEnviades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_ENVIADES_TOTAL.toString()).valor(efe.getFirmesEnviadesTotal()!=null?efe.getFirmesEnviadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_INICIADES.toString()).valor(efe.getFirmesIniciades()!=null?efe.getFirmesIniciades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_INICIADES_TOTAL.toString()).valor(efe.getFirmesIniciadesTotal()!=null?efe.getFirmesIniciadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_PARCIALS.toString()).valor(efe.getFirmesParcials()!=null?efe.getFirmesParcials().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_PARCIALS_TOTAL.toString()).valor(efe.getFirmesParcialsTotal()!=null?efe.getFirmesParcialsTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_PAUSADES.toString()).valor(efe.getFirmesPausades()!=null?efe.getFirmesPausades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_PAUSADES_TOTAL.toString()).valor(efe.getFirmesPausadesTotal()!=null?efe.getFirmesPausadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_REBUTJADES.toString()).valor(efe.getFirmesRebutjades()!=null?efe.getFirmesRebutjades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.FIR_REBUTJADES_TOTAL.toString()).valor(efe.getFirmesRebutjadesTotal()!=null?efe.getFirmesRebutjadesTotal().doubleValue():null));
		
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_ENVIADES.toString()).valor(efe.getNotificacionsEnviades()!=null?efe.getNotificacionsEnviades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_ENVIADES_TOTAL.toString()).valor(efe.getNotificacionsEnviadesTotal()!=null?efe.getNotificacionsEnviadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_PENDENTS.toString()).valor(efe.getNotificacionsPendents()!=null?efe.getNotificacionsPendents().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_PENDENTS_TOTAL.toString()).valor(efe.getNotificacionsPendentsTotal()!=null?efe.getNotificacionsPendentsTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_REGISTRADES.toString()).valor(efe.getNotificacionsRegistrades()!=null?efe.getNotificacionsRegistrades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_REGISTRADES_TOTAL.toString()).valor(efe.getNotificacionsRegistradesTotal()!=null?efe.getNotificacionsRegistradesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_FINALITZADES.toString()).valor(efe.getNotificacionsFinalitzades()!=null?efe.getNotificacionsFinalitzades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_FINALITZADES_TOTAL.toString()).valor(efe.getNotificacionsFinalitzadesTotal()!=null?efe.getNotificacionsFinalitzadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_PROCESSADES.toString()).valor(efe.getNotificacionsProcessades()!=null?efe.getNotificacionsProcessades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_PROCESSADES_TOTAL.toString()).valor(efe.getNotificacionsProcessadesTotal()!=null?efe.getNotificacionsProcessadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_ENVIADES_ERROR.toString()).valor(efe.getNotificacionsEnvError()!=null?efe.getNotificacionsEnvError().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_ENVIADES_ERROR_TOTAL.toString()).valor(efe.getNotificacionsEnvErrorTotal()!=null?efe.getNotificacionsEnvErrorTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_FINALITZADES_ERROR.toString()).valor(efe.getNotificacionsFinError()!=null?efe.getNotificacionsFinError().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.NOT_FINALITZADES_ERROR_TOTAL.toString()).valor(efe.getNotificacionsFinErrorTotal()!=null?efe.getNotificacionsFinErrorTotal().doubleValue():null));
		
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.PIN_ENVIAMENTS_OK.toString()).valor(efe.getPinbalEnviamentsOk()!=null?efe.getPinbalEnviamentsOk().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.PIN_ENVIAMENTS_ERROR.toString()).valor(efe.getPinbalEnviamentsError()!=null?efe.getPinbalEnviamentsError().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.PIN_ENVIAMENTS_OK_TOTAL.toString()).valor(efe.getPinbalEnviamentsTotalOk()!=null?efe.getPinbalEnviamentsTotalOk().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.PIN_ENVIAMENTS_ERROR_TOTAL.toString()).valor(efe.getPinbalEnviamentsTotalError()!=null?efe.getPinbalEnviamentsTotalError().doubleValue():null));
		
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_AGAFADES.toString()).valor(efe.getTasquesAgafades()!=null?efe.getTasquesAgafades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_AGAFADES_TOTAL.toString()).valor(efe.getTasquesAgafadesTotal()!=null?efe.getTasquesAgafadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_CANCELADES.toString()).valor(efe.getTasquesCancelades()!=null?efe.getTasquesCancelades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_CANCELADES_TOTAL.toString()).valor(efe.getTasquesCanceladesTotal()!=null?efe.getTasquesCanceladesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_CREADES.toString()).valor(efe.getTasquesCreades()!=null?efe.getTasquesCreades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_CREADES_TOTAL.toString()).valor(efe.getTasquesCreadesTotal()!=null?efe.getTasquesCreadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_FIN_DINS_TERMINI.toString()).valor(efe.getTasquesFinalitzadesDinsTermini()!=null?efe.getTasquesFinalitzadesDinsTermini().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_FIN_DINS_TERMINI_TOTAL.toString()).valor(efe.getTasquesFinalitzadesTotalDinsTermini()!=null?efe.getTasquesFinalitzadesTotalDinsTermini().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_FIN_FORA_TERMINI.toString()).valor(efe.getTasquesFinalitzadesForaTermini()!=null?efe.getTasquesFinalitzadesForaTermini().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_FIN_FORA_TERMINI_TOTAL.toString()).valor(efe.getTasquesFinalitzadesTotalForaTermini()!=null?efe.getTasquesFinalitzadesTotalForaTermini().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_INICIADES.toString()).valor(efe.getTasquesIniciades()!=null?efe.getTasquesIniciades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_INICIADES_TOTAL.toString()).valor(efe.getTasquesIniciadesTotal()!=null?efe.getTasquesIniciadesTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_NOTFIN_FORA_TERMINI.toString()).valor(efe.getTasquesNoFinalitzadesForaTermini()!=null?efe.getTasquesNoFinalitzadesForaTermini().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_NOTFIN_FORA_TERMINI_TOTAL.toString()).valor(efe.getTasquesNoFinalitzadesForaTerminiTotal()!=null?efe.getTasquesNoFinalitzadesForaTerminiTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_PENDENTS.toString()).valor(efe.getTasquesPendents()!=null?efe.getTasquesPendents().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_PENDENTS_TOTAL.toString()).valor(efe.getTasquesPendentsTotal()!=null?efe.getTasquesPendentsTotal().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_REBUTJADES.toString()).valor(efe.getTasquesRebutjades()!=null?efe.getTasquesRebutjades().doubleValue():null));
		resultat.add(new Fet().codi(ExplotFetsAmbDimensioDto.FetsEnum.TAS_REBUTJADES_TOTAL.toString()).valor(efe.getTasquesRebutjadesTotal()!=null?efe.getTasquesRebutjadesTotal().doubleValue():null));
		
		return resultat;
	}
	
	@Override
	@Transactional
	public List<ExplotFetsAmbDimensioDto> generarEstadistiquesDiaries(Date fecha) throws Exception {

		LocalDate avui = LocalDate.now();
		
		if (fecha==null) {
			fecha = Date.from(avui.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		
		LocalDateTime dataIni	= DateUtil.getLocalDateTimeFromDate(fecha, true, false);
		LocalDateTime dataFi	= DateUtil.getLocalDateTimeFromDate(fecha, false, true);
		
		Calendar calendarFechaAvui = Calendar.getInstance();
		calendarFechaAvui.setTime(fecha);
		
		Calendar calendarFechaAhir = Calendar.getInstance();
		calendarFechaAhir.setTime(fecha);
		calendarFechaAhir.add(Calendar.DAY_OF_MONTH, -1);
		
		Date dateIni	= DateUtil.startOfDay(calendarFechaAvui).getTime();
		Date dateFi		= DateUtil.endOfDay(calendarFechaAvui).getTime();
		Date ahirFi		= DateUtil.endOfDay(calendarFechaAhir).getTime();
		
		LocalDateTime dataAhirFi	= DateUtil.getLocalDateTimeFromDate(ahirFi, false, true);
		
		//Cada grup d'indicadors es resol amb una unica consulta agregada per taula d'origen, i els
		//resultats s'acumulen per dimensio (entitat-procediment-organ-usuari) dins d'un mapa.
		Map<ExplotFetsAmbDimensioDto.DimensioKey, ExplotFetsAmbDimensioDto> acumulador = new LinkedHashMap<ExplotFetsAmbDimensioDto.DimensioKey, ExplotFetsAmbDimensioDto>();

		//PROCEDIMENTS I SERVEIS
		for (ExplotFetsProcedimentsDto fet: explotacioFetsRepository.getProcedimentsPerDimensio(dataFi)) {
			ExplotFetsAmbDimensioDto dim = obtenirDimensio(acumulador, fet.getEntitatId(), fet.getProcedimentId(), fet.getOrganId(), fet.getUsuariCodi());
			dim.setProcedimentActiusTotal(fet.getProcedimentsActiusTotal());
			dim.setServeisActiusTotal(fet.getServeisActiusTotal());
		}

		//EXPEDIENTS
		for (ExplotFetsExpedientsObertsDto fet: explotacioFetsRepository.getExpedientsObertsPerDimensio(dataIni, dataFi)) {
			ExplotFetsAmbDimensioDto dim = obtenirDimensio(acumulador, fet.getEntitatId(), fet.getProcedimentId(), fet.getOrganId(), fet.getUsuariCodi());
			dim.setExpedientsObertsTotal(fet.getExpedientsObertsTotal());
			dim.setExpedientsOberts(fet.getExpedientsOberts());
		}
		for (ExplotFetsExpedientsTancatsDto fet: explotacioFetsRepository.getExpedientsTancatsPerDimensio(dateIni, dateFi)) {
			ExplotFetsAmbDimensioDto dim = obtenirDimensio(acumulador, fet.getEntitatId(), fet.getProcedimentId(), fet.getOrganId(), fet.getUsuariCodi());
			dim.setExpedientsTancatsTotal(fet.getExpedientsTancatsTotal());
			dim.setExpedientsTancats(fet.getExpedientsTancats());
		}

		//TASQUES (les dades totals o acumulades es poden aconseguir directament; per les diaries, s'ha de restar el total del dia anterior)
		for (ExplotFetsTasquesDto fet: explotacioFetsRepository.getTasquesPerDimensio(dataFi, dataAhirFi, dateFi, ahirFi)) {
			ExplotFetsAmbDimensioDto dim = obtenirDimensio(acumulador, fet.getEntitatId(), fet.getProcedimentId(), fet.getOrganId(), fet.getUsuariCodi());
			dim.setTasquesPendentsTotal(fet.getPendentsTotal());
			dim.setTasquesPendents(parcial(fet.getPendentsTotal(), fet.getPendentsTotalAhir()));
			dim.setTasquesIniciadesTotal(fet.getIniciadesTotal());
			dim.setTasquesIniciades(parcial(fet.getIniciadesTotal(), fet.getIniciadesTotalAhir()));
			dim.setTasquesFinalitzadesTotalDinsTermini(fet.getFinalitzadesDinsTerminiTotal());
			dim.setTasquesFinalitzadesDinsTermini(parcial(fet.getFinalitzadesDinsTerminiTotal(), fet.getFinalitzadesDinsTerminiTotalAhir()));
			dim.setTasquesFinalitzadesTotalForaTermini(fet.getFinalitzadesForaTerminiTotal());
			dim.setTasquesFinalitzadesForaTermini(parcial(fet.getFinalitzadesForaTerminiTotal(), fet.getFinalitzadesForaTerminiTotalAhir()));
			dim.setTasquesCanceladesTotal(fet.getCanceladesTotal());
			dim.setTasquesCancelades(parcial(fet.getCanceladesTotal(), fet.getCanceladesTotalAhir()));
			dim.setTasquesRebutjadesTotal(fet.getRebutjadesTotal());
			dim.setTasquesRebutjades(parcial(fet.getRebutjadesTotal(), fet.getRebutjadesTotalAhir()));
			dim.setTasquesAgafadesTotal(fet.getAgafadesTotal());
			dim.setTasquesAgafades(parcial(fet.getAgafadesTotal(), fet.getAgafadesTotalAhir()));
			dim.setTasquesCreadesTotal(fet.getCreadesTotal());
			dim.setTasquesCreades(parcial(fet.getCreadesTotal(), fet.getCreadesTotalAhir()));
			dim.setTasquesNoFinalitzadesForaTerminiTotal(fet.getNoFinalitzadesForaTerminiTotal());
			dim.setTasquesNoFinalitzadesForaTermini(parcial(fet.getNoFinalitzadesForaTerminiTotal(), fet.getNoFinalitzadesForaTerminiTotalAhir()));
		}

		//ANOTACIONS
		for (ExplotFetsAnotacionsDto fet: explotacioFetsRepository.getAnotacionsPerDimensio(dateIni, dateFi, ahirFi)) {
			ExplotFetsAmbDimensioDto dim = obtenirDimensio(acumulador, fet.getEntitatId(), fet.getProcedimentId(), fet.getOrganId(), fet.getUsuariCodi());
			dim.setAnotacionsNovesTotal(fet.getNovesTotal());
			dim.setAnotacionsNoves(fet.getNoves());
			dim.setAnotacionsProcessadesTotal(fet.getProcessadesTotal());
			dim.setAnotacionsProcessades(parcial(fet.getProcessadesTotal(), fet.getProcessadesTotalAhir()));
			dim.setAnotacionsRebutjadesTotal(fet.getRebutjadesTotal());
			dim.setAnotacionsRebutjades(parcial(fet.getRebutjadesTotal(), fet.getRebutjadesTotalAhir()));
		}

		//PINBAL
		for (ExplotFetsPinbalDto fet: explotacioFetsRepository.getPinbalEnviamentsPerDimensio(dataIni, dataFi)) {
			ExplotFetsAmbDimensioDto dim = obtenirDimensio(acumulador, fet.getEntitatId(), fet.getProcedimentId(), fet.getOrganId(), fet.getUsuariCodi());
			dim.setPinbalEnviamentsTotalOk(fet.getOkTotal());
			dim.setPinbalEnviamentsOk(fet.getOk());
			dim.setPinbalEnviamentsTotalError(fet.getErrorTotal());
			dim.setPinbalEnviamentsError(fet.getError());
		}

		//NOTIFICACIONS
		for (ExplotFetsNotificacionsDto fet: explotacioFetsRepository.getNotificacionsPerDimensio(dataFi, dataAhirFi)) {
			ExplotFetsAmbDimensioDto dim = obtenirDimensio(acumulador, fet.getEntitatId(), fet.getProcedimentId(), fet.getOrganId(), fet.getUsuariCodi());
			dim.setNotificacionsEnviadesTotal(fet.getEnviadesTotal());
			dim.setNotificacionsEnviades(parcial(fet.getEnviadesTotal(), fet.getEnviadesTotalAhir()));
			dim.setNotificacionsPendentsTotal(fet.getPendentsTotal());
			dim.setNotificacionsPendents(parcial(fet.getPendentsTotal(), fet.getPendentsTotalAhir()));
			dim.setNotificacionsRegistradesTotal(fet.getRegistradesTotal());
			dim.setNotificacionsRegistrades(parcial(fet.getRegistradesTotal(), fet.getRegistradesTotalAhir()));
			dim.setNotificacionsFinalitzadesTotal(fet.getFinalitzadesTotal());
			dim.setNotificacionsFinalitzades(parcial(fet.getFinalitzadesTotal(), fet.getFinalitzadesTotalAhir()));
			dim.setNotificacionsProcessadesTotal(fet.getProcessadesTotal());
			dim.setNotificacionsProcessades(parcial(fet.getProcessadesTotal(), fet.getProcessadesTotalAhir()));
			dim.setNotificacionsEnvErrorTotal(fet.getEnviadesErrorTotal());
			dim.setNotificacionsEnvError(parcial(fet.getEnviadesErrorTotal(), fet.getEnviadesErrorTotalAhir()));
			dim.setNotificacionsFinErrorTotal(fet.getFinalitzadesErrorTotal());
			dim.setNotificacionsFinError(parcial(fet.getFinalitzadesErrorTotal(), fet.getFinalitzadesErrorTotalAhir()));
		}

		//PORTAFIRMES
		for (ExplotFetsPortafirmesDto fet: explotacioFetsRepository.getPortafirmesPerDimensio(dataFi, dataAhirFi)) {
			ExplotFetsAmbDimensioDto dim = obtenirDimensio(acumulador, fet.getEntitatId(), fet.getProcedimentId(), fet.getOrganId(), fet.getUsuariCodi());
			dim.setFirmesEnviadesTotal(fet.getEnviadesTotal());
			dim.setFirmesEnviades(parcial(fet.getEnviadesTotal(), fet.getEnviadesTotalAhir()));
			dim.setFirmesIniciadesTotal(fet.getIniciadesTotal());
			dim.setFirmesIniciades(parcial(fet.getIniciadesTotal(), fet.getIniciadesTotalAhir()));
			dim.setFirmesPausadesTotal(fet.getPausadesTotal());
			dim.setFirmesPausades(parcial(fet.getPausadesTotal(), fet.getPausadesTotalAhir()));
			dim.setFirmesFirmadesTotal(fet.getFirmadesTotal());
			dim.setFirmesFirmades(parcial(fet.getFirmadesTotal(), fet.getFirmadesTotalAhir()));
			dim.setFirmesRebutjadesTotal(fet.getRebutjadesTotal());
			dim.setFirmesRebutjades(parcial(fet.getRebutjadesTotal(), fet.getRebutjadesTotalAhir()));
			dim.setFirmesParcialsTotal(fet.getParcialsTotal());
			dim.setFirmesParcials(parcial(fet.getParcialsTotal(), fet.getParcialsTotalAhir()));
		}

		List<ExplotFetsAmbDimensioDto> dimensions = new ArrayList<ExplotFetsAmbDimensioDto>(acumulador.values());
		logger.debug("Generacio d'estadistiques diaries: " + dimensions.size() + " dimensions");
		
		for (ExplotFetsAmbDimensioDto dim: dimensions) {
			//Recuperam la dimensió si ja existís a BBDD
			ExplotacioDimensioEntity dimensioEntity = explotacioDimensioRepository.findByEntitatIdAndProcedimentIdAndOrganGestorIdAndUsuariCodi(
					dim.getEntitatId(),
					dim.getProcedimentId(),
					dim.getOrganId(),
					dim.getUsuariCodi());
			
			if (dimensioEntity==null) {
				dimensioEntity = new ExplotacioDimensioEntity();
				EntitatEntity entitatEntity = null;
				if (dim.getEntitatId()!=null) {
					entitatEntity = entitatRepository.findById(dim.getEntitatId()).orElse(null);
				}
				MetaExpedientEntity metaExpedientEntity = null;
				if (dim.getProcedimentId()!=null) {
					metaExpedientEntity = metaExpedientRepository.findById(dim.getProcedimentId()).orElse(null);
				}
				OrganGestorEntity organGestorEntity = null;
				if (dim.getOrganId()!=null) {
					organGestorEntity = organGestorRepository.findById(dim.getOrganId()).orElse(null);
				}
				UsuariEntity usuariEntity = null;
				if (dim.getUsuariCodi()!=null) {
					usuariEntity = usuariRepository.findById(dim.getUsuariCodi()).orElse(null);
				}
				if (entitatEntity!=null && metaExpedientEntity!=null) {
					dimensioEntity.inicializaDimensio(entitatEntity, metaExpedientEntity, organGestorEntity, usuariEntity);
					dimensioEntity = explotacioDimensioRepository.save(dimensioEntity);
				}
			}
			
			LocalDate dataEstadistiques = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			ExplotacioTempsEntity ete = explotacioTempsRepository.findFirstByData(dataEstadistiques);
			if (ete==null) {
				ete = new ExplotacioTempsEntity(dataEstadistiques);
			}
			
			ete = explotacioTempsRepository.save(ete);
			
			ExplotacioFetsEntity explotacioFetsEntity = explotacioFetsRepository.findByDimensioAndTemps(dimensioEntity, ete);
			if (explotacioFetsEntity==null) {
				explotacioFetsEntity = new ExplotacioFetsEntity();
			}
			
			explotacioFetsEntity.updateFromDto(dim);
			explotacioFetsEntity.setTemps(ete);
			explotacioFetsEntity.setDimensio(dimensioEntity);
			
			explotacioFetsRepository.save(explotacioFetsEntity);
		}
		
		return dimensions;
	}
	
	//Recupera del mapa la dimensio (entitat-procediment-organ-usuari) indicada, creant-la si encara
	//no hi era. Substitueix la cerca lineal que abans feia agruparDimensions per cada fet rebut.
	private ExplotFetsAmbDimensioDto obtenirDimensio(
			Map<ExplotFetsAmbDimensioDto.DimensioKey, ExplotFetsAmbDimensioDto> acumulador,
			Long entitatId,
			Long procedimentId,
			Long organId,
			String usuariCodi) {
		ExplotFetsAmbDimensioDto.DimensioKey key = new ExplotFetsAmbDimensioDto.DimensioKey(entitatId, procedimentId, organId, usuariCodi);
		ExplotFetsAmbDimensioDto dimensio = acumulador.get(key);
		if (dimensio == null) {
			dimensio = new ExplotFetsAmbDimensioDto(entitatId, procedimentId, organId, usuariCodi);
			acumulador.put(key, dimensio);
		}
		return dimensio;
	}

	//Dada parcial del dia: total d'avui menys el total del dia anterior. El conjunt del dia anterior
	//sempre es un subconjunt del d'avui (nomes canvia el tall de la data), i per tant la resta mai
	//es negativa. Equival al que abans feia restarDadaMateixaDimensio amb dues consultes.
	private Long parcial(Long total, Long totalAhir) {
		if (total == null) {
			return null;
		}
		return totalAhir == null ? total : Long.valueOf(total.longValue() - totalAhir.longValue());
	}
	
	@Override
	public void restartSchedulledTasks(String taskCodi) {
		schedulingConfig.restartSchedulledTasks(taskCodi);
	}

	private int getArxiuMaxReintentsExpedients() {
		String arxiuMaxReintentsExpedients = configHelper.getConfig(PropertyConfig.MAX_REINTENTS_EXPEDIENT);
		return arxiuMaxReintentsExpedients != null && !arxiuMaxReintentsExpedients.isEmpty() ? Integer.valueOf(arxiuMaxReintentsExpedients) : 0;
	}
	
	private int getArxiuMaxReintentsDocuments() {
		String arxiuMaxReintentsDocuments = configHelper.getConfig(PropertyConfig.MAX_REINTENTS_DOCUMENTS);
		return arxiuMaxReintentsDocuments != null && !arxiuMaxReintentsDocuments.isEmpty() ? Integer.valueOf(arxiuMaxReintentsDocuments) : 0;
	}
	private int getArxiuMaxReintentsInteressats() {
		String arxiuMaxReintentsInteressats = configHelper.getConfig(PropertyConfig.MAX_REINTENTS_INTERESSATS);
		return arxiuMaxReintentsInteressats != null && !arxiuMaxReintentsInteressats.isEmpty() ? Integer.valueOf(arxiuMaxReintentsInteressats) : 0;
	}
	
	private int getMaxReintentsCanviEstatRebudaDistribucio() {
		String maxReintentsCanviEstatRebudaDistribucio = configHelper.getConfig(PropertyConfig.MAX_REINTENTS_CANVI_ESTST_DISTRIBUCIO);
		return maxReintentsCanviEstatRebudaDistribucio != null && !maxReintentsCanviEstatRebudaDistribucio.isEmpty() ? Integer.valueOf(maxReintentsCanviEstatRebudaDistribucio) : 0;
	}

	private static final Logger logger = LoggerFactory.getLogger(SegonPlaServiceImpl.class);
}