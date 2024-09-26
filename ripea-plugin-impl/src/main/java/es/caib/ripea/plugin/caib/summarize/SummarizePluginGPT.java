package es.caib.ripea.plugin.caib.summarize;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import es.caib.ripea.core.api.dto.Resum;
import es.caib.ripea.plugin.RipeaAbstractPluginProperties;
import es.caib.ripea.plugin.SistemaExternException;
import es.caib.ripea.plugin.summarize.SummarizePlugin;
import lombok.extern.log4j.Log4j;
import opennlp.tools.tokenize.SimpleTokenizer;

@Log4j
public class SummarizePluginGPT extends RipeaAbstractPluginProperties implements SummarizePlugin {

    private Client jerseyClient;
    private ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(SummarizePluginGPT.class);
   
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

        Resum resum = new Resum();

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
            String prompt = "Donat el següent texte, proporciona un títol de "+longitudTitol+" caràcters màxim i un resum de "+longitudDesc+" caràcters màxim. En idioma català.:" + text;
            //en idioma català, separa el titol de la descripció amb la seqüència de caràcters ####. No afegeixis cap altre text addicional.
            //Given the following text, which can be in Catalan or Spanish, provide a title of up to 50 characters and a summary of the most relevant sections of up to 500 characters. The result must be translated into Catalan.
            
            int maxTokensPrompt = geMaxTokens()-longitudDesc; //Realment la longitud desc son caracters, no tokens, pero així ens curam en salut de que tendrem espai per la resposta.

            /**
             * Utilitza la llibreria Apache OpenNLP para la tokenización básica.
             * Pero els tokens amb el algoritme utilitzat per la llibreria no es el mateix que utilitza GPT4All.
             * GPT4All utiliza un algoritmo de tokenización basado en técnicas de subword tokenization, como Byte Pair Encoding (BPE) o WordPiece,
             * similares a las utilizadas por otros modelos de lenguaje basados en transformers.
             * Estas técnicas dividen las palabras en subcomponentes más pequeños, lo que puede resultar en un mayor número de tokens comparado con la tokenización basada en palabras.
             * 
             * Per tant, durant les proves realitzades, s'ha comprovat que el metode reduceTokens conta aproximadament el doble de tokens que GPT4All.
             * 
             * Si passam algun dia a Java8, podrem utilitzar la llibreria propia de com.hexadevlabs gpt4all-java-bindings 0.1.0 que hauria de ser més aproximada.
             */
            prompt = reduceTokens(prompt, maxTokensPrompt/2);

            rootNode.put("prompt", prompt);

            String input = mapper.writeValueAsString(rootNode);

            System.out.println(input);
            long t1 = Calendar.getInstance().getTimeInMillis();
            
            WebResource webResource = client.resource(gptUrl);
            response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, input);
            
	        String summaryText = null;
	        String titleText = null;
	
	        if (response.getStatus() == 200) {
	        	
	        	long t2 = Calendar.getInstance().getTimeInMillis();
	        	
                String jsonResponse = response.getEntity(String.class);
                
                System.out.println(jsonResponse);
                
                System.out.println("Temps: "+((t2-t1)/1000)+" segons.");
                
                JsonNode root = mapper.readTree(jsonResponse);
                String generatedText = root.path("choices").get(0).path("text").textValue();

                if (generatedText.startsWith("ERROR")) {
                	
                	if (generatedText.indexOf("prompt size exceeds")>0) {
                		resum.setError("El texte del document es massa llarg per que el model de IA pugui fer-ne un resum.");
                	} else {
                		resum.setError(generatedText);
                	}
                	
                } else {
                
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
	
	                //De vegades el model inclou les paraules "**Titol**" o "**Titulo**" o "Resum (500 caràcters)" abans del titol i la descripcio...
	                titleText = removeOccurrences(titleText, "*", "Títol", "Titulo", ":", longitudTitol+" caràcters", "en català", "(", ")").trim();
	                summaryText = removeOccurrences(summaryText, "*", "Resum", "Resumen", ":", longitudDesc+" caràcters", "en català", "(", ")").trim();
	                
	                if (titleText!=null && titleText.length()>longitudTitol) {
	                	titleText = titleText.substring(0, longitudTitol);
	                }
	                
	                if (summaryText!=null && summaryText.length()>longitudDesc) {
	                	summaryText = summaryText.substring(0, longitudDesc);	
	                }
	                
	               resum.setTitol(titleText);
	               resum.setResum(summaryText);
                }
	
	        } else {       	
	            throwSistemaExternException(""+response.getStatus(), null, debug);
	        }
        
        } catch (Exception e) {
            throwSistemaExternException("Error realitzant petició al model de IA: "+e.getMessage(), e, debug);
        }

        return resum;
    }

	public String removeOccurrences(String original, String... toRemove) {
		
		if (original == null || toRemove == null) {
			return original;
		}

		int length = original.length();
		int boundary = 15;

		// Definir las secciones a revisar
		String startSection = original.substring(0, Math.min(boundary, length));
		String endSection = original.substring(Math.max(length - boundary, 0));

		// Eliminar ocurrencias en la sección inicial
		for (String remove : toRemove) {
			if (remove != null && !remove.isEmpty()) {
				startSection = startSection.replace(remove, "");
				endSection = endSection.replace(remove, "");
			}
		}
		
		// Reconstruir el string original
		String middleSection = original.substring(Math.min(boundary, length), Math.max(length - boundary, 0));
		String resultat = startSection + middleSection + endSection;
		return resultat.trim();
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
    
    public String reduceTokens(String promptInicial, int maxTokens) {
    	
    	SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    	String[] tokens = tokenizer.tokenize(promptInicial);
    	
    	if (tokens.length > maxTokens) {
    		String[] reducedTokens = new String[maxTokens];
    		System.arraycopy(tokens, 0, reducedTokens, 0, maxTokens);
    		tokens = reducedTokens;
    		
        	// Volver a formar un String a partir de los tokens reducidos usando StringBuilder
        	StringBuilder reducedTextBuilder = new StringBuilder();
        	for (String token : tokens) {
    	    	if (reducedTextBuilder.length() > 0) {
    	    		reducedTextBuilder.append(" ");
    	    	}
    	    	reducedTextBuilder.append(token);
        	}
        	String resultat = reducedTextBuilder.toString();
        	
        	String[] tokensFinals = tokenizer.tokenize(resultat);
        	logger.debug("Summarize tokens reduits de "+tokens.length+" a "+tokensFinals.length);
        	
        	return resultat;
        	
    	} else {
    		return 	promptInicial;
    	}
    }
    
    private String getUrl() {
        return getProperty("plugin.summarize.url");
    }
    private String getModel() {
        return getProperty("plugin.summarize.model");
    }
    private Integer geMaxTokens() {
        return Integer.valueOf(getProperty("plugin.summarize.model.maxTokens", "8192"));
    }
    private String getApiKey() {
        return getProperty("plugin.summarize.gpt.apiKey");
    }
    private Integer getTimeout() {
        return Integer.valueOf(getProperty("plugin.summarize.service.timeout", "60000"));
    }
    private boolean isDebug() {
        return getAsBoolean("plugin.summarize.debug");
    }
}
