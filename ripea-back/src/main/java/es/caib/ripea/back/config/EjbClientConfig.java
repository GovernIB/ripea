/**
 * 
 */
package es.caib.ripea.back.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import es.caib.ripea.service.intf.base.service.PermissionEvaluatorService;
import es.caib.ripea.service.intf.base.service.ResourceApiService;
import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.resourceservice.DadaResourceService;
import es.caib.ripea.service.intf.resourceservice.DocumentEnviamentInteressatResourceService;
import es.caib.ripea.service.intf.resourceservice.DocumentNotificacioResourceService;
import es.caib.ripea.service.intf.resourceservice.DocumentPublicacioResourceService;
import es.caib.ripea.service.intf.resourceservice.DocumentResourceService;
import es.caib.ripea.service.intf.resourceservice.EntitatResourceService;
import es.caib.ripea.service.intf.resourceservice.ExpedientComentariResourceService;
import es.caib.ripea.service.intf.resourceservice.ExpedientEstatResourceService;
import es.caib.ripea.service.intf.resourceservice.ExpedientPeticioResourceService;
import es.caib.ripea.service.intf.resourceservice.ExpedientResourceService;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaComentariResourceService;
import es.caib.ripea.service.intf.resourceservice.ExpedientTascaResourceService;
import es.caib.ripea.service.intf.resourceservice.GrupResourceService;
import es.caib.ripea.service.intf.resourceservice.InteressatResourceService;
import es.caib.ripea.service.intf.resourceservice.MetaDadaResourceService;
import es.caib.ripea.service.intf.resourceservice.MetaDocumentResourceService;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientResourceService;
import es.caib.ripea.service.intf.resourceservice.MetaExpedientTascaResourceService;
import es.caib.ripea.service.intf.resourceservice.OrganGestorResourceService;
import es.caib.ripea.service.intf.resourceservice.RegistreAnnexResourceService;
import es.caib.ripea.service.intf.resourceservice.RegistreInteressatResourceService;
import es.caib.ripea.service.intf.resourceservice.UsuariResourceService;
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
public class EjbClientConfig {

