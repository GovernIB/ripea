package es.caib.ripea.service.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.ripea.persistence.entity.EntitatEntity;
import es.caib.ripea.persistence.entity.TipusDocumentalEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.EntitatRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.persistence.repository.TipusDocumentalRepository;
import es.caib.ripea.service.intf.dto.TipusDocumentalDto;
import es.caib.ripea.service.intf.exception.ValidationException;
import es.caib.ripea.service.intf.utils.Utils;

/**
 * Helper per a la gestió dels tipus documentals.
 *
 * <p>Els tipus de document (IPA_METADOCUMENT) i els documents (IPA_DOCUMENT) guarden el
 * <b>codi</b> del tipus documental a la columna NTI_TIPDOC, sense FK cap a
 * IPA_TIPUS_DOCUMENTAL. Per això el nom s'ha de resoldre de manera tolerant (el codi pot no
 * existir a la taula) i no es pot esborrar ni canviar el codi d'un tipus documental en ús.</p>
 */
@Component
public class TipusDocumentalHelper {

    private static final Logger logger = LoggerFactory.getLogger(TipusDocumentalHelper.class);
    private static final String TIPOS_DOCUMENTALES_JSON = "tiposDocumentales.json";
    private static final String MESSAGE_KEY_ENUM_NTI = "document.nti.tipdoc.enum.";

    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private TipusDocumentalRepository tipusDocumentalRepository;
    @Autowired
    private MetaDocumentRepository metaDocumentRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private MessageHelper messageHelper;
    @Autowired
    private PluginHelper pluginHelper;

    /**
     * Retorna el nom a mostrar del tipus documental amb el codi especificat. Mai retorna null
     * si el codi té valor, ja que el codi pot no existir a la taula de tipus documentals (p.ex.
     * perquè s'ha esborrat). L'ordre de resolució és:
     * <ol>
     * <li>Tipus documental de l'entitat (actiu o no), amb el nom en l'idioma de l'usuari.</li>
     * <li>Nom estàndard NTI del codi (document.nti.tipdoc.enum.*), si n'hi ha. Va abans del plugin
     * perquè un tipus addicional mai és un codi NTI estàndard i així s'evita la crida externa.</li>
     * <li>Tipus documental addicional del plugin d'arxiu, només si {@code consultarAddicionals}.</li>
     * <li>Text genèric de tipus documental no disponible.</li>
     * </ol>
     *
     * @param codi el codi del tipus documental.
     * @param entitatId l'id de l'entitat del tipus de document o document. Si és null es pren el primer tipus documental amb el codi.
     * @param consultarAddicionals si s'han de consultar els tipus addicionals al plugin d'arxiu. Implica una
     *            crida externa: no s'ha d'activar en conversions de llistats massius.
     * @return el nom del tipus documental o null si el codi és buit.
     */
    public String getNomTipusDocumental(String codi, Long entitatId, boolean consultarAddicionals) {
        if (Utils.isEmpty(codi)) {
            return null;
        }
        TipusDocumentalEntity tipusDocumental = findByCodi(codi, entitatId);
        if (tipusDocumental != null) {
            return getNom(tipusDocumental);
        }
        String nomNti = messageHelper.getMessage(MESSAGE_KEY_ENUM_NTI + codi);
        if (nomNti != null && !nomNti.startsWith("???")) {
            return nomNti;
        }
        if (consultarAddicionals) {
            String nomAddicional = getNomTipusAddicional(codi);
            if (nomAddicional != null) {
                return nomAddicional;
            }
        }
        return messageHelper.getMessage("tipusdocumental.nom.no.disponible");
    }

    /**
     * Comprova que el tipus documental es pot esborrar: no ha d'estar assignat a cap tipus de
     * document ni a cap document de l'entitat, ja que la relació és pel codi i quedarien orfes.
     *
     * @throws ValidationException si el tipus documental està en ús.
     */
    public void comprovarEsborrable(Long entitatId, String codi) throws ValidationException {
        if (isUtilitzat(entitatId, codi)) {
            throw new ValidationException(messageHelper.getMessage(
                    "tipusdocumental.esborrar.error.utilitzat",
                    new Object[] { codi }));
        }
    }

    /**
     * Comprova que es pot canviar el codi del tipus documental: si el codi anterior està assignat a
     * tipus de document o documents, el canvi els deixaria orfes.
     *
     * @throws ValidationException si el codi canvia i el codi anterior està en ús.
     */
    public void comprovarCanviCodi(Long entitatId, String codiAnterior, String codiNou) throws ValidationException {
        if (!Objects.equals(codiAnterior, codiNou) && isUtilitzat(entitatId, codiAnterior)) {
            throw new ValidationException(messageHelper.getMessage(
                    "tipusdocumental.modificar.error.codi.utilitzat",
                    new Object[] { codiAnterior }));
        }
    }

    /**
     * Comprova que el tipus documental es pot assignar a un tipus de document creat o modificat per
     * l'usuari: no es permet assignar un tipus documental desactivat, però sí conservar el que ja
     * tenia assignat. Els codis que no són a la taula (p.ex. tipus addicionals del plugin d'arxiu)
     * no es validen aquí.
     *
     * @param codiAnterior el codi que tenia assignat el tipus de document (null si es crea).
     * @throws ValidationException si s'assigna un tipus documental desactivat.
     */
    public void comprovarAssignable(Long entitatId, String codiAnterior, String codiNou) throws ValidationException {
        if (Utils.isEmpty(codiNou) || Objects.equals(codiAnterior, codiNou)) {
            return;
        }
        TipusDocumentalEntity tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitatId(codiNou, entitatId);
        if (tipusDocumental != null && !tipusDocumental.isActiu()) {
            throw new ValidationException(messageHelper.getMessage(
                    "tipusdocumental.assignar.error.inactiu",
                    new Object[] { codiNou }));
        }
    }

    private boolean isUtilitzat(Long entitatId, String codi) {
        return metaDocumentRepository.existsByEntitatIdAndNtiTipoDocumental(entitatId, codi)
                || documentRepository.existsByEntitatIdAndNtiTipoDocumental(entitatId, codi);
    }

    private TipusDocumentalEntity findByCodi(String codi, Long entitatId) {
        if (entitatId != null) {
            return tipusDocumentalRepository.findByCodiAndEntitatId(codi, entitatId);
        }
        List<TipusDocumentalEntity> tipusDocumentals = tipusDocumentalRepository.findByCodi(codi);
        return Utils.isNotEmpty(tipusDocumentals) ? tipusDocumentals.get(0) : null;
    }

    private String getNom(TipusDocumentalEntity tipusDocumental) {
        if ("ca".equals(LocaleContextHolder.getLocale().getLanguage()) && Utils.isNotEmpty(tipusDocumental.getNomCatala())) {
            return tipusDocumental.getNomCatala();
        }
        return tipusDocumental.getNomEspanyol();
    }

    private String getNomTipusAddicional(String codi) {
        try {
            List<TipusDocumentalDto> addicionals = pluginHelper.documentTipusAddicionals();
            if (addicionals != null) {
                for (TipusDocumentalDto addicional : addicionals) {
                    if (codi.equals(addicional.getCodi())) {
                        return addicional.getNom();
                    }
                }
            }
        } catch (Exception ex) {
            // La consulta al plugin només serveix per mostrar un nom: si falla no ha d'impedir la consulta.
            logger.warn("No s'han pogut consultar els tipus documentals addicionals del plugin d'arxiu (codi={}): {}", codi, ex.getMessage());
        }
        return null;
    }

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
