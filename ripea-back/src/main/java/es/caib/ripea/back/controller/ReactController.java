package es.caib.ripea.back.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ReactController {

    private final ServletContext servletContext;

    @RequestMapping("/reactapp/**")
    public ResponseEntity<?> serveReact(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");

        try {
            // Intentem obrir el recurs
            InputStream resource = servletContext.getResourceAsStream(path);

            if (resource != null) {
                // Serveix el fitxer si existeix
                String mimeType = servletContext.getMimeType(path);
                MediaType mediaType = mimeType != null ? MediaType.parseMediaType(mimeType) : MediaType.APPLICATION_OCTET_STREAM;
                return ResponseEntity
                        .ok()
                        .contentType(mediaType)
                        .body(new InputStreamResource(resource));
            }

            // Si no existeix el fitxer, i és un recurs estàtic retornam un NOT FOUND
            String uri = request.getRequestURI();
            if (uri.matches(".*\\.(js|css|ico|png|jpg|svg|woff2?|map)$") || uri.endsWith("index.html")) {
                return ResponseEntity.notFound().build();
            }
            // En cas contrari, retornem index.html
            InputStream indexHtml = servletContext.getResourceAsStream("/reactapp/index.html");

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(new InputStreamResource(Objects.requireNonNull(indexHtml)));

        } catch (Exception e) {
            log.error("Error carregant recurs", e);
            return ResponseEntity.internalServerError().body("Error carregant recurs");
        }
    }
}
