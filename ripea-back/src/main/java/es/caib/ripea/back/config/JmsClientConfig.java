package es.caib.ripea.back.config;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
@EnableJms
public class JmsClientConfig {

	@Value("${es.caib.ripea.jms.broker.url:vm://0}")
	private String brokerUrl;

    @Bean
    @ConditionalOnWarDeployment
    public ConnectionFactory connectionFactory() throws JMSException{
    	return new ActiveMQConnectionFactory(brokerUrl);
    }
    
    @Bean
    @ConditionalOnWarDeployment
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
    	return new JmsTemplate(connectionFactory);
    }

    /**
     * Factory dels contenidors dels @JmsListener (SseResourceController).
     *
     * Es declara explícitament per NO heretar el que configura Spring Boot per defecte: sobre JBoss el context
     * té un JtaTransactionManager (BasePersistenceConfig.mainTransactionManager) i Spring Boot l'injecta al
     * factory; llavors el DefaultMessageListenerContainer degrada la caché de CACHE_AUTO a CACHE_NONE i crea i
     * tanca connexió + sessió + consumidor a cada cicle de poll (receiveTimeout = 1s) per cadascuna de les cues.
     * Amb 9 @JmsListener això són unes 27 traces d'auditoria d'Artemis per segon (~2,3M línies de log al dia).
     *
     * Amb CACHE_CONSUMER el consumidor es crea una sola vegada al desplegar i es reutilitza.
     *
     * No es configura cap transaction manager: els listeners són @Async (retornen immediatament i el treball es
     * fa en un altre fil), de manera que la transacció ja es confirmava abans de fer res i no aportava garanties.
     */
    @Bean
    @ConditionalOnWarDeployment
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
    	DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    	factory.setConnectionFactory(connectionFactory);
    	factory.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER);
    	return factory;
    }

}
