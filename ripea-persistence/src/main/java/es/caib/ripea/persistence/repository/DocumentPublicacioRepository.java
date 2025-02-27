
package es.caib.ripea.persistence.repository;

import es.caib.ripea.persistence.entity.DocumentEntity;
import es.caib.ripea.persistence.entity.DocumentPublicacioEntity;
import es.caib.ripea.persistence.entity.ExpedientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DocumentPublicacioRepository extends JpaRepository<DocumentPublicacioEntity, Long> {

	List<DocumentPublicacioEntity> findByExpedientOrderByEnviatDataAsc(
			ExpedientEntity expedient);

	List<DocumentPublicacioEntity> findByDocumentOrderByEnviatDataAsc(
			DocumentEntity document);
}