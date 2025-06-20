package es.caib.ripea.back.base.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador per retornar la informació del fitxer META-INF/MANIFEST.MF.
 * 
 * @author Limit Tecnologies
 */
@Hidden
@RestController
public class PingController {

	@GetMapping("/ping")
	public ResponseEntity<Void> ping() {
		return ResponseEntity.ok().build();
	}

}