	static final String EJB_JNDI_PREFIX = "java:app/" + BaseConfig.APP_NAME + "-ejb/";
	static final String EJB_JNDI_SUFFIX = "Ejb";

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean alertaService() {
		return getLocalEjbFactoyBean(AlertaService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean avisService() {
		return getLocalEjbFactoyBean(AvisService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean carpetaService() {
		return getLocalEjbFactoyBean(CarpetaService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean configService() {
		return getLocalEjbFactoyBean(ConfigService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean contingutService() {
		return getLocalEjbFactoyBean(ContingutService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean dadesExternesService() {
		return getLocalEjbFactoyBean(DadesExternesService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean digitalitzacioService() {
		return getLocalEjbFactoyBean(DigitalitzacioService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean documentEnviamentService() {
		return getLocalEjbFactoyBean(DocumentEnviamentService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean documentService() {
		return getLocalEjbFactoyBean(DocumentService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean dominiService() {
		return getLocalEjbFactoyBean(DominiService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean entitatService() {
		return getLocalEjbFactoyBean(EntitatService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean execucioMassivaService() {
		return getLocalEjbFactoyBean(ExecucioMassivaService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientEstatService() {
		return getLocalEjbFactoyBean(ExpedientEstatService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientInteressatService() {
		return getLocalEjbFactoyBean(ExpedientInteressatService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientPeticioService() {
		return getLocalEjbFactoyBean(ExpedientPeticioService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientSeguidorService() {
		return getLocalEjbFactoyBean(ExpedientSeguidorService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientService() {
		return getLocalEjbFactoyBean(ExpedientService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientTascaService() {
		return getLocalEjbFactoyBean(ExpedientTascaService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean fluxFirmaUsuariService() {
		return getLocalEjbFactoyBean(FluxFirmaUsuariService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean grupService() {
		return getLocalEjbFactoyBean(GrupService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean historicService() {
		return getLocalEjbFactoyBean(HistoricService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean importacioService() {
		return getLocalEjbFactoyBean(ImportacioService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean metaDadaService() {
		return getLocalEjbFactoyBean(MetaDadaService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean metaDocumentService() {
		return getLocalEjbFactoyBean(MetaDocumentService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean metaExpedientService() {
		return getLocalEjbFactoyBean(MetaExpedientService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean monitorTasquesService() {
		return getLocalEjbFactoyBean(MonitorTasquesService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean organGestorService() {
		return getLocalEjbFactoyBean(OrganGestorService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean pinbalServeiService() {
		return getLocalEjbFactoyBean(PinbalServeiService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean portafirmesFluxService() {
		return getLocalEjbFactoyBean(PortafirmesFluxService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean segonPlaService() {
		return getLocalEjbFactoyBean(SegonPlaService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean seguimentService() {
		return getLocalEjbFactoyBean(SeguimentService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean tipusDocumentalService() {
		return getLocalEjbFactoyBean(TipusDocumentalService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean unitatOrganitzativaService() {
		return getLocalEjbFactoyBean(UnitatOrganitzativaService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean urlInstruccioService() {
		return getLocalEjbFactoyBean(URLInstruccioService.class);
	}
	
	//INICI BEANS RESOURCES

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean permissionEvaluatorService() {
		return getLocalEjbFactoyBean(PermissionEvaluatorService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean documentResourceService() {
		return getLocalEjbFactoyBean(DocumentResourceService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean documentPublicacioResourceService() {
		return getLocalEjbFactoyBean(DocumentPublicacioResourceService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean documentEnviamentInteressatResourceService() {
		return getLocalEjbFactoyBean(DocumentEnviamentInteressatResourceService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean documentNotificacioResourceService() {
		return getLocalEjbFactoyBean(DocumentNotificacioResourceService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientComentariResourceService() {
		return getLocalEjbFactoyBean(ExpedientComentariResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientPeticioResourceService() {
		return getLocalEjbFactoyBean(ExpedientPeticioResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientResourceService() {
		return getLocalEjbFactoyBean(ExpedientResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientTascaResourceService() {
		return getLocalEjbFactoyBean(ExpedientTascaResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean grupResourceService() {
		return getLocalEjbFactoyBean(GrupResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean interessatResourceService() {
		return getLocalEjbFactoyBean(InteressatResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean metaExpedientResourceService() {
		return getLocalEjbFactoyBean(MetaExpedientResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean metaExpedientTascaResourceService() {
		return getLocalEjbFactoyBean(MetaExpedientTascaResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean organGestorResourceService() {
		return getLocalEjbFactoyBean(OrganGestorResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean usuariResourceService() {
		return getLocalEjbFactoyBean(UsuariResourceService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean resourceApiService() {
		return getLocalEjbFactoyBean(ResourceApiService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean metaDocumentResourceService() {
		return getLocalEjbFactoyBean(MetaDocumentResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientTascaComentariResourceService() {
		return getLocalEjbFactoyBean(ExpedientTascaComentariResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean dadaResourceService() {
		return getLocalEjbFactoyBean(DadaResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean metaDadaResourceService() {
		return getLocalEjbFactoyBean(MetaDadaResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean registreAnnexResourceService() {
		return getLocalEjbFactoyBean(RegistreAnnexResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean registreInteressatResourceService() {
		return getLocalEjbFactoyBean(RegistreInteressatResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean entitatResourceServiceEjb() {
		return getLocalEjbFactoyBean(EntitatResourceService.class);
	}
	
	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean expedientEstatResourceServiceEjb() {
		return getLocalEjbFactoyBean(ExpedientEstatResourceService.class);
	}
	
	/*
	@Bean
	@ConditionalOnWarDeployment
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
	@ConditionalOnWarDeployment
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
