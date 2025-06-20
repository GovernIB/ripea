package es.caib.ripea.back.base.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

/**
 * Controlador per retornar la informació del fitxer META-INF/MANIFEST.MF.
 * 
 * @author Limit Tecnologies
 */
@Hidden
@RestController
public class ManifestController {

	@Autowired
	private ServletContext servletContext;

	@GetMapping("/manifest")
	public ResponseEntity<String> manifest() throws IOException {
		Map<String, Object> manifestProps = getManifestProperties();
		MediaType contentType = MediaType.valueOf("text/javascript"); // MediaType.TEXT_PLAIN;
		String json = manifestProps.entrySet().stream().
				filter(e -> !e.getKey().equalsIgnoreCase("Class-Path")).
				map(e -> "\"" + e.getKey() + "\":\"" + e.getValue() + "\",").
				collect(Collectors.joining("\n"));
		String response = "window.__MANIFEST__ = {\n" + json + "\n}";
		return ResponseEntity.
				ok().
				contentType(contentType).
				body(response);
	}

	public Map<String, Object> getManifestProperties() throws IOException {
		InputStream manifestIs = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF");
		if (manifestIs != null) {
			Manifest manifest = new Manifest(manifestIs);
			Attributes attributes = manifest.getMainAttributes();
			Map<String, Object> props = attributes.keySet().stream().collect(Collectors.toMap(
					k -> k.toString(),
					k -> attributes.get(k)));
			return props;
		} else {
			return Collections.emptyMap();
		}
	}

}
