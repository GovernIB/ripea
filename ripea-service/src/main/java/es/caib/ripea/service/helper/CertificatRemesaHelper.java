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
    @Autowired private ContingutHelper contingutHelper;

    /**
     * Incorpora com a documents de l'expedient tots els certificats pendents (un per interessat/destinatari) d'una notificació.
     *
     * @return Nombre de certificats creats.
     */
    public int crearDocumentsCertificatNotificacio(DocumentEnviamentInteressatEntity interessatEnviament) throws Exception {

        ExpedientEntity expedient = interessatEnviament.getNotificacio().getExpedient();
        int creats = 0;


        String nif = interessatEnviament.getInteressat() != null
            ? interessatEnviament.getInteressat().getDocumentNum()
            : null;

        String nifPerNom = nif != null && !nif.trim().isEmpty()
            ? nif.trim().replace("/", "_")
            : "SENSE_NIF";

        String nomFitxer = nomFitxerCertificat(expedient.getId(), interessatEnviament.getId(), nifPerNom);

        boolean jaExisteix = documentRepository
            .existsByExpedientIdAndFitxerNom(expedient.getId(), nomFitxer);
        if (jaExisteix) {
            return 0;
        }

        MetaDocumentEntity metaDocument = metaDocumentRepository
            .findByMetaExpedientAndCodi(
                expedient.getMetaExpedient(),
                MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi());

        if (metaDocument == null) {
            throw new Exception("No s'ha trobat el MetaDocument per defecte NOTIB_JUSTIFICANT_RECEPCIO per al metaexpedient "
                + expedient.getMetaExpedient().getId());
        }

        byte[] contingutCertificat = documentNotificacioHelper.getCertificacio(interessatEnviament.getId());
        if (contingutCertificat == null) {
            LOGGER.warn("No s'ha pogut descarregar el certificat de l'enviament {} (notificació {})",
                interessatEnviament.getId(), interessatEnviament.getNotificacio().getId());
            return 0;
        }

        DocumentDto document = new DocumentDto();
        document.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
        document.setNom(nomUnicDinsExpedient(expedient, "Justificant enviament notib destinatari " + nifPerNom));
        document.setData(interessatEnviament.getEnviamentCertificacioData() != null
            ? interessatEnviament.getEnviamentCertificacioData()
            : new Date());
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

    /**
     * Retorna un nom de document lliure dins l'expedient. El nom del certificat només
     * depèn del NIF del destinatari, així que un expedient amb diverses remeses al mateix
     * interessat repetiria el nom i {@code documentHelper.crearDocument} llançaria
     * ContingutNotUniqueException; en aquest cas s'hi afegeix un comptador.
     */
    private String nomUnicDinsExpedient(ExpedientEntity expedient, String nomBase) {
        String nom = nomBase;
        int comptador = 1;
        while (contingutHelper.checkUniqueContraint(
                nom,
                expedient,
                expedient.getEntitat(),
                ContingutTipusEnumDto.DOCUMENT) > 0) {
            comptador++;
            nom = nomBase + " (" + comptador + ")";
        }
        return nom;
    }

    private String nomFitxerCertificat(Long expedientId, Long enviamentInteressatId, String nif) {
        return MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi()
            + "_" + expedientId + "_" + enviamentInteressatId + "_" + nif + ".pdf";
    }
}
