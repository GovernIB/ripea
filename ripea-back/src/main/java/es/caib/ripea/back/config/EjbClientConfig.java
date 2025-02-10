/**
 * 
 */
package es.caib.ripea.back.config;

import es.caib.ripea.service.intf.config.BaseConfig;
import es.caib.ripea.service.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

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

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = jndiServiceName(serviceClass, false);
		log.info("Creating EJB proxy for " + serviceClass.getSimpleName() + " with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factoryBean = new LocalStatelessSessionProxyFactoryBean();
		factoryBean.setBusinessInterface(serviceClass);
		factoryBean.setExpectedType(serviceClass);
		factoryBean.setJndiName(jndiName);
		return factoryBean;
	}

	private String jndiServiceName(Class<?> serviceClass, boolean addServiceClassName) {
		return EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX + (addServiceClassName ? "!" + serviceClass.getName() : "");
	}

}
