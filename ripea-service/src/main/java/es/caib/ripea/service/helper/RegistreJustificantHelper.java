package es.caib.ripea.service.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.ExpedientPeticioEntity;
import es.caib.ripea.persistence.entity.MetaDocumentEntity;
import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.ExpedientPeticioRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;
import es.caib.ripea.service.intf.dto.MetaDocumentPerDefecteEnumDto;
import es.caib.ripea.service.intf.utils.RegistreJustificantUtils;


@Component
public class RegistreJustificantHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistreJustificantHelper.class);

    @Autowired private ExpedientPeticioRepository expedientPeticioRepository;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private MetaDocumentRepository metaDocumentRepository;
    @Autowired private ExpedientHelper expedientHelper;

    /**
     * Incorpora el justificant de registre d'una anotació com a document del seu expedient.
     * Com a molt tracta un justificant, el de l'anotació indicada.
     *
     * @param anotacioRegistreId identificador de l'anotació de registre a processar.
     * @return missatge descriptiu de l'acció feta, que també queda registrat al log.
     * @throws Exception si l'anotació no existeix o el procediment no té el tipus de document per defecte.
     */
    public String incorporarJustificantsRegistreExpedient(Long anotacioRegistreId) throws Exception {

//        boolean incorporarJustificant = aplicacioService.propertyBooleanFindByKey(PropertyConfig.INCORPORAR_JUSTIFICANT, false);
//        
//        if (!incorporarJustificant) {
//            return logIRetorna("La configuració " + PropertyConfig.INCORPORAR_JUSTIFICANT
//                + " està desactivada: no s'incorpora el justificant de l'anotació " + anotacioRegistreId + ".");
//        }

        ExpedientPeticioEntity peticio = expedientPeticioRepository.findById(anotacioRegistreId)
            .orElseThrow(() -> new Exception("Anotació de registre no trobada amb id " + anotacioRegistreId));

        String justificantArxiuUuid = peticio.getRegistre().getJustificantArxiuUuid();
        if (justificantArxiuUuid == null) {
            return logIRetorna("L'anotació " + anotacioRegistreId + " no té cap justificant de registre a l'arxiu.");
        }

        ExpedientEntity expedient = peticio.getExpedient();
        if (expedient == null) {
            return logIRetorna("L'anotació " + anotacioRegistreId + " no està associada a cap expedient.");
        }

        String registreIdentificador = peticio.getRegistre().getIdentificador();
        String fitxerNom = RegistreJustificantUtils.nomFitxerJustificant(peticio.getId(), registreIdentificador);

        boolean jaExisteix = documentRepository.existsByExpedientIdAndFitxerNom(expedient.getId(), fitxerNom);
        
        if (jaExisteix) {
            return logIRetorna("El justificant de l'anotació " + anotacioRegistreId
                + " ja està incorporat al contingut de l'expedient " + expedient.getId() + ".");
        }

        MetaDocumentEntity metaDocument = metaDocumentRepository.findByMetaExpedientAndCodi(
            expedient.getMetaExpedient(),
            MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi());

        if (metaDocument == null) {
            throw new Exception("No s'ha trobat el MetaDocument per defecte REGISTRE_JUSTIFICANT_ENTRADA per al procediment "
                + expedient.getMetaExpedient().getCodi());
        }

        expedientHelper.crearDocFromUuid(
            expedient.getId(),
            justificantArxiuUuid,
            peticio.getId(),
            metaDocument.getId(),
            fitxerNom,
            RegistreJustificantUtils.titolJustificant(registreIdentificador));

        return logIRetorna("Incorporat el justificant de l'anotació " + anotacioRegistreId
            + (registreIdentificador != null ? " (registre " + registreIdentificador + ")" : "")
            + " com a document " + fitxerNom + " al contingut de l'expedient " + expedient.getId() + ".");
    }

    private String logIRetorna(String missatge) {
        LOGGER.info(missatge);
        return missatge;
    }
}
