package es.caib.ripea.service.config;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
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
	    
	    //Prevenció de WARNINGS: 
	    //AMQ222165: No se ha definido una dirección de mensajes muertos (DLA). 
	    // - Esto significa que si un mensaje no puede ser entregado después de varios intentos, simplemente se eliminará sin dejar rastro.
	    //AMQ222166: No se ha definido una dirección de expiración. 
	    // - Los mensajes que expiran no se redirigen a ninguna cola para su inspección posterior2.
	    
        AddressSettings settings = new AddressSettings()
            .setDeadLetterAddress(SimpleString.toSimpleString("DLQ.RIPEA"))
            .setExpiryAddress(SimpleString.toSimpleString("Expiry.RIPEA"))
            .setMaxDeliveryAttempts(5);
	    
        Map<String, AddressSettings> addressesSettings = new HashMap <String, AddressSettings>();
        // # --> Totes les cues (anotacions, fluxos, avisos, etc. compartenixen la mateixa configuració
        addressesSettings.put("#", settings);
        
	    configuration.setAddressesSettings(addressesSettings);
	    
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