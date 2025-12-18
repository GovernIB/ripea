package es.caib.ripea.plugin.caib.comanda;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.comanda.model.v1.avis.Avis;
import es.caib.comanda.model.v1.tasca.Tasca;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.comanda.ComandaCaibPlugin;
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.utils.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComandaCaibPluginImpl extends RipeaAbstractPluginProperties implements ComandaCaibPlugin {

	public ComandaCaibPluginImpl(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	
	@Override
	public String getEndpointURL() {
		String resultat = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_ENDPOINT));
		if (!Utils.hasValue(resultat)) {
			resultat = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_URL));
			if (Utils.hasValue(resultat)) {
				resultat = resultat.replace("http://", "").replace("https://", "");
			}
		}
		return resultat;
	}

	@Override
	public ResponseEntity<String> sendTasca(Tasca tasca) throws Exception {
        var httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        var username = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_USR));
        var password = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_PWR));
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);
        var mapper = new ObjectMapper();
        var requestBody = mapper.writeValueAsString(tasca);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        String url = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_URL));
        if (url == null) {
            throw new Exception("La propietat es.caib.ripea.plugin.comanda.url.base no pot ser null");
        }
        url += (url.charAt(url.length()-1) != '/' ? "/" : "") + "jms/tasques";
        return getRestTemplate().postForEntity(url, requestEntity, String.class);
	}

	@Override
	public ResponseEntity<String> sendAvis(Avis avis) throws Exception {
        var httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        var username = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_USR));
        var password = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_PWR));
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set("Authorization", authHeader);
        var mapper = new ObjectMapper();
        var requestBody = mapper.writeValueAsString(avis);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        String url = getProperty(PropertyConfig.getPropertySuffix(PropertyConfig.COMANDA_PLUGIN_URL));
        if (url == null) {
            throw new Exception("La propietat es.caib.ripea.plugin.comanda.url.base no pot ser null");
        }
        url += (url.charAt(url.length()-1) != '/' ? "/" : "") + "jms/avisos";
        return getRestTemplate().postForEntity(url, requestEntity, String.class);
	}
	
	private RestTemplate getRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return new RestTemplate(factory);
	}
}