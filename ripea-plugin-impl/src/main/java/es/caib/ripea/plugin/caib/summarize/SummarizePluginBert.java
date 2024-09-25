package es.caib.ripea.plugin.caib.summarize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import es.caib.ripea.core.api.dto.Resum;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.summarize.SummarizePlugin;
import lombok.extern.log4j.Log4j;

import java.util.Properties;

@Log4j
public class SummarizePluginBert extends RipeaAbstractPluginProperties implements SummarizePlugin {

    private Client jerseyClient;
    private ObjectMapper mapper;

    public SummarizePluginBert(String propertyKeyBase, Properties properties) {
        super(propertyKeyBase, properties);
    }

	@Override
	public Resum getSummarize(String text, int longitudDesc) throws SistemaExternException {
		return getSummarize(text, longitudDesc, 10);
	}

	@Override
	public Resum getSummarize(String text) throws SistemaExternException {
		return getSummarize(text, 250, 10);
	}
	
	@Override
	public Resum getSummarize(String text, int longitudDesc, int longitudTitol) throws SistemaExternException {

        Resum resum = new Resum();

        boolean debug = isDebug();

        ObjectMapper objectMapper = new ObjectMapper();

        String bertUrl = getUrl();
        String resumUrl = bertUrl + "/summarize_by_ratio?ratio=0.1&max_length="+longitudDesc;
        String titolUrl = bertUrl + "/summarize_by_ratio?ratio=0.1&max_length="+longitudTitol;

        if (debug) {
            log.info("Realitzant petició de resum a les URLs: " + resumUrl + ", " + titolUrl);
            log.info("Text a resumir: " + text);
        }

        try {
            String summaryText = null;
            String titleText = null;

            Client jerseyClient = getJerseyClient();
            if (resumUrl != null) {
                String summaryJson = jerseyClient.
                        resource(resumUrl).
                        accept("application/json").
                        type("text/plain").
                        post(String.class, text);
                summaryText = objectMapper.readValue(summaryJson, BertResponse.class).getSummary();
            }
            if (debug) log.info("Resum obtingut: " + summaryText);


            if (titolUrl != null) {
                String titleJson = jerseyClient.
                        resource(titolUrl).
                        accept("application/json").
                        type("text/plain").
                        post(String.class, text);
                titleText = objectMapper.readValue(titleJson, BertResponse.class).getSummary();
            }
            if (debug) log.info("Titol obtingut: " + titleText);

            resum.setTitol(titleText);
            resum.setResum(summaryText);

        } catch (Exception e) {
            String errorMsg = "Error obtenint el text resumit del servei remot";
            if (debug) log.error(errorMsg, e);
            throw new SistemaExternException(errorMsg, e);
        }
        return resum;
    }

    public boolean isActive() {

        String bertUrl = getUrl();

        if (bertUrl == null || bertUrl.isEmpty())
            return false;

        ObjectMapper objectMapper = new ObjectMapper();
        String resumUrl = bertUrl + "/summarize_by_ratio?ratio=0.1&max_length=500";

        try {
            Client jerseyClient = getJerseyClient();
            ClientResponse clientResponse = jerseyClient.
                    resource(resumUrl).
                    accept("application/json").
                    type("text/plain").
                    post(ClientResponse.class, null);
            if (clientResponse.getStatus() != ClientResponse.Status.BAD_REQUEST.getStatusCode()) {
                return false;
            }

            String jsonResponse = clientResponse.getEntity(String.class);
            String response = objectMapper.readValue(jsonResponse, BertResponse.class).getMessage();
            if (!"Request must have raw text".equals(response)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    private Client getJerseyClient() {
        if (jerseyClient == null) {
            jerseyClient = new Client();
            Integer serviceTimeout = getTimeout();
            if (serviceTimeout != null) {
                jerseyClient.setConnectTimeout(serviceTimeout);
                jerseyClient.setReadTimeout(serviceTimeout);
            }
            if (getUsuari() != null) {
                jerseyClient.addFilter(new HTTPBasicAuthFilter(getUsuari(), getPassword()));
            }
            //jerseyClient.addFilter(new LoggingFilter(System.out));
            mapper = new ObjectMapper();
            // Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            // Mecanisme de deserialització dels enums
            mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            // Per a no serialitzar propietats amb valors NULL
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            // No falla si hi ha propietats que no estan definides a l'objecte destí
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
        return jerseyClient;
    }

    private String getUrl() {
        return getProperty("plugin.summarize.url");
    }
    private String getUsuari() {
        return getProperty("plugin.summarize.bert.usuari");
    }
    private String getPassword() {
        return getProperty("plugin.summarize.bert.password");
    }
    private Integer getTimeout() {
        return Integer.valueOf(getProperty("plugin.summarize.service.timeout", "5000"));
    }
    private boolean isDebug() {
        return getAsBoolean("plugin.summarize.bert.debug");
    }
}
