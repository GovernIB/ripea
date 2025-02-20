/**
 * 
 */
package es.caib.ripea.persistence.config;

import es.caib.ripea.persistence.base.config.BasePersistenceConfig;
import es.caib.ripea.persistence.base.repository.BaseRepositoryImpl;
import es.caib.ripea.persistence.entity.resourcerepository.ExpedientResourceRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.service.intf.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuració dels components de persistència.
 * 
 * @author Límit Tecnologies
 */
@Slf4j
@Configuration
@EnableJpaRepositories(
		basePackageClasses = {
				EntitatRepository.class,
				ExpedientResourceRepository.class
		},
		entityManagerFactoryRef = "mainEntityManager",
		transactionManagerRef = "mainTransactionManager",
		repositoryBaseClass = BaseRepositoryImpl.class
)
public class PersistenceConfig extends BasePersistenceConfig {

	@Override
	protected String getPersistenceUnitName() {
		return "ripeaPU";
	}

	@Override
	protected String[] getEntityPackages() {
		return new String[] {
			BaseConfig.BASE_PACKAGE + ".persistence.entity"
		};
	}

}
