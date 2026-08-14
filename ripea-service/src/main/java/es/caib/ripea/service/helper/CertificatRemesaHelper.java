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
     * Incorpora com a document de l'expedient el certificat pendent d'un enviament (interessat/destinatari) d'una notificació.
     * Com a molt tracta un certificat, el de l'enviament indicat.
     *
     * @param interessatEnviament enviament de la notificació a processar.
     * @return missatge descriptiu de l'acció feta, que també queda registrat al log.
     * @throws Exception si el procediment no té el tipus de document per defecte.
     */
    public String crearDocumentsCertificatNotificacio(DocumentEnviamentInteressatEntity interessatEnviament) throws Exception {

        ExpedientEntity expedient = interessatEnviament.getNotificacio().getExpedient();

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
            return logIRetorna("El certificat de l'enviament " + interessatEnviament.getId()
                + " ja està incorporat al contingut de l'expedient " + expedient.getId() + ".");
        }

        MetaDocumentEntity metaDocument = metaDocumentRepository
            .findByMetaExpedientAndCodi(
                expedient.getMetaExpedient(),
                MetaDocumentPerDefecteEnumDto.NOTIB_JUSTIFICANT_RECEPCIO.getCodi());

        if (metaDocument == null) {
            throw new Exception("No s'ha trobat el MetaDocument per defecte NOTIB_JUSTIFICANT_RECEPCIO per al procediment "
                + expedient.getMetaExpedient().getCodi());
        }

        byte[] contingutCertificat = documentNotificacioHelper.getCertificacio(interessatEnviament.getId());
        if (contingutCertificat == null || contingutCertificat.length==0) {
            String missatge = "No s'ha pogut descarregar el certificat de l'enviament " + interessatEnviament.getId()
                + " (notificació " + interessatEnviament.getNotificacio().getId() + ").";
            LOGGER.warn(missatge);
            return missatge;
        }

        DocumentDto document = new DocumentDto();
        document.setDocumentTipus(DocumentTipusEnumDto.DIGITAL);
        document.setNom(nomUnicDinsExpedient(expedient, "Justificant enviament notib "+interessatEnviament.getId()+" destinatari " + nifPerNom));
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

        return logIRetorna("Incorporat el certificat de l'enviament " + interessatEnviament.getId()
            + " (notificació " + interessatEnviament.getNotificacio().getId() + ")"
            + " com a document " + nomFitxer + " al contingut de l'expedient " + expedient.getId() + ".");
    }

    private String logIRetorna(String missatge) {
        LOGGER.info(missatge);
        return missatge;
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
