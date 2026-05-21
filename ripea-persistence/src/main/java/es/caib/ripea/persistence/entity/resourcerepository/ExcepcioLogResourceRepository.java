package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.ExcepcioLogResourceEntity;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Date;

public interface ExcepcioLogResourceRepository extends BaseRepository<ExcepcioLogResourceEntity, Long> {

	@Modifying
	int deleteByDataBefore(Date data);

}
