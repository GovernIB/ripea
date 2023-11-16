package es.caib.ripea.core.config;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.api.service.MonitorTasquesService;
import es.caib.ripea.core.api.service.SegonPlaService;
import es.caib.ripea.core.helper.ConfigHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private ExecucioMassivaService execucioMassivaService;
    @Autowired
    private SegonPlaService segonPlaService;
    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
	private ConfigHelper configHelper;
    @Autowired
    private MonitorTasquesService monitorTasquesService;

    private Boolean[] primeraVez = {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};

    private static final long DEFAULT_INITIAL_DELAY_MS = 30000L;
    private ScheduledTaskRegistrar taskRegistrar;
    
    public void restartSchedulledTasks() {
        if (taskRegistrar != null) {
            taskRegistrar.destroy();
            taskRegistrar.afterPropertiesSet();
        }

    }
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	taskRegistrar.setScheduler(taskScheduler);
    	this.taskRegistrar = taskRegistrar;

        // Enviament d'execucions massives
        ////////////////////////////////////////////////////////////////
		final String codiEnviarDocumentsAlPortafirmes = "enviarDocumentsAlPortafirmes";
		monitorTasquesService.addTasca(codiEnviarDocumentsAlPortafirmes);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
						monitorTasquesService.inici(codiEnviarDocumentsAlPortafirmes);
						try {
							execucioMassivaService.comprovarExecucionsMassives();
							monitorTasquesService.fi(codiEnviarDocumentsAlPortafirmes);
						} catch (Throwable th) {
							tractarErrorTascaSegonPla(th, codiEnviarDocumentsAlPortafirmes);
						}                  
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.EXECUTAR_EXECUCIONS_MASSIVES_RATE), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							log.error("Error getting next execution date for comprovarExecucionsMassives()", e);
						}
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        long registrarEnviamentsPendentsInitialDelayLong = 0L;
                        if (primeraVez[0]) {
                        	registrarEnviamentsPendentsInitialDelayLong = DEFAULT_INITIAL_DELAY_MS;
                        	primeraVez[0] = false;
                        }
                        trigger.setInitialDelay(registrarEnviamentsPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiEnviarDocumentsAlPortafirmes, longNextExecution);     
                        return nextExecution;
                    }
                }
        );

        // Consultar i guardar anotacions peticions pendents
        ///////////////////////////////////////////////////////
		final String codiConsultarIGuardarAnotacionsPendents = "consultarIGuardarAnotacionsPendents";
		monitorTasquesService.addTasca(codiConsultarIGuardarAnotacionsPendents);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
						monitorTasquesService.inici(codiConsultarIGuardarAnotacionsPendents);
						try {
							segonPlaService.consultarIGuardarAnotacionsPeticionsPendents();
							monitorTasquesService.fi(codiConsultarIGuardarAnotacionsPendents);
						} catch (Throwable th) {
							tractarErrorTascaSegonPla(th, codiConsultarIGuardarAnotacionsPendents);
						}                     	
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                         PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.PROCESSAR_ANOTACIONS_PETICIONS_PENDENTS_RATE), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
                            log.error("Error getting next execution date for consultarIGuardarAnotacionsPeticionsPendents()", e);
						}
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        long delay = 0L;
                        if (primeraVez[1]) {
                            delay = DEFAULT_INITIAL_DELAY_MS;
                        	primeraVez[1] = false;
                        }
                        trigger.setInitialDelay(delay);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiConsultarIGuardarAnotacionsPendents, longNextExecution);     
                        return nextExecution;
                    }
                }
        );

        // Reintentar canvi estat BACK_REBUDA a DISTRIBUCIO
        //////////////////////////////////////////////////////////////////
		final String codiCanviarEstatEnDistribucio = "canviarEstatEnDistribucio";
		monitorTasquesService.addTasca(codiCanviarEstatEnDistribucio);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@SneakyThrows
					@Override
					public void run() {
						monitorTasquesService.inici(codiCanviarEstatEnDistribucio);
						try {
							segonPlaService.reintentarCanviEstatDistribucio();
							monitorTasquesService.fi(codiCanviarEstatEnDistribucio);
						} catch (Throwable th) {
							tractarErrorTascaSegonPla(th, codiCanviarEstatEnDistribucio);
						}  						
					}
		},
				new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = null;
                        try {
                            trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.REINTENTAR_CANVI_ESTAT_DISTRIBUCIO), TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                            log.error("Error getting next execution date for buidarCacheDominis()", e);
                        }
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        long delay = 0L;
                        if (primeraVez[2]) {
                            delay = DEFAULT_INITIAL_DELAY_MS;
                            primeraVez[2] = false;
                        }
                        trigger.setInitialDelay(delay);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiCanviarEstatEnDistribucio, longNextExecution);     
                        return nextExecution;
                    }
                });


        // Buidar caches dominis
        //////////////////////////////////////////////////////////////////
