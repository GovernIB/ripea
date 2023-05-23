/**
 * 
 */
package es.caib.ripea.core.service.ws;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import es.caib.portafib.ws.callback.api.v1.PortaFIBEvent;
import es.caib.portafib.ws.callback.api.v1.SigningRequest;


public class CallbackPortafibRestTest {

    private static final String URL_BASE = "http://localhost:8080/ripea/rest/portafib/v1";



	public static void main(String[] args) {
		try {
			// Estats:
			//   0  - DOCUMENT_PENDENT;
			//   50 - DOCUMENT_PENDENT;
			//   60 - DOCUMENT_FIRMAT;
			//   70 - DOCUMENT_REBUTJAT;
			//   80 - DOCUMENT_PAUSAT;
			new CallbackPortafibRestTest().test(
					1330338,
					60,
					null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void test(
			long documentId,
			int estat,
			String motiuRebuig) throws Exception {
		PortaFIBEvent event = new PortaFIBEvent();
		SigningRequest signingRequest = new SigningRequest();
		signingRequest.setID(documentId);
		signingRequest.setRejectionReason(motiuRebuig);
		event.setSigningRequest(signingRequest);
		event.setEventTypeID(estat);
		
		

	      Client client = Client.create();
	      WebResource webResource = client.resource(URL_BASE + "/event");

	      ObjectMapper mapper = new ObjectMapper();
	      String json = mapper.writeValueAsString(event);

	      System.out.println(json);

	      ClientResponse response = webResource.type("application/json").post(
	          ClientResponse.class, json);

	      if (response.getStatus() != 200) {
	        throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
	      }

	      String output = response.getEntity(String.class);
	      Assert.assertEquals("OK", output);
	      System.out.println("Resposta cridada REST a mètode event(): ]" + output + "[ \n");

		
		
	}





}
