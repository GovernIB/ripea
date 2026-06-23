package es.caib.ripea.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import es.caib.ripea.persistence.entity.MetaDocumentFluxPortafibEntity;

@Component
public interface MetaDocumentFluxPortafibRepository extends JpaRepository<MetaDocumentFluxPortafibEntity, Long> {

	public List<MetaDocumentFluxPortafibEntity> findByMetaDocumentId(Long metaDocumentId);

	public MetaDocumentFluxPortafibEntity findByMetaDocumentIdAndPortafirmesFluxId(Long metaDocumentId, String portafirmesFluxId);

	public MetaDocumentFluxPortafibEntity findByMetaDocumentIsNullAndPortafirmesFluxId(String portafirmesFluxId);
}