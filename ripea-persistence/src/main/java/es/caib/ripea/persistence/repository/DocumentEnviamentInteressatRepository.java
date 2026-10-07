package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.DocumentNotificacioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.DocumentEnviamentInteressatEntity;

import java.util.List;

@Component
public interface DocumentEnviamentInteressatRepository extends JpaRepository<DocumentEnviamentInteressatEntity, Long> {


	@Query(	"from" +
			"    DocumentEnviamentInteressatEntity e "
			+ "where "
			+ "	 e.notificacio.notificacioIdentificador = :notificacioIdentificador " +
			"and e.enviamentReferencia = :enviamentReferencia")
	DocumentEnviamentInteressatEntity findByIdentificadorIReferencia(
			@Param("notificacioIdentificador") String notificacioIdentificador,
			@Param("enviamentReferencia") String enviamentReferencia);

	@Modifying
 	@Query(value = "UPDATE IPA_DOCUMENT_ENVIAMENT " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoriaDocEnv(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

	@Modifying
 	@Query(value = "UPDATE IPA_DOCUMENT_ENVIAMENT_INTER " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
	public int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

    @Query("SELECT dei FROM DocumentEnviamentInteressatEntity dei "
        + "WHERE dei.notificacio.expedient.id = :expedientId "
        + "AND dei.enviamentCertificacioData IS NOT NULL")
    List<DocumentEnviamentInteressatEntity> findAmbCertificatByExpedientId(@Param("expedientId") Long expedientId);

    /**
     * Obté els IDs dels enviaments d'expedients oberts i no esborrats que tenen certificació i encara no tenen
     * el certificat incorporat com a document, dels més recents als més antics, amb id inferior a darrerId (el
     * darrer element tractat pel procés en segon pla, o Long.MAX_VALUE per obtenir-los tots).
     *
     * El certificat es considera ja incorporat si l'expedient té un document (esborrat o no, igual que la
     * comprovació de CertificatRemesaHelper) el nom de fitxer del qual comença per
     * &lt;prefixFitxer&gt;&lt;expedientId&gt;_&lt;enviamentId&gt;_ (veure CertificatRemesaHelper.nomFitxerCertificat).
     * Es compara el prefix exacte i no amb LIKE perquè '_' hi és un comodí: l'enviament 345 casaria amb el
     * fitxer de l'enviament 3456 del mateix expedient. El NIF final no es compara: no afecta la identificació
     * de l'enviament i pot haver canviat des que es va crear el document.
     *
     * @param prefixFitxer codi del tipus de document NOTIB_JUSTIFICANT_RECEPCIO seguit de '_'.
     * @param darrerId només es retornen els enviaments amb id inferior.
     */
    @Query("SELECT n.id FROM DocumentEnviamentInteressatEntity n " +
        "WHERE n.id < :darrerId " +
        "AND n.notificacio.expedient.estat = es.caib.ripea.service.intf.dto.ExpedientEstatEnumDto.OBERT " +
        "AND n.notificacio.expedient.esborrat = 0 " +
        "AND n.enviamentCertificacioData IS NOT NULL " +
        "AND NOT EXISTS (" +
        "    SELECT 1 FROM DocumentEntity d " +
        "    WHERE d.expedient = n.notificacio.expedient " +
        "    AND SUBSTRING(d.fitxerNom, 1, LENGTH(CONCAT(:prefixFitxer, str(n.notificacio.expedient.id), '_', str(n.id), '_'))) " +
        "        = CONCAT(:prefixFitxer, str(n.notificacio.expedient.id), '_', str(n.id), '_')" +
        ") " +
        "ORDER BY n.id DESC")
    List<Long> findIdsAmbCertificatPendentIncorporar(
            @Param("prefixFitxer") String prefixFitxer,
            @Param("darrerId") Long darrerId);
}
