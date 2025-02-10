package es.caib.ripea.core.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
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
    //Mantenir un registre de les tasques que s'han enregistrat
    private final Map<String, Runnable> tasks = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

    private final String codiTancarExpedientsEnArxiu = "tancarExpedientsEnArxiu";
    private final String codiEnviarDocumentsAlPortafirmes = "enviarDocumentsAlPortafirmes";
    private final String codiConsultarIGuardarAnotacionsPendents = "consultarIGuardarAnotacionsPendents";
    private final String codiCanviarEstatEnDistribucio = "canviarEstatEnDistribucio";
    private final String codiEnviarEmailsInformantDeNouComentariPerProcediment = "enviarEmailsInformantDeNouComentariPerProcediment";
    private final String codiEnviarEmailsAgrupats = "enviarEmailsAgrupats";
    private final String codiGuardarEnArxiuContingutsPendents = "guardarEnArxiuContingutsPendents";
    private final String codiGuardarEnArxiuInteressats = "guardarEnArxiuInteressats";
    private final String codiActualitzacioDeProcediments = "actualitzacioDeProcediments";
    private final String codiConsultaDeCanvisAlOrganigrama = "consultaDeCanvisAlOrganigrama";
    private final String codiBuidarCachesDominis = "buidarCachesDominis";

    public void restartSchedulledTasks(String taskCodi) {

        if (taskRegistrar != null) {
            //taskRegistrar.destroy();
            //taskRegistrar.afterPropertiesSet();
            if (codiTancarExpedientsEnArxiu.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("tancarExpedientsEnArxiu", getTrigger(codiTancarExpedientsEnArxiu));
            }
            if (codiEnviarDocumentsAlPortafirmes.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("enviarDocumentsAlPortafirmes", getTrigger(codiEnviarDocumentsAlPortafirmes));
            }
            if (codiConsultarIGuardarAnotacionsPendents.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("consultarIGuardarAnotacionsPendents", getTrigger(codiConsultarIGuardarAnotacionsPendents));
            }
            if (codiCanviarEstatEnDistribucio.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("canviarEstatEnDistribucio", getTrigger(codiCanviarEstatEnDistribucio));
            }
            if (codiEnviarEmailsInformantDeNouComentariPerProcediment.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("enviarEmailsInformantDeNouComentariPerProcediment", getTrigger(codiEnviarEmailsInformantDeNouComentariPerProcediment));
            }
            if (codiEnviarEmailsAgrupats.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("enviarEmailsAgrupats", getTrigger(codiEnviarEmailsAgrupats));
            }
            if (codiGuardarEnArxiuContingutsPendents.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("guardarEnArxiuContingutsPendents", getTrigger(codiGuardarEnArxiuContingutsPendents));
            }
            if (codiGuardarEnArxiuInteressats.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("guardarEnArxiuInteressats", getTrigger(codiGuardarEnArxiuInteressats));
            }
            if (codiActualitzacioDeProcediments.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("actualitzacioDeProcediments", getTrigger(codiActualitzacioDeProcediments));
            }
            if (codiConsultaDeCanvisAlOrganigrama.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("consultaDeCanvisAlOrganigrama", getTrigger(codiConsultaDeCanvisAlOrganigrama));
            }
            if (codiBuidarCachesDominis.equals(taskCodi) || "totes".equals(taskCodi)) {
                rescheduleTask("buidarCachesDominis", getTrigger(codiBuidarCachesDominis));
            }
        }
    }

    public void addTask(String taskId, Runnable task, Trigger trigger) {
        monitorTasquesService.addTasca(taskId);
        tasks.put(taskId, task);
        ScheduledFuture<?> scheduledTask = taskRegistrar.getScheduler().schedule(task, trigger);
        scheduledTasks.put(taskId, scheduledTask);
    }

    public void rescheduleTask(String taskId, Trigger newTrigger) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(taskId);
        if (scheduledTask != null) {
            // Cancelar la tarea existente
            scheduledTask.cancel(true);
            // Añadir la tarea con el nuevo trigger
            Runnable task = tasks.get(taskId);
            if (task != null) {
                ScheduledFuture<?> newScheduledTask = taskRegistrar.getScheduler().schedule(task, newTrigger);
                scheduledTasks.put(taskId, newScheduledTask);
            }
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

    	taskRegistrar.setScheduler(taskScheduler);
    	this.taskRegistrar = taskRegistrar;

        addTask(
                codiEnviarDocumentsAlPortafirmes,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiEnviarDocumentsAlPortafirmes);
                        try {
                            execucioMassivaService.executeNextMassiveScheduledTask();
                            monitorTasquesService.fi(codiEnviarDocumentsAlPortafirmes);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiEnviarDocumentsAlPortafirmes);
                        }
                    }
                },
                getTrigger(codiEnviarDocumentsAlPortafirmes)
        );

        addTask(
                codiConsultarIGuardarAnotacionsPendents,
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
                getTrigger(codiConsultarIGuardarAnotacionsPendents)
        );

        addTask(
                codiCanviarEstatEnDistribucio,
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
                getTrigger(codiCanviarEstatEnDistribucio)
        );

        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        segonPlaService.buidarCacheDominis();
                    }
                },
                getTrigger(codiBuidarCachesDominis)
        );

        addTask(
                codiEnviarEmailsInformantDeNouComentariPerProcediment,
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
                getTrigger(codiEnviarEmailsInformantDeNouComentariPerProcediment)
        );

        addTask(
                codiEnviarEmailsAgrupats,
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
                getTrigger(codiEnviarEmailsAgrupats)
        );

        addTask(
                codiGuardarEnArxiuContingutsPendents,
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
                getTrigger(codiGuardarEnArxiuContingutsPendents)
        );

        addTask(
                codiGuardarEnArxiuInteressats,
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
                getTrigger(codiGuardarEnArxiuInteressats)
        );

        addTask(
                codiActualitzacioDeProcediments,
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
                getTrigger(codiActualitzacioDeProcediments)
        );

        addTask(
                codiConsultaDeCanvisAlOrganigrama,
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
                getTrigger(codiConsultaDeCanvisAlOrganigrama)
        );
        
        addTask(
                codiTancarExpedientsEnArxiu,
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
                getTrigger(codiTancarExpedientsEnArxiu)
        );
    } //Fi de configureTasks

    private Trigger getTrigger(String taskCodi) {
        if (taskCodi.equals(codiTancarExpedientsEnArxiu)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiConsultaDeCanvisAlOrganigrama)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiActualitzacioDeProcediments)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiGuardarEnArxiuInteressats)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiGuardarEnArxiuContingutsPendents)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiEnviarEmailsAgrupats)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiEnviarEmailsInformantDeNouComentariPerProcediment)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiBuidarCachesDominis)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiCanviarEstatEnDistribucio)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiConsultarIGuardarAnotacionsPendents)) {
            return new Trigger() {
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
            };
        } else if (taskCodi.equals(codiEnviarDocumentsAlPortafirmes)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    PeriodicTrigger trigger = null;
                    try {
                        trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.EXECUTAR_EXECUCIONS_MASSIVES_RATE), TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("Error getting next execution date for comprovarExecucionsMassives()", e);
                    }
                    //El intervalo se mide desde el tiempo de finalización de cada ejecución.
                    //Esto significa que el siguiente ciclo de ejecución no comenzará hasta que la tarea actual haya terminado.
                    //No volem que s'agafin noves execucions massives fins que les actuals hagin acabat, ja que sino es processen repetides.
                    trigger.setFixedRate(false);
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
            };
        }
        return null;
    }

    /** Enregistre l'error als logs i marca la tasca amb error. */
	private void tractarErrorTascaSegonPla(Throwable th, String codiTasca) {
		String errMsg = th.getClass() + ": " + th.getMessage() + " (" + new Date().getTime() + ")";
		logger.error("Error no controlat a l'execució de la tasca en segon pla amb codi \"" + codiTasca + "\": " + errMsg, th);
		monitorTasquesService.error(codiTasca, errMsg);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);
}