package es.caib.ripea.back.config;

import es.caib.ripea.service.intf.resourceservice.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import es.caib.ripea.service.intf.base.service.PermissionEvaluatorService;
import es.caib.ripea.service.intf.base.service.ResourceApiService;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.service.AlertaService;
import es.caib.ripea.service.intf.service.AplicacioService;
import es.caib.ripea.service.intf.service.AvisService;
import es.caib.ripea.service.intf.service.CarpetaService;
import es.caib.ripea.service.intf.service.ConfigService;
import es.caib.ripea.service.intf.service.ContingutService;
import es.caib.ripea.service.intf.service.DadesExternesService;
import es.caib.ripea.service.intf.service.DigitalitzacioService;
import es.caib.ripea.service.intf.service.DocumentEnviamentService;
import es.caib.ripea.service.intf.service.DocumentService;
import es.caib.ripea.service.intf.service.DominiService;
import es.caib.ripea.service.intf.service.EntitatService;
import es.caib.ripea.service.intf.service.EventService;
import es.caib.ripea.service.intf.service.ExecucioMassivaService;
import es.caib.ripea.service.intf.service.ExpedientEstatService;
import es.caib.ripea.service.intf.service.ExpedientInteressatService;
import es.caib.ripea.service.intf.service.ExpedientPeticioService;
import es.caib.ripea.service.intf.service.ExpedientSeguidorService;
import es.caib.ripea.service.intf.service.ExpedientService;
import es.caib.ripea.service.intf.service.ExpedientTascaService;
import es.caib.ripea.service.intf.service.FluxFirmaUsuariService;
import es.caib.ripea.service.intf.service.GrupService;
import es.caib.ripea.service.intf.service.HistoricService;
import es.caib.ripea.service.intf.service.ImportacioService;
import es.caib.ripea.service.intf.service.MetaDadaService;
import es.caib.ripea.service.intf.service.MetaDocumentService;
import es.caib.ripea.service.intf.service.MetaExpedientService;
import es.caib.ripea.service.intf.service.MonitorTasquesService;
import es.caib.ripea.service.intf.service.OrganGestorService;
import es.caib.ripea.service.intf.service.PinbalServeiService;
import es.caib.ripea.service.intf.service.PortafirmesFluxService;
import es.caib.ripea.service.intf.service.SegonPlaService;
import es.caib.ripea.service.intf.service.SeguimentService;
import es.caib.ripea.service.intf.service.TipusDocumentalService;
import es.caib.ripea.service.intf.service.URLInstruccioService;
import es.caib.ripea.service.intf.service.UnitatOrganitzativaService;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Configuration
@ConditionalOnWarDeployment
public class EjbClientConfig {

	static final String EJB_JNDI_PREFIX = "java:app/" + BaseConfig.APP_NAME + "-ejb/";
	static final String EJB_JNDI_SUFFIX = "Ejb";

