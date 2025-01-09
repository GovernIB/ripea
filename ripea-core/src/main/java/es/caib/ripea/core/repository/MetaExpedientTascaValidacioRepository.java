package es.caib.ripea.core.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.ripea.core.api.dto.ItemValidacioTascaEnum;
import es.caib.ripea.core.api.dto.TipusValidacioTascaEnum;
import es.caib.ripea.core.entity.MetaExpedientTascaValidacioEntity;

public interface MetaExpedientTascaValidacioRepository extends JpaRepository<MetaExpedientTascaValidacioEntity, Long> {
	public List<MetaExpedientTascaValidacioEntity> findByMetaExpedientTascaId(Long metaExpedientTascaId);
	public List<MetaExpedientTascaValidacioEntity> findByItemValidacioAndTipusValidacioAndItemId(
			ItemValidacioTascaEnum itemValidacio,
			TipusValidacioTascaEnum tipusValidacio,
			Long itemId);
	public List<MetaExpedientTascaValidacioEntity> findByItemValidacioAndItemId(
			ItemValidacioTascaEnum itemValidacio,
			Long itemId);
}