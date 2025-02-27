package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.MetaExpedientEntity;
import es.caib.ripea.persistence.entity.MetaExpedientSequenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface MetaExpedientSequenciaRepository extends JpaRepository<MetaExpedientSequenciaEntity, Long> {
	MetaExpedientSequenciaEntity findByMetaExpedientAndAny(MetaExpedientEntity metaExpedient, int any);
}