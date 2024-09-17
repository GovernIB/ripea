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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.ws.rs.core.MediaType;

@Log4j
public class SummarizePluginGPT extends RipeaAbstractPluginProperties implements SummarizePlugin {

    private Client jerseyClient;
    private ObjectMapper mapper;
   
	public SummarizePluginGPT() {
		super();
	}

	public SummarizePluginGPT(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}

	public SummarizePluginGPT(String propertyKeyBase) {
		super(propertyKeyBase);
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

        Resum resum = Resum.builder().build();

        boolean debug = isDebug();

        if (text == null || text.isEmpty()) {
            return resum;
        } else {
        	text = cleanText(text);
        }

        String gptUrl = getUrl();

        if (debug) {
            log.info("Realitzant petició de resum a la URL: " + gptUrl);
            log.info("Text a resumir: " + text);
        }

        Client client = getJerseyClient();

        ObjectMapper mapper = new ObjectMapper();
        ClientResponse response = null;

        try {

            ObjectNode rootNode = mapper.createObjectNode();            
            rootNode.put("model", getModel());
            
            /**
             * El parámetro temperature en la API de GPT4All controla la aleatoriedad de las respuestas generadas por el modelo. Este parámetro toma valores entre 0 y 1.
             * 
             * Valores bajos (cercanos a 0): Hacen que el modelo sea más determinista y menos creativo. Las respuestas tienden a ser más predecibles y repetitivas.
             * Valores altos (cercanos a 1): Hacen que el modelo sea más creativo y variado, generando respuestas menos predecibles.
             * 
             * En el contexto de resumir un texto, para obtener resúmenes que sean coherentes y precisos, generalmente es mejor usar un valor bajo de temperature.
             * Esto asegura que el modelo se enfoque en la información más relevante y no divague.
             * 
             * 0.2 a 0.5: Estos valores suelen ser adecuados para tareas de resumen, ya que mantienen la coherencia y precisión sin introducir demasiada aleatoriedad.
             */
            rootNode.put("temperature", 0.4);
            /**
             * El parámetro max_tokens en la API de GPT4All especifica el número máximo de tokens que el modelo puede generar en su respuesta.
             * Un "token" puede ser tan corto como un carácter o tan largo como una palabra completa, dependiendo del contexto.
             * Por ejemplo, la palabra "fantástico" podría ser un solo token, mientras que "una casa" podría ser dos tokens.
             * 
             * Generalmente, 50 tokens pueden equivaler a aproximadamente 35-40 palabras, dependiendo del contenido.
             */
            rootNode.put("max_tokens", longitudDesc+longitudTitol);
            
            /**
             * A TENIR EN COMPTE:
             * 
             * El error "The prompt size exceeds the context window size and cannot be processed" 
             * indica que el tamaño del prompt que estás enviando al método completions de GPT4All es mayor que la capacidad máxima del modelo para procesar texto en una sola solicitud.
             * 
             * El modelo Llama 3 8B Instruct tiene un límite de contexto de 16,000 tokens. 
             * Esto significa que el tamaño combinado del prompt y la respuesta generada no puede exceder este límite.
             * Si tu prompt tiene 15,000 tokens, solo quedarán 1,000 tokens disponibles para la respuesta generada por el modelo.
             */
            String prompt = "Donat el següent texte, proporciona un títol de "+longitudTitol+" caràcters màxim i un resum de "+longitudDesc+" caràcters màxim, en idioma català.:" + text;
            //en idioma català, separa el titol de la descripció amb la seqüència de caràcters ####. No afegeixis cap altre text addicional.
           
            StringTokenizer sTok = new StringTokenizer(prompt);
            int numeroDeTokensPrompt = sTok.countTokens();
            int maxTokensModel = geMaxTokens();
            
            //Convé reservar un % dels tokens maxims del model per la resposta, que sempré será bastant mes petta que el promtp inicial, ja que estam resumint texte.
            int tokensReservats = maxTokensModel/5;
            
            if (numeroDeTokensPrompt+tokensReservats>maxTokensModel) {
            	prompt = tokenRemover(prompt, maxTokensModel-tokensReservats);
            }
            
            prompt = prompt.substring(0, 2048);
            
            rootNode.put("prompt", prompt);

            String input = mapper.writeValueAsString(rootNode);

            WebResource webResource = client.resource(gptUrl);
            response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);
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
                int posRes = generatedText.indexOf("Resum");
                if (posRes>0) {
                	titleText	= generatedText.substring(0, posRes);
                	summaryText = generatedText.substring(posRes, generatedText.length());
                } else {
                	String[] lines = generatedText.split("\\r?\\n");
                	List<String> filteredLines = new ArrayList<String>();
                	for (String line : lines) {
                		if (line != null && !line.trim().isEmpty()) {
                			filteredLines.add(line);
                		}
                	}
                	if (filteredLines.size()==1) {
                		titleText = filteredLines.get(0);
                		summaryText = filteredLines.get(0);
                	} else if (filteredLines.size()>1) {
                		titleText = filteredLines.get(0);
                		summaryText = filteredLines.get(1);
                	}
                }

                resum = Resum.builder()
                		.titol(titleText)
                        .resum(summaryText)
                        .build();

            } catch (Exception e) {
                throwSistemaExternException("Error processant la resposta del API", e, debug);
            }
        } else {
            throwSistemaExternException("Error en la resposta de la API: " + response.getStatus(), null, debug);
        }

        return resum;
    }

	/**
	 * Puesto que parte del prompt es texto extraido de un PDF, contiene caracteres especiales como circulos correspondientes a listas de elementos o quizas tabulaciones o espacios en blanco extra.
	 * Función en Java que elimina caracteres especiales, tabulaciones y espacios en blanco extra de un texto, dejando solo las palabras entendibles	 * 
	 */
	private static String cleanText(String input) {
		// Eliminar caracteres especiales como círculos de listas
		String cleanedText = input.replaceAll("[•◦]", "");
	
		// Eliminar tabulaciones y espacios en blanco extra
		cleanedText = cleanedText.replaceAll("\\s+", " ").trim();
	
		// Eliminar otros caracteres especiales que no aportan valor
		cleanedText = cleanedText.replaceAll("[^\\p{L}\\p{N}\\s]", "");
	
		return cleanedText;
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

  //int n Número de tokens a eliminar desde el final
    private String tokenRemover(String texto, int n) {

    	StringTokenizer tokenizer = new StringTokenizer(texto);
    	int totalTokens = tokenizer.countTokens();

    	// Si n es mayor o igual al número total de tokens, devolver una cadena vacía
    	if (n >= totalTokens) {
    		return "";
    	}

    	// Construir el nuevo String sin los últimos n tokens
    	StringBuilder resultado = new StringBuilder();
    	for (int i = 0; i < totalTokens - n; i++) {
	    	resultado.append(tokenizer.nextToken());
	    	if (i < totalTokens - n - 1) {
	    		resultado.append(" ");
	    	}
    	}

    	return resultado.toString();
    }
    
    private String getUrl() {
        return getProperty("plugin.summarize.url");
    }
    private String getModel() {
        return getProperty("plugin.summarize.model");
    }
    private Integer geMaxTokens() {
        return Integer.valueOf(getProperty("plugin.summarize.model.maxTokens", "10000"));
    }
    private String getApiKey() {
        return getProperty("plugin.summarize.gpt.apiKey");
    }
    private Integer getTimeout() {
        return Integer.valueOf(getProperty("plugin.summarize.service.timeout", "300000"));
    }
    private boolean isDebug() {
        return getAsBoolean("plugin.summarize.debug");
    }
}
