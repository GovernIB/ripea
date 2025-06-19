package es.caib.ripea.service.config;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class JmsConfig {

	@Value("${es.caib.ripea.jms.broker.url:tcp://localhost:61617}") private String brokerUrl;
	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public EmbeddedActiveMQ createEmbeddedBroker() throws Exception {
	    EmbeddedActiveMQ embeddedBroker = new EmbeddedActiveMQ();
	    org.apache.activemq.artemis.core.config.Configuration configuration = new ConfigurationImpl()
	        .setPersistenceEnabled(false)
	        .setSecurityEnabled(false)
	        .addAcceptorConfiguration("tcp", brokerUrl);
	    embeddedBroker.setConfiguration(configuration);
	    return embeddedBroker;
	}
	
    @Bean
    public ConnectionFactory connectionFactory() throws JMSException{
    	return new ActiveMQConnectionFactory(brokerUrl);
    }
    
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
    	return new JmsTemplate(connectionFactory);
    }

}