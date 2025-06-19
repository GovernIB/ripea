package es.caib.ripea.back.config;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class JmsClientConfig {
	
    @Bean
    @ConditionalOnWarDeployment
    public ConnectionFactory connectionFactory() throws JMSException{
    	return new ActiveMQConnectionFactory("tcp://localhost:61617");
    }
    
    @Bean
    @ConditionalOnWarDeployment
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
    	return new JmsTemplate(connectionFactory);
    }

}