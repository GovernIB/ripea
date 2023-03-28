/**
 * 
 */
package es.caib.ripea.core.service.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.LoggingFilter;

import es.caib.notib.client.domini.NotificacioCanviClient;



public class CallbackNotibTest {

	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/ripea/rest/notib";
	private static final String IDENTIFICADOR = "db620b4f-9bf0-486d-a3e5-ac800604a9f1";
	private static final String REFERENCIA = "058fd010-d7b2-422e-ae0b-7914dc062692";
	


	public static void main(String[] args) {

		new CallbackNotibTest().testCallbackNotificaCanvi(
				IDENTIFICADOR,
				REFERENCIA);
	
	}
	

	public void testCallbackNotificaCanvi(String identificador, String referenciaEnviament) {
		
		final String NOTIFICACIO_SERVICE_PATH = "/notificaCanvi";
		NotificacioCanviClient notificacio;

		notificacio = new NotificacioCanviClient(
				identificador, 
				referenciaEnviament);
		try {
			String urlAmbMetode = ENDPOINT_ADDRESS + NOTIFICACIO_SERVICE_PATH;
			ObjectMapper mapper  = new ObjectMapper();
			String body = mapper.writeValueAsString(notificacio);
			Client jerseyClient = Client.create();
			jerseyClient.addFilter(new LoggingFilter(System.out));
			ClientResponse response = jerseyClient.
					resource(urlAmbMetode).
					type("application/json").
					post(ClientResponse.class, body);
				
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
