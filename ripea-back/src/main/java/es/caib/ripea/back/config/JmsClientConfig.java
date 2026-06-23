package es.caib.ripea.back.config;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

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

}