package es.caib.ripea.plugin.caib.unitat;



import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import es.caib.ripea.plugin.helper.RestClient;



public class UnitatsOrganitzativesRestClient extends RestClient{



	public UnitatsOrganitzativesRestClient() {}
	public UnitatsOrganitzativesRestClient(
			String baseUrl,
			String username,
			String password) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}
	
	public UnitatsOrganitzativesRestClient(
			String baseUrl,
			String username,
			String password,
			boolean autenticacioBasic) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.autenticacioBasic = autenticacioBasic;
	}

	
	public List<UnidadRest> obtenerArbolUnidades(String codigo, String fechaActualizacion, String fechaSincronizacion) {
		try {
			
			MultivaluedMap<String, String> params = new MultivaluedMapImpl();
			params.add("codigo", codigo); 
			params.add("fechaActualizacion", fechaActualizacion);
			params.add("fechaSincronizacion", fechaSincronizacion);
			params.add("denominacionCooficial", "false");
			
			
			String urlAmbMetode = baseUrl + "rest/unidades/obtenerArbolUnidades";
			Client jerseyClient = generarIAuthenticarClient(urlAmbMetode);
			
			String json = jerseyClient.
					resource(urlAmbMetode).
					queryParams(params).
					type("application/json").
					get(String.class);
			
			
			return getMapper().readValue(json, new TypeReference<List<UnidadRest>>(){});
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	

	

	


}
