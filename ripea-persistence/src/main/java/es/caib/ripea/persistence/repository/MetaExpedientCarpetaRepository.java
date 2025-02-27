package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.MetaExpedientCarpetaEntity;
import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MetaExpedientCarpetaRepository extends JpaRepository<MetaExpedientCarpetaEntity, Long> {

	List<MetaExpedientCarpetaEntity> findByMetaExpedientAndPare(MetaExpedientEntity metaExpedient, MetaExpedientCarpetaEntity pare);
	List<MetaExpedientCarpetaEntity> findByMetaExpedient(MetaExpedientEntity metaExpedient);
}