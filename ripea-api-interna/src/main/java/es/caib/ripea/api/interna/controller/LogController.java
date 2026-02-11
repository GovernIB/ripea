package es.caib.ripea.api.interna.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import es.caib.comanda.model.v1.log.FitxerContingut;
import es.caib.comanda.model.v1.log.FitxerInfo;
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

    @GetMapping("/{nom}")
    public FitxerContingut getFitxerByNom(@PathVariable("nom") String nom) {
        return logService.getFitxerByNom(nom);
    }

    @GetMapping("/{nomFitxer}/linies/{nLinies}")
    public List<String> llegitUltimesLinies(@PathVariable("nLinies") Long nLinies, @PathVariable("nomFitxer") String nomFitxer) {
        return logService.readLastNLines(nomFitxer, nLinies);
    }

    @GetMapping(value = "/stream/{filename}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogFile(@PathVariable String filename, HttpServletResponse response) throws IOException {
    	
        logService.tailLogFile(filename);

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
    }
}