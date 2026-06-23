package es.caib.ripea.service.test.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.ConnectionFactory;
import javax.persistence.EntityManagerFactory;

import org.mockito.Mockito;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * Configuració de test que substitueix les configuracions de producció
 * que no es carreguen en tests (AclConfig, JmsConfig, SchedulingConfig,
 * ExecutorConfig, CacheConfig).
 *
 * Proporciona:
 *  - SimpleMeterRegistry: mètriques en memòria per a ApplicationHelper
 *  - MutableAclService mock: per a CacheHelper (les crides retornen null/buit)
 *  - JmsTemplate mock: per a EventHelper (les crides no fan res)
 *  - ConnectionFactory mock: dependència de JmsTemplate
 *  - ExecutorService: per a ContingutHelper (pool petit per a tests)
 *  - @EnableCaching: activa el suport de caché (necessari per a CacheHelper)
 */
@Configuration
@EnableCaching
public class TestServiceConfig {

    /**
     * MeterRegistry en memòria. Substitueix qualsevol registre extern
     * perquè Timer.start(meterRegistry) a MetaExpedientServiceImpl funcioni.
     */
    @Bean
    @Primary
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    /**
     * Mock de MutableAclService. CacheHelper.readAclById() retornarà null,
     * cosa que significa "sense ACL definida" (els mètodes isGrantedAny dels
     * serveis retornaran false, però PermisosHelper es gestiona via @MockBean).
     */
    @Bean
    @Primary
    public MutableAclService aclService() {
        return Mockito.mock(MutableAclService.class);
    }

    /**
     * Mock de AclCache. AplicacioServiceImpl l'utilitza però no l'invoca
     * als tests (PermisosHelper ja està mockat).
     */
    @Bean
    @Primary
    public AclCache aclCache() {
        return Mockito.mock(AclCache.class);
    }

    /**
     * TransactionTemplate. AclSidResourceServiceImpl l'utilitza per operacions ACL.
     * S'inicia diferit per evitar problemes d'ordre de creació de beans.
     */
    @Bean
    public TransactionTemplate transactionTemplate(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager(entityManagerFactory);
        return new TransactionTemplate(txManager);
    }

    /**
     * Mock de JmsTemplate. EventHelper pot cridar send() sense errors.
     */
    @Bean
    @Primary
    public JmsTemplate jmsTemplate() {
        return Mockito.mock(JmsTemplate.class);
    }

    /**
     * Mock de ConnectionFactory per satisfer dependències transitòries de JMS.
     */
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        return Mockito.mock(ConnectionFactory.class);
    }

    /**
     * Pool de fils reduït per a tests. ContingutHelper el necessita per a
     * operacions asíncrones (que en tests s'executen de forma síncrona).
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(2);
    }

}
