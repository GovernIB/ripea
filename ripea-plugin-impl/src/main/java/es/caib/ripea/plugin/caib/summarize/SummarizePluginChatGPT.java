package es.caib.ripea.plugin.caib.summarize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import es.caib.ripea.core.api.dto.Resum;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.summarize.SummarizePlugin;
import lombok.extern.log4j.Log4j;

import javax.ws.rs.core.MediaType;

@Log4j
public class SummarizePluginChatGPT extends RipeaAbstractPluginProperties implements SummarizePlugin {

    private final static int MAX_LENGTH = 25000;

    private Client jerseyClient;
    private ObjectMapper mapper;

    public Resum getSummarize(String text) throws SistemaExternException {
        Resum resum = Resum.builder().build();

        boolean debug = isDebug();

        if (text == null || text.isEmpty())
            return resum;
        if (text.length() > 10) {
            text = text.substring(0, MAX_LENGTH);
        }

        String gptUrl = getUrl();
        String gptApiKey = getApiKey();

        if (debug) {
            log.info("Realitzant petició de resum a la URL: " + gptUrl);
            log.info("Text a resumir: " + text);
        }

        Client client = getJerseyClient();

        // Crear una instància d'ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        ClientResponse response = null;

        try {
            // Crear l'objecte principal
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("model", "gpt-4o-mini");

            // Crear el node 'messages'
            ArrayNode messagesNode = mapper.createArrayNode();
            ObjectNode message = mapper.createObjectNode();
            message.put("role", "user");
            message.put("content", "Genera un títol (màxim 70 caràcters) i un resum (màxim 500 caràcters) pel següent text: " + text);
            messagesNode.add(message);

            // Afegir 'messages' a l'objecte principal
            rootNode.put("messages", messagesNode);

            // Afegir 'temperature' a l'objecte principal
            rootNode.put("temperature", 0.7);

            String input = mapper.writeValueAsString(rootNode);

            WebResource webResource = client.resource(gptUrl);
            response = webResource.type(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + gptApiKey)
                    .post(ClientResponse.class, input);
        } catch (Exception e) {
            throwSistemaExternException("Error realitzant petició al ChatGPT", e, debug);
        }

        String summaryText = null;
        String titleText = null;

        if (response.getStatus() == 200) {
            try {
                String jsonResponse = response.getEntity(String.class);
                JsonNode root = mapper.readTree(jsonResponse);
                String generatedText = root.path("choices").get(0).path("text").textValue();

                // Divideix el text generat per obtenir el títol i resum
                String[] parts = generatedText.split("\nResumen:\n");
                if (parts.length == 2) {
                    titleText = parts[0].replace("Títol:", "").trim();
                    summaryText = parts[1].trim();

                    resum = Resum.builder()
                            .titol(titleText)
                            .resum(summaryText)
                            .build();
                } else {
                    throwSistemaExternException("Resposta inesperada del API", null, debug);
                }
            } catch (Exception e) {
                throwSistemaExternException("Error processant la resposta del API", e, debug);
            }
        } else {
            throwSistemaExternException("Error en la resposta de la API: " + response.getStatus(), null, debug);
        }


        return resum;
    }

    private void throwSistemaExternException(String msg, Exception e, boolean debug) throws SistemaExternException {
        if (e == null) {
            if (debug) log.error(msg);
            throw new SistemaExternException(msg);
        } else {
            if (debug) log.error(msg, e);
            throw new SistemaExternException(msg, e);
        }
    }

    public boolean isActive() {

        String chatGPTUrl = getUrl();
        String gptApiKey = getApiKey();

        if (chatGPTUrl == null || chatGPTUrl.isEmpty() || gptApiKey == null || gptApiKey.isEmpty())
            return false;

        // TODO: Mètode de salut

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
//            if (getApiKey() != null) {
//                jerseyClient.addFilter(new HTTPBasicAuthFilter(getUsuari(), getPassword()));
//            }
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
    private String getApiKey() {
        return getProperty("plugin.summarize.gpt.apiKey");
    }
    private Integer getTimeout() {
        return Integer.valueOf(getProperty("plugin.summarize.service.timeout", "5000"));
    }

    private boolean isDebug() {
        return getAsBoolean("plugin.summarize.debug");
    }
}
