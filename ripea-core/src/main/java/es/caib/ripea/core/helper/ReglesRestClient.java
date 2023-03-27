package es.caib.ripea.core.helper;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import es.caib.ripea.core.api.dto.ReglaDistribucioDto;
import es.caib.ripea.core.api.utils.Utils;
import es.caib.ripea.plugin.helper.RestClient;


public class ReglesRestClient extends RestClient{

	private static final String CARPETA_SERVICE_PATH = "/api/rest/regles";
	
	public ReglesRestClient() {
		super();
	}
	
	public ReglesRestClient(
			String baseUrl,
			String username,
			String password) {
		super(baseUrl, username, password);

	}
	
	public ReglesRestClient(
			String baseUrl,
			String username,
			String password,
			boolean autenticacioBasic) {
		super(baseUrl, username, password, autenticacioBasic);
	}
	
	/** Mètode per crear una regla per un codi Sia, un backoffice i a l'entitat indicada.
	 * 
	 */
	public ClientResponse add(
			String entitat, 
			String sia,
			String backoffice) {
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/add?entitat=" + entitat + "&sia=" + sia + "&backoffice=" + backoffice;
			Client jerseyClient = generarIAuthenticarClient(urlAmbMetode);
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.post(ClientResponse.class);
			
			return response;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}
	
	public ClientResponse canviEstat(
			String sia,
			boolean activa) {
		try {

			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/canviEstat";
			
			MultivaluedMap<String, String> params = new MultivaluedMapImpl();
			params.add("sia", sia); 
			params.add("activa", String.valueOf(activa)); 
			Client jerseyClient = generarIAuthenticarClient(urlAmbMetode);
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.queryParams(params)
					.post(ClientResponse.class);
			
			return response;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}
	
	
	
	
	public ReglaDistribucioDto consultarRegla(String sia) {
		try {
			
			MultivaluedMap<String, String> params = new MultivaluedMapImpl();
			params.add("sia", sia); 
			
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH +  "/consultarRegla";
			Client jerseyClient = generarIAuthenticarClient(urlAmbMetode);
			
			String json = jerseyClient.
					resource(urlAmbMetode).
					queryParams(params).
					type("application/json").
					get(String.class);
			
			List<Map<String, Object>> regles = getMapper().readValue(json, new TypeReference<List<Map<String, Object>>>(){});
			 
			ReglaDistribucioDto regla = new ReglaDistribucioDto();
			if (Utils.isNotEmpty(regles)) {
				Map<String, Object> reg = regles.get(0);
				regla.setData((String)reg.get("data"));
				regla.setNom((String)reg.get("nom"));
				regla.setActiva((Boolean)reg.get("activa"));
			}
			
			return regla;
			
		} catch (Exception ex) { 
			if (ex instanceof UniformInterfaceException) { 
				if (((UniformInterfaceException) ex).getResponse().getStatusInfo().getStatusCode() == 404) { //com.sun.jersey.api.client.UniformInterfaceException: GET returned a response status of 404 Not Found
					return null;
				}
			}
			throw new RuntimeException(ex);
		}
	}
	
	
}