//		final String codiBuidarCachesDominis = "buidarCachesDominis";
//		monitorTasquesService.addTasca(codiBuidarCachesDominis);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
//						monitorTasquesService.inici(codiBuidarCachesDominis);
//						try {
	                        segonPlaService.buidarCacheDominis();
//							monitorTasquesService.fi(codiBuidarCachesDominis);
//						} catch (Throwable th) {
//							tractarErrorTascaSegonPla(th, codiBuidarCachesDominis);
//						}                         
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                         PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.BUIDAR_CACHES_DOMINIS_RATE), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
                            log.error("Error getting next execution date for buidarCacheDominis()", e);
						}
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        long enviamentRefrescarEstatPendentsInitialDelayLong = 0L;
                        if (primeraVez[2]) {
                        	enviamentRefrescarEstatPendentsInitialDelayLong = DEFAULT_INITIAL_DELAY_MS;
                        	primeraVez[2] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarEstatPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
//                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
//        				monitorTasquesService.updateProperaExecucio(codiBuidarCachesDominis, longNextExecution);     
                        return nextExecution;
                    }
                }
        );

        
        
        
        // Enviament de correus electrònics per comentaris als responsables dels procediments (is in background because it takes long time to calculate destinataris)
        /////////////////////////////////////////////////////////////////////////
		final String codiEnviarEmailsInformantDeNouComentariPerProcediment = "enviarEmailsInformantDeNouComentariPerProcediment";
		monitorTasquesService.addTasca(codiEnviarEmailsInformantDeNouComentariPerProcediment);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
						monitorTasquesService.inici(codiEnviarEmailsInformantDeNouComentariPerProcediment);
						try {
	                        segonPlaService.enviarEmailPerComentariMetaExpedient();
							monitorTasquesService.fi(codiEnviarEmailsInformantDeNouComentariPerProcediment);
						} catch (Throwable th) {
							tractarErrorTascaSegonPla(th, codiEnviarEmailsInformantDeNouComentariPerProcediment);
						}                         
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = null;
						try {
							trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ENVIAR_EMAILS_PENDENTS_PROCEDIMENT_COMENTARI_CRON));
						} catch (Exception e) {
                            log.error("Error getting next execution date for enviarEmailPerComentariMetaExpedient()", e);
						}
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiEnviarEmailsInformantDeNouComentariPerProcediment, longNextExecution);     
                        return nextExecution;
                    }
                }
        );
        

        // Enviament de correus electrònics pendents agrupats
        /////////////////////////////////////////////////////////////////////////
        final String codiEnviarEmailsAgrupats = "enviarEmailsAgrupats";
		monitorTasquesService.addTasca(codiEnviarEmailsAgrupats);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(codiEnviarEmailsAgrupats);
                        try{ 
                        	segonPlaService.enviarEmailsPendentsAgrupats();
                        	monitorTasquesService.fi(codiEnviarEmailsAgrupats);
                        } catch(Throwable th) {                        	
                        	tractarErrorTascaSegonPla(th, codiEnviarEmailsAgrupats);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = null;
						try {
							trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ENVIAR_EMAILS_PENDENTS_AGRUPATS_CRON));
						} catch (Exception e) {
                            log.error("Error getting next execution date for enviarEmailsPendentsAgrupats()", e);
						}
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiEnviarEmailsAgrupats, longNextExecution);                        
                        return nextExecution;
                    }
                }
        );
        
        
        // Guardar en arxiu continguts pendents
        /////////////////////////////////////////////////////////////////////////
        final String codiGuardarEnArxiuContingutsPendents = "guardarEnArxiuContingutsPendents";
		monitorTasquesService.addTasca(codiGuardarEnArxiuContingutsPendents);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
						monitorTasquesService.inici(codiGuardarEnArxiuContingutsPendents);
						try {
	                        segonPlaService.guardarExpedientsDocumentsArxiu();
							monitorTasquesService.fi(codiGuardarEnArxiuContingutsPendents);
						} catch (Throwable th) {
							tractarErrorTascaSegonPla(th, codiGuardarEnArxiuContingutsPendents);
						}                         
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                         PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.GUARDAR_ARXIU_CONTINGUTS_PENDENTS), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
                            log.error("Error getting next execution date for guardarExpedientsDocumentsArxiu()", e);
						}
                        // Només la primera vegada que s'executa
                        long enviamentRefrescarEstatPendentsInitialDelayLong = 0L;
                        if (primeraVez[3]) {
                        	enviamentRefrescarEstatPendentsInitialDelayLong = DEFAULT_INITIAL_DELAY_MS;
                        	primeraVez[3] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarEstatPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiGuardarEnArxiuContingutsPendents, longNextExecution);     
                        return nextExecution;
                    }
                }
        );
        
        // Guardar en arxiu interessats
        /////////////////////////////////////////////////////////////////////////
        final String codiGuardarEnArxiuInteressats = "guardarEnArxiuInteressats";
		monitorTasquesService.addTasca(codiGuardarEnArxiuInteressats);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
						monitorTasquesService.inici(codiGuardarEnArxiuInteressats);
						try {
	                        segonPlaService.guardarInteressatsArxiu();
							monitorTasquesService.fi(codiGuardarEnArxiuInteressats);
						} catch (Throwable th) {
							tractarErrorTascaSegonPla(th, codiGuardarEnArxiuInteressats);
						}                         
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                         PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.GUARDAR_ARXIU_INTERESSATS), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
                            log.error("Error getting next execution date for guardarInteressatsArxiu()", e);
						}
                        // Només la primera vegada que s'executa
                        long enviamentRefrescarEstatPendentsInitialDelayLong = 0L;
                        if (primeraVez[4]) {
                        	enviamentRefrescarEstatPendentsInitialDelayLong = DEFAULT_INITIAL_DELAY_MS;
                        	primeraVez[4] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarEstatPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiGuardarEnArxiuInteressats, longNextExecution);     
                        return nextExecution;
                    }
                }                
        );

        // 7. Actualització automàtica de procediments (MetaExpedients)
        /////////////////////////////////////////////////////////////////////////
        final String codiActualitzacioDeProcediments = "actualitzacioDeProcediments";
        monitorTasquesService.addTasca(codiActualitzacioDeProcediments);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
						monitorTasquesService.inici(codiActualitzacioDeProcediments);
						try {
	                        segonPlaService.actualitzarProcediments();
							monitorTasquesService.fi(codiActualitzacioDeProcediments);
						} catch (Throwable th) {
							tractarErrorTascaSegonPla(th, codiActualitzacioDeProcediments);
						}                           
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        String cron = configHelper.getConfig(PropertiesConstants.ACTUALITZAR_PROCEDIMENTS);
                        if (cron == null)
                            cron = "0 15 * * * *";
                        CronTrigger trigger = new CronTrigger(cron);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiActualitzacioDeProcediments, longNextExecution);     
                        return nextExecution;
                    }
                }
        );

        // 8. Consulta de canvis en l'organigrama
        /////////////////////////////////////////////////////////////////////////
        final String codiConsultaDeCanvisAlOrganigrama = "consultaDeCanvisAlOrganigrama";
        monitorTasquesService.addTasca(codiConsultaDeCanvisAlOrganigrama);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
						monitorTasquesService.inici(codiConsultaDeCanvisAlOrganigrama);
						try {
	                        segonPlaService.consultaCanvisOrganigrama();
							monitorTasquesService.fi(codiConsultaDeCanvisAlOrganigrama);
						} catch (Throwable th) {
							tractarErrorTascaSegonPla(th, codiConsultaDeCanvisAlOrganigrama);
						}                         
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        String cron = configHelper.getConfig(PropertiesConstants.CONSULTA_CANVIS_ORGANIGRAMA);
                        if (cron == null)
                            cron = "0 45 2 * * *";
                        CronTrigger trigger = new CronTrigger(cron);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiConsultaDeCanvisAlOrganigrama, longNextExecution);     
                        return nextExecution;
                    }
                }
        );
        
        // Consulta expedients pendents de tancar a l'arxiu i que ha arribat l'hora programada
        /////////////////////////////////////////////////////////////////////////
        final String codiTancarExpedientsEnArxiu = "tancarExpedientsEnArxiu";
        monitorTasquesService.addTasca(codiTancarExpedientsEnArxiu);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
						monitorTasquesService.inici(codiTancarExpedientsEnArxiu);
						try {
	                        segonPlaService.tancarExpedientsArxiu();
							monitorTasquesService.fi(codiTancarExpedientsEnArxiu);
						} catch (Throwable th) {
							tractarErrorTascaSegonPla(th, codiTancarExpedientsEnArxiu);
						}                            
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = null;
						try {
	                        String cron = configHelper.getConfig(PropertiesConstants.TANCAMENT_LOGIC_CRON);
	                        if (cron == null)
	                            cron = "0 0 20 * * *";
							trigger = new CronTrigger(cron);
						} catch (Exception e) {
                            log.error("Error getting next execution date for tancarExpedientsArxiu()", e);
						}
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiTancarExpedientsEnArxiu, longNextExecution);     
                        return nextExecution;
                    }
                }
        );

    }
    
    /** Enregistre l'error als logs i marca la tasca amb error. */
	private void tractarErrorTascaSegonPla(Throwable th, String codiTasca) {
		String errMsg = th.getClass() + ": " + th.getMessage() + " (" + new Date().getTime() + ")";
		logger.error("Error no controlat a l'execució de la tasca en segon pla amb codi \"" + codiTasca + "\": " + errMsg, th);
		monitorTasquesService.error(codiTasca, errMsg);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);
}
