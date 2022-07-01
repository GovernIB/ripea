package es.caib.ripea.core.config;

import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.api.service.SegonPlaService;
import es.caib.ripea.core.helper.ConfigHelper;
import lombok.SneakyThrows;

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

import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    private Boolean[] primeraVez = {Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};

    private static final long DEFAULT_INITIAL_DELAY_MS = 30000L;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	taskRegistrar.setScheduler(taskScheduler);

        // 1. Enviament d'execucions massives
        ////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        execucioMassivaService.comprovarExecucionsMassives();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.EXECUTAR_EXECUCIONS_MASSIVES_RATE), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							logger.error("Error getting next execution date for comprovarExecucionsMassives()", e);
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
                        return nextExecution;
                    }
                }
        );

        // 2. Consultar i guardar anotacions peticions pendents
        ///////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        segonPlaService.consultarIGuardarAnotacionsPeticionsPendents();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                         PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.PROCESSAR_ANOTACIONS_PETICIONS_PENDENTS_RATE), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							logger.error("Error getting next execution date for consultarIGuardarAnotacionsPeticionsPendents()", e);
						}
                        trigger.setFixedRate(true);
                        // Només la primera vegada que s'executa
                        long notificaEnviamentsRegistratsInitialDelayLong = 0L;
                        if (primeraVez[1]) {
                        	notificaEnviamentsRegistratsInitialDelayLong = DEFAULT_INITIAL_DELAY_MS;
                        	primeraVez[1] = false;
                        }
                        trigger.setInitialDelay(notificaEnviamentsRegistratsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );

        // 3. Buidar caches dominis
        //////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        segonPlaService.buidarCacheDominis();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                         PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.BUIDAR_CACHES_DOMINIS_RATE), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							logger.error("Error getting next execution date for buidarCacheDominis()", e);
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
                        return nextExecution;
                    }
                }
        );


        // 4. Enviament de correus electrònics pendents agrupats
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        segonPlaService.enviarEmailsPendentsAgrupats();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        CronTrigger trigger = null;
						try {
							trigger = new CronTrigger(configHelper.getConfig(PropertiesConstants.ENVIAR_EMAILS_PENDENTS_AGRUPATS_CRON));
						} catch (Exception e) {
							logger.error("Error getting next execution date for enviarEmailsPendentsAgrupats()", e);
						}
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
        
        // 5. Guardar en arxiu continguts pendents
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        segonPlaService.guardarExpedientsDocumentsArxiu();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                         PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.GUARDAR_ARXIU_CONTINGUTS_PENDENTS), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							logger.error("Error getting next execution date for guardarExpedientsDocumentsArxiu()", e);
						}
                        // Només la primera vegada que s'executa
                        long enviamentRefrescarEstatPendentsInitialDelayLong = 0L;
                        if (primeraVez[3]) {
                        	enviamentRefrescarEstatPendentsInitialDelayLong = DEFAULT_INITIAL_DELAY_MS;
                        	primeraVez[3] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarEstatPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
        // 6. Guardar en arxiu interessats
        /////////////////////////////////////////////////////////////////////////
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        segonPlaService.guardarInteressatsArxiu();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                         PeriodicTrigger trigger = null;
						try {
							trigger = new PeriodicTrigger(configHelper.getAsLong(PropertiesConstants.GUARDAR_ARXIU_INTERESSATS), TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							logger.error("Error getting next execution date for guardarInteressatsArxiu()", e);
						}
                        // Només la primera vegada que s'executa
                        long enviamentRefrescarEstatPendentsInitialDelayLong = 0L;
                        if (primeraVez[4]) {
                        	enviamentRefrescarEstatPendentsInitialDelayLong = DEFAULT_INITIAL_DELAY_MS;
                        	primeraVez[4] = false;
                        }
                        trigger.setInitialDelay(enviamentRefrescarEstatPendentsInitialDelayLong);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }                
        );

    }
    
    
	private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);
    
}
