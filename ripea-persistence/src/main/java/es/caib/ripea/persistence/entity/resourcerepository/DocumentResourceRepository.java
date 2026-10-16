package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import es.caib.ripea.persistence.entity.resourceentity.DocumentResourceEntity;
import es.caib.ripea.persistence.entity.resourceentity.ExpedientResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentResourceRepository extends BaseRepository<DocumentResourceEntity, Long> {

    List<DocumentResourceEntity> findAllByPareId(Long pareId);
	List<DocumentResourceEntity> findAllByPareIdOrderByOrdreAsc(Long pareId);
    List<DocumentResourceEntity> findByExpedientAndEsborrat(ExpedientResourceEntity expedient, int esborrat);

    @Query("select case when count(d) > 0 then true else false end " +
            "from DocumentResourceEntity d " +
            "where d.expedient = :expedient " +
            "and d.arxiuEstat = es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto.DEFINITIU")
    Boolean expedientHasDocumentsDefinitius(@Param("expedient") ExpedientResourceEntity expedient);

	@Query(	"select case when (count(c) > 0) then true else false end " +
			"from " +
			"    DocumentResourceEntity c " +
			"where " +
			"    c.expedient.id = :expedientId "  +
			"and c.documentTipus = 0 " + //= DIGITAL
			"and c.esborrat = 0 " +
			"and c.estat = 0 ")
	Boolean hasFillsEsborranys(@Param("expedientId") Long expedientId);

    @Query(	"select " +
            "    c " +
            "from " +
            "    DocumentResourceEntity c " +
            "where " +
            "    c.expedient = :expedient " +
            "and c.esborrat = 0 " +
            "and (c.estat = es.caib.ripea.service.intf.dto.DocumentEstatEnumDto.FIRMA_PENDENT " +
            "	or c.estat = es.caib.ripea.service.intf.dto.DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA " +
            "	or c.estat = es.caib.ripea.service.intf.dto.DocumentEstatEnumDto.FIRMA_PARCIAL)")
    List<DocumentResourceEntity> findEnProccessDeFirma(
            @Param("expedient") ExpedientResourceEntity expedient);

    @Query(	"select " +
            "    d " +
            "from " +
            "    DocumentResourceEntity d " +
            "where " +
            "    d.expedient = :expedient " +
            "and d.esborrat = 0 " +
            "and d.gesDocFirmatId is not null")
    List<DocumentResourceEntity> findDocumentsDePortafirmesNoCustodiats(
            @Param("expedient") ExpedientResourceEntity expedient);

    @Query(	"select " +
            "    d " +
            "from " +
            "    DocumentResourceEntity d " +
            "where " +
            "    d.expedient = :expedient " +
            "and d.esborrat = 0 " +
            "and d.arxiuUuid = null " +
            "and d.arxiuReintents < :arxiuMaxReintentsDocuments")
    List<DocumentResourceEntity> findDocumentsPendentsReintentsArxiu(
            @Param("expedient") ExpedientResourceEntity expedient,
            @Param("arxiuMaxReintentsDocuments") int arxiuMaxReintentsDocuments);

    int countAllByExpedientIdAndEsborrat(Long expedientId, int esborrat);

    // Versions per lots de les consultes anteriors per a la perspectiva BASIC del llistat d'expedients: retornen
    // els ids dels expedients que compleixen la condició (amb el mateix criteri que la consulta individual).
    // Màxim 1000 ids per crida (límit IN d'Oracle).

    @Query("select distinct d.expedient.id from DocumentResourceEntity d " +
            "where d.expedient.id in (:expedientIds) and d.esborrat = 0")
    List<Long> findExpedientIdsAmbDocuments(@Param("expedientIds") List<Long> expedientIds);

    @Query("select distinct d.expedient.id from DocumentResourceEntity d " +
            "where d.expedient.id in (:expedientIds) " +
            "and d.arxiuEstat = es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto.DEFINITIU")
    List<Long> findExpedientIdsAmbDocumentsDefinitius(@Param("expedientIds") List<Long> expedientIds);

    @Query("select distinct c.expedient.id from DocumentResourceEntity c " +
            "where c.expedient.id in (:expedientIds) " +
            "and c.documentTipus = 0 " + //= DIGITAL
            "and c.esborrat = 0 " +
            "and c.estat = 0 ")
    List<Long> findExpedientIdsAmbFillsEsborranys(@Param("expedientIds") List<Long> expedientIds);

    @Query("select distinct c.expedient.id from DocumentResourceEntity c " +
            "where c.expedient.id in (:expedientIds) " +
            "and c.esborrat = 0 " +
            "and (c.estat = es.caib.ripea.service.intf.dto.DocumentEstatEnumDto.FIRMA_PENDENT " +
            "	or c.estat = es.caib.ripea.service.intf.dto.DocumentEstatEnumDto.FIRMA_PENDENT_VIAFIRMA " +
            "	or c.estat = es.caib.ripea.service.intf.dto.DocumentEstatEnumDto.FIRMA_PARCIAL)")
    List<Long> findExpedientIdsAmbDocumentsEnProcesDeFirma(@Param("expedientIds") List<Long> expedientIds);

    @Query("select distinct d.expedient.id from DocumentResourceEntity d " +
            "where d.expedient.id in (:expedientIds) " +
            "and d.esborrat = 0 " +
            "and d.gesDocFirmatId is not null")
    List<Long> findExpedientIdsAmbDocumentsDePortafirmesNoCustodiats(@Param("expedientIds") List<Long> expedientIds);

    @Query("select distinct d.expedient.id from DocumentResourceEntity d " +
            "where d.expedient.id in (:expedientIds) " +
            "and d.esborrat = 0 " +
            "and d.arxiuUuid = null " +
            "and d.arxiuReintents < :arxiuMaxReintentsDocuments")
    List<Long> findExpedientIdsAmbDocumentsPendentsReintentsArxiu(
            @Param("expedientIds") List<Long> expedientIds,
            @Param("arxiuMaxReintentsDocuments") int arxiuMaxReintentsDocuments);
}