	@Bean
	public LocalStatelessSessionProxyFactoryBean alertaService() {
		return getLocalEjbFactoyBean(AlertaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean avisService() {
		return getLocalEjbFactoyBean(AvisService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean carpetaService() {
		return getLocalEjbFactoyBean(CarpetaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean configService() {
		return getLocalEjbFactoyBean(ConfigService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean contingutService() {
		return getLocalEjbFactoyBean(ContingutService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean dadesExternesService() {
		return getLocalEjbFactoyBean(DadesExternesService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean digitalitzacioService() {
		return getLocalEjbFactoyBean(DigitalitzacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean documentEnviamentService() {
		return getLocalEjbFactoyBean(DocumentEnviamentService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean documentService() {
		return getLocalEjbFactoyBean(DocumentService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean dominiService() {
		return getLocalEjbFactoyBean(DominiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatService() {
		return getLocalEjbFactoyBean(EntitatService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean execucioMassivaService() {
		return getLocalEjbFactoyBean(ExecucioMassivaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientEstatService() {
		return getLocalEjbFactoyBean(ExpedientEstatService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientInteressatService() {
		return getLocalEjbFactoyBean(ExpedientInteressatService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientPeticioService() {
		return getLocalEjbFactoyBean(ExpedientPeticioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientSeguidorService() {
		return getLocalEjbFactoyBean(ExpedientSeguidorService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientService() {
		return getLocalEjbFactoyBean(ExpedientService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientTascaService() {
		return getLocalEjbFactoyBean(ExpedientTascaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean fluxFirmaUsuariService() {
		return getLocalEjbFactoyBean(FluxFirmaUsuariService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean grupService() {
		return getLocalEjbFactoyBean(GrupService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean historicService() {
		return getLocalEjbFactoyBean(HistoricService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean importacioService() {
		return getLocalEjbFactoyBean(ImportacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaDadaService() {
		return getLocalEjbFactoyBean(MetaDadaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaDocumentService() {
		return getLocalEjbFactoyBean(MetaDocumentService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaExpedientService() {
		return getLocalEjbFactoyBean(MetaExpedientService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean monitorTasquesService() {
		return getLocalEjbFactoyBean(MonitorTasquesService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean organGestorService() {
		return getLocalEjbFactoyBean(OrganGestorService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean pinbalServeiService() {
		return getLocalEjbFactoyBean(PinbalServeiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean portafirmesFluxService() {
		return getLocalEjbFactoyBean(PortafirmesFluxService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean segonPlaService() {
		return getLocalEjbFactoyBean(SegonPlaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean seguimentService() {
		return getLocalEjbFactoyBean(SeguimentService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean tipusDocumentalService() {
		return getLocalEjbFactoyBean(TipusDocumentalService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean unitatOrganitzativaService() {
		return getLocalEjbFactoyBean(UnitatOrganitzativaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean urlInstruccioService() {
		return getLocalEjbFactoyBean(URLInstruccioService.class);
	}

	//INICI BEANS RESOURCES

	@Bean
	public LocalStatelessSessionProxyFactoryBean permissionEvaluatorService() {
		return getLocalEjbFactoyBean(PermissionEvaluatorService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean documentResourceService() {
		return getLocalEjbFactoyBean(DocumentResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean documentPublicacioResourceService() {
		return getLocalEjbFactoyBean(DocumentPublicacioResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean documentEnviamentInteressatResourceService() {
		return getLocalEjbFactoyBean(DocumentEnviamentInteressatResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean documentNotificacioResourceService() {
		return getLocalEjbFactoyBean(DocumentNotificacioResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientComentariResourceService() {
		return getLocalEjbFactoyBean(ExpedientComentariResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientPeticioResourceService() {
		return getLocalEjbFactoyBean(ExpedientPeticioResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientResourceService() {
		return getLocalEjbFactoyBean(ExpedientResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientTascaResourceService() {
		return getLocalEjbFactoyBean(ExpedientTascaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean grupResourceService() {
		return getLocalEjbFactoyBean(GrupResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean interessatResourceService() {
		return getLocalEjbFactoyBean(InteressatResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaExpedientResourceService() {
		return getLocalEjbFactoyBean(MetaExpedientResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaExpedientComentariResourceService() {
		return getLocalEjbFactoyBean(MetaExpedientComentariResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaExpedientTascaResourceService() {
		return getLocalEjbFactoyBean(MetaExpedientTascaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean organGestorResourceService() {
		return getLocalEjbFactoyBean(OrganGestorResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean usuariResourceService() {
		return getLocalEjbFactoyBean(UsuariResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean resourceApiService() {
		return getLocalEjbFactoyBean(ResourceApiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaDocumentResourceService() {
		return getLocalEjbFactoyBean(MetaDocumentResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean expedientTascaComentariResourceService() {
		return getLocalEjbFactoyBean(ExpedientTascaComentariResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean dadaResourceService() {
		return getLocalEjbFactoyBean(DadaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaDadaResourceService() {
		return getLocalEjbFactoyBean(MetaDadaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean registreAnnexResourceService() {
		return getLocalEjbFactoyBean(RegistreAnnexResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean registreInteressatResourceService() {
		return getLocalEjbFactoyBean(RegistreInteressatResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatResourceServiceEjb() {
		return getLocalEjbFactoyBean(EntitatResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaExpedientEstatResourceServiceEjb() {
		return getLocalEjbFactoyBean(MetaExpedientEstatResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean carpetaResourceServiceEjb() {
		return getLocalEjbFactoyBean(CarpetaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean contingutMovimentResourceServiceEjb() {
		return getLocalEjbFactoyBean(ContingutMovimentResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean contingutLogResourceServiceEjb() {
		return getLocalEjbFactoyBean(ContingutLogResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean execucioMassivaResourceServiceEjb() {
		return getLocalEjbFactoyBean(ExecucioMassivaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean execucioMassivaContingutResourceServiceEjb() {
		return getLocalEjbFactoyBean(ExecucioMassivaContingutResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean documentPortafirmesResourceServiceEjb() {
		return getLocalEjbFactoyBean(DocumentPortafirmesResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean documentViafirmaResourceServiceEjb() {
		return getLocalEjbFactoyBean(DocumentViafirmaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean alertaResourceServiceEjb() {
		return getLocalEjbFactoyBean(AlertaResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean consultaPinbalResourceServiceEjb() {
		return getLocalEjbFactoyBean(ConsultaPinbalResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean pinbalServeiResourceServiceEjb() {
		return getLocalEjbFactoyBean(PinbalServeiResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean contingutResourceServiceEjb() {
		return getLocalEjbFactoyBean(ContingutResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean tipusDocumentalResourceServiceEjb() {
		return getLocalEjbFactoyBean(TipusDocumentalResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean aclSidResourceServiceEjb() {
		return getLocalEjbFactoyBean(AclSidResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean aclObjIdentityResourceServiceEjb() {
		return getLocalEjbFactoyBean(AclObjIdentityResourceService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean eventServiceEjb() {
		return getLocalEjbFactoyBean(EventService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean dominiServiceEjb() {
		return getLocalEjbFactoyBean(DominiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean interessatGrupResourceServiceEjb() {
		return getLocalEjbFactoyBean(InteressatGrupResourceService.class);
	}
	
	@Bean
	public LocalStatelessSessionProxyFactoryBean metaNodeResourceServiceEjb() {
		return getLocalEjbFactoyBean(MetaNodeResourceService.class);
	}
	
	@Bean
	public LocalStatelessSessionProxyFactoryBean dominiResourceServiceEjb() {
		return getLocalEjbFactoyBean(DominiResourceService.class);
	}
	
	@Bean
	public LocalStatelessSessionProxyFactoryBean metaDocumentFluxPortafibResourceServiceEjb() {
		return getLocalEjbFactoyBean(MetaDocumentFluxPortafibResourceService.class);
	}
	
	@Bean
	public LocalStatelessSessionProxyFactoryBean uRLInstruccioResourceServiceEjb() {
		return getLocalEjbFactoyBean(URLInstruccioResourceService.class);
	}
	
	@Bean
	public LocalStatelessSessionProxyFactoryBean metaExpedientTascaValidacioResourceServiceEjb() {
		return getLocalEjbFactoyBean(MetaExpedientTascaValidacioResourceService.class);
	}
	
	@Bean
	public LocalStatelessSessionProxyFactoryBean metaExpedientCarpetaResourceServiceEjb() {
		return getLocalEjbFactoyBean(MetaExpedientCarpetaResourceService.class);
	}
	
	/*
	@Bean
	public static BeanFactoryPostProcessor configurarEJBsDinamicament() {
		return beanFactory -> {
	        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
	        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class)); // Inclou totes les classes
	
	        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("es.caib.ripea.back.resourcecontroller");
	        for (BeanDefinition beanDefinition : beanDefinitions) {
	            String className = beanDefinition.getBeanClassName();
	            try {
	                Class<?> controllerClass = Class.forName(className);
	
	                // Verificar que sigui una classe que estenem de Controller (o similar)
	                //if (Controller.class.isAssignableFrom(controllerClass)) {
	
	                    Object controllerInstance = controllerClass.getConstructor().newInstance();
	                    
	                    Arrays.stream(controllerClass.getSuperclass().getSuperclass().getDeclaredFields())
	                            .filter(field -> ReadonlyResourceService.class.isAssignableFrom(field.getType()))
	                            .forEach(field -> {
	                                String serviceName = field.getType().getSimpleName();
	                                String beanName = Character.toLowerCase(serviceName.charAt(0)) + serviceName.substring(1);
	
	                                System.out.println("Dynamic EJB. Trying to create EJB for serviceName " + serviceName + " and BeanName " + beanName);
	
	                                if (!beanFactory.containsBean(beanName)) {
	                                    try {
	                                        field.setAccessible(true); // Important per accedir a camps privats
	                                        LocalStatelessSessionProxyFactoryBean factoryBean = getLocalEjbFactoyBean(field.getType());
	                                        beanFactory.registerSingleton(beanName, factoryBean.getObject());
	                                        field.set(controllerInstance, beanFactory.getBean(beanName));
	
	                                    } catch (Exception e) {
	                                        // Gestiona les excepcions adequadament
	                                        System.err.println("Error al crear l'EJB: " + e.getMessage());
	                                    }
	                                }
	                            });
	               // }
	            } catch (Exception e) {
	                // Gestiona les excepcions adequadament
	                System.err.println("Error al processar la classe: " + className + ": " + e.getMessage());
	            }
	        }
		};
    }
*/

	// TODO: Prova a veure si funciona:
	/*@Bean
	public static BeanFactoryPostProcessor dynamicEjbProxyPostProcessor(ConfigurableApplicationContext context) {
		return beanFactory -> {
			Map<String, Object> controllers = context.getBeansWithAnnotation(RestController.class);
			controllers.values().forEach(controller -> {
				Arrays.stream(controller.getClass().getDeclaredFields())
						.filter(field -> ReadonlyResourceService.class.isAssignableFrom(field.getType()))
						.forEach(field -> {
							String serviceName = field.getType().getSimpleName();
							String beanName = Character.toLowerCase(serviceName.charAt(0)) + serviceName.substring(1);

							log.info("Dynamic EJB. Trying to create EJB for serviceName " + serviceName + " and BeanName " + beanName);
							if (!beanFactory.containsBean(beanName)) {
								LocalStatelessSessionProxyFactoryBean factoryBean = getLocalEjbFactoyBean(field.getType());
								beanFactory.registerSingleton(beanName, factoryBean);
							}
						});
			});
		};
	}*/

//	private LocalStatelessSessionProxyFactoryBean getLocalEjbProxyFactory(Class<?> serviceType) {
//		LocalStatelessSessionProxyFactoryBean factoryBean = new LocalStatelessSessionProxyFactoryBean();
//		factoryBean.setBusinessInterface(serviceType);
//		factoryBean.setJndiName(EJB_JNDI_PREFIX + serviceType.getSimpleName() + EJB_JNDI_SUFFIX);
//		return factoryBean;
//	}

	private static LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = jndiServiceName(serviceClass, false);
		log.info("Creating EJB proxy for " + serviceClass.getSimpleName() + " with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factoryBean = new LocalStatelessSessionProxyFactoryBean();
		factoryBean.setBusinessInterface(serviceClass);
		factoryBean.setExpectedType(serviceClass);
		factoryBean.setJndiName(jndiName);
		return factoryBean;
	}

	private static String jndiServiceName(Class<?> serviceClass, boolean addServiceClassName) {
		return EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX + (addServiceClassName ? "!" + serviceClass.getName() : "");
	}

}
