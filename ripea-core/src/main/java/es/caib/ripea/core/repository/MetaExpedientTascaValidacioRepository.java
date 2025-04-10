package es.caib.ripea.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.ripea.core.api.dto.ItemValidacioTascaEnum;
import es.caib.ripea.core.api.dto.TipusValidacioTascaEnum;
import es.caib.ripea.core.entity.MetaExpedientTascaValidacioEntity;

public interface MetaExpedientTascaValidacioRepository extends JpaRepository<MetaExpedientTascaValidacioEntity, Long> {
	public List<MetaExpedientTascaValidacioEntity> findByMetaExpedientTascaId(Long metaExpedientTascaId);
	public List<MetaExpedientTascaValidacioEntity> findByItemValidacioAndTipusValidacioAndItemIdAndMetaExpedientTascaId(
			ItemValidacioTascaEnum itemValidacio,
			TipusValidacioTascaEnum tipusValidacio,
			Long itemId,
			Long metaExpedientTascaId);
	public List<MetaExpedientTascaValidacioEntity> findByItemValidacioAndItemId(
			ItemValidacioTascaEnum itemValidacio,
			Long itemId);
	
	@Modifying
 	@Query(value = "UPDATE IPA_METAEXP_TASCA_VALIDACIO " +
 			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
 			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
 			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
 			nativeQuery = true)
 	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}