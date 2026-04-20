package es.caib.ripea.service.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;

/**
 * Helper per a la gestió dels tipus documentals.
 */
@Component
public class TipusDocumentalHelper {

    private static final Logger logger = LoggerFactory.getLogger(TipusDocumentalHelper.class);
    private static final String TIPOS_DOCUMENTALES_JSON = "tiposDocumentales.json";

    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private TipusDocumentalRepository tipusDocumentalRepository;

    /**
     * Crea els tipus documentals per defecte per a una entitat nova,
     * llegint-los del fitxer tiposDocumentales.json del classpath.
     * Si un tipus documental (codi + entitat) ja existeix, s'omete.
     *
     * @param codiEntitat el codi de l'entitat per a la qual es creen els tipus documentals
     */
    public void crearTipusDocumentalsEntitat(String codiEntitat) {
        EntitatEntity entitat = entitatRepository.findByCodi(codiEntitat);
        if (entitat == null) {
            logger.error("No s'ha trobat l'entitat amb codi: {}", codiEntitat);
            return;
        }
        List<TipusDocumentalEntry> entrades = llegirTipusDocumentalsJson();
        List<TipusDocumentalEntity> nous = new ArrayList<>();
        for (TipusDocumentalEntry entrada : entrades) {
            if (tipusDocumentalRepository.findByCodiAndEntitat(entrada.codi, entitat) == null) {
                nous.add(TipusDocumentalEntity.getBuilder(
                        entrada.codi,
                        entrada.nom,
                        entitat,
                        entrada.nomCatala
                ).build());
            }
        }
        if (!nous.isEmpty()) {
            tipusDocumentalRepository.saveAll(nous);
        }
    }

    /**
     * Elimina tots els tipus documentals associats a una entitat.
     * S'ha de cridar abans d'eliminar l'entitat per evitar violació de FK.
     *
     * @param codiEntitat el codi de l'entitat els tipus documentals de la qual s'eliminen
     */
    public void deleteTipusDocumentalsEntitat(String codiEntitat) {
        tipusDocumentalRepository.deleteByEntitatCodi(codiEntitat);
    }

    private List<TipusDocumentalEntry> llegirTipusDocumentalsJson() {
        List<TipusDocumentalEntry> entrades = new ArrayList<>();
        InputStream is = getClass().getClassLoader().getResourceAsStream(TIPOS_DOCUMENTALES_JSON);
        if (is == null) {
            logger.error("No s'ha trobat el fitxer de tipus documentals: {}", TIPOS_DOCUMENTALES_JSON);
            return entrades;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);
            JsonNode items = root.path("results").get(0).path("items");
            for (JsonNode item : items) {
                TipusDocumentalEntry entrada = new TipusDocumentalEntry();
                entrada.codi = item.path("codi").asText();
                entrada.nom = item.path("nom").asText();
                entrada.nomCatala = item.path("nom_catala").asText();
                entrades.add(entrada);
            }
        } catch (IOException e) {
            logger.error("Error llegint el fitxer de tipus documentals: {}", TIPOS_DOCUMENTALES_JSON, e);
        }
        return entrades;
    }

    private static class TipusDocumentalEntry {
        String codi;
        String nom;
        String nomCatala;
    }
}
