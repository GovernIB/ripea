package es.caib.ripea.core.persistence.repository;

import es.caib.ripea.core.persistence.entity.MetaExpedientTascaValidacioEntity;
import es.caib.ripea.service.intf.dto.ItemValidacioTascaEnum;
import es.caib.ripea.service.intf.dto.TipusValidacioTascaEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}