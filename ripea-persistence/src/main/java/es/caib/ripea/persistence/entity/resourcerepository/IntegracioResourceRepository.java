package es.caib.ripea.persistence.entity.resourcerepository;

import es.caib.ripea.persistence.base.repository.BaseRepository;
import es.caib.ripea.persistence.entity.resourceentity.IntegracioResourceEntity;
import es.caib.ripea.service.intf.dto.IntegracioCodiEnum;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Date;
import java.util.List;

public interface IntegracioResourceRepository extends BaseRepository<IntegracioResourceEntity, Long> {

	List<IntegracioResourceEntity> findByCodiOrderByDataDesc(IntegracioCodiEnum codi);

	@Modifying
	int deleteByDataBefore(Date data);

}
