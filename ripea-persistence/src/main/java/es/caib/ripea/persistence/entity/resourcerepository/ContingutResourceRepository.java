package es.caib.ripea.persistence.entity.resourcerepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.ContingutResourceEntity;
import es.caib.ripea.service.intf.dto.ContingutTipusEnumDto;
import es.caib.ripea.service.intf.model.ContingutResource;

public interface ContingutResourceRepository extends BaseRepository<ContingutResourceEntity<ContingutResource>, Long> {

	public List<ContingutResourceEntity> findByPareId(Long pareId);
    List<ContingutResourceEntity<ContingutResource>> findAllByPareIdAndEsborratOrderByOrdreAsc(Long pareId, Integer esborrat);

    //Indica si el contingut té algun fill directe del tipus indicat, sense carregar les entitats.
    @Query("select case when count(c) > 0 then true else false end from ContingutResourceEntity c " +
    		"where c.pare.id = :pareId and c.tipus = :tipus")
    boolean existsFillByPareIdAndTipus(@Param("pareId") Long pareId, @Param("tipus") ContingutTipusEnumDto tipus);

    //Ids dels fills directes que no són del tipus indicat, sense carregar les entitats.
    @Query("select c.id from ContingutResourceEntity c where c.pare.id = :pareId and c.tipus <> :tipus")
    List<Long> findFillIdsByPareIdAndTipusNot(@Param("pareId") Long pareId, @Param("tipus") ContingutTipusEnumDto tipus);
}
