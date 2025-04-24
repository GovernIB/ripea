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
    List<DocumentResourceEntity> findByExpedientAndEsborrat(ExpedientResourceEntity expedient, int esborrat);

    @Query("select case when count(d) > 0 then true else false end " +
            "from DocumentResourceEntity d " +
            "where d.expedient = :expedient " +
            "and d.arxiuEstat = es.caib.ripea.service.intf.dto.ArxiuEstatEnumDto.DEFINITIU")
    Boolean expedientHasDocumentsDefinitius(@Param("expedient") ExpedientResourceEntity expedient);

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
}