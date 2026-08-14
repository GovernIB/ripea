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
import es.caib.ripea.service.intf.config.PropertyConfig;
import es.caib.ripea.service.intf.dto.MetaDocumentPerDefecteEnumDto;
import es.caib.ripea.service.intf.service.AplicacioService;


@Component
public class RegistreJustificantHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistreJustificantHelper.class);

    @Autowired private ExpedientPeticioRepository expedientPeticioRepository;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private MetaDocumentRepository metaDocumentRepository;
    @Autowired private AplicacioService aplicacioService;
    @Autowired private ExpedientHelper expedientHelper;

    public int incorporarJustificantsRegistreExpedient(Long peticioId) throws Exception {

        boolean incorporarJustificant = aplicacioService
            .propertyBooleanFindByKey(PropertyConfig.INCORPORAR_JUSTIFICANT, false);
        if (!incorporarJustificant) {
            LOGGER.info("La configuració {} està desactivada. No s'incorporen justificants per al registre {}",
                PropertyConfig.INCORPORAR_JUSTIFICANT, peticioId);
            return 0;
        }

        ExpedientPeticioEntity peticio = expedientPeticioRepository.findById(peticioId)
            .orElseThrow(() -> new Exception("Petició d'expedient no trobada amb id " + peticioId));

        String justificantArxiuUuid = peticio.getRegistre().getJustificantArxiuUuid();
        if (justificantArxiuUuid == null) {
            return 0;
        }

        ExpedientEntity expedient = peticio.getExpedient();
        if (expedient == null) {
            return 0;
        }

        String registreIdentificador = peticio.getRegistre().getIdentificador();
        String identificadorPerNom = registreIdentificador != null
            ? registreIdentificador.replace("/", "_")
            : String.valueOf(peticio.getId());

        String fitxerNom = nomFitxerJustificant(peticio.getId(), identificadorPerNom);

        boolean jaExisteix = documentRepository
            .existsByExpedientIdAndFitxerNom(expedient.getId(), fitxerNom);
        if (jaExisteix) {
            return 0;
        }

        MetaDocumentEntity metaDocument = metaDocumentRepository.findByMetaExpedientAndCodi(
            expedient.getMetaExpedient(),
            MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi());

        if (metaDocument == null) {
            throw new Exception("No s'ha trobat el MetaDocument per defecte REGISTRE_JUSTIFICANT_ENTRADA per al metaexpedient "
                + expedient.getMetaExpedient().getId());
        }

        expedientHelper.crearDocFromUuid(
            expedient.getId(),
            justificantArxiuUuid,
            peticio.getId(),
            metaDocument.getId(),
            fitxerNom,
            "Justificant del registre " + registreIdentificador);

        LOGGER.info("Creat document de justificant de registre {} per a l'expedient {}", fitxerNom, expedient.getId());
        return 1;
    }

    // Nom del fitxer: TIPUSDOC_<idpeticio>_<registre.identificador amb / -> _>.pdf
    private String nomFitxerJustificant(Long peticioId, String identificadorPerNom) {
        return MetaDocumentPerDefecteEnumDto.REGISTRE_JUSTIFICANT_ENTRADA.getCodi()
            + "_" + peticioId + "_" + identificadorPerNom + ".pdf";
    }
}
