package es.caib.ripea.service.helper;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.CarpetaEntity;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.service.helper.FilaInformeJustificantRegistre.AccioRipea;
import es.caib.ripea.service.intf.dto.MetaDocumentPerDefecteEnumDto;
import es.caib.ripea.service.intf.utils.RegistreJustificantUtils;


@Component
public class RegistreJustificantHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistreJustificantHelper.class);

    @Autowired private ExpedientPeticioRepository expedientPeticioRepository;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private MetaDocumentHelper metaDocumentHelper;
    @Autowired private ExpedientHelper expedientHelper;
    @Autowired private ContingutHelper contingutHelper;
    @Autowired private CacheHelper cacheHelper;

    /**
     * Tracta el justificant de registre d'una anotació al procés en segon pla (i a /userajax/initJustificantsRegistre),
     * que recorre totes les anotacions dels expedients oberts:
     * <ul>
     * <li>Si l'expedient ja té el justificant (document de tipus REGISTRE_JUSTIFICANT_ENTRADA amb el número de registre
     * de l'anotació, que no sigui un annex de l'anotació), no es torna a descarregar: es mou a la carpeta del registre
     * si no hi és ({@link ExpedientHelper#recolocarJustificantRegistre}), sigui on sigui. Si només n'hi ha d'esborrats,
     * no es fa res; si n'hi ha més d'un, no se'n mou cap.</li>
     * <li>Si la carpeta del registre ({@link ExpedientHelper#findCarpetaRegistre}) està esborrada, s'esborra també el
     * justificant i, si no n'hi ha, no es descarrega.</li>
     * <li>Si no el té, s'incorpora a la carpeta del registre ({@link #incorporarJustificantRegistreExpedient}).</li>
     * </ul>
     * Un error en recol·locar el justificant no es desa a l'anotació: l'error de l'anotació indica que li falta el
     * justificant, i l'usuari en podria afegir un altre.
     *
     * @param anotacioRegistreId identificador de l'anotació de registre a processar.
     * @param peticio anotació a processar.
     * @return missatge descriptiu de l'acció feta, que també queda registrat al log.
     * @throws Exception si l'anotació no existeix, si no s'ha pogut recol·locar o incorporar el justificant.
     */
    public String processarJustificantSegonPla(Long anotacioRegistreId, ExpedientPeticioEntity peticio) throws Exception {
        comprovarAnotacio(anotacioRegistreId, peticio);
        FilaInformeJustificantRegistre.actualitzar(fila -> fila.setAnotacio(peticio));
        String registreIdentificador = peticio.getRegistre().getIdentificador();
        if (peticio.getRegistre().getJustificantArxiuUuid() == null) {
            return logIRetorna("L'anotació " + registreIdentificador + " no té cap justificant de registre a l'arxiu.");
        }
        ExpedientEntity expedient = peticio.getExpedient();
        if (expedient == null) {
            return logIRetorna("L'anotació " + registreIdentificador + " no està associada a cap expedient.");
        }

        List<DocumentEntity> justificants = documentRepository.findByExpedientAndMetaDocumentCodiAndNumeroRegistre(
                expedient,
                MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi(),
                peticio.getIdentificador());
        List<DocumentEntity> actius = justificants.stream()
                .filter(justificant -> justificant.getEsborrat() == 0)
                .collect(Collectors.toList());
        if (!justificants.isEmpty() && actius.isEmpty()) {
            FilaInformeJustificantRegistre.actualitzar(fila -> {
                fila.setDocument(justificants.get(0), false);
                fila.setAccioRipea(AccioRipea.JUSTIFICANT_ESBORRAT);
            });
            return logIRetorna("El justificant de l'anotació " + registreIdentificador + " està esborrat a l'expedient "
                + expedient.getId() + ": no es torna a incorporar ni es mou.");
        }
        if (actius.size() > 1) {
            throw new Exception("L'expedient " + expedient.getId() + " té " + actius.size() + " justificants de l'anotació "
                + registreIdentificador + " (documents " + actius.stream().map(d -> String.valueOf(d.getId())).collect(Collectors.joining(", "))
                + "): no se'n mou cap.");
        }
        DocumentEntity justificant = actius.isEmpty() ? null : actius.get(0);
        if (justificant != null) {
            FilaInformeJustificantRegistre.actualitzar(fila -> fila.setDocument(justificant, false));
        }

        CarpetaEntity carpeta = expedientHelper.findCarpetaRegistre(expedient, peticio);
        if (carpeta != null && carpeta.getEsborrat() != 0) {
            FilaInformeJustificantRegistre.actualitzar(fila -> fila.setCarpeta(carpeta));
            if (justificant == null) {
                FilaInformeJustificantRegistre.actualitzar(fila -> fila.setAccioRipea(AccioRipea.CARPETA_ESBORRADA_NO_DESCARREGAT));
                return logIRetorna("La carpeta \"" + carpeta.getNom() + "\" de l'anotació " + registreIdentificador
                    + " està esborrada a l'expedient " + expedient.getId() + ": no s'hi incorpora el justificant.");
            }
            // Només a RIPEA, igual que el contingut de la carpeta quan es va esborrar
            contingutHelper.marcarEsborrat(expedient.getEntitat().getId(), justificant, null);
            if (justificant.isObligatori()) {
                cacheHelper.evictErrorsValidacioAndNotify(expedient.getId());
            }
            FilaInformeJustificantRegistre.actualitzar(fila -> fila.setAccioRipea(AccioRipea.CARPETA_ESBORRADA_JUSTIFICANT_ESBORRAT));
            return logIRetorna("La carpeta \"" + carpeta.getNom() + "\" de l'anotació " + registreIdentificador
                + " està esborrada a l'expedient " + expedient.getId() + ": s'ha esborrat també el justificant (document "
                + justificant.getId() + ").");
        }

        if (justificant != null) {
            String resultat = expedientHelper.recolocarJustificantRegistre(justificant, carpeta, peticio);
            // Dades finals del document: l'uuid pot haver canviat en moure'l a l'Arxiu
            FilaInformeJustificantRegistre.actualitzar(fila -> fila.setDocument(justificant, false));
            // El justificant ja hi és: s'esborra l'error d'un intent anterior, a la mateixa transacció que el moviment
            if (peticio.getRegistre().getJustificantError() != null) {
                peticio.getRegistre().updateJustificantError(null);
            }
            return logIRetorna("El justificant de l'anotació " + registreIdentificador + " ja està incorporat a l'expedient "
                + expedient.getId() + " (document " + justificant.getId() + "): " + resultat + ".");
        }

        return incorporarJustificantRegistreExpedient(anotacioRegistreId, peticio, null);
    }

    private void comprovarAnotacio(Long anotacioRegistreId, ExpedientPeticioEntity peticio) throws Exception {
        if (peticio == null) {
            throw new Exception("Anotació de registre no trobada amb id " + anotacioRegistreId);
        }
        if (peticio.getRegistre() == null) {
            throw new Exception("Anotació " + anotacioRegistreId + " no té cap registre asociat.");
        }
    }

    /**
     * Incorpora el justificant de registre d'una anotació com a document del seu expedient amb el tipus de
     * document indicat. Si no se n'indica cap, s'usa el REGISTRE_JUSTIFICANT_ENTRADA del procediment.
     *
     * Deixa l'error de la incorporació desat al registre de l'anotació perquè l'usuari la pugui tornar a
     * intentar, i el buida si el justificant s'incorpora o ja hi era.
     *
     * @param anotacioRegistreId identificador de l'anotació de registre a processar.
     * @param peticio anotació a processar.
     * @param metaDocumentId tipus de document del justificant, o null per usar el per defecte del procediment.
     * @return missatge descriptiu de l'acció feta, que també queda registrat al log.
     * @throws Exception si l'anotació no existeix o no s'ha pogut incorporar el justificant.
     */
    public String incorporarJustificantRegistreExpedient(Long anotacioRegistreId, ExpedientPeticioEntity peticio, Long metaDocumentId) throws Exception {

//        boolean incorporarJustificant = aplicacioService.propertyBooleanFindByKey(PropertyConfig.INCORPORAR_JUSTIFICANT, false);
//        
//        if (!incorporarJustificant) {
//            return logIRetorna("La configuració " + PropertyConfig.INCORPORAR_JUSTIFICANT
//                + " està desactivada: no s'incorpora el justificant de l'anotació " + anotacioRegistreId + ".");
//        }

        comprovarAnotacio(anotacioRegistreId, peticio);

        String justificantArxiuUuid = peticio.getRegistre().getJustificantArxiuUuid();
        String registreIdentificador = peticio.getRegistre().getIdentificador();
        
        if (justificantArxiuUuid == null) {
            return logIRetorna("L'anotació " + registreIdentificador + " no té cap justificant de registre a l'arxiu.");
        }

        ExpedientEntity expedient = peticio.getExpedient();
        if (expedient == null) {
            return logIRetorna("L'anotació " + registreIdentificador + " no està associada a cap expedient.");
        }

        String fitxerNom = RegistreJustificantUtils.nomFitxerJustificant(peticio.getId(), registreIdentificador);

        DocumentEntity documentMateixFitxer = documentRepository.findFirstByExpedientIdAndFitxerNomOrderByIdAsc(
                expedient.getId(),
                fitxerNom);

        if (documentMateixFitxer != null) {
            if (peticio.getRegistre().getJustificantError() != null) {
                expedientHelper.updateRegistreJustificantErrorNewTransaction(peticio.getId(), null);
            }
            FilaInformeJustificantRegistre.actualitzar(fila -> {
                fila.setDocument(documentMateixFitxer, false);
                fila.setAccioRipea(AccioRipea.JA_INCORPORAT_AMB_ALTRE_TIPUS);
            });
            return logIRetorna("El justificant de l'anotació " + peticio.getRegistre().getIdentificador()
                + " ja està incorporat al contingut de l'expedient " + expedient.getId() + " (document "
                + documentMateixFitxer.getId() + ", amb el mateix nom de fitxer).");
        }

        // Sense uuid de l'expedient no es pot consultar ni moure res a l'Arxiu: s'informa l'error sense cridar-lo.
        if (expedient.getArxiuUuid() == null) {
            String error = "L'expedient " + expedient.getNumero() + " (id " + expedient.getId()
                + ") no està guardat a l'arxiu digital: cal guardar-lo a l'arxiu abans d'incorporar-hi el justificant.";
            LOGGER.error("No s'incorpora el justificant de l'anotació " + registreIdentificador + ": " + error);
            expedientHelper.updateRegistreJustificantErrorNewTransaction(peticio.getId(), error);
            throw new Exception(error);
        }

        try {
            if (metaDocumentId == null) {
                metaDocumentId = metaDocumentHelper.getOrCreateMetaDocumentPerDefecte(
                    expedient.getMetaExpedient(),
                    MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA).getId();
            }

            // Si va bé, crearDocFromJustificantRegistreUuid buida l'error dins la seva mateixa transacció.
            expedientHelper.crearDocFromJustificantRegistreUuid(
                expedient.getId(),
                justificantArxiuUuid,
                peticio.getId(),
                metaDocumentId,
                fitxerNom,
                RegistreJustificantUtils.titolJustificant(registreIdentificador));
        } catch (Exception ex) {
            LOGGER.error("Error incorporant el justificant de l'anotació " + registreIdentificador + " a l'expedient " + expedient.getId(), ex);
            expedientHelper.updateRegistreJustificantErrorNewTransaction(peticio.getId(), ExceptionUtils.getStackTrace(ex));
            throw ex;
        }

        return logIRetorna("Incorporat el justificant de l'anotació " + anotacioRegistreId
            + (registreIdentificador != null ? " (registre " + registreIdentificador + ")" : "")
            + " com a document " + fitxerNom + " al contingut de l'expedient " + expedient.getId() + ".");
    }

    private String logIRetorna(String missatge) {
        LOGGER.info(missatge);
        return missatge;
    }
}
