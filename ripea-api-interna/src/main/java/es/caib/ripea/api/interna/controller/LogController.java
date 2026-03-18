package es.caib.ripea.api.interna.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.ms.log.helper.LogFileStream;
import es.caib.ripea.api.interna.config.BaseApiInternaSecurityConfig;
import es.caib.ripea.service.intf.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(BaseApiInternaSecurityConfig.VERSIO_API_COMANDA+"/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping
    public List<FitxerInfo> llistarFitxers() {
        return logService.llistarFitxers();
    }

    @GetMapping("/{nomFitxer}")
    public FitxerContingut getFitxerByNom(@PathVariable("nomFitxer") String nomFitxer) {
        return logService.getFitxerByNom(nomFitxer);
    }

    @GetMapping("/{nomFitxer}/linies/{nLinies}")
    public List<String> llegitUltimesLinies(@PathVariable("nLinies") Long nLinies, @PathVariable("nomFitxer") String nomFitxer) {
        return logService.readLastNLines(nomFitxer, nLinies);
    }

    @GetMapping(value = "/{nomFitxer}/directe", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> streamLogFile(@PathVariable String nomFitxer, HttpServletResponse response) throws IOException {
    	
    	LogFileStream file = logService.tailLogFile(nomFitxer);
    	
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fitxer no trobat");
        }

        StreamingResponseBody body = outputStream -> {
            try (InputStream in = file.getInputStream()) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            }
        };

        MediaType mediaType;
        try {
            mediaType = (file.getContentType() != null && !file.getContentType().isBlank())
                    ? MediaType.parseMediaType(file.getContentType())
                    : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(file.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(file.getFileName())
                                .build()
                                .toString())
                .body(body);
    	
        /*
    	logService.tailLogFile(nomFitxer);

        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                while (true) {
                    String line = logService.getQueue().take(); // Block until a line is available
                    emitter.send(line);
                }
            } catch (Exception e) {
                log.error("Error sending data to client: " + e.getMessage(), e);
                emitter.completeWithError(e);  // Complete emitter on error
            } finally {
                emitter.complete();  // Ensure completion happens if exiting
            }
        }).start();

        // Ensure proper cleanup on completion/timing out
        emitter.onCompletion(() -> log.info("Emitter completed."));
        emitter.onTimeout(() -> {
            log.info("Emitter timed out. Attempting to notify client to reconnect.");
            // Notify client (you could send a periodic message or keep this simple)
            try {
                emitter.send("timeout"); // Optional: Signal the client to reconnect
            } catch (IOException e) {
                log.error("Error notifying client of timeout: " + e.getMessage());
            }
        });

        return emitter; // Returns the emitter and allows the client to receive updates
        */
    }
}