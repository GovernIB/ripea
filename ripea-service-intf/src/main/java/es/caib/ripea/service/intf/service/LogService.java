package es.caib.ripea.service.intf.service;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.comanda.model.v1.log.FitxerContingut;
import es.caib.comanda.model.v1.log.FitxerInfo;

public interface LogService {

    @PreAuthorize("hasRole('IPA_COM')")
    List<FitxerInfo> llistarFitxers();

    @PreAuthorize("hasRole('IPA_COM')")
    FitxerContingut getFitxerByNom(String nom);

    @PreAuthorize("hasRole('IPA_COM')")
    void tailLogFile(String filePath);

    @PreAuthorize("hasRole('IPA_COM')")
    BlockingQueue<String> getQueue();

    @PreAuthorize("hasRole('IPA_COM')")
    List<String> readLastNLines(String nomFitxer, Long nLinies);
}
