package es.caib.ripea.back.base.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador base per retornar la informació de les variables d'entorn.
 * 
 * @author Limit Tecnologies
 */
@Slf4j
public abstract class BaseSysenvController {

	@Autowired
	private Environment env;

	@GetMapping("/sysenv")
	public ResponseEntity<String> systemEnvironment(
			@RequestParam(required = false) String format) {
		Map<String, Object> systemEnv = getAllProperties(env);
		if (log.isTraceEnabled()) {
			log.trace("All properties: {}", systemEnv);
		}
		MediaType contentType = MediaType.TEXT_PLAIN;
		String envJson;
		if ("jsall".equalsIgnoreCase(format)) {
			String json = systemEnv.entrySet().stream().
					map(e -> "\"" + e.getKey() + "\":\"" + e.getValue() + "\",").
					collect(Collectors.joining("\n"));
			envJson = "window.__RUNTIME_CONFIG__ = {" + json + "}";
			contentType = MediaType.valueOf("text/javascript");
		} else if ("reactapp".equalsIgnoreCase(format)) {
			String json = systemEnv.entrySet().stream().
					filter(e -> e.getKey().startsWith("REACT_APP") || isReactAppMappedFrontProperty(e.getKey())).
					map(e -> {
						if (isReactAppMappedFrontProperty(e.getKey())) {
							return "\"" + getReactAppMappedFrontProperty(e.getKey()) + "\":\"" + e.getValue() + "\",";
						} else {
							return "\"" + e.getKey() + "\":\"" + e.getValue() + "\",";
						}
					}).
					collect(Collectors.joining("\n"));
			if (log.isDebugEnabled()) {
				log.debug("React properties: {}", json);
			}
			envJson = "window.__RUNTIME_CONFIG__ = {" + json + "}";
			contentType = MediaType.valueOf("text/javascript");
		} else if ("vite".equalsIgnoreCase(format)) {
			String json = systemEnv.entrySet().stream().
					filter(e -> e.getKey().startsWith("VITE") || isViteMappedFrontProperty(e.getKey())).
					map(e -> {
						if (isViteMappedFrontProperty(e.getKey())) {
							return "\"" + getViteMappedFrontProperty(e.getKey()) + "\":\"" + e.getValue() + "\",";
						} else {
							return "\"" + e.getKey() + "\":\"" + e.getValue() + "\",";
						}
					}).
					collect(Collectors.joining("\n"));
			if (log.isDebugEnabled()) {
				log.debug("Vite properties: {}", json);
			}
			envJson = "window.__RUNTIME_CONFIG__ = {" + json + "}";
			contentType = MediaType.valueOf("text/javascript");
		} else if ("showall".equalsIgnoreCase(format)) {
			envJson = systemEnv.entrySet().stream().
					map(e -> e.getKey() + "=" + e.getValue()).
					collect(Collectors.joining("\n"));
		} else {
			envJson = "";
		}
		return ResponseEntity.
				ok().
				contentType(contentType).
				body(envJson);
	}

	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getAllProperties(Environment env) {
		Map<String, Object> props = new HashMap<>();
		if (env instanceof ConfigurableEnvironment) {
			for (PropertySource<?> propertySource : ((ConfigurableEnvironment) env).getPropertySources()) {
				if (propertySource instanceof EnumerablePropertySource) {
					for (String key: ((EnumerablePropertySource)propertySource).getPropertyNames()) {
						props.put(key, propertySource.getProperty(key));
					}
				}
			}
		}
		return props;
	}

	protected abstract boolean isReactAppMappedFrontProperty(String propertyName);
	protected abstract String getReactAppMappedFrontProperty(String propertyName);
	protected abstract boolean isViteMappedFrontProperty(String propertyName);
	protected abstract String getViteMappedFrontProperty(String propertyName);

}
