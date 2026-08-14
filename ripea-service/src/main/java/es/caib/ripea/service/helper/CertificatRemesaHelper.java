package es.caib.ripea.service.helper;

import es.caib.ripea.persistence.entity.*;
import es.caib.ripea.service.intf.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.repository.DocumentRepository;
import es.caib.ripea.persistence.repository.MetaDocumentRepository;

import java.util.Date;


@Component
public class CertificatRemesaHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificatRemesaHelper.class);

    @Autowired private DocumentRepository documentRepository;
    @Autowired private MetaDocumentRepository metaDocumentRepository;
    @Autowired private DocumentNotificacioHelper documentNotificacioHelper;
    @Autowired private DocumentHelper documentHelper;

    /**
     * Incorpora com a documents de l'expedient tots els certificats pendents (un per interessat/destinatari) d'una notificació.
     *
     * @return Nombre de certificats creats.
     */
    public int crearDocumentsCertificatNotificacio(DocumentEnviamentInteressatEntity interessatEnviament) throws Exception {

        ExpedientEntity expedient = interessatEnviament.getNotificacio().getExpedient();
        int creats = 0;


        String nif = interessatEnviament.getInteressat() != null
            ? interessatEnviament.getInteressat().getDocumentNum().replace("/", "_")
            : null;

        String nifPerNom = nif != null ? nif : "SENSE_NIF";

        String nomFitxer = nomFitxerCertificat(expedient.getId(), interessatEnviament.getId(), nifPerNom);

        boolean jaExisteix = documentRepository
            .existsByExpedientIdAndFitxerNom(expedient.getId(), nomFitxer);
        if (jaExisteix) {
            return 0;
        }

        byte[] contingutCertificat = documentNotificacioHelper.getCertificacio(interessatEnviament.getId());
        if (contingutCertificat == null) {
            LOGGER.warn("No s'ha pogut descarregar el certificat de l'enviament {} (notificació {})",
                interessatEnviament.getId(), interessatEnviament.getId());
            return 0;
        }

        MetaDocumentEntity metaDocument = metaDocumentRepository
            .findByMetaExpedientAndCodi(
                expedient.getMetaExpedient(),
                MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi());

        DocumentDto document = new DocumentDto();
        document.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
        document.setNom("Justificant enviament notib destinatari " + nifPerNom);
        document.setData(new Date());
        document.setNtiOrgano(expedient.getNtiOrgano());
        document.setNtiOrigen(metaDocument.getNtiOrigen());
        document.setNtiEstadoElaboracion(metaDocument.getNtiEstadoElaboracion());
        document.setNtiTipoDocumental(metaDocument.getNtiTipoDocumental());
        document.setFitxerNom(nomFitxer);
        document.setFitxerContentType("application/pdf");
        document.setFitxerContingut(contingutCertificat);
        document.setFitxerTamany((long) contingutCertificat.length);
        document.setAmbFirma(true);
        document.setFirmaSeparada(false);

        MetaNodeDto metaNode = new MetaNodeDto();
        metaNode.setId(metaDocument.getId());
        document.setMetaNode(metaNode);

        documentHelper.crearDocument(
            expedient.getEntitat().getId(),
            document,
            expedient,
            true,
            true,
            true);

        creats++;
        LOGGER.info("Creat document de certificat {} per a l'expedient {}", nomFitxer, expedient.getId());

        return creats;
    }

    private String nomFitxerCertificat(Long expedientId, Long enviamentInteressatId, String nif) {
        return MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi()
            + "_" + expedientId + "_" + enviamentInteressatId + "_" + nif + ".pdf";
    }
}
